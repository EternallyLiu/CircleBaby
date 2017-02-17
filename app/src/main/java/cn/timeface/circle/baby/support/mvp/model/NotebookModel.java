package cn.timeface.circle.baby.support.mvp.model;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cn.timeface.circle.baby.support.api.models.DataResponse;
import cn.timeface.circle.baby.support.api.models.base.BaseResponse;
import cn.timeface.circle.baby.support.mvp.bases.BasePresenterModel;
import cn.timeface.circle.baby.support.mvp.presentations.NotebookPresentation;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.open.api.bean.base.TFOBaseResponse;
import cn.timeface.open.api.bean.obj.TFOBookContentModel;
import cn.timeface.open.api.bean.obj.TFOBookElementModel;
import cn.timeface.open.api.bean.obj.TFOBookModel;
import cn.timeface.open.api.bean.obj.TFOSimpleTemplate;
import cn.timeface.open.api.bean.response.CoverTemplateInfo;
import cn.timeface.open.api.bean.response.EditBookCover;
import cn.timeface.open.api.bean.response.EditPod;
import cn.timeface.open.api.bean.response.EditText;
import cn.timeface.open.api.bean.response.InsertPageInfo;
import cn.timeface.open.model.TFOpenDataProvider;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by JieGuo on 16/11/21.
 */

public class NotebookModel extends BasePresenterModel implements NotebookPresentation.Model {

    public static final String KEY_INNER_PAGE_STYLE = "notebook:inner:page:style";
    public static final String KEY_PAGE_SETTING = "notebook:page:setting";

    /**
     * 台历在开放平台里对应的 type
     */
    public static final int TYPE = 105;

    private final TFOpenDataProvider openApi;
    private Gson gson = new Gson();

    public NotebookModel() {
        openApi = TFOpenDataProvider.get();
    }

    @Override
    public Observable<TFOBaseResponse<TFOBookModel>> create() {

        return openApi.createBook(TYPE, getTitle(), getAuthor(), null);
    }

    @Override
    public Observable<TFOBaseResponse<TFOBookModel>> get(String id) {

        return openApi.getBook(id, TYPE);
    }

    @Override
    public Observable<TFOBaseResponse<EditPod>> update(TFOBookModel content) {

        return openApi.editPod(content.getBookId(), content.getContentList());
    }

    public Observable<GeneralBookItemResponse> getRemoteBook(String remoteId, String type) {
        return Observable.create(new Observable.OnSubscribe<GeneralBookItemResponse>() {
            @Override
            public void call(Subscriber<? super GeneralBookItemResponse> subscriber) {

            }
        });
        // FIXME: 2017/1/9 记事本功能完善
//        return apiServiceV2.sdkBookGet(remoteId, type);
    }

    public Observable<TFOBaseResponse<DataResponse>> save(
            TFOBookModel book, String hasInsert, int innerPageId) {

        return Observable.create(new Observable.OnSubscribe<TFOBaseResponse<DataResponse>>() {
            @Override
            public void call(Subscriber<? super TFOBaseResponse<DataResponse>> subscriber) {

            }
        });
        // FIXME: 2017/1/9 记事本功能完善
//        String extra = "{\"has_insert\":\"%s\", \"inner_paper_id\":\"%s\"}";
//        return apiServiceV2.sdkBookSave(new SaveBookRequest(
//                book.getBookId(),
//                TYPE,
//                book.getBookCover(),
//                FastData.getUserName(),
//                book.getBookTitle(),
//                book.getBookSummary(),
//                String.valueOf(book.getTotalPage()),
//                String.valueOf(book.getCreateDate()),
//                String.format(Locale.CHINESE, extra, hasInsert, innerPageId)
//        ));
    }

    public Observable<TFOBaseResponse<DataResponse>> update(
            String remoteId, TFOBookModel book, String hasInsert, int innerPageId) {

        return Observable.create(new Observable.OnSubscribe<TFOBaseResponse<DataResponse>>() {
            @Override
            public void call(Subscriber<? super TFOBaseResponse<DataResponse>> subscriber) {

            }
        });
        // FIXME: 2017/1/9 记事本功能完善
//        String extra = "{\"has_insert\":\"%s\", \"inner_paper_id\":\"%s\"}";
//        return apiServiceV2.sdkBookUpdate(
//                remoteId,
//                new SaveBookRequest(
//                        book.getBookId(),
//                        TYPE,
//                        book.getBookCover(),
//                        FastData.getUserName(),
//                        book.getBookTitle(),
//                        book.getBookSummary(),
//                        String.valueOf(book.getContentList().size()),
//                        String.valueOf(book.getCreateDate()),
//                        String.format(Locale.CHINESE, extra, hasInsert, innerPageId)
//                )
//        );
    }

    @Override
    public Observable<BaseResponse> deleteRemoteNotebook(String id) {
        String finalId = String.format(Locale.CHINESE, "%s$%s", id, TYPE);
        return Observable.create(new Observable.OnSubscribe<BaseResponse>() {
            @Override
            public void call(Subscriber<? super BaseResponse> subscriber) {

            }
        });
        // FIXME: 2017/1/9 记事本功能完善
//        return apiServiceV2.sdkBookDelete(finalId);
    }

    @Override
    public Observable<TFOBaseResponse<List<TFOSimpleTemplate>>> getCoverTemplates() {
        return openApi.getTemplateList(TYPE, true);
    }

    @Override
    public Observable<TFOBaseResponse<CoverTemplateInfo>> changeCoverTemplate(int templateId, TFOBookModel bookModel) {
        List<TFOBookContentModel> contents = new ArrayList<>();
        contents.add(bookModel.getContentList().get(0));
        contents.add(bookModel.getContentList().get(bookModel.getContentList().size() - 1));

        return openApi.templateInfo(bookModel.getBookId(),templateId, contents, "");
    }

    @Override
    public Observable<TFOBaseResponse<EditBookCover>> saveBookCover(TFOBookModel bookModel, int templateId) {
        List<TFOBookContentModel> contents = new ArrayList<>(1);
        contents.add(bookModel.getContentList().get(0));
        return openApi.editBookCover(bookModel.getBookId(), getTitle(), getAuthor(), templateId, contents);
    }

    @Override
    public Observable<TFOBaseResponse<TFOBookModel>> getBookModel(String bookId) {
        return openApi.getBook(bookId, NotebookModel.TYPE);
    }

    @Override
    public Observable<TFOBaseResponse<List<InsertPageInfo>>> listInsertPage() {
        return openApi.listInsertPage(NotebookModel.TYPE);
    }

    @Override
    public Observable<TFOBaseResponse<List<TFOBookContentModel>>> listContentPaper() {
        return openApi.listContentPaper(NotebookModel.TYPE);
    }

    public Observable<TFOBaseResponse<EditPod>> savePage(TFOBookContentModel content, String bookId) {
        List<TFOBookContentModel> contentList = new ArrayList<>();
        contentList.add(content);
        return openApi.editPod(bookId, contentList);
    }

    public Observable<TFOBaseResponse<EditPod>> saveBook(TFOBookModel book) {
        return openApi.editPod(book.getBookId(), book.getContentList());
    }

    public void setNotebookInsertPageStyle(String bookId, int style) {
        FastData.putInt(KEY_INNER_PAGE_STYLE + "$" + bookId, style);
    }

    public int getNotebookInsertPageStyle(String bookId) {
        return FastData.getInt(KEY_INNER_PAGE_STYLE + "$" + bookId, -1);
    }

    public void setNotebookPaperStyle(String bookId, String templateId) {
        FastData.putInt(KEY_PAGE_SETTING + "$" + bookId, Integer.valueOf(templateId));
    }

    public int getNotebookPaperStyle(String bookId) {
        int value = FastData.getInt(KEY_PAGE_SETTING + "$" + bookId, 0);
        if (value == 0) {
            return 17150;
        }
        return value;
    }

    private String getTitle() {
        return getAuthor() + "的时光记事本";
    }

    private String getAuthor() {
        return FastData.getUserName();
    }

    public Observable<TFOBaseResponse<EditText>> updateElement(
            String bookId,
            String contentId,
            TFOBookElementModel element,
            String text) {
        element.setReContentId(contentId);
        String elementString = new Gson().toJson(element);
        return openApi.editText(bookId, element, text);
    }
}
