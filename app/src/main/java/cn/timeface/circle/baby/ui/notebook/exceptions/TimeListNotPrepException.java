package cn.timeface.circle.baby.ui.notebook.exceptions;

/**
 * Created by JieGuo on 16/6/17.
 */
public class TimeListNotPrepException extends Exception {

    public TimeListNotPrepException(String detailMessage) {
        super(detailMessage);
    }

    public TimeListNotPrepException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }
}
