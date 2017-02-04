package cn.timeface.circle.baby.support.mvp.presentations;

import java.util.List;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;
import cn.timeface.circle.baby.support.api.models.objs.BookObj;
import cn.timeface.circle.baby.support.api.models.responses.BookListResponse;
import cn.timeface.circle.baby.support.api.models.responses.CardListResponse;
import cn.timeface.circle.baby.support.api.models.responses.DiaryCardListResponse;
import cn.timeface.circle.baby.support.api.models.responses.KnowledgeCardListResponse;
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
        Observable<BookListResponse> productionList(int bookType);

        /**
         * 成长纪念册列表
         * @return book list
         */
        Observable<BookListResponse> commemorationBookList();

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
        Observable<BookListResponse> calendarBookList();

        /**
         * 精装照片书
         * @return book list
         */
        Observable<BookListResponse> hardcoverPhotoBookList();

        /**
         * 成长语录
         * @return book list
         */
        Observable<BookListResponse> growthQuotationBookList();

        /**
         * 记事本
         * @return book list
         */
        Observable<BookListResponse> noteBookList();

        /**
         * 绘画集
         * @return book list
         */
        Observable<BookListResponse> paintBookList();

        /**
         * 删除作品
         * @param bookId 书id
         * @return base response
         */
        Observable<BaseResponse> deleteBook(String bookId);
    }

    interface Presenter{
        /**
         * 创建书作品
         */
        void create();

        void loadData(int bookType);

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

        void setBookData(List<BookObj> bookObjs);

    }

}
