package cn.timeface.circle.baby.ui.notebook.exceptions;

/**
 * Created by JieGuo on 16/6/17.
 */
public class BookDataErrorException extends Exception {

    public BookDataErrorException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public BookDataErrorException(String detailMessage) {
        super(detailMessage);
    }
}
