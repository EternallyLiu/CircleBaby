package cn.timeface.circle.baby.support.mvp.presenter;

import android.content.Intent;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import cn.timeface.circle.baby.support.api.models.TFUploadFile;
import cn.timeface.circle.baby.support.api.models.objs.ImgObj;
import cn.timeface.circle.baby.support.mvp.bases.BasePresenter;
import cn.timeface.circle.baby.support.mvp.model.NotebookModel;
import cn.timeface.circle.baby.support.mvp.presentations.NotebookPresentation;
import cn.timeface.circle.baby.support.oss.OSSManager;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.notebook.beans.NoteBookObj;
import cn.timeface.circle.baby.ui.notebook.beans.SelectableWrapper;
import cn.timeface.circle.baby.ui.notebook.cache.BookCache;
import cn.timeface.circle.baby.ui.notebook.dialogs.UploadImageProgressDialog;
import cn.timeface.circle.baby.ui.notebook.events.DelayPostEvent;
import cn.timeface.circle.baby.ui.notebook.events.PhotoUploadProgressEvent;
import cn.timeface.circle.baby.ui.notebook.exceptions.BookDataErrorException;
import cn.timeface.circle.baby.views.dialog.TFProgressDialog;
import cn.timeface.open.api.bean.base.TFOBaseResponse;
import cn.timeface.open.api.bean.obj.TFOBookContentModel;
import cn.timeface.open.api.bean.obj.TFOBookElementModel;
import cn.timeface.open.api.bean.obj.TFOBookImageModel;
import cn.timeface.open.api.bean.obj.TFOBookModel;
import cn.timeface.open.api.bean.response.EditPod;
import cn.timeface.open.api.bean.response.InsertPageInfo;
import cn.timeface.open.event.ContentChangeEvent;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by JieGuo on 16/11/22.
 */

public class NotebookInsertPagePresenter extends BasePresenter<NotebookPresentation.InsertPagePresentation.View, NotebookModel> implements
        NotebookPresentation.InsertPagePresentation.Presenter {

    private TFOBookModel originBookModel;
    private TFProgressDialog progressDialog;
    private HashMap<String, HashMap<Long, TFOBookElementModel>> elementMap = new LinkedHashMap<>();
    private HashMap<String, TFOBookContentModel> contentMap = new LinkedHashMap<>();

    private Queue<DelayPostEvent> eventBusMessageQueue = new LinkedBlockingQueue<>();
    private Subscription eventLooperSubscription = null;
    private List<SelectableWrapper<InsertPageInfo>> insertPageList = new ArrayList<>();
    private String remoteId = "";

//    private CalendarModel calendarModel;

    public NotebookInsertPagePresenter(NotebookPresentation.InsertPagePresentation.View view) {
        setup(view, new NotebookModel());

//        calendarModel = new CalendarModel();
    }

    @Override
    public void load(String bookId, String remoteId) {

        this.remoteId = remoteId;

        view.getCurrentActivity().runOnUiThread(() ->
                view.setStateView(true, "正在玩命加载中...")
        );
        originBookModel = BookCache.getInstance().getModelById(bookId);
        if (originBookModel == null) {
            view.addSubscription(
                    model.getBookModel(bookId)
                            .compose(SchedulersCompat.applyIoSchedulers())
                            .doOnCompleted(() -> {
                                loadTemplates(bookId);
                            })
                            .subscribe(response -> {

                                originBookModel = response.getData();
                                try {
                                    view.setData(getContentModel());
                                    view.setStateView(false);

                                    if (model.getNotebookInsertPageStyle(originBookModel.getBookId()) == 0) {
                                        view.showMaskView();
                                    } else {
                                        view.showUploadDialog();
                                    }
                                } catch (BookDataErrorException e) {
                                    Log.e(TAG, "error", e);
                                    view.showError(e, () -> load(bookId, remoteId));
                                }
                            }, throwable -> {

                                Log.e(TAG, "error", throwable);
                                view.showError(throwable, () -> load(bookId, remoteId));
                            })
            );
        } else {
            loadTemplates(bookId);

            Observable.just(originBookModel)
                    .delay(1, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(avoid -> {

                        view.setStateView(false);
                        try {
                            view.setData(getContentModel());
                            if (model.getNotebookInsertPageStyle(originBookModel.getBookId()) == 0) {
                                view.showMaskView();
                            } else {
                                view.showUploadDialog();
                            }
                        } catch (BookDataErrorException e) {
                            Log.e(TAG, "error", e);
                            view.showError(e, () -> load(bookId, remoteId));
                        }
                    }, throwable -> {

                        view.showError(throwable, () -> load(bookId, remoteId));
                        Log.e(TAG, "error", throwable);
                    });
        }
    }

    private void loadTemplates(String bookId) {
        view.getCurrentActivity().runOnUiThread(this::showProgress);

        if (originBookModel == null) {
            Log.e(TAG, "error : 数据不完整");
            return;
        }

        contentMap.clear();
        elementMap.clear();

        for (TFOBookContentModel content :
                originBookModel.getContentList()) {
            elementMap.put(content.getContentId(), new LinkedHashMap<>());
            for (TFOBookElementModel ele :
                    content.getElementList()) {
                elementMap.get(content.getContentId()).put(ele.getElementId(), ele);
            }

            contentMap.put(content.getContentId(), content);
        }

        view.addSubscription(
                model.listInsertPage().compose(SchedulersCompat.applyIoSchedulers())
                        .subscribe(response -> {

                            insertPageList.clear();

                            for (InsertPageInfo item : response.getData()) {
                                SelectableWrapper<InsertPageInfo> info = new SelectableWrapper<>();
                                info.setItem(item);
                                insertPageList.add(info);
                            }
                            view.setTemplates(insertPageList);
                            hideProgress();
                        }, throwable -> {
                            Log.e(TAG, "error", throwable);
                            view.showError(throwable, () -> load(bookId, remoteId));
                        })
        );
    }

    @Override
    public void showUploadDialog() {

    }

    @Override
    public void changeImage(String contentId, TFOBookElementModel element) {

        if (elementMap.containsKey(contentId)
                && elementMap.get(contentId).containsKey(element.getElementId())) {
            TFOBookElementModel ele = elementMap.get(contentId).get(element.getElementId());
            NoteBookObj.copyElement(element, ele);

            showProgress();
            Subscription subscription = Observable.from(originBookModel.getContentList())
                    .observeOn(Schedulers.io())
                    .filter(contentModel -> contentModel.getContentId().equals(contentId))
                    .first()
                    .flatMap(content -> {
                        return model.savePage(content, originBookModel.getBookId());
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(response -> {
                        if (response.success()) {
                            eventBusMessageQueue.add(new DelayPostEvent<>(
                                    new ContentChangeEvent(contentId)
                            ));
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
    public void changeText(String contentId, int elementId, String text, TFOBookElementModel elementModel) {
        String errorMessage = "修改失败";
        TFOBookElementModel target = elementMap.get(contentId).get(elementId);
        showProgress();
        view.addSubscription(
                model.updateElement(originBookModel.getBookId(), contentId, target, text)
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io())
                        .flatMap(response -> {
                            if (response.success()) {
                                NoteBookObj.copyElement(
                                        elementModel,
                                        elementMap.get(contentId).get(elementId)
                                );
                            } else {
                                Observable.error(new Exception("change text error"));
                            }
                            return Observable.from(originBookModel.getContentList())
                                    .filter(
                                            contentModel ->
                                                    contentModel.getContentId().equals(contentId)
                                    )
                                    .first();
                        })
                        .flatMap(new Func1<TFOBookContentModel, Observable<TFOBaseResponse<EditPod>>>() {
                            @Override
                            public Observable<TFOBaseResponse<EditPod>> call(TFOBookContentModel contentModel) {
                                return model.savePage(contentModel, originBookModel.getBookId());
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(response -> {

                            if (response.success()) {
                                eventBusMessageQueue.add(new DelayPostEvent<>(
                                        new ContentChangeEvent(contentId)
                                ));
                            } else {
                                view.showError(errorMessage);
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
    public void changeTemplate(InsertPageInfo insertPageInfo, TFOBookContentModel contentModel) {

        showProgress();
        Observable.just(contentModel)
                .observeOn(Schedulers.computation())
                .map(content -> {
                    // 替换模板

                    TFOBookElementModel sourceElement = null;
                    for (TFOBookElementModel ele : content.getElementList()) {
                        if (ele.getElementType() == 1) {
                            sourceElement = ele;
                            break;
                        }
                    }
                    NoteBookObj.copyContent2Content(insertPageInfo.getPAGE(), content);

                    // 把图粘回去
                    if (sourceElement != null) {

                        TFOBookImageModel imageModel = sourceElement.getImageContentExpand();
                        for (TFOBookElementModel ele : content.getElementList()) {
                            if (ele.getElementType() == 1) {

                                NoteBookObj.replaceImageElment(imageModel, ele);
                                break;
                            }
                        }
                    }
                    return content;
                })
                .map(content -> {
                    // 索引值更新
                    elementMap.get(content.getContentId()).clear();
                    for (TFOBookElementModel element :
                            content.getElementList()) {
                        elementMap.get(content.getContentId()).put(element.getElementId(), element);
                    }

                    return content;
                })
                .flatMap(content -> {

                    return model.savePage(content, originBookModel.getBookId());
                })
                .subscribe(response -> {
                    if (response.success()) {
                        EventBus.getDefault().post(new ContentChangeEvent(contentModel.getContentId()));
                        hideProgress();
                    } else {
                        view.showError("保存数据失败");
                    }
                    // view.notifyDataSetChange();
                }, throwable -> {
                    hideProgress();
                    Log.e(TAG, "error", throwable);
                });
    }

    @Override
    public void setInsertPageStyle(NotebookPresentation.NotebookStyle style) {

    }

    @Override
    public void setCurrentSelectedTemplateByContentId(TFOBookContentModel content) {

        for (int i = 0; i < insertPageList.size(); i++) {
            String templateId = insertPageList.get(i).getItem().getPAGE().getTemplateId();
            Log.d(TAG, "templateId : " + templateId + ", content template : " + content.getTemplateId());
            if (content.getTemplateId().equals(templateId)) {

                view.setCurrentSelectedTemplate(i);
                break;
            }

        }

    }

    @Override
    public void postEvents() {

        if (eventLooperSubscription == null) {

            eventLooperSubscription = Observable
                    .interval(200, TimeUnit.MILLISECONDS, Schedulers.computation())
                    .subscribe(aLong -> {

                        DelayPostEvent event = eventBusMessageQueue.poll();
                        if (event != null) {
                            Log.i(TAG, "post a event :" + event.hashCode());
                            EventBus.getDefault().post(event.getData());
                        }
                    }, throwable -> {
                        Log.e(TAG, "error", throwable);
                    });

            view.addSubscription(eventLooperSubscription);
        }
    }

    @Override
    public void stopPostEvents() {
        if (eventLooperSubscription != null && !eventLooperSubscription.isUnsubscribed()) {
            eventLooperSubscription.unsubscribe();
        }
        eventLooperSubscription = null;
    }

    @Override
    public void upload(Intent data) {

        ArrayList<ImgObj> images = data.getParcelableArrayListExtra("select_photos");

        if (images == null && images.size() == 0) {
            return;
        }

        UploadImageProgressDialog uploadProgress = UploadImageProgressDialog.newInstance();
        uploadProgress.show(view.getCurrentActivity().getSupportFragmentManager(), "uploadProgress");

        Subscription subscription = Observable.just(images)
                .observeOn(Schedulers.io())
                .map(new Func1<ArrayList<ImgObj>, ArrayList<ImgObj>>() {
                    @Override
                    public ArrayList<ImgObj> call(ArrayList<ImgObj> photoModels) {

                        OSSManager ossManager = OSSManager.getOSSManager(view.getCurrentActivity());

                        try {
                            for (int i = 0; i < photoModels.size(); i++) {
                                ImgObj item = photoModels.get(i);
                                TFUploadFile uploadFile = new TFUploadFile(item.getLocalPath(), "times");
                                if (!ossManager.checkFileExist(uploadFile.getObjectKey())) {

                                    ossManager.upload(uploadFile.getObjectKey(), uploadFile.getFilePath());
                                }
                                String url = String.format(
                                        Locale.CHINESE, "http://img1.timeface.cn/%s",
                                        uploadFile.getObjectKey()
                                );

                                // FIXME: 2017/1/9 记事本功能完善
//                                photoModels.get(i).setImage_url(url);
                                float progress = ((float) i + 1) / photoModels.size();
                                EventBus.getDefault().post(new PhotoUploadProgressEvent(progress));
                            }
                        } catch (Exception e) {
                            Observable.error(e);
                        }

                        return photoModels;
                    }
                }).map(
                        photoModels -> {

                            try {
                                TFOBookModel book = getContentModel();
                                List<Long> ids = getAllPicElement();

                                if (ids == null) {
                                    Observable.error(new Exception("ids is null."));
                                } else {

                                    for (int i = 0; i < book.getContentList().size(); i++) {

                                        if (i > ids.size() - 1) {
                                            break;
                                        }

                                        if (i > photoModels.size() - 1) {
                                            break;
                                        }

                                        ImgObj item = photoModels.get(i);

                                        // 快速查找 element并完成替换
                                        String contentId = book.getContentList().get(i).getContentId();
                                        if (ids.get(i) == 0) {
                                            // 忽略找不到的。
                                            continue;
                                        }
                                        TFOBookElementModel element = elementMap.get(contentId).get(ids.get(i));

                                        NoteBookObj.replaceImageElement(item, element);
                                    }
                                }
                            } catch (Throwable e) {
                                Observable.error(e);
                            }
                            return null;
                        })
                .flatMap(o -> {
                    return model.saveBook(originBookModel);
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {

                    if (response.success()) {
                        for (String contentId : elementMap.keySet()) {
                            ContentChangeEvent event = new ContentChangeEvent(contentId);
                            eventBusMessageQueue.add(new DelayPostEvent<>(event));
                        }
                    } else {
                        view.showError("保存到远程服务器失败");
                    }

                    uploadProgress.dismiss();
                }, throwable -> {
                    Log.e(TAG, "error", throwable);
                });

        view.addSubscription(subscription);
    }

    private TFOBookModel getContentModel() throws BookDataErrorException {
        try {
            TFOBookModel tfoBookModel = new NoteBookObj(originBookModel);
            tfoBookModel.getContentList().remove(0);
            tfoBookModel.getContentList().remove(0);
            int size = tfoBookModel.getContentList().size();
            tfoBookModel.getContentList().remove(size - 1);
            tfoBookModel.getContentList().remove(size - 2);
            tfoBookModel.getContentList().remove(size - 3);
            return tfoBookModel;
        } catch (Exception e) {
            throw new BookDataErrorException("data error", e);
        }
    }

    private void showProgress() {
        if (progressDialog == null) {
            progressDialog = TFProgressDialog.getInstance("正在加载中");
        }
        progressDialog.show(view.getCurrentActivity().getSupportFragmentManager(), "progress");
    }

    private void hideProgress() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    private List<Long> getAllPicElement() throws BookDataErrorException {

        List<Long> ids = new ArrayList<>();
        TFOBookModel book = getContentModel();
        for (int i = 0; i < book.getContentList().size(); i++) {
            String contentId = book.getContentList().get(i).getContentId();
            Collection<TFOBookElementModel> elements = elementMap.get(contentId).values();
            long eleId = 0;
            for (TFOBookElementModel ele :
                    elements) {
                if (ele.getElementType() == 1) {
                    eleId = ele.getElementId();
                }
            }

            ids.add(eleId);
        }
        return ids;
    }
}
