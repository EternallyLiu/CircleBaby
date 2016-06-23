package cn.timeface.circle.baby.events;

/**
 * Created by yusen on 2014/11/25.
 */
public class BookChangeEvent {

    public static final int CIRCLE_BOOK_TYPE = 1;//圈子时光书

    public static final int TYPE_DELETE = 0;
    public static final int TYPE_CREATE = 1;
    public static final int TYPE_MODIFY = 2;
    public int type;
    public String bookId;
    public int bookType;

    public BookChangeEvent() {
        super();
    }

    public BookChangeEvent(String bookId, int type) {
        this.bookId = bookId;
        this.type = type;
    }

    public BookChangeEvent(String bookId, int type, int bookType) {
        this.bookId = bookId;
        this.type = type;
        this.bookType = bookType;
    }
}
