package cn.timeface.circle.baby.ui.calendar.exceptions;

/**
 * Created by JieGuo on 16/10/11.
 */

public class AlradyExistException extends RuntimeException {

    public AlradyExistException() {
    }

    public AlradyExistException(String detailMessage) {
        super(detailMessage);
    }

    public AlradyExistException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public AlradyExistException(Throwable throwable) {
        super(throwable);
    }
}
