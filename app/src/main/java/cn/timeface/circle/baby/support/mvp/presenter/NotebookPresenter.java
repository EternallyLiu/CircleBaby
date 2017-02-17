package cn.timeface.circle.baby.support.mvp.presenter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.timeface.circle.baby.BuildConfig;
import cn.timeface.circle.baby.support.api.models.DataResponse;
import cn.timeface.circle.baby.support.mvp.bases.BasePresenter;
import cn.timeface.circle.baby.support.mvp.model.CalendarModel;
import cn.timeface.circle.baby.support.mvp.model.GeneralBookObj;
import cn.timeface.circle.baby.support.mvp.model.NotebookModel;
import cn.timeface.circle.baby.support.mvp.presentations.NotebookPresentation;
import cn.timeface.circle.baby.support.mvp.response.bases.BaseResponse;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.notebook.beans.NoteBookObj;
import cn.timeface.circle.baby.ui.notebook.beans.TemplateItem;
import cn.timeface.circle.baby.ui.notebook.cache.BookCache;
import cn.timeface.circle.baby.ui.notebook.events.NotebookSavedEvent;
import cn.timeface.circle.baby.ui.notebook.exceptions.BookDataErrorException;
import cn.timeface.circle.baby.views.ShareDialog;
import cn.timeface.circle.baby.views.dialog.TFProgressDialog;
import cn.timeface.open.api.bean.base.TFOBaseResponse;
import cn.timeface.open.api.bean.obj.TFOBookContentModel;
import cn.timeface.open.api.bean.obj.TFOBookElementModel;
import cn.timeface.open.api.bean.obj.TFOBookModel;
import cn.timeface.open.api.bean.obj.TFOEditTextReplaceObj;
import cn.timeface.open.api.bean.obj.TFOSimpleTemplate;
import cn.timeface.open.api.bean.response.CoverTemplateInfo;
import cn.timeface.open.api.bean.response.EditBookCover;
import cn.timeface.open.api.bean.response.EditPod;
import cn.timeface.open.api.bean.response.EditText;
import cn.timeface.open.event.ContentChangeEvent;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * Created by JieGuo on 16/11/21.
 */

public class NotebookPresenter extends BasePresenter<NotebookPresentation.View, NotebookModel>
        implements NotebookPresentation.Presenter {

    private CalendarModel calendarModel;
    private GeneralBookObj remoteBook = null;

    private TFOBookModel originBook;
    private List<TFOSimpleTemplate> coverTemplates;
    private TFProgressDialog progressDialog;
    private int currentTemplateId = 0;
    private List<String> contentTitles = new ArrayList<>();

    private HashMap<String, HashMap<Integer, TFOBookElementModel>>
            elementMap = new LinkedHashMap<>();

    public NotebookPresenter(NotebookPresentation.View view) {
        setup(view, new NotebookModel());
        calendarModel = new CalendarModel();
//        new TimeFaceOpenSDKModel();
        progressDialog = TFProgressDialog.getInstance("加载中……");
    }

    @Override
    public void create() {
        view.setStateView(true, "正在玩命加载");
        view.addSubscription(
                model.create().compose(SchedulersCompat.applyIoSchedulers())
                        .map(initCacheMap())
                        .subscribe(response -> {

                            if (!response.success()) {
                                view.showError("数据不完整");
                            }
                            originBook = response.getData();
                            BookCache.getInstance().putModelById(originBook.getBookId(), originBook);

                            try {
                                view.setData(getCoverPage());
                                view.setStateView(false);

                                loadCoverTemplates();
                            } catch (BookDataErrorException e) {
                                view.showError(e, this::create);
                            }
                        }, throwable -> {
                            view.showError("数据在传输过程中迷路了");
                            view.showError(throwable, this::create);
                        })
        );
    }

    private Func1<TFOBaseResponse<TFOBookModel>, TFOBaseResponse<TFOBookModel>> initCacheMap() {

        return response -> {
            elementMap.clear();
            for (TFOBookContentModel c :
                    response.getData().getContentList()) {
                HashMap<Integer, TFOBookElementModel> em = new LinkedHashMap<>();

                for (TFOBookElementModel ele : c.getElementList()) {
                    em.put(ele.getElementId(), ele);
                }
                elementMap.put(c.getContentId(), em);
            }
            return response;
        };
    }

    @Override
    public void edit(String remoteId) {
        view.getCurrentActivity().runOnUiThread(
                () -> view.setStateView(true, "正在加载……")
        );
        Subscription subscription = model
                .getRemoteBook(remoteId, String.valueOf(NotebookModel.TYPE))
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(response -> {

                    remoteBook = response.getData();

                    // 存一下扩展信息
                    String extra = response.getData().getExtra();
                    String bookId = response.getData().getBook_id();

                    try {
                        if (!TextUtils.isEmpty(extra)) {
                            JSONObject jsonObject = new JSONObject(extra);
                            if (jsonObject.has("has_insert")) {
                                model.setNotebookInsertPageStyle(
                                        bookId,
                                        jsonObject.getInt("has_insert")
                                );
                            }

                            if (jsonObject.has("inner_paper_id")) {
                                model.setNotebookPaperStyle(
                                        bookId,
                                        jsonObject.getString("inner_paper_id")
                                );
                            }
                        }
                    } catch (JSONException e) {
                        Observable.error(e);
                    }

                    load(bookId);
                }, throwable -> {
                    view.showError(throwable, () -> edit(remoteId));
                });

        view.addSubscription(subscription);
    }

    @Override
    public void load(String bookId) {
        if (TextUtils.isEmpty(bookId)) {
            Log.e(TAG, "book id can not be empty.");
            return;
        }
        view.setStateView(true);
        view.addSubscription(
                model.get(bookId).compose(SchedulersCompat.applyIoSchedulers())
                        .map(initCacheMap())
                        .doOnCompleted(this::loadCoverTemplates)
                        .subscribe(response -> {
                            originBook = response.getData();
                            view.setData(originBook);

                            view.setPreviewData(getPreviewData());

                            view.setStateView(false);
                        }, throwable -> {
                            view.showError("未找到对应的数据");
                            view.showError(throwable, () -> load(bookId));
                        })
        );
    }

    public void loadByRemoteId(String remoteId) {
        view.setStateView(true);
        view.addSubscription(
                model.getRemoteBook(remoteId, String.valueOf(NotebookModel.TYPE))
                        .compose(SchedulersCompat.applyIoSchedulers())
                        .subscribe(response -> {

                            remoteBook = response.getData();

                            // 存一下扩展信息
                            String extra = response.getData().getExtra();
                            String bookId = response.getData().getBook_id();

                            try {
                                if (!TextUtils.isEmpty(extra)) {
                                    JSONObject jsonObject = new JSONObject(extra);
                                    if (jsonObject.has("has_insert")) {
                                        model.setNotebookInsertPageStyle(
                                                bookId,
                                                jsonObject.getInt("has_insert")
                                        );
                                    }

                                    if (jsonObject.has("inner_paper_id")) {
                                        model.setNotebookPaperStyle(
                                                bookId,
                                                jsonObject.getString("inner_paper_id")
                                        );
                                    }
                                }
                            } catch (JSONException e) {
                                Observable.error(e);
                            }

                            load(bookId);
                        }, throwable -> {
                            Log.e(TAG, "error", throwable);
                            view.showError(throwable, () -> loadByRemoteId(remoteId));
                        })
        );
    }

    @Override
    public void upload() {

    }

    @Override
    public void changeCoverTemplate(int templateId) {

        String errorMessage = "模板数据错误";
        showProgress();
        view.addSubscription(
                model.changeCoverTemplate(templateId, originBook)
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io())
                        .flatMap(new Func1<TFOBaseResponse<CoverTemplateInfo>, Observable<TFOBaseResponse<EditBookCover>>>() {
                            @Override
                            public Observable<TFOBaseResponse<EditBookCover>> call(TFOBaseResponse<CoverTemplateInfo> coverTemplateInfoBaseResponse) {
                                return model.saveBookCover(originBook, currentTemplateId);
                            }
                        }, (coverTemplateInfoBaseResponse, editBookCoverBaseResponse) -> {

                            if (coverTemplateInfoBaseResponse.getData().getContentList().size() > 1) {
                                TFOBookContentModel c = coverTemplateInfoBaseResponse.getData().getContentList().get(0);

                                String ccId = originBook.getContentList().get(0).getContentId();

                                // 封面
                                originBook.getContentList().remove(0);
                                originBook.getContentList().add(0, c);

                                elementMap.remove(ccId);

                                Log.d(TAG, "--------------------");
                                HashMap<Integer, TFOBookElementModel> eMap = new LinkedHashMap<>();
                                for (TFOBookElementModel e :
                                        c.getElementList()) {
                                    eMap.put(e.getElementId(), e);
                                    Log.i(TAG, "content : " + e.getElementContent());
                                }

                                Log.d(TAG, "--------------------");
                                elementMap.put(c.getContentId(), eMap);


                                // 封底
                                originBook.getContentList().remove(originBook.getContentList().size() - 1);
                                originBook.getContentList().add(coverTemplateInfoBaseResponse.getData().getContentList().get(1));
                            } else {
                                return null;
                            }

                            return coverTemplateInfoBaseResponse;
                        })
                        .flatMap(coverTemplateInfoBaseResponse -> {
                            return model.savePage(originBook.getContentList().get(0), originBook.getBookId());
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(response -> {

                            hideProgress();
                            if (response == null) {
                                return;
                            }
                            try {
                                view.setData(getCoverPage());
                                currentTemplateId = templateId;
                            } catch (BookDataErrorException e) {
                                Log.e(TAG, e.getMessage(), e);
                                view.showError(errorMessage);
                            }

                        }, throwable -> {
                            view.showError(errorMessage);
                            Log.e(TAG, "error", throwable);
                            hideProgress();
                        })
        );
    }

    @Override
    public void changeContentStyle(TFOBookContentModel contentModel) {

        Observable.just(contentModel)
                .map(cm -> {
                    TFOBookContentModel content = originBook.getContentList().get(14);
                    cm.setContentId(content.getContentId());

                    originBook.getContentList().remove(14);
                    originBook.getContentList().add(14, cm);
                    return originBook;
                })
                .flatMap(bookModel -> {

                    // save content model
                    return Observable.just(bookModel);
                })
                .subscribe();
    }

    @Override
    public void changeImage(String contentId, TFOBookElementModel element) {
        if (elementMap.containsKey(contentId)
                && elementMap.get(contentId).containsKey(element.getElementId())) {
            TFOBookElementModel ele = elementMap.get(contentId).get(element.getElementId());
            NoteBookObj.copyElement(element, ele);

            showProgress();
            Subscription subscription = Observable.from(originBook.getContentList())
                    .observeOn(Schedulers.io())
                    .filter(contentModel -> contentModel.getContentId().equals(contentId))
                    .first()
                    .flatMap(content -> {
                        return model.savePage(content, originBook.getBookId());
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(response -> {
                        if (response.success()) {
                            try {
                                view.setData(getCoverPage());
                            } catch (BookDataErrorException e) {
                                Observable.error(e);
                            }
                        }
                        hideProgress();
                    }, throwable -> {
                        Log.e(TAG, "error", throwable);
                        view.showError(throwable);
                        hideProgress();
                    });

            view.addSubscription(subscription);
        } else {
            Log.e(TAG, "error : 找不到对应的 contentModel or elementModel");
        }
    }

    @Override
    public void changeText(String contentId, int elementId, String text, final TFOBookElementModel elementModel) {
        String errorMessage = "修改失败";
        TFOBookElementModel target = elementMap.get(contentId).get(elementId);
        showProgress();
        view.addSubscription(
                calendarModel.updateElement(originBook.getBookId(), contentId, target, text)
                        .compose(SchedulersCompat.applyIoSchedulers())
                        .flatMap(new Func1<TFOBaseResponse<EditText>, Observable<TFOBookContentModel>>() {
                            @Override
                            public Observable<TFOBookContentModel> call(TFOBaseResponse<EditText> response) {

                                if (response.success()) {
                                    NoteBookObj.copyElement(
                                            elementModel,
                                            elementMap.get(contentId).get(elementId)
                                    );
                                } else {
                                    Observable.error(new Exception("edit text error."));
                                }
                                return Observable.from(originBook.getContentList())
                                        .filter(contentModel -> contentModel.getContentId().equals(contentId))
                                        .first();
                            }
                        })
                        .observeOn(Schedulers.io())
                        .flatMap(contentModel -> {
                            return model.savePage(contentModel, originBook.getBookId());
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(response -> {

                            try {
                                view.setData(getCoverPage());
                            } catch (BookDataErrorException e) {
                                view.showError(errorMessage);
                                Log.e(TAG, "error", e);
                            }
                            hideProgress();
                        }, throwable -> {

                            Log.e(TAG, "error", throwable);
                            view.showError(errorMessage);
                            hideProgress();
                        })
        );
    }

    @Override
    public void changeElementModels(List<TFOEditTextReplaceObj> models) {
        String contentId = "";

        showProgress();

        Subscription subscription = Observable.from(models)
                .observeOn(Schedulers.io())
                .flatMap(editTextReplaceObj -> Observable.just(
                        originBook.getContentList()
                                .get(editTextReplaceObj.getPageIndex())
                ).first(), new Func2<TFOEditTextReplaceObj, TFOBookContentModel, TFOBookContentModel>() {
                    @Override
                    public TFOBookContentModel call(TFOEditTextReplaceObj editTextReplaceObj, TFOBookContentModel contentModel) {
                        int eleId = editTextReplaceObj.getElementModel().getElementId();
                        TFOBookElementModel target = elementMap.get(contentModel.getContentId())
                                .get(eleId);

                        // replace element
                        NoteBookObj.copyElement(editTextReplaceObj.getElementModel(), target);
                        return contentModel;
                    }
                })
                .flatMap(new Func1<TFOBookContentModel, Observable<TFOBaseResponse<EditPod>>>() {
                    @Override
                    public Observable<TFOBaseResponse<EditPod>> call(TFOBookContentModel contentModel) {
                        EventBus.getDefault().post(
                                new ContentChangeEvent(contentModel.getContentId())
                        );
                        Log.d(TAG, "post events");
                        return model.savePage(contentModel, originBook.getBookId());
                    }
                }).delay(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    try {
                        view.setData(getCoverPage());

                        if (models.size() > 0) {

                            // 修改书名
                            String title = models.get(0).getElementModel().getElementContent();
                            originBook.setBookTitle(title);
                        }
                    } catch (BookDataErrorException e) {
                        Observable.error(e);
                    }
                    hideProgress();
                }, throwable -> {
                    Log.e(TAG, "error", throwable);
                    hideProgress();
                });

        view.addSubscription(subscription);
    }

    @Override
    public void openPageSetting() {

        view.setStateView(true, "正在保存");

        view.addSubscription(
                model.saveBook(originBook)
                        .compose(SchedulersCompat.applyIoSchedulers())
                        .subscribe(response -> {
                            if (response.success()) {
                                // FIXME: 2017/1/9 记事本功能完善
//                                NotebookPhotoPageActivity.open(
//                                        view.getCurrentActivity(),
//                                        originBook.getBookId(),
//                                        remoteBook != null ? String.valueOf(remoteBook.getId()) : ""
//                                );
                            }
                            view.setStateView(false);
                        }, throwable -> {
                            view.showError(throwable, this::openPageSetting);
                            Log.e(TAG, "error", throwable);
                        })
        );
    }

    // FIXME: 2017/1/9 记事本功能完善
//    public void saveWithContent(
//            Action1<BaseResponse> onLoad,
//            Action1<Throwable> onError,
//            TFOBookContentModel content) {
//
//        showProgress();
//        Observable.just(content)
//                .observeOn(Schedulers.io())
//                .map(contentModel -> {
//                    TFOBookContentModel pageContent = originBook.getContentList().get(14);
//                    contentModel.setContentId(pageContent.getContentId());
//                    originBook.getContentList().remove(14);
//                    originBook.getContentList().add(14, contentModel);
//                    return contentModel;
//                })
//                .flatMap(contentModel -> {
//                    return model.savePage(contentModel, originBook.getBookId());
//                })
//                .flatMap(response -> {
//                    if (response.success()) {
//
//                        String hasInsert = model.getNotebookInsertPageStyle(originBook.getBookId()) + "";
//                        int innerPageId = model.getNotebookPaperStyle(originBook.getBookId());
//
//                        if (hasInsert.equals("-1")) {
//                            hasInsert = "2";
//                        }
//                        if (remoteBook == null) {
//                            return model.save(originBook, hasInsert, innerPageId);
//                        } else {
//                            return model.update(originBook.getBookId(), originBook, hasInsert, innerPageId);
//                        }
//                    } else {
//                        return Observable.error(new Exception("保存设置失败"));
//                    }
//                })
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(baseResponse -> {
//
//                    BaseResponse
//                            response = new BaseResponse();
//
//                    if (remoteBook != null) {
//                        response.dataId = remoteBook.getBook_id();
//                    } else {
//                        response.dataId = String.valueOf(baseResponse.getData().getId());
//                    }
//                    EventBus.getDefault().post(new NotebookSavedEvent());
//                    onLoad.call(response);
//                    hideProgress();
//                }, throwable -> {
//                    onError.call(throwable);
//                    hideProgress();
//                });
//    }

    @Override
    public TFOBookModel getBookModel() {
        return originBook;
    }

    @Override
    public void share(String id) {
        String shareUrl = "";

        shareUrl = "http://";
        shareUrl += BuildConfig.DEBUG ?
                "wechat.v5time.net" :
                "wechat.timeface.cn";
        shareUrl += "/calendar/notepreview/?bookId=" + id + "&share=1";

        new ShareDialog(view.getCurrentActivity())
                .share(
                        FastData.getUserName() + "的时光记事本",
                        "我在时光流影，上传自己的照片就能生成专属个性笔记本，你也来试试吧！",
                        originBook.getBookCover(),
                        shareUrl
                );
    }

    public void setNotebookPaperStyle(String bookId, String templateId) {
        model.setNotebookPaperStyle(bookId, templateId);
    }

    public int getNotebookPaperStyle(String bookId) {
        return model.getNotebookPaperStyle(bookId);
    }

    public void loadNotebookPagersList() {

        view.addSubscription(
                model.listContentPaper()
                        .compose(SchedulersCompat.applyIoSchedulers())
                        .subscribe(listBaseResponse -> {

                            view.setPagersData(listBaseResponse.getData());
                            hideProgress();
                        }, throwable -> {

                            hideProgress();
                            Log.e(TAG, "error", throwable);
                        })
        );
    }

    @Override
    public void save(Action1<cn.timeface.circle.baby.support.api.models.base.BaseResponse> onLoad, Action1<Throwable> onError) {
        showProgress();

        String hasInsert = model.getNotebookInsertPageStyle(originBook.getBookId()) + "";
        int innerPageId = model.getNotebookPaperStyle(originBook.getBookId());

        if (hasInsert.equals("-1")) {
            hasInsert = "2";
        }

        Observable<TFOBaseResponse<DataResponse>> observable;
        if (remoteBook == null) {
            observable = model.save(originBook, hasInsert, innerPageId);
        } else {
            observable = model.update(String.valueOf(remoteBook.getId()), originBook, hasInsert, innerPageId);
        }

        view.addSubscription(
                observable.compose(SchedulersCompat.applyIoSchedulers())
                        .subscribe(baseResponse -> {

                            BaseResponse
                                    response = new BaseResponse();

                            if (remoteBook != null) {
                                //baseResponse.getData().setId(remoteBook.getId());
                                response.dataId = remoteBook.getBook_id();
                            } else {
                                response.dataId = String.valueOf(baseResponse.getData().getId());
                            }

                            // FIXME: 2017/1/9 记事本功能完善
//                            onLoad.call(response);

                            EventBus.getDefault().post(new NotebookSavedEvent());
                            hideProgress();
                        }, throwable -> {
                            onError.call(throwable);
                            hideProgress();
                            view.showError("保存失败");
                        })
        );
    }

    @Override
    public void saveWithContent(Action1<cn.timeface.circle.baby.support.api.models.base.BaseResponse> onLoad, Action1<Throwable> onError, TFOBookContentModel content) {

    }

    @Override
    public String getCurrentTitleInPreview(int position) {
        String title = "";
        try {
            title = contentTitles.get(position);
        } catch (Exception e) {
            Log.e(TAG, "title error", e);
        }
        return title;
    }

    private void loadCoverTemplates() {

        view.addSubscription(
                model.getCoverTemplates().compose(SchedulersCompat.applyIoSchedulers())
                        .subscribe(listBaseResponse -> {
                            coverTemplates = listBaseResponse.getData();

                            TFOBookContentModel coverContent = originBook.getContentList().get(0);
                            List<TemplateItem> templateItems = new ArrayList<>();
                            for (int i = 0; i < coverTemplates.size(); i++) {
                                TemplateItem item = new TemplateItem();

                                item.isSelected = coverContent.getTemplateId()
                                        .equals(String.valueOf(coverTemplates.get(i).getTemplateId()));

                                item.template = coverTemplates.get(i);
                                templateItems.add(item);
                            }
                            view.setCoverTemplates(templateItems);
                            hideProgress();
                        }, throwable -> {
                            view.showError("加载封面模板失败");
                            hideProgress();
                        })
        );
    }

    private TFOBookModel getCoverPage() throws BookDataErrorException {
        if (originBook.getContentList().size() > 0) {
            NoteBookObj tfoBookModel = new NoteBookObj(originBook);

            tfoBookModel.getContentList().clear();
            tfoBookModel.getContentList().add(
                    originBook.getContentList().get(0)
            );
            return tfoBookModel;
        } else {
            throw new BookDataErrorException("");
        }
    }

    public TFOBookModel getOriginBook() {
        return originBook;
    }

    private void showProgress() {

        if (progressDialog == null) progressDialog = TFProgressDialog.getInstance("正在加载");
        if (!progressDialog.isAdded())
            progressDialog.show(view.getCurrentActivity().getSupportFragmentManager(), "progress");
        else {
            Fragment fragment = view.getCurrentActivity().getSupportFragmentManager().findFragmentByTag("progress");
            if (fragment != null) {
                FragmentTransaction ft = view.getCurrentActivity().getSupportFragmentManager().beginTransaction();
                ft.show(fragment);
                ft.commitAllowingStateLoss();
            }
        }
    }

    private void hideProgress() {
        if (progressDialog != null && progressDialog.isVisible()) {
            progressDialog.dismiss();
        }
    }

    private TFOBookModel getPreviewData() {

        int hasInsert = model.getNotebookInsertPageStyle(originBook.getBookId());
        if (hasInsert == -1) {
            hasInsert = 2;
        }

        contentTitles.clear();

        switch (hasInsert) {
            case 1: {
                contentTitles.add("封面");
                contentTitles.add("封面");
                for (int i = 1; i < 13; i++) {
                    contentTitles.add(i + "/188");
                }
                contentTitles.add("13~188/188");
                contentTitles.add("封底");
                contentTitles.add("封底");
            }
            return originBook;

            case 2: {
                NoteBookObj tfoBookModel = new NoteBookObj(originBook);

                TFOBookContentModel content = originBook.getContentList().get(14);
                tfoBookModel.getContentList().add(4, content);
                tfoBookModel.getContentList().add(7, content);
                tfoBookModel.getContentList().add(10, content);
                tfoBookModel.getContentList().add(13, content);
                tfoBookModel.getContentList().add(16, content);

                contentTitles.add("封面");
                contentTitles.add("封面");
                contentTitles.add("1/188");
                contentTitles.add("2/188");
                contentTitles.add("3~18/188");
                contentTitles.add("19/188");
                contentTitles.add("20/188");
                contentTitles.add("21~36/188");
                contentTitles.add("37/188");
                contentTitles.add("38/188");
                contentTitles.add("39~54/188");
                contentTitles.add("55/188");
                contentTitles.add("56/188");
                contentTitles.add("57~72/188");
                contentTitles.add("73/188");
                contentTitles.add("74/188");
                contentTitles.add("75~90/188");
                contentTitles.add("91/188");
                contentTitles.add("92/188");
                contentTitles.add("93~188/188");
                contentTitles.add("封底");
                contentTitles.add("封底");
                return tfoBookModel;
            }

            case 0: {
                NoteBookObj tfoBookModel = new NoteBookObj(originBook);

                TFOBookContentModel content = originBook.getContentList().get(14);

                tfoBookModel.getContentList().clear();
                tfoBookModel.getContentList().add(originBook.getContentList().get(0));
                tfoBookModel.getContentList().add(originBook.getContentList().get(1));
                tfoBookModel.getContentList().add(content);

                tfoBookModel.getContentList().add(originBook.getContentList().get(
                        originBook.getContentList().size() - 2)
                );
                tfoBookModel.getContentList().add(originBook.getContentList().get(
                        originBook.getContentList().size() - 1)
                );

                contentTitles.add("封面");
                contentTitles.add("封面");
                contentTitles.add("1~187");
                contentTitles.add("封底");
                contentTitles.add("封底");
                return tfoBookModel;
            }

            default:
                break;
        }
        return originBook;
    }
}
