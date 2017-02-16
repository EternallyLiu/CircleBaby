package cn.timeface.circle.baby.events;

/**
 * 书籍操作选项event
 * author : YW.SUN Created on 2017/2/13
 * email : sunyw10@gmail.com
 */
public class BookOptionEvent {

    public final static int BOOK_OPTION_DELETE = 1;//成功删除书籍操作
    public final static int BOOK_OPTION_CREATE = 0;//成功创建书籍操作
    private int option;
    private int bookType;
    private String bookId;

    public BookOptionEvent() {}

    public BookOptionEvent(int option, int bookType, String bookId) {
        this.option = option;
        this.bookType = bookType;
        this.bookId = bookId;
    }

    public static int getBookOptionDelete() {
        return BOOK_OPTION_DELETE;
    }

    public int getOption() {
        return option;
    }

    public void setOption(int option) {
        this.option = option;
    }

    public int getBookType() {
        return bookType;
    }

    public void setBookType(int bookType) {
        this.bookType = bookType;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }
}
