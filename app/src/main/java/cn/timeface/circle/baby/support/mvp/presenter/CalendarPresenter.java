package cn.timeface.circle.baby.support.mvp.presenter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import cn.timeface.circle.baby.App;
import cn.timeface.circle.baby.BuildConfig;
import cn.timeface.circle.baby.dialogs.TFDialog;
import cn.timeface.circle.baby.support.api.exception.ResultException;
import cn.timeface.circle.baby.support.api.models.TFUploadFile;
import cn.timeface.circle.baby.support.api.models.db.PhotoModel;
import cn.timeface.circle.baby.support.mvp.bases.BasePresenter;
import cn.timeface.circle.baby.support.mvp.model.CalendarModel;
import cn.timeface.circle.baby.support.mvp.model.GeneralBookObj;
import cn.timeface.circle.baby.support.mvp.presentations.CalendarPresentation;
import cn.timeface.circle.baby.support.oss.OSSManager;
import cn.timeface.circle.baby.support.oss.OpenSDKUploader;
import cn.timeface.circle.baby.support.utils.ExifUtil;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.calendar.BookCache;
import cn.timeface.circle.baby.ui.calendar.CalendarActivity;
import cn.timeface.circle.baby.ui.calendar.CalendarPreviewActivity;
import cn.timeface.circle.baby.ui.calendar.bean.CalendarExtendObj;
import cn.timeface.circle.baby.ui.calendar.bean.CommemorationDataManger;
import cn.timeface.circle.baby.ui.calendar.bean.DateObj;
import cn.timeface.circle.baby.ui.calendar.bean.UploadedPhotoTemplate;
import cn.timeface.circle.baby.ui.calendar.events.PhotoUploadProgressEvent;
import cn.timeface.circle.baby.views.ShareDialog;
import cn.timeface.circle.baby.views.dialog.TFProgressDialog;
import cn.timeface.open.api.bean.base.TFOBaseResponse;
import cn.timeface.open.api.bean.obj.TFOBookContentModel;
import cn.timeface.open.api.bean.obj.TFOBookElementModel;
import cn.timeface.open.api.bean.obj.TFOBookModel;
import cn.timeface.open.api.bean.obj.TFOContentObj;
import cn.timeface.open.api.bean.obj.TFOPublishObj;
import cn.timeface.open.api.bean.obj.TFOResourceObj;
import cn.timeface.open.api.bean.response.EditPod;
import cn.timeface.open.api.bean.response.EditText;
import cn.timeface.open.api.bean.response.SimplePageTemplate;
import cn.timeface.open.constant.TFOConstant;
import cn.timeface.open.event.ContentChangeEvent;

import cn.timeface.open.model.BookModelCache;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.FuncN;
import rx.schedulers.Schedulers;

/**
 * Created by JieGuo on 16/9/29.
 */

public class CalendarPresenter extends BasePresenter<CalendarPresentation.View, CalendarModel>
        implements CalendarPresentation.Presenter {

    public CalendarExtendObj bookModel;
    private GeneralBookObj remoteBook;
    private Map<String, List<SimplePageTemplate>> templates = new ArrayMap<>();
    private Map<String, Integer> currentTemplate = new TreeMap<>();

    // 实现O1的快速查找 element 通过elementName 和 月份
    private Map<String, Map<String, TFOBookElementModel>> elementsMap = null;
    private Map<String, TFOBookContentModel> contentModelMap = null;
    private Gson gson = new Gson();

    public CalendarPresenter(CalendarPresentation.View view) {
        setup(view, new CalendarModel());
//        new TimeFaceOpenSDKModel();
    }

    @Override
    public CalendarExtendObj getOriginalModel() {
        return bookModel;
    }

    @Override
    public TFOBookModel getBackSide() {
        return bookModel.getBackSide();
    }

    @Override
    public TFOBookModel getFrontSide() {
        return bookModel.getFrontSide();
    }

    public TFOBookModel getFrontSideWithCover() throws Throwable {
        TFOBookModel m = bookModel.getFrontSide();
        // 把封面加回去
        m.getContentList().add(0, bookModel.getContentList().get(0));
        return m;
    }

    public TFOBookModel getBackSideWithCover() throws Throwable {
        TFOBookModel m = bookModel.getBackSide();
        m.getContentList().add(0, bookModel.getContentList().get(
                bookModel.getContentList().size() - 1
        ));
        return m;
    }

    @Override
    public void create(
            int type,
            Action1<TFOBaseResponse<CalendarExtendObj>> onLoad,
            Action1<Throwable> onError) {

        view.addSubscription(
                model.create(type)
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io()
                        )
                        .flatMap(convert())
                        .observeOn(AndroidSchedulers.mainThread())
                        .flatMap(calendarExtendObjBaseResponse -> {
                            onLoad.call(calendarExtendObjBaseResponse);
                            return Observable.just(calendarExtendObjBaseResponse);
                        })
                        .observeOn(Schedulers.io())
                        .flatMap(cbr -> {

                                    CalendarExtendObj obj = cbr.getData();
                                    String firstContentId = obj.getFrontSide().getContentList().get(0).getContentId();
                                    return model.templateList(
                                            obj.getBookId(), obj.getBookType(), firstContentId
                                    );
                                }
                        )
                        .observeOn(AndroidSchedulers.mainThread()
                        )
                        .doOnCompleted(this::loadAllPageTemplate)
                        .subscribe(listBaseResponse -> {
                            // 这是要默认就把那些模板给加
                            String contentId = bookModel.getFrontSide().getContentList().get(0).getContentId();
                            templates.put(contentId, listBaseResponse.getData());
                            currentTemplate.put(contentId, 0);

                            // 更新显示的数字
                            view.setCurrentTemplateSize(currentTemplate.get(contentId), listBaseResponse.getData().size());
                        }, onError)
        );
    }

    public void create(
            int type,
            List<TFOPublishObj> publishObjs,
            Action1<TFOBaseResponse<CalendarExtendObj>> onLoad,
            Action1<Throwable> onError) {

        view.addSubscription(
                model.create(type, publishObjs)
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io()
                        )
                        .flatMap(convert())
                        .observeOn(AndroidSchedulers.mainThread())
                        .flatMap(calendarExtendObjBaseResponse -> {

                            onLoad.call(calendarExtendObjBaseResponse);
                            return Observable.just(calendarExtendObjBaseResponse);
                        })
                        .observeOn(Schedulers.io())
                        .flatMap(cbr -> {

                                    CalendarExtendObj obj = cbr.getData();
                                    String firstContentId = obj.getFrontSide().getContentList().get(0).getContentId();
                                    return model.templateList(
                                            obj.getBookId(), obj.getBookType(), firstContentId
                                    );
                                }
                        )
                        .observeOn(AndroidSchedulers.mainThread()
                        )
                        .doOnCompleted(this::loadAllPageTemplate)
                        .subscribe(listBaseResponse -> {
                            // 这是要默认就把那些模板给加
                            String contentId = bookModel.getFrontSide().getContentList().get(0).getContentId();
                            templates.put(contentId, listBaseResponse.getData());
                            currentTemplate.put(contentId, 0);

                            // 更新显示的数字
                            view.setCurrentTemplateSize(currentTemplate.get(contentId), listBaseResponse.getData().size());
                        }, onError)
        );
    }

    public void loadAllPageTemplate() {
        if (bookModel != null) {
            Observable.from(
                    bookModel.getFrontSide().getContentList()
            ).observeOn(Schedulers.io()
            ).flatMap(new Func1<TFOBookContentModel,
                    Observable<TFOBaseResponse<List<SimplePageTemplate>>>>() {

                @Override
                public Observable<TFOBaseResponse<List<SimplePageTemplate>>>
                call(TFOBookContentModel contentModel) {
                    // 加载一页的模板 数据
                    return model.templateList(
                            bookModel.getBookId(), bookModel.getBookType(), contentModel.getContentId()
                    );
                }
            }, (contentModel, o) -> {

                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "data : " + new Gson().toJson(o));
                }

                templates.put(contentModel.getContentId(), o.getData());

                String tId = contentModelMap.get(contentModel.getContentId()).getTemplateId();
                int index = 0;
                if (tId != null) {
                    for (int i = 0; i < o.getData().size(); i++) {
                        if (tId.equals(String.valueOf(o.getData().get(i).getTemplateId()))) {
                            index = i;
                            break;
                        }
                    }
                }

                currentTemplate.put(contentModel.getContentId(), index);
                return contentModel;
            }).observeOn(AndroidSchedulers.mainThread()
            ).doOnCompleted(() -> {
                Log.d(TAG, "load all template completed ---------->");
                List<TFOBookContentModel> cms = getCurrentContentModels();
                String cId = cms.get(0).getContentId();
                if (currentTemplate.containsKey(cId)) {
                    view.setCurrentTemplateSize(currentTemplate.get(cId), templates.get(cId).size());
                }
            }).subscribe(contentModel -> {

                Log.v(TAG, "load ok");
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "load content model : " + contentModel.getContentId());
                }
            }, throwable -> {
                Log.e(TAG, "error", throwable);
            });
        }
    }

    @Override
    public void list(Action1<?> onLoad, Action1<Throwable> onError) {

    }

    public void listTemplate(
            String bookId, int bookType, String contentId,
            Action1<TFOBaseResponse<List<SimplePageTemplate>>> onLoad,
            Action1<Throwable> onError) {
        view.addSubscription(
                model.templateList(bookId, bookType, contentId)
                        .compose(SchedulersCompat.applyIoSchedulers())
                        .subscribe(onLoad, onError)
        );
    }

    @Override
    public void get(
            String id,
            Action1<TFOBaseResponse<CalendarExtendObj>> onLoad,
            Action1<Throwable> onError) {
        view.addSubscription(
                model.get(
                        id
                ).compose(
                        SchedulersCompat.applyIoSchedulers()
                ).flatMap(convert()
                ).subscribe(response -> {
                    this.bookModel = response.getData();
                    BookCache.getInstance().putModelById(bookModel.getBookId(), bookModel);
                    onLoad.call(response);
                }, onError)
        );
    }

    @Override
    public void get(String id, String type,
                    Action1<TFOBaseResponse<CalendarExtendObj>> onLoad,
                    Action1<Throwable> onError) {
        view.addSubscription(
                model.get(
                        id, type
                ).compose(
                        SchedulersCompat.applyIoSchedulers()
                ).flatMap(convert()
                ).subscribe(response -> {
                    this.bookModel = response.getData();
                    BookCache.getInstance().putModelById(bookModel.getBookId(), bookModel);
                    onLoad.call(response);
                }, onError)
        );
    }

    @Override
    public void getByRemoteId(
            String remoteId, String type,
            Action1<TFOBaseResponse<CalendarExtendObj>> onLoad, Action1<Throwable> onError) {
        Subscription s = model.getRemoteBook(remoteId, type
        ).subscribeOn(Schedulers.io()
        ).observeOn(Schedulers.io()
        ).subscribe(response -> {
            remoteBook = response.getData();
            String bookId = remoteBook.getBookId();

            CalendarExtendObj book = (CalendarExtendObj) BookCache.getInstance().getModelById(bookId);
            if (book != null) {
                this.bookModel = book;
                TFOBaseResponse<TFOBookModel>
                        tbs = new TFOBaseResponse<>();
                tbs.setData(book);
                tbs.setInfo("ok");
                Observable.just(tbs)
                        .flatMap(convert())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(onLoad, onError);
            } else {
                get(response.getData().getBookId(), String.valueOf(response.getData().getBookType())
                        , onLoad, onError);
            }

            if (response.getData() != null) {
                CommemorationDataManger.getInstance().loadData(response.getData().getDays());
            }
        }, onError);
        view.addSubscription(s);
    }

    @Override
    public void update(Action1<CalendarExtendObj> onLoad, Action1<Throwable> onError) {

        view.addSubscription(
                model.update(bookModel)
                        .compose(
                                SchedulersCompat.applyIoSchedulers()
                        ).flatMap(
                        editPodBaseResponse -> {
                            return model.get(editPodBaseResponse.getData().getBookId());
                        }
                ).flatMap(response -> {
                    //response.getData().setMyViewScale(bookModel.getMyViewScale());
                    return Observable.just(new CalendarExtendObj(response.getData()));
                }).subscribe(onLoad, onError)
        );
    }

    @Override
    public void save() {
        float viewScale = bookModel.getMyViewScale();
        Subscription s = Observable.just(viewScale
        ).doOnSubscribe(() -> {

            view.showLoading();
        }).observeOn(Schedulers.io()
        ).concatMap(new Func1<Float, Observable<TFOBaseResponse<String>>>() {
            @Override
            public Observable<TFOBaseResponse<String>> call(Float aFloat) {
                return createCover();
            }
        }).flatMap(response -> {
            if (response.success()) {
                bookModel.setBookCover(response.getData());
            }
            bookModel.setBookAuthor(FastData.getUserName());
            bookModel.setBookTitle(FastData.getUserName() + "的2017时光台历");
            BookCache.getInstance().putModelById(bookModel.getBookId(), bookModel);
            if (remoteBook == null) {
                return model.addRemoteCalendar(bookModel);
            } else {
                return model.updateRemoteCalendar(String.valueOf(remoteBook.getId()), bookModel);
            }
        }).observeOn(
                AndroidSchedulers.mainThread()
        ).subscribe(response -> {

            view.hideLoading();
            if (TextUtils.isDigitsOnly(response.dataId)) {
                CalendarPreviewActivity.open(view.getCurrentActivity(),
                        bookModel.getBookId(), String.valueOf(bookModel.getBookType()), response.dataId);
                if (view instanceof CalendarActivity) {
                    // 关闭这个界面
                    ((CalendarActivity) view).finish();
                }
            } else {
                ToastUtil.showToast("数据错误");
            }
        }, throwable -> {
            view.hideLoading();
            if (throwable instanceof ResultException) {
                ToastUtil.showToast(throwable.getMessage());
            } else {
                ToastUtil.showToast("保存失败");
            }
            Log.e(TAG, "error", throwable);
        });

        view.addSubscription(s);
    }

    /**
     * uploadAndCreateBook cover with conditions
     *
     * @return Observable
     */
    private Observable<TFOBaseResponse<String>> createCover() {
        try {
            int width = (int) bookModel.getBookWidth();
            int height = (int) bookModel.getBookHeight();
            TFOBookContentModel content = bookModel.getFrontSide().getContentList().get(0);
            return model.createCover(width, height, content);
        } catch (Exception e) {
            return Observable.error(e);
        }
    }

    @Override
    public void delete() {
        view.addSubscription(
                model.delete(remoteBook.getId()+"$"+remoteBook.getBookType())
                        .subscribe(baseResponse -> {
                            view.getCurrentActivity().finish();
                        }, throwable -> {
                            Log.e(TAG, "error", throwable);
                        })
        );
    }

    @Override
    public void addCommemorationByDay(
            TFOBookContentModel contentModel, String month,
            String day, String text) {

        if (contentModel == null) return;

        view.showLoading();

        Thread addTask = new Thread(() -> {

            // 正面的和背面的两页数据
            List<TFOBookContentModel> contentList = getCurrentContentModels();

            // 待编辑的element 列表
            List<Observable<TFOBaseResponse<EditText>>> observableList = new LinkedList<>();

            // 开始编辑

            // 正面
            String frontContentId = contentList.get(0).getContentId();
            if (elementsMap.containsKey(frontContentId)) {

                // 显示白板
                setElementVisibility(frontContentId, "pendant101", true);

                // 日期标记
                setElementVisibility(frontContentId, "pendant" + day, true);

                // 左边的纪念日列表条目
                changeFrontList(month, frontContentId, observableList);
            }

            // 背面
            String backContentId = contentList.get(1).getContentId();

            if (elementsMap.containsKey(backContentId)
                    && elementsMap.get(backContentId).containsKey("word" + day)) {

                TFOBookElementModel element = elementsMap.get(backContentId).get("word" + day);
                if (element.getElementType() == 2) {

                    // 对应日期的element
                    element.setElementContent(text);
                    element.setElementDeleted(false);

                    observableList.add(
                            model.updateElement(bookModel.getBookId(), backContentId, element, text)
                    );
                }
            }


            Observable.zip(observableList, new FuncN<List<TFOBaseResponse<EditText>>>() {
                @Override
                public List<TFOBaseResponse<EditText>> call(Object... args) {

                    List<TFOBaseResponse<EditText>> resultList = new ArrayList<>();
                    for (Object o : args) {

                        if (o instanceof TFOBaseResponse) {

                            Object data = ((TFOBaseResponse) o).getData();
                            if (data instanceof EditText) {

                                TFOBookElementModel element = ((EditText) data).getElementModel();

                                if (TextUtils.isEmpty(element.getReContentId())) {
                                    Log.e(TAG, "请服务器接口里不要把 : ReContentId 值给删除了.");
                                } else {
                                    TFOBookElementModel ele = elementsMap.get(element.getReContentId())
                                            .get(element.getElementName());

                                    // 操你大爷的， 这里需要重置这个属性，否则再次计算的时候就不对了。
                                    element.setReContentId("");
                                    bookModel.replaceElement(element, ele);
                                }
                            }
                        }
                    }
                    return resultList;
                }
            }).subscribeOn(Schedulers.io()
            ).observeOn(Schedulers.io()
            ).flatMap(baseResponses -> {

                // 这个有个错可能抓不到，  上帝保佑
                return model.updateContents(bookModel.getBookId(), getCurrentContentModels());
            }).observeOn(AndroidSchedulers.mainThread()
            ).subscribe(response -> {

                view.hideLoading();

                // 通知刷新界面
                for (TFOBookContentModel cc :
                        getCurrentContentModels()) {
                    EventBus.getDefault().post(
                            new ContentChangeEvent(cc.getContentId())
                    );
                }
            }, throwable -> {
                view.hideLoading();
                Log.e(TAG, "error", throwable);
            });
        });

        addTask.setPriority(Thread.MAX_PRIORITY);
        addTask.start();
    }

    private Func1<TFOBaseResponse<TFOBookModel>,
            Observable<TFOBaseResponse<CalendarExtendObj>>> convert() {
        // 转换成我们要的格式
        return response -> {
            TFOBaseResponse<CalendarExtendObj> baseResponse =
                    new TFOBaseResponse<>();
            BookModelCache.getInstance().setBookModel(response.getData());
            CalendarExtendObj obj = new CalendarExtendObj(response.getData());
            bookModel = obj;
            BookCache.getInstance().putModelById(bookModel.getBookId(), bookModel);
            baseResponse.setData(obj);

            elementsMap = new TreeMap<>();
            contentModelMap = new TreeMap<>();
            for (int i = 0; i < obj.getContentList().size(); i++) {
                TFOBookContentModel content = obj.getContentList().get(i);
                if (!elementsMap.containsKey(content.getContentId())) {
                    elementsMap.put(content.getContentId(), new TreeMap<>());
                }

                if (!contentModelMap.containsKey(content.getContentId())) {
                    contentModelMap.put(content.getContentId(), content);
                }

                for (int j = 0; j < content.getElementList().size(); j++) {

                    TFOBookElementModel element = content.getElementList().get(j);
                    if (!elementsMap.get(content.getContentId())
                            .containsKey(element.getElementName())) {

                        elementsMap.get(content.getContentId())
                                .put(element.getElementName(), element);
                    }
                }
            }
            return Observable.just(baseResponse);
        };
    }


    /**
     * 设置一个 element 显示与隐藏
     *
     * @param contentId  content id
     * @param elementId  element id
     * @param visibility visibility
     */
    private TFOBookElementModel setElementVisibility(String contentId, String elementId, boolean visibility) {
        if (elementsMap.containsKey(contentId)) {
            if (elementsMap.get(contentId).containsKey(elementId)) {

                TFOBookElementModel elementModel = elementsMap.get(contentId).get(elementId);
                elementModel.setElementDeleted(visibility);
                return elementModel;
            }
        }
        return null;
    }

    /**
     * 重排左下角的列表
     *
     * @param month       month
     * @param contentId   content id
     * @param observables list
     */
    private void changeFrontList(String month, String contentId,
                                 List<Observable<TFOBaseResponse<EditText>>> observables) {

        CommemorationDataManger manager = CommemorationDataManger.getInstance();
        List<DateObj> daList = new ArrayList<>(
                manager.getSource().get(month)
        );
        Collections.sort(daList);

        for (int i = 0; i < 6; i++) {

            // 设置对应的值
            TFOBookElementModel ele =
                    setElementVisibility(contentId, "word10" + (i + 1), true);
            if (daList.size() > i) {

                if (ele != null) {
                    ele.setElementDeleted(false);
                    DateObj dateObj = daList.get(i);
                    String elementText = String.format(Locale.CHINESE, "%s / %s",
                            dateObj.getDay(), dateObj.getContent());

                    observables.add(
                            model.updateElement(bookModel.getBookId(),
                                    contentId, ele, elementText)
                    );
                }
            } else {
                if (ele != null) {
                    ele.setElementDeleted(true);
                }
            }
        }
    }

    @Override
    public void deleteCommemorationByDay(
            TFOBookContentModel contentModel,
            String month,
            String day,
            String text) {

        view.getCurrentActivity().runOnUiThread(()->{
            view.showLoading();
        });

        new Thread(() -> {

            List<TFOBookContentModel> contentList = getCurrentContentModels();

            // 待编辑的element 列表
            List<Observable<TFOBaseResponse<EditText>>> observableList = new LinkedList<>();

            // 正面
            String frontContentId = contentList.get(0).getContentId();
            if (elementsMap.containsKey(frontContentId)) {

                // 更改那6个值的重排
                changeFrontList(month, frontContentId, observableList);

                // 日期标记
                setElementVisibility(frontContentId, "pendant" + day, false);
            }

            // 背面
            String backContentId = contentList.get(1).getContentId();

            // 背面的纪念日字
            setElementVisibility(backContentId, "word" + day, false);


            Subscription subscription = null;
            if (observableList.size() < 1) {

                // 显示白板
                boolean isShowWhitePad = CommemorationDataManger.getInstance()
                        .getSource().get(month).size() > 0;
                setElementVisibility(frontContentId, "pendant101", isShowWhitePad);

                for (int i = 1; i < 7; i++) {
                    setElementVisibility(frontContentId, "word10" + i, false);
                }

                subscription = model.updateContents(bookModel.getBookId(), getCurrentContentModels())
                        .compose(SchedulersCompat.applyIoSchedulers())
                        .subscribe(response -> {
                            bookModel.getFrontSide();
                            bookModel.getBackSide();

                            // 通知刷新界面
                            for (TFOBookContentModel cc :
                                    getCurrentContentModels()) {

                                EventBus.getDefault().post(
                                        new ContentChangeEvent(cc.getContentId())
                                );
                            }
                            view.hideLoading();
                        }, throwable -> {

                            view.hideLoading();
                            Log.e(TAG, "error", throwable);
                        });
            } else {
                subscription = Observable.zip(observableList, new FuncN<Object>() {
                    @Override
                    public Object call(Object... args) {
                        for (Object o : args) {

                            if (o instanceof TFOBaseResponse) {

                                Object data = ((TFOBaseResponse) o).getData();
                                if (data instanceof EditText) {

                                    //resultList.add((TFOBaseResponse<EditText>) data);
                                    TFOBookElementModel element = ((EditText) data).getElementModel();

                                    if (TextUtils.isEmpty(element.getReContentId())) {
                                        Log.e(TAG, "请服务器接口里不要把 : ReContentId 值给删除了.");
                                    } else {
                                        TFOBookElementModel ele = elementsMap.get(element.getReContentId())
                                                .get(element.getElementName());

                                        // 操你大爷的， 这里需要重置这个属性，否则再次计算的时候就不对了。
                                        Log.v("DEBUG_ELEMENT", "after ---- > width : " + element.getElementWidth() + ", height : "
                                                + element.getElementHeight() + ", point ("
                                                + element.getElementLeft() + ", " + element.getElementTop() + ")");
                                        element.setReContentId("");
                                        bookModel.replaceElement(element, ele);
                                    }
                                }
                            }
                        }
                        return args;
                    }
                }).observeOn(Schedulers.io()
                ).flatMap(response -> {

                    return saveCurrentPageData();
                }).observeOn(AndroidSchedulers.mainThread()
                ).subscribe(response -> {

                    // 通知刷新界面
                    for (TFOBookContentModel cc :
                            getCurrentContentModels()) {

                        EventBus.getDefault().post(
                                new ContentChangeEvent(cc.getContentId())
                        );
                    }
                    view.hideLoading();
                }, throwable -> {

                    Log.e(TAG, "error", throwable);
                    view.hideLoading();
                });
            }

            view.addSubscription(subscription);
        }).start();
    }

    @Override
    public void updateCommemorationByDay(
            TFOBookContentModel contentModel,
            String oldMonth, String oldDay, String oldText,
            String month, String day, String text) {

        view.showLoading();

        new Thread(() -> {
            // 先把显示的old置为不显示
            // 再设置一个新的值,
            // 更新数据
            // 正面的ID

            // 正面的和背面的两页数据
            List<TFOBookContentModel> contentList = getCurrentContentModels();

            // 待编辑的element 列表
            List<Observable<TFOBaseResponse<EditText>>> observableList = new LinkedList<>();

            // 开始编辑

            // 正面
            String frontContentId = contentList.get(0).getContentId();
            if (elementsMap.containsKey(frontContentId)) {

                // 旧日期标记
                setElementVisibility(frontContentId, "pendant" + oldDay, false);
                // 新日期标记
                setElementVisibility(frontContentId, "pendant" + day, true);

                // 左边的纪念日列表条目
                changeFrontList(month, frontContentId, observableList);
            }

            // 背面
            String backContentId = contentList.get(1).getContentId();

            if (elementsMap.containsKey(backContentId)
                    && elementsMap.get(backContentId).containsKey("word" + day)) {

                // 旧的数据
                setElementVisibility(backContentId, "word" + oldDay, false);

                // 新的数据
                TFOBookElementModel element = elementsMap.get(backContentId).get("word" + day);
                if (element.getElementType() == 2) {

                    // 对应日期的element
                    element.setElementContent(text);
                    element.setElementDeleted(false);

                    observableList.add(
                            model.updateElement(bookModel.getBookId(), backContentId, element, text)
                    );
                }
            }

            // 保一条，刷新 数据
            bookModel.getFrontSide();
            bookModel.getBackSide();

            Subscription s = Observable.zip(observableList, new FuncN<List<TFOBaseResponse<EditText>>>() {
                @Override
                public List<TFOBaseResponse<EditText>> call(Object... args) {

                    List<TFOBaseResponse<EditText>> resultList = new ArrayList<>();
                    for (Object o : args) {

                        if (o instanceof TFOBaseResponse) {

                            Object data = ((TFOBaseResponse) o).getData();
                            if (data instanceof EditText) {

                                //resultList.add((TFOBaseResponse<EditText>) data);
                                TFOBookElementModel element = ((EditText) data).getElementModel();

                                if (TextUtils.isEmpty(element.getReContentId())) {
                                    Log.e(TAG, "请服务器接口里不要把 : ReContentId 值给删除了.");
                                } else {
                                    TFOBookElementModel ele = elementsMap.get(element.getReContentId())
                                            .get(element.getElementName());

                                    Log.v("DEBUG_ELEMENT", "after ---- > width : " + element.getElementWidth() + ", height : "
                                            + element.getElementHeight() + ", point ("
                                            + element.getElementLeft() + ", " + element.getElementTop() + ")");
                                    element.setReContentId("");
                                    bookModel.replaceElement(element, ele);
                                }
                            }
                        }
                    }
                    return resultList;
                }
            }).subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .flatMap(baseResponses -> {
                        // 这个有个错可能抓不到，  上帝保佑
                        return model.updateContents(bookModel.getBookId(), getCurrentContentModels());
                    }).flatMap(
                            response -> {
                                return saveCurrentPageData();
                            }
                    ).observeOn(AndroidSchedulers.mainThread()
                    ).subscribe(response -> {

                        view.hideLoading();

                        // 通知刷新界面
                        for (TFOBookContentModel cc :
                                getCurrentContentModels()) {

                            EventBus.getDefault().post(
                                    new ContentChangeEvent(cc.getContentId())
                            );
                        }
                    }, throwable -> {
                        view.hideLoading();
                        Log.e(TAG, "error", throwable);
                    });

            view.addSubscription(s);

        }).start();
    }

    @Override
    public void changeTemplate(String contentId) {

        view.addSubscription(
                Observable.from(
                        getCurrentContentModels()
                ).filter(contentModel -> {

                    return contentId.equals(contentModel.getContentId());
                }).filter(contentModel -> {

                    return templates.containsKey(contentId);
                }).doOnCompleted(() -> {
                    if (!templates.containsKey(contentId)) {
                        view.setCurrentTemplateSize(0, 0);
                    }
                }).subscribe(contentModel -> {

                    // 更新 界面显示的版式序号
                    int count = templates.get(contentId).size();
                    int current = currentTemplate.get(contentId);

                    // 更新 view
                    if (current < count - 1) {
                        view.setCurrentTemplateSize(current + 1, count);
                        currentTemplate.put(contentId, current + 1);
                    } else {
                        view.setCurrentTemplateSize(0, count);
                        currentTemplate.put(contentId, 0);
                    }

                    changeTemplateData(contentId);
                }, throwable -> {
                    Log.e(TAG, "error", throwable);
                })
        );
    }

    @Override
    public void refreshTemplate(String contentId) {
        if (templates.containsKey(contentId)) {
            int count = templates.get(contentId).size();
            int current = currentTemplate.get(contentId);
            view.setCurrentTemplateSize(current, count);
        } else {
            view.setCurrentTemplateSize(0, 0);
        }
    }

    private void changeTemplateData(String contentId) {
        view.showLoading();
        view.addSubscription(
                Observable.just(getCurrentContentModels().get(0))
                        .observeOn(Schedulers.io())
                        .filter(contentModel -> {
                            return contentId.equals(contentModel.getContentId());
                        })
                        .flatMap(contentModel -> {

                            int templateIndex = currentTemplate.get(contentModel.getContentId());

                            return model.changePageTemplate(
                                    bookModel.getBookId(),
                                    templates.get(contentId).get(templateIndex).getTemplateId(),
                                    Collections.singletonList(contentModel)
                            );
                        })
                        .flatMap(listBaseResponse -> {
                            int realIndex = bookModel.getContentList()
                                    .indexOf(contentModelMap.get(contentId));

                            TFOBookContentModel content = listBaseResponse.getData().getContentList().get(0);

                            bookModel.getContentList().set(
                                    realIndex,
                                    content
                            );

                            updateContentKepMap(content);

                            // 需要重新设置上显示纪念日 标记
                            String frontContentId = content.getContentId();
                            String month = String.valueOf(view.getCurrentPageIndex() + 1);
                            CommemorationDataManger manger = CommemorationDataManger.getInstance();
                            if (elementsMap.containsKey(frontContentId)) {

                                List<DateObj> dList = manger.getSource().get(month);
                                for (int i = 0; i < dList.size(); i++) {

                                    // 日期标记
                                    setElementVisibility(frontContentId, "pendant" + dList.get(i).getDay(), true);
                                }
                                if (dList.size() > 0) {
                                    // 显示白板
                                    setElementVisibility(frontContentId, "pendant101", true);
                                }
                            }

                            // 重置纪念日标记结束
                            return Observable.just(listBaseResponse);
                        })
                        // 保存到服务器上
                        .flatMap(listBaseResponse -> {

                            return model.updateContents(bookModel.getBookId(), listBaseResponse.getData().getContentList());
                        }, ((listBaseResponse, editPodBaseResponse) -> {
                            if (!editPodBaseResponse.success()) {
                                return Observable.error(new Exception("修改版式失败"));
                            }
                            return listBaseResponse;
                        }))
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(listBaseResponse -> {

                            bookModel.refreshData();
                            view.refreshView();
                            view.hideLoading();
                        }, throwable -> {
                            ToastUtil.showToast("切换模板失败");
                            view.hideLoading();
                            int current = currentTemplate.get(contentId);
                            int count = templates.get(contentId).size();
                            if (current == 0) {
                                view.setCurrentTemplateSize(0, count);
                            } else {
                                view.setCurrentTemplateSize(current - 1, count);
                            }
                            Log.e(TAG, "error", throwable);
                        })
        );
    }

    /**
     * 更新字典引用
     *
     * @param content
     */
    private void updateContentKepMap(TFOBookContentModel content) {
        if (contentModelMap.containsKey(content.getContentId())) {
            contentModelMap.put(content.getContentId(), content);
        }

        if (elementsMap.containsKey(content.getContentId())) {
            Map<String, TFOBookElementModel> elMap = elementsMap.get(content.getContentId());
            elMap.clear();
            for (TFOBookElementModel el : content.getElementList()) {
                elMap.put(el.getElementName(), el);
            }
        }
    }

    private List<TFOBookContentModel> getCurrentContentModels() {

        List<TFOBookContentModel> contentModels = new LinkedList<>();

        try {
            int index = view.getCurrentPageIndex();

            TFOBookContentModel contentFront = bookModel.getFrontSide().getContentList().get(index);
            TFOBookContentModel contentBack = bookModel.getBackSide().getContentList().get(index);
            contentModels.add(contentFront);
            contentModels.add(contentBack);
        } catch (Exception e) {
            Log.e(TAG, "error", e);
        }

        return contentModels;
    }

    @Override
    public void editImage(TFOBookContentModel contentModel, Intent data) throws Exception {
        if (data == null) {
            throw new Exception("data is null");
        }

        view.showLoading();
        TFOBookElementModel elementModel = data.getParcelableExtra(TFOConstant.ELEMENT_MODEL);
        String contentId = data.getStringExtra(TFOConstant.CONTENT_ID);

        if (elementModel == null || TextUtils.isEmpty(contentId)) {
            throw new Exception("element model is null.");
        }

        Observable.from(contentModel.getElementList()
        ).observeOn(Schedulers.computation()
        ).filter(ele -> {

            return ele.getElementType() == TFOBookElementModel.TYPE_IMAGE
                    && "pic1".equals(ele.getElementName());
        }).observeOn(Schedulers.io()
        ).first(

        ).flatMap(ele -> {

            bookModel.replaceElement(
                    elementModel, ele
            );

            List<TFOBookContentModel> cms = getCurrentContentModels();
            return model.updateContents(bookModel.getBookId(), cms);
        }).observeOn(AndroidSchedulers.mainThread()
        ).subscribe(response -> {

            if (!response.success()) {
                Log.e(TAG, "error : change content data failure. " + new Gson().toJson(response));
            } else {
                List<TFOBookContentModel> cms = getCurrentContentModels();
                EventBus.getDefault().post(new ContentChangeEvent(contentId));
            }
            view.hideLoading();
        }, throwable -> {
            ToastUtil.showToast("替换图片失败");
            Log.e(TAG, "change image failure", throwable);
        });
    }

    @Override
    public void editText(TFOBookContentModel contentModel, Intent data) throws Exception {
        if (data == null) {
            throw new Exception("data is null");
        }

        view.showLoading();

        String contentId = data.getStringExtra(TFOConstant.CONTENT_ID);

        if (data.getParcelableExtra(TFOConstant.ELEMENT_MODEL) == null || TextUtils.isEmpty(contentId)) {
            throw new Exception("element model is null.");
        }

        Observable.from(contentModel.getElementList()
        ).observeOn(
                Schedulers.io()
        ).filter(tfoBookElementModel -> {

            return tfoBookElementModel.getElementType() == TFOBookElementModel.TYPE_TEXT
                    && "word1".equals(tfoBookElementModel.getElementName());
        }).first(

        ).flatMap(tfoBookElementModel -> {
            TFOBookElementModel elementModel = data.getParcelableExtra(TFOConstant.ELEMENT_MODEL);
            if (TextUtils.isEmpty(elementModel.getElementContent())) {
                elementModel.setElementContent("请输入文字");
            }
            return model.updateElement(bookModel.getBookId(), contentId, tfoBookElementModel,
                    elementModel.getElementContent());
        }).flatMap(response -> {

            TFOBookElementModel elementModel = data.getParcelableExtra(TFOConstant.ELEMENT_MODEL);
            bookModel.replaceElement(response.getData().getElementModel(),
                    elementsMap.get(contentId).get(
                            response.getData().getElementModel().getElementName()
                    ));

            return Observable.just(elementModel);
        }).flatMap(ele -> {

            return model.updateContents(bookModel.getBookId(), getCurrentContentModels());
        }).observeOn(AndroidSchedulers.mainThread()
        ).subscribe(response -> {

            if (!response.success()) {
                Log.e(TAG, "error : change content data failure. " + new Gson().toJson(response));
            } else {
                EventBus.getDefault().post(new ContentChangeEvent(contentId));
            }
            view.hideLoading();
        }, throwable -> {
            ToastUtil.showToast("更改文字失败");
            Log.e(TAG, "error", throwable);
        });
    }

    @Override
    public String createShareUrl() throws Exception {
        //h5.stg1.v5time.net
        //m.timeface.cn

        if (remoteBook == null) {
            throw new Exception("时光流影不存在这本书");
        }

        String shareUrl = "";

        shareUrl = "http://";
        shareUrl += BuildConfig.DEBUG ?
                "wechat.v5time.net" :
                "wechat.timeface.cn";
        shareUrl += "/calendar/preview/?bid=" + remoteBook.getId() + "&share=1";

        new ShareDialog(view.getCurrentActivity()
        ).share(
                FastData.getUserName() + "的2017时光台历",
                "上传12张照片就能制作自己的专属台历，时光台历，让爱陪自己度过一整年。",
                remoteBook.getBookCover(),
                shareUrl
        );
        return shareUrl;
    }

    public static void shareCalendar(Context context, String bid, String coverUrl) {

        String shareUrl = "";

        shareUrl = "http://";
        shareUrl += BuildConfig.DEBUG ?
                "wechat.v5time.net" :
                "wechat.timeface.cn";
        shareUrl += "/calendar/preview/?bid=" + bid + "&share=1";

        new ShareDialog(context).share(
                FastData.getUserName() + "的2017时光台历",
                "上传12张照片就能制作自己的专属台历，时光台历，让爱陪自己度过一整年。",
                coverUrl,
                shareUrl
        );
    }

    public static String getH5PageShareUrl() {
        String shareUrl = "http://%s/calendar/index";

        return String.format(Locale.CHINESE, shareUrl, BuildConfig.DEBUG ? "wechat.v5time.net"
                : "wechat.timeface.cn");
    }

    /**
     * 保存两页数据
     *
     * @return
     */
    private Observable<TFOBaseResponse<EditPod>> saveCurrentPageData() {

        List<TFOBookContentModel> contents = getCurrentContentModels();
        if (contents.size() > 0) {
            return model.updateContents(bookModel.getBookId(), getCurrentContentModels());
        }

        return Observable.empty();
    }

    public GeneralBookObj getRemoteBook() {
        return remoteBook;
    }

    private List<TFOResourceObj> uploadedTemplate = new ArrayList<>();

    @Override
    public void uploadImageWithProgress(
            int type, List<PhotoModel> images,
            Action1<TFOBaseResponse<CalendarExtendObj>> onLoad,
            Action1<Throwable> onError) throws Exception {

        uploadedTemplate.clear();
        OSSManager ossManager = OSSManager.getOSSManager(App.getInstance());

        if (view.getCurrentActivity() instanceof CalendarActivity) {
            ((CalendarActivity) view.getCurrentActivity()).showUploadProgressDialog();
        }

        view.addSubscription(
                Observable.from(images
                ).observeOn(
                        Schedulers.io()
                ).map(photoModel -> {

                    TFUploadFile uploadFile = new TFUploadFile(
                            photoModel.getLocalPath(),
                            OpenSDKUploader.UPLOAD_CALENDAR_FOLDER);
                    try {
                        if (!ossManager.checkFileExist(uploadFile.getObjectKey())) {
                            ossManager.upload(uploadFile.getObjectKey(), uploadFile.getFilePath());
                        }

                        String url = String.format(Locale.CHINESE, "http://img1.timeface.cn/%s", uploadFile.getObjectKey());
                        photoModel.setUrl(url);
                    } catch (ClientException e) {
                        Log.e(TAG, "error", e);
                    } catch (ServiceException e) {
                        Log.e(TAG, "error", e);
                    }

                    return photoModel;
                }).filter(photoModel -> {

                    return !TextUtils.isEmpty(photoModel.getUrl());
                }).observeOn(
                        AndroidSchedulers.mainThread()
                ).doOnCompleted(() -> {


                    if (view.getCurrentActivity() instanceof CalendarActivity) {
                        ((CalendarActivity) view.getCurrentActivity()).hideUploadProgressDialog();
                    }

                    //全部上传成功
                    if(uploadedTemplate.size() >= images.size()){
                        ToastUtil.showToast("上传成功，正在玩命创建这本台历!");
                    } else {
                        ToastUtil.showToast("啊噢！你有" + (images.size() - uploadedTemplate.size()) + "张图片上传失败~");
                    }

                    TFOPublishObj tfoPublishObj = new TFOPublishObj();
                    TFOContentObj contentObj = new TFOContentObj("", uploadedTemplate);
                    tfoPublishObj.setContentlList(Collections.singletonList(contentObj));
                    List<TFOPublishObj> publishObjs = Collections.singletonList(tfoPublishObj);
                    create(type, publishObjs, onLoad, onError);
                }).subscribe(photoModel -> {

                    UploadedPhotoTemplate template = new UploadedPhotoTemplate(photoModel);
                    try {
                        Log.e("you got the orientaion", ExifUtil.getExifOrientation(photoModel.getLocalPath()) + "");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    uploadedTemplate.add(template);
                    float progress = uploadedTemplate.size() / (images.size() * 1f);

                    EventBus.getDefault().post(new PhotoUploadProgressEvent(progress));
                    Log.i(TAG, "uploaded : " + photoModel.getUrl());
                }, throwable -> {

                    if (view.getCurrentActivity() instanceof CalendarActivity) {
                        ((CalendarActivity) view.getCurrentActivity()).hideUploadProgressDialog();
                    }
                    Log.e(TAG, "error", throwable);
                })
        );
    }

    public void closeActivity() {

        if (remoteBook == null) {

            TFDialog dialog = TFDialog.getInstance();
            dialog.setMessage("您是否要保存？");
            dialog.setNegativeButton("否", v1 -> {
                dialog.dismiss();
                view.getCurrentActivity().finish();
            });
            dialog.setPositiveButton("是", v2 -> {
                save();
            });
            dialog.show(
                    view.getCurrentActivity().getSupportFragmentManager()
                    , "save dialog");
        } else {

            // 偷偷保存一下
            TFProgressDialog progressDialog = TFProgressDialog.getInstance("正在保存");
            progressDialog.show(view.getCurrentActivity().getSupportFragmentManager(), "progress");
            createCover(
            ).observeOn(Schedulers.io()
            ).subscribeOn(Schedulers.io()
            ).flatMap(response -> {
                if (response.success()) {
                    bookModel.setBookCover(response.getData());
                }
                bookModel.setBookAuthor(FastData.getUserName());
                bookModel.setBookTitle(FastData.getUserName() + "的2017时光台历");
                return model.updateRemoteCalendar(String.valueOf(remoteBook.getId()), bookModel);
            }).observeOn(AndroidSchedulers.mainThread()
            ).subscribe(response -> {
                progressDialog.dismiss();
                view.getCurrentActivity().finish();
            }, throwable -> {
                Log.e(TAG, "error", throwable);
            });
        }
    }

    @Override
    public void deleteRemoteBook() {
        view.addSubscription(
        model.deleteRemoteCalendar(String.valueOf(remoteBook.getId()), String.valueOf(remoteBook.getBookType()))
                .subscribe(baseResponse -> {
                    view.getCurrentActivity().finish();
                }, throwable -> {
                    Log.e(TAG, "error", throwable);
                })
        );
    }
}
