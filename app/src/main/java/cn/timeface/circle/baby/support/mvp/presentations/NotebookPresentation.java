package cn.timeface.circle.baby.support.mvp.presentations;

import android.content.Intent;

import java.util.List;

import cn.timeface.circle.baby.support.api.models.DataResponse;
import cn.timeface.circle.baby.support.api.models.base.BaseResponse;
import cn.timeface.circle.baby.ui.notebook.beans.SelectableWrapper;
import cn.timeface.circle.baby.ui.notebook.beans.TemplateItem;
import cn.timeface.circle.baby.views.StateView;
import cn.timeface.open.api.bean.base.TFOBaseResponse;
import cn.timeface.open.api.bean.obj.TFOBookContentModel;
import cn.timeface.open.api.bean.obj.TFOBookElementModel;
import cn.timeface.open.api.bean.obj.TFOBookModel;
import cn.timeface.open.api.bean.obj.TFOEditTextReplaceObj;
import cn.timeface.open.api.bean.obj.TFOSimpleTemplate;
import cn.timeface.open.api.bean.response.CoverTemplateInfo;
import cn.timeface.open.api.bean.response.EditBookCover;
import cn.timeface.open.api.bean.response.EditPod;
import cn.timeface.open.api.bean.response.InsertPageInfo;
import rx.Observable;
import rx.functions.Action1;

/**
 * note book presentation
 * <p>
 * Created by JieGuo on 16/11/21.
 */

public interface NotebookPresentation {

    interface Model {


        /**
         * create a notebook
         *
         * @return Observable
         */
        Observable<TFOBaseResponse<TFOBookModel>> create();

        /**
         * load a notebook data
         *
         * @param id id
         * @return Observable
         */
        Observable<TFOBaseResponse<TFOBookModel>> get(String id);

        /**
         * update a notebook content model
         *
         * @param content content model
         * @return Observable
         */
        Observable<TFOBaseResponse<EditPod>> update(TFOBookModel content);


        /**
         * save
         *
         * @param book book model
         * @return Observable
         */
        Observable<TFOBaseResponse<DataResponse>> save(
                TFOBookModel book,
                String hasInsert,
                int innerPageId
        );

        /**
         * deleteRemoteNotebook
         *
         * @param id book id
         * @return Observable
         */
        Observable<?> deleteRemoteNotebook(String id);

        /**
         * get cover template list
         *
         * @return Observable
         */
        Observable<TFOBaseResponse<List<TFOSimpleTemplate>>> getCoverTemplates();

        Observable<TFOBaseResponse<CoverTemplateInfo>> changeCoverTemplate(
                int templateId,
                TFOBookModel bookModel
        );

        Observable<TFOBaseResponse<EditBookCover>> saveBookCover(TFOBookModel bookModel, int templateId);

        Observable<TFOBaseResponse<TFOBookModel>> getBookModel(String bookId);

        Observable<TFOBaseResponse<List<InsertPageInfo>>> listInsertPage();

        Observable<TFOBaseResponse<List<TFOBookContentModel>>> listContentPaper();

        Observable<TFOBaseResponse<EditPod>> savePage(TFOBookContentModel content, String bookId);
    }

    interface Presenter {

        void create();

        void load(String bookId);

        void edit(String remoteId);

        void loadByRemoteId(String remoteId);

        void upload();

        void changeCoverTemplate(int templateId);

        void changeContentStyle(TFOBookContentModel contentModel);

        void changeImage(String contentId, TFOBookElementModel element);

        void changeText(String contentId, int elementId, String text, TFOBookElementModel elementModel);

        void changeElementModels(List<TFOEditTextReplaceObj> models);

        void openPageSetting();

        void loadNotebookPagersList();

        void save(Action1<BaseResponse> onLoad, Action1<Throwable> onError);

        void saveWithContent(
                Action1<BaseResponse> onLoad,
                Action1<Throwable> onError,
                TFOBookContentModel content);

        TFOBookModel getBookModel();

        void share(String id);

        void setNotebookPaperStyle(String bookId, String templateId);

        int getNotebookPaperStyle(String bookId);

        String getCurrentTitleInPreview(int position);
    }

    interface View extends BasePresenterView {

        void setData(TFOBookModel bookModel);

        void setPreviewData(TFOBookModel bookModel);

        void setCoverTemplates(List<TemplateItem> templates);

        void setStateView(boolean loading);

        void setStateView(boolean loading, String message);

        void showError(String message);

        void showError(Throwable throwable);

        void showError(Throwable throwable, StateView.RetryListener retryListener);

        void setPagersData(List<TFOBookContentModel> data);
    }

    interface InsertPagePresentation {

        interface Model {

            Observable<TFOBaseResponse<TFOBookModel>> getBookModel(String bookId);


        }

        interface View extends BasePresenterView {

            void setData(TFOBookModel book);

            void setTemplates(List<SelectableWrapper<InsertPageInfo>> data);

            void setIndexText(String value);

            void showUploadDialog();

            void showMaskView();

            void setCurrentSelectedTemplate(int index);

            void setStateView(boolean loading);

            void setStateView(boolean loading, String message);

            void showError(String message);

            void showError(Throwable throwable);

            void showError(Throwable throwable, StateView.RetryListener retryListener);
        }

        interface Presenter {

            void showUploadDialog();

            void changeImage(String contentId, TFOBookElementModel element);

            void changeText(String contentId, int elementId, String text, TFOBookElementModel elementModel);

            void changeTemplate(InsertPageInfo insertPageInfo, TFOBookContentModel content);

            void setCurrentSelectedTemplateByContentId(TFOBookContentModel content);

            void setInsertPageStyle(NotebookStyle style);

            void load(String bookId, String remoteId);

            void postEvents();

            void stopPostEvents();

            void upload(Intent data);
        }
    }

    /**
     * 插页显示样式
     */
    enum NotebookStyle {

        /**
         * 插页显示在前面
         */
        FRONT,
        /**
         * 穿插显示
         */
        INSIDE,
        /**
         * 不需要插页
         */
        NONE
    }
}
