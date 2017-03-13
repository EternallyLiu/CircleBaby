package cn.timeface.circle.baby.support.payment;

/**
 * Created by JieGuo on 1/22/16.
 */
public class PrepareOrderException extends Exception {

    public PrepareOrderException() {
    }

    public PrepareOrderException(String detailMessage) {
        super(detailMessage);
    }

    public PrepareOrderException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public PrepareOrderException(Throwable throwable) {
        super(throwable);
    }
}
