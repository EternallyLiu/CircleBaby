package cn.timeface.circle.baby.support.mvp.presentations;

import java.util.List;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;
import cn.timeface.circle.baby.support.api.models.objs.BookObj;
import cn.timeface.circle.baby.support.api.models.responses.BookListResponse;
import cn.timeface.circle.baby.support.api.models.responses.CardListResponse;
import cn.timeface.circle.baby.support.api.models.responses.DiaryCardListResponse;
import cn.timeface.circle.baby.support.api.models.responses.KnowledgeCardListResponse;
import cn.timeface.circle.baby.ui.circle.bean.CircleBookObj;
import cn.timeface.circle.baby.ui.circle.response.QueryCirclePhotoResponse;
import rx.Observable;

/**
 * book presentation
 * author : YW.SUN Created on 2017/1/12
 * email : sunyw10@gmail.com
 */
public interface BookPresentation {

    interface Model {

        /**
         * production list
         * @param bookType book type
         * @return production list
         */
        Observable<BookListResponse> productionList(int bookType, int permissionType);

        /**
         * 成长纪念册列表
         * @return book list
         */
        Observable<BookListResponse> commemorationBookList(int permissionType);

        /**
         * 日记卡片
         * @return book list
         */
        Observable<DiaryCardListResponse> diaryCardBookList();

        /**
         * 识图卡片
         * @return book list
         */
        Observable<KnowledgeCardListResponse> recognizeCardBookList();

        /**
         * 台历
         * @return book list
         */
        Observable<BookListResponse> calendarBookList(int permissionType);

        /**
         * 精装照片书
         * @return book list
         */
        Observable<BookListResponse> hardcoverPhotoBookList(int permissionType);

        /**
         * 成长语录
         * @return book list
         */
        Observable<BookListResponse> growthQuotationBookList(int permissionType);

        /**
         * 记事本
         * @return book list
         */
        Observable<BookListResponse> noteBookList(int permissionType);

        /**
         * 绘画集
         * @return book list
         */
        Observable<BookListResponse> paintBookList(int permissionType);

        /**
         * 删除作品
         * @param bookId 书id
         * @return base response
         */
        Observable<BaseResponse> deleteBook(String bookId);

        Observable<QueryCirclePhotoResponse<CircleBookObj>> circleBookList(long circleId, int permissionType);
    }

    interface Presenter{
        /**
         * 创建书作品
         */
        void create();

        void loadData(int bookType, int permissionType);

        void circleBooks(long circleId, int permissionType);

        /**
         * 编辑书作品
         */
        void edit();

        /**
         * 申请印刷
         */
        void askForPrint();

        /**
         * 更换主题
         */
        void changeTheme();



    }

    interface View extends BasePresenterView{
        /**
         * 提示错误信息
         * @param errMsg
         */
        void showErr(String errMsg);

        /**
         * 设置loading
         * @param loading
         */
        void setStateView(boolean loading);

        void setBookData(List<BookObj> bookObjs, boolean hasPic);

        void setCircleBookData(List<CircleBookObj> circleBookObjs);

    }

}
