package cn.timeface.circle.baby.support.mvp.model;

import android.util.Log;

import com.google.gson.Gson;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import cn.timeface.circle.baby.support.mvp.bases.BasePresenterModel;
import cn.timeface.circle.baby.support.mvp.presentations.CalendarPresentation;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.open.api.bean.base.TFOBaseResponse;
import cn.timeface.open.api.bean.obj.TFOBookContentModel;
import cn.timeface.open.api.bean.obj.TFOBookElementModel;
import cn.timeface.open.api.bean.obj.TFOBookModel;
import cn.timeface.open.api.bean.response.EditPod;
import cn.timeface.open.api.bean.response.EditText;
import cn.timeface.open.api.bean.response.SimplePageTemplate;
import cn.timeface.open.model.TFOpenDataProvider;
import rx.Observable;

/**
 * Created by JieGuo on 16/9/29.
 */

public class CalendarModel extends BasePresenterModel implements CalendarPresentation.Model {

    /**
     * 竖版的台历
     * 在开放平台里对应的台历 type就是这个
     */
    public static final int BOOK_TYPE_CALENDAR_VERTICAL = 70;

    /**
     * 横版的台历
     */
    public static final int BOOK_TYPE_CALENDAR_HORIZONTAL = 69;

    /**
     * 明星粉丝互动台历
     */
    public static final int BOOK_TYPE_CALENDAR_ACTIVITY_STARS = 103;

    TFOpenDataProvider openApi;

    public CalendarModel() {
        openApi = TFOpenDataProvider.get();
    }

    @Override
    public Observable<TFOBaseResponse<TFOBookModel>> create(int type) {
        return openApi.createBook(type, FastData.getUserName() + "的2017时光台历", FastData.getUserName(), null);
    }

    public Observable<TFOBaseResponse<TFOBookModel>> create(int type, String contentList) {
        return openApi.createBook(type, FastData.getUserName() + "的2017时光台历", FastData.getUserName(), null);
    }

    public Observable<TFOBaseResponse<EditPod>> update(TFOBookModel model) {

        return openApi.editPod(model.getBookId(), model.getContentList());
    }

    public Observable<TFOBaseResponse<List<SimplePageTemplate>>> templateList(
            String bookId, long bookType, String contentId) {

        // 请注意,这面要的是数组,但是传多个的时候,他不起作用,只能传一个。
        // 为了发泄我的愤怒,  我要打高指导一下。
        return openApi.pageTemplate(bookId, bookType, Collections.singletonList(contentId));
    }

    public Observable<TFOBaseResponse<List<TFOBookContentModel>>> changePageTemplate(
            String bookId,
            int templateId,
            String contentList) {
        String content = String.format(Locale.CHINESE, "[%s]", contentList);
        //return openApi.reformat(bookId, templateId, Collections.singletonList(contentList));
        return null;
    }

    @Override
    public Observable<?> list() {
        return null;
    }

    @Override
    public Observable<TFOBaseResponse<TFOBookModel>> get(String id) {
        return openApi.getBook(id, BOOK_TYPE_CALENDAR_VERTICAL);
    }

    public Observable<TFOBaseResponse<TFOBookModel>> get(String id, String type) {


        int bType = 0;
        try {
            bType = Integer.valueOf(type);
        } catch (NumberFormatException e) {
            Log.e("Error", "book type error", e);
            return Observable.empty();
        }

        return openApi.getBook(id, bType);
    }

    @Override
    public Observable<?> delete(String id) {
        return null;
    }

    public Observable<TFOBaseResponse<EditText>> updateElement(
            String bookId,
            TFOBookContentModel content,
            TFOBookElementModel element,
            String text) {

        String elementString = new Gson().toJson(element);
        return openApi.editText(bookId, element, text);
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

    @Override
    public Observable<TFOBaseResponse<EditPod>> updateContents(
            String bookId,
            List<TFOBookContentModel> contentArray) {
        String contentString = new Gson().toJson(contentArray);
        return openApi.editPod(bookId, contentArray);

    }

    public Observable<TFOBaseResponse<String>> createCover(int width, int height, TFOBookContentModel content) {
        String jStr = new Gson().toJson(content);
        return openApi.createBookCover(width, height, content);
    }
}
