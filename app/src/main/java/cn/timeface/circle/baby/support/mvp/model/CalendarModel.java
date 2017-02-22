package cn.timeface.circle.baby.support.mvp.model;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cn.timeface.circle.baby.support.mvp.bases.BasePresenterModel;
import cn.timeface.circle.baby.support.mvp.presentations.CalendarPresentation;
import cn.timeface.circle.baby.support.mvp.response.bases.BaseResponse;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.ui.calendar.bean.CalendarExtendObj;
import cn.timeface.circle.baby.ui.calendar.bean.CommemorationDataManger;
import cn.timeface.open.api.bean.base.TFOBaseResponse;
import cn.timeface.open.api.bean.obj.TFOBookContentModel;
import cn.timeface.open.api.bean.obj.TFOBookElementModel;
import cn.timeface.open.api.bean.obj.TFOBookModel;
import cn.timeface.open.api.bean.obj.TFOPublishObj;
import cn.timeface.open.api.bean.response.EditPod;
import cn.timeface.open.api.bean.response.EditText;
import cn.timeface.open.api.bean.response.ReFormat;
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
        try {
            return openApi.createBook(type, FastData.getUserName() + "的2017时光台历", FastData.getUserName(), null);
        } catch (Throwable e) {
            return Observable.error(new Throwable(e));
        }
    }

    public Observable<TFOBaseResponse<TFOBookModel>> create(int type, List<TFOPublishObj> contentList) {
        try {
            return openApi.createBook(type, FastData.getUserName() + "的2017时光台历", FastData.getUserName(), contentList);
        } catch (Throwable e) {
            return Observable.error(new Throwable(e));
        }
    }

    public Observable<TFOBaseResponse<EditPod>> update(TFOBookModel model) {

        return openApi.editPod(model.getBookId(), model.getContentList());
    }

    public Observable<TFOBaseResponse<List<SimplePageTemplate>>> templateList(
            String bookId, long bookType, String contentId) {

        // 请注意,这面要的是数组,但是传多个的时候,他不起作用,只能传一个。
        // 为了发泄我的愤怒,  我要打高指导一下。
        List<String> idList = new ArrayList<>();
        idList.add(contentId);
        return openApi.pageTemplate(bookId, bookType, idList);
    }

    public Observable<TFOBaseResponse<ReFormat>> changePageTemplate(
            String bookId,
            int templateId,
            List<TFOBookContentModel> contentList) {
        return openApi.reformat(bookId, templateId, contentList);
    }

    @Override
    public Observable<?> list() {
        return null;
    }

    @Override
    public Observable<TFOBaseResponse<TFOBookModel>> get(String id) {
        return openApi.getBook(id, BOOK_TYPE_CALENDAR_VERTICAL, true);
    }

    public Observable<TFOBaseResponse<TFOBookModel>> get(String id, String type) {


        int bType = 0;
        try {
            bType = Integer.valueOf(type);
        } catch (NumberFormatException e) {
            Log.e("Error", "book type error", e);
            return Observable.empty();
        }

        return openApi.getBook(id, bType, true);
    }

    @Override
    public Observable<BaseResponse> delete(String id) {
        return null;
    }

    public Observable<TFOBaseResponse<EditText>> updateElement(
            String bookId,
            TFOBookContentModel content,
            TFOBookElementModel element,
            String text) {

        return openApi.editText(bookId, element, text);
    }

    public Observable<TFOBaseResponse<EditText>> updateElement(
            String bookId,
            String contentId,
            TFOBookElementModel element,
            String text) {
        element.setReContentId(contentId);
        return openApi.editText(bookId, element, text);
    }

    @Override
    public Observable<TFOBaseResponse<EditPod>> updateContents(
            String bookId,
            List<TFOBookContentModel> contentArray) {
        return openApi.editPod(bookId, contentArray);
    }

    @Override
    public Observable<BaseResponse>
    addRemoteCalendar(CalendarExtendObj obj) {

//        obj.getBookId(),
//                String.valueOf(obj.getBookType()),
//                obj.getBookCover(),
//                FastData.getUserName(),
//                FastData.getAvatar(),
//                obj.getBookTitle(),
//                obj.getBookSummary(),
//                CommemorationDataManger.getInstance().toData(),
//                ""

        //return apiService.sdkBookSave();
        // TODO: 2/22/17 fill sdk book save in params.
        CommemorationDataManger.getInstance().toData();
        return Observable.empty();
    }

    @Override
    public Observable<GeneralBookItemResponse> getRemoteBook(String id, String type) {
        String finalId = String.format(Locale.CHINESE, "%s$%s", id, type);
        return apiService.sdkBookGet(finalId);
    }

    @Override
    public Observable<BaseResponse>
    deleteRemoteCalendar(String id, String type) {
        String finalId = String.format(Locale.CHINESE, "%s$%s", id, type);
        return apiService.sdkBookDelete(finalId);
    }

    @Override
    public Observable<BaseResponse>
    updateRemoteCalendar(String remoteId, CalendarExtendObj obj) {
        // todo delete update .

        return Observable.empty();
//        return apiService.sdkBookUpdate(
//                remoteId,
//                obj.getBookId(),
//                String.valueOf(obj.getBookType()),
//                obj.getBookCover(),
//                FastData.getUserName(),
//                FastData.getAvatar(),
//                obj.getBookTitle(),
//                obj.getBookSummary(),
//                CommemorationDataManger.getInstance().toData(),
//                ""
//        );
    }

    public Observable<TFOBaseResponse<String>> createCover(int width, int height, TFOBookContentModel content) {

        return openApi.createBookCover(width, height, content);
    }

    public static boolean isCalendar(int bookType) {
        switch (bookType) {

            case BOOK_TYPE_CALENDAR_VERTICAL:
            case BOOK_TYPE_CALENDAR_HORIZONTAL:
            case BOOK_TYPE_CALENDAR_ACTIVITY_STARS:

                return true;

            default:
                return false;
        }
    }
}
