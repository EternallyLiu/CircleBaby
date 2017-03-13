package cn.timeface.circle.baby.ui.calendar.exceptions;

/**
 * Created by JieGuo on 16/10/11.
 */

public class OutOfLimitException extends RuntimeException {

    public OutOfLimitException(String detailMessage) {
        super(detailMessage);
    }

    public OutOfLimitException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public OutOfLimitException(Throwable throwable) {
        super(throwable);
    }
}
