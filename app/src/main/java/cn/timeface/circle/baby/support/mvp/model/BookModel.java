package cn.timeface.circle.baby.support.mvp.model;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;
import cn.timeface.circle.baby.support.api.models.responses.BookListResponse;
import cn.timeface.circle.baby.support.api.models.responses.CardListResponse;
import cn.timeface.circle.baby.support.api.models.responses.DiaryCardListResponse;
import cn.timeface.circle.baby.support.api.models.responses.KnowledgeCardListResponse;
import cn.timeface.circle.baby.support.mvp.bases.BasePresenterModel;
import cn.timeface.circle.baby.support.mvp.presentations.BookPresentation;
import rx.Observable;

/**
 * book model
 * author : YW.SUN Created on 2017/1/12
 * email : sunyw10@gmail.com
 */
public class BookModel extends BasePresenterModel implements BookPresentation.Model{
    public static final int BOOK_TYPE_GROWTH_COMMEMORATION_BOOK = 1;//成长纪念册
    public static final int BOOK_TYPE_DIARY_CARD = 2;//日记卡片
    public static final int BOOK_TYPE_RECOGNIZE_PHOTO_CARD = 3;//识图卡片
    public static final int BOOK_TYPE_CALENDAR = 4;//台历
    public static final int BOOK_TYPE_HARDCOVER_PHOTO_BOOK = 5;//精装照片书
    public static final int BOOK_TYPE_GROWTH_QUOTATIONS = 6;//成长语录
    public static final int BOOK_TYPE_NOTEBOOK = 7;//记事本
    public static final int BOOK_TYPE_PAINTING = 8;//绘画集

    public static String getGrowthBookName(int bookType) {
        switch (bookType) {
            case BOOK_TYPE_GROWTH_COMMEMORATION_BOOK:
                return "成长纪念册";

            case BOOK_TYPE_DIARY_CARD:
                return "日记卡片";

            case BOOK_TYPE_RECOGNIZE_PHOTO_CARD:
                return "识图卡片";

            case BOOK_TYPE_CALENDAR:
                return "台历";

            case BOOK_TYPE_HARDCOVER_PHOTO_BOOK:
                return "精装照片书";

            case BOOK_TYPE_GROWTH_QUOTATIONS:
                return "成长语录";

            case BOOK_TYPE_NOTEBOOK:
                return "记事本";

            case BOOK_TYPE_PAINTING:
                return "绘画集";

            default: return "";
        }
    }

    @Override
    public Observable<BookListResponse> productionList(int bookType) {
        return apiService.bookList(bookType);
    }

    /**
     * 成长纪念册列表
     * @return bookListResponse
     */
    @Override
    public Observable<BookListResponse> commemorationBookList(){
        return apiService.bookList(BOOK_TYPE_GROWTH_COMMEMORATION_BOOK);
    }

    /**
     * 日记卡片
     * @return bookListResponse
     */
    @Override
    public Observable<DiaryCardListResponse> diaryCardBookList(){
        return apiService.diaryCardList();
    }

    /**
     * 识图卡片
     * @return bookListResponse
     */
    @Override
    public Observable<KnowledgeCardListResponse> recognizeCardBookList(){
        return apiService.recognizeCardList();
    }

    /**
     * 台历
     * @return bookListResponse
     */
    @Override
    public Observable<BookListResponse> calendarBookList(){
        return apiService.bookList(BOOK_TYPE_CALENDAR);
    }

    /**
     * 精装照片书
     * @return bookListResponse
     */
    @Override
    public Observable<BookListResponse> hardcoverPhotoBookList(){
        return apiService.bookList(BOOK_TYPE_HARDCOVER_PHOTO_BOOK);
    }

    /**
     * 成长语录
     * @return bookListResponse
     */
    @Override
    public Observable<BookListResponse> growthQuotationBookList(){
        return apiService.bookList(BOOK_TYPE_GROWTH_QUOTATIONS);
    }

    /**
     * 记事本
     * @return bookListResponse
     */
    @Override
    public Observable<BookListResponse> noteBookList(){
        return apiService.bookList(BOOK_TYPE_NOTEBOOK);
    }

    /**
     * 绘画集
     * @return bookListResponse
     */
    @Override
    public Observable<BookListResponse> paintBookList(){
        return apiService.bookList(BOOK_TYPE_PAINTING);
    }

    /**
     * 删除作品
     * @param bookId 书id
     * @return baseResponse
     */
    @Override
    public Observable<BaseResponse> deleteBook(String bookId){
        return apiService.deleteBook(bookId);
    }
}
