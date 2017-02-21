package cn.timeface.circle.baby.support.mvp.presentations;

import android.content.Intent;

import java.util.List;

import cn.timeface.circle.baby.support.api.models.db.PhotoModel;
import cn.timeface.circle.baby.support.mvp.model.GeneralBookItemResponse;
import cn.timeface.circle.baby.support.mvp.response.bases.BaseResponse;
import cn.timeface.circle.baby.ui.calendar.bean.CalendarExtendObj;
import cn.timeface.circle.baby.ui.calendar.bean.DateObj;
import cn.timeface.circle.baby.ui.calendar.response.DateResponse;
import cn.timeface.open.api.bean.base.TFOBaseResponse;
import cn.timeface.open.api.bean.obj.TFOBookContentModel;
import cn.timeface.open.api.bean.obj.TFOBookModel;
import cn.timeface.open.api.bean.response.EditPod;
import cn.timeface.open.api.bean.response.SimplePageTemplate;

import rx.Observable;
import rx.functions.Action1;

/**
 * 日历Presenter
 * <p>
 * Created by JieGuo on 16/9/29.
 */

public interface CalendarPresentation {

    interface Model {

        Observable<TFOBaseResponse<TFOBookModel>> create(int type);


        /**
         * 更新一页的POD数据
         *
         * @param model model
         * @return
         */
        Observable<TFOBaseResponse<EditPod>> update(
                TFOBookModel model
        );

        /**
         * 查询所有的台历数据,  新的台历数据
         *
         * @return Observable
         */
        Observable<?> list();

        /**
         * 查询一个台历的数据
         *
         * @param id calendar data id
         * @return Observable
         */
        Observable<TFOBaseResponse<TFOBookModel>> get(String id);

        /**
         * query a calendar data
         *
         * @param id calendar id
         * @param type calendar type
         * @return Observable
         */
        Observable<TFOBaseResponse<TFOBookModel>> get(String id, String type);

        /**
         * 删除一个台历
         *
         * @param id id
         * @return Observable
         */
        Observable<BaseResponse> delete(String id);

        /**
         * 获取版式列表
         *
         * @param bookId    book id
         * @param contentId content id
         * @return Observable
         */
        Observable<TFOBaseResponse<List<SimplePageTemplate>>> templateList(
                String bookId, long bookType, String contentId);

        /**
         * 重新排版一页数据
         *
         * @param bookId      book id
         * @param templateId  template id
         * @param contentList content list
         * @return Observable
         */
        Observable<TFOBaseResponse<List<TFOBookContentModel>>> changePageTemplate(
                String bookId,
                int templateId,
                List<TFOBookContentModel> contentList);


        /**
         * 修改多页数据
         *
         * @param bookId   book id
         * @param contents content list
         * @return
         */
        Observable<TFOBaseResponse<EditPod>>
        updateContents(String bookId, List<TFOBookContentModel> contents);

        /**
         * 添加一个台历记录到 时光流影后台
         *
         * @return
         */
        Observable<BaseResponse> addRemoteCalendar(CalendarExtendObj obj);

        /**
         * 获取一个台历记录,从时光流影后台
         *
         * @param id id
         * @return
         */
        Observable<GeneralBookItemResponse> getRemoteBook(String id, String type);

        /**
         * 删除 存在时光流影里的一本开放平台的书,注意这个ID 不是Book ID
         *
         * @param id id
         * @return
         */
        Observable<BaseResponse> deleteRemoteCalendar(String id, String type);

        /**
         * 更新时光流影里存的一个台历
         *
         * @param id  id
         * @param obj obj
         * @return
         */
        Observable<?> updateRemoteCalendar(String id, CalendarExtendObj obj);

        /**
         * 创建一个台历封面
         * @param width width
         * @param height height
         * @param content content model
         * @return Observable
         */
        Observable<TFOBaseResponse<String>>
        createCover(int width, int height, TFOBookContentModel content);
    }

    interface View extends BasePresenterView {

        // 设置模板 上显示的 当前数和总个数
        void setCurrentTemplateSize(int current, int count);

        void showLoading();

        void hideLoading();

        void refreshView();

        int getCurrentPageIndex();
    }

    interface Presenter {

        void create(
                int type,
                Action1<TFOBaseResponse<CalendarExtendObj>> onLoad,
                Action1<Throwable> onError);

        void list(Action1<?> onLoad, Action1<Throwable> onError);

        /**
         * get content template list
         *
         * @param bookId    book id
         * @param contentId content id
         * @param onLoad    action
         * @param onError   error action.
         */
        void listTemplate(
                String bookId, int bookType, String contentId,
                Action1<TFOBaseResponse<List<SimplePageTemplate>>> onLoad,
                Action1<Throwable> onError);

        void get(String id,
                 Action1<TFOBaseResponse<CalendarExtendObj>> onLoad,
                 Action1<Throwable> onError);

        void get(String id,
                 String type,
                 Action1<TFOBaseResponse<CalendarExtendObj>> onLoad,
                 Action1<Throwable> onError);

        /**
         * get by remote id
         *
         * @param remoteId remote id in timeFace server persistent's id
         * @param onLoad   on load action
         * @param onError  on error action
         */
        void getByRemoteId(String remoteId, String type,
                           Action1<TFOBaseResponse<CalendarExtendObj>> onLoad,
                           Action1<Throwable> onError);

        void update(Action1<CalendarExtendObj> onLoad, Action1<Throwable> onError);

        void addCommemorationByDay(
                TFOBookContentModel contentModel,
                String month, String day, String text
        );

        void deleteCommemorationByDay(
                TFOBookContentModel contentModel,
                String month, String day, String text
        );

        void updateCommemorationByDay(
                TFOBookContentModel contentModel,
                String oldMonth, String oldDay, String oldText,
                String month, String day, String text
        );

        /**
         * 保存编辑好的这本书
         */
        void save();

        /**
         * 删除编辑 好的这本书
         */
        void delete();

        /**
         * 获取正面数据 不包涵封面
         *
         * @return TFOBookModel
         */
        TFOBookModel getFrontSide();

        /**
         * 获取正面数据  包涵封面
         *
         * @return TFOBookModel
         * @throws Throwable
         */
        TFOBookModel getFrontSideWithCover() throws Throwable;

        /**
         * 获取背面数据 不包涵封底
         *
         * @return TFOBookModel
         */
        TFOBookModel getBackSide();

        /**
         * 获取背面数据, 包涵封底并封底在每一个位置
         *
         * @return TFOBookModel
         * @throws Throwable
         */
        TFOBookModel getBackSideWithCover() throws Throwable;

        CalendarExtendObj getOriginalModel();

        /**
         * 切换模板 根据页码
         *
         * @param contentId the content Id
         */
        void changeTemplate(String contentId);

        void refreshTemplate(String contentId);

        void editImage(TFOBookContentModel contentModel, Intent data) throws Exception;

        void editText(TFOBookContentModel contentModel, Intent data) throws Exception;

        String createShareUrl() throws Exception;

        /**
         * 上传多图， 带进度的
         */
        void uploadImageWithProgress(
                int type,
                List<PhotoModel> images,
                Action1<TFOBaseResponse<CalendarExtendObj>> onLoad,
                Action1<Throwable> onError) throws Exception;

        void loadAllPageTemplate();

        /**
         * 关闭界面时的处理
         */
        void closeActivity();

        void deleteRemoteBook();
    }

    interface CommemorationPresentation {

        interface Model {
            /**
             * 添加一个纪念日
             *
             * @return Observable
             */
            Observable<BaseResponse> add(CommemorationParamsBuilder builder);

            /**
             * 获取一个纪念日信息
             *
             * @param id id
             * @return Observable
             */
            Observable<DateResponse> get(String id);

            /**
             * 获取一个月的所有纪念日
             * <p>
             * 当月为0时,获取当年所有的纪念日
             *
             * @param month month
             * @return Observable
             */
            Observable<DateResponse> list(String calendarId, String year, String month);

            /**
             * 更新一个纪念日
             *
             * @param builder builder
             * @return Observable
             */
            Observable<BaseResponse> update(CommemorationParamsBuilder builder, String oldDay);

            /**
             * 删除一个纪念日
             *
             * @return Observable
             */
            Observable<BaseResponse> delete(DateObj dateObj);
        }

        interface Presenter {

            void add(CommemorationParamsBuilder builder, Action1<BaseResponse> onLoad,
                     Action1<Throwable> onError);

            void get(String id, Action1<DateResponse> onLoad, Action1<Throwable> onError);

            void list(String calendarId, String year, String month, Action1<DateResponse> onLoad, Action1<Throwable> onError);

            void update(CommemorationParamsBuilder builder,
                        String oldDay,
                        Action1<BaseResponse> onLoad,
                        Action1<Throwable> onError);

            void delete(DateObj dateObj, Action1<BaseResponse> onLoad, Action1<Throwable> onError);

            int getMaxDateOfMonth(int year, int month);

            List<Long> getAllDayInMonth(int year, int month);
        }

        interface View extends BasePresenterView {

        }
    }

    class CommemorationParamsBuilder extends ParamsBuilder {

        public CommemorationParamsBuilder calendarId(String calendarId) {
            put("calendarId", calendarId);
            return this;
        }

        public CommemorationParamsBuilder year(String year) {
            put("year", year);
            return this;
        }

        public CommemorationParamsBuilder month(String month) {
            put("month", month);
            return this;
        }

        public CommemorationParamsBuilder day(String day) {
            put("day", day);
            return this;
        }

        public CommemorationParamsBuilder content(String content) {
            put("content", content);
            return this;
        }
    }
}
