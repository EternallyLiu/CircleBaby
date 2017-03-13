package cn.timeface.party.ui.notebook.exceptions;

/**
 * Created by JieGuo on 16/6/15.
 */
public class BookNameEmptyException extends Exception {

    public BookNameEmptyException() {
        super();
        
    }

    public BookNameEmptyException(String detailMessage) {
        super(detailMessage);
    }

    public BookNameEmptyException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public BookNameEmptyException(Throwable throwable) {
        super(throwable);
    }
}
