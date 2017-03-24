package cn.timeface.circle.baby.support.mvp.model;

import cn.timeface.circle.baby.constants.TypeConstants;
import cn.timeface.circle.baby.support.api.models.base.BaseResponse;
import cn.timeface.circle.baby.support.api.models.responses.BookListResponse;
import cn.timeface.circle.baby.support.api.models.responses.DiaryCardListResponse;
import cn.timeface.circle.baby.support.api.models.responses.KnowledgeCardListResponse;
import cn.timeface.circle.baby.support.mvp.bases.BasePresenterModel;
import cn.timeface.circle.baby.support.mvp.presentations.BookPresentation;
import cn.timeface.circle.baby.ui.circle.bean.CircleBookObj;
import cn.timeface.circle.baby.ui.circle.response.QueryCirclePhotoResponse;
import rx.Observable;

/**
 * book model
 * author : YW.SUN Created on 2017/1/12
 * email : sunyw10@gmail.com
 */
public class BookModel extends BasePresenterModel implements BookPresentation.Model {
    public static final int BOOK_TYPE_GROWTH_COMMEMORATION_BOOK = 1;//成长纪念册
    public static final int BOOK_TYPE_DIARY_CARD = 2;//日记卡片
    public static final int BOOK_TYPE_RECOGNIZE_PHOTO_CARD = 3;//识图卡片
    public static final int BOOK_TYPE_CALENDAR = 4;//台历
    public static final int BOOK_TYPE_HARDCOVER_PHOTO_BOOK = 5;//精装照片书
    public static final int BOOK_TYPE_GROWTH_QUOTATIONS = 6;//成长语录
    public static final int BOOK_TYPE_NOTEBOOK = 7;//记事本
    public static final int BOOK_TYPE_PAINTING = 8;//绘画集
    public static final int CIRCLE_BOOK_TYPE_FAMILY_SCHOOL = 9;//家校纪念册
    public static final int CIRCLE_BOOK_TYPE_TIME = 10;//圈时光书
    public static final int CIRCLE_BOOK_TYPE_PHOTO = 11;//圈照片书

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
                return "照片书";

            case BOOK_TYPE_GROWTH_QUOTATIONS:
                return "成长语录";

            case BOOK_TYPE_NOTEBOOK:
                return "记事本";

            case BOOK_TYPE_PAINTING:
                return "绘画集";

            case CIRCLE_BOOK_TYPE_FAMILY_SCHOOL:
                return "家校纪念册";

            case CIRCLE_BOOK_TYPE_TIME:
                return "圈时光书";

            case CIRCLE_BOOK_TYPE_PHOTO:
                return "圈照片书";

            default:
                return "";
        }
    }

    public static int getOpenBookType(int bookType) {
        if (bookType == BOOK_TYPE_PAINTING) {
            return TypeConstants.OPEN_BOOK_TYPE_PAINTING;
        } else if (bookType == BOOK_TYPE_GROWTH_QUOTATIONS) {
            return TypeConstants.OPEN_BOOK_TYPE_GROWTH_QUOTATIONS;
        } else if (bookType == BOOK_TYPE_GROWTH_COMMEMORATION_BOOK) {
            return TypeConstants.OPEN_BOOK_TYPE_GROWTH_COMMEMORATION_BOOK;
        } else {
            return 0;
        }
    }

    @Override
    public Observable<BookListResponse> productionList(int bookType, int permissionType) {
        return apiService.bookList(bookType, permissionType);
    }

    /**
     * 成长纪念册列表
     *
     * @return bookListResponse
     */
    @Override
    public Observable<BookListResponse> commemorationBookList(int permissionType) {
        return apiService.bookList(BOOK_TYPE_GROWTH_COMMEMORATION_BOOK, permissionType);
    }

    /**
     * 日记卡片
     *
     * @return bookListResponse
     */
    @Override
    public Observable<DiaryCardListResponse> diaryCardBookList() {
        return apiService.diaryCardList();
    }

    /**
     * 识图卡片
     *
     * @return bookListResponse
     */
    @Override
    public Observable<KnowledgeCardListResponse> recognizeCardBookList() {
        return apiService.recognizeCardList();
    }

    /**
     * 台历
     *
     * @return bookListResponse
     */
    @Override
    public Observable<BookListResponse> calendarBookList(int permissionType) {
        return apiService.bookList(BOOK_TYPE_CALENDAR, permissionType);
    }

    /**
     * 精装照片书
     *
     * @return bookListResponse
     */
    @Override
    public Observable<BookListResponse> hardcoverPhotoBookList(int permissionType) {
        return apiService.bookList(BOOK_TYPE_HARDCOVER_PHOTO_BOOK, permissionType);
    }

    /**
     * 成长语录
     *
     * @return bookListResponse
     */
    @Override
    public Observable<BookListResponse> growthQuotationBookList(int permissionType) {
        return apiService.bookList(BOOK_TYPE_GROWTH_QUOTATIONS, permissionType);
    }

    /**
     * 记事本
     *
     * @return bookListResponse
     */
    @Override
    public Observable<BookListResponse> noteBookList(int permissionType) {
        return apiService.bookList(BOOK_TYPE_NOTEBOOK, permissionType);
    }

    /**
     * 绘画集
     *
     * @return bookListResponse
     */
    @Override
    public Observable<BookListResponse> paintBookList(int permissionType) {
        return apiService.bookList(BOOK_TYPE_PAINTING, permissionType);
    }

    /**
     * 删除作品
     *
     * @param bookId 书id
     * @return baseResponse
     */
    @Override
    public Observable<BaseResponse> deleteBook(String bookId) {
        return apiService.deleteBook(bookId);
    }

    /**
     * 圈作品列表
     *
     * @param circleId
     * @param permissionType
     * @return
     */
    @Override
    public Observable<QueryCirclePhotoResponse<CircleBookObj>> circleBookList(long circleId, int permissionType) {
        return apiService.circleBookList(circleId, permissionType);
    }

    /**
     * 圈作品列表
     *
     * @return
     */
    @Override
    public Observable<QueryCirclePhotoResponse<CircleBookObj>> circleBookList() {
        return apiService.circleBookList();
    }
}
