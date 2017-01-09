package cn.timeface.circle.baby.support.mvp.presentations;

import java.util.List;

import cn.timeface.open.api.bean.base.TFOBaseResponse;
import cn.timeface.open.api.bean.obj.TFOBookContentModel;
import cn.timeface.open.api.bean.obj.TFOBookModel;
import cn.timeface.open.api.bean.response.EditPod;
import cn.timeface.open.api.bean.response.SimplePageTemplate;
import rx.Observable;

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
        Observable<?> delete(String id);

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
                String contentList);


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
}
