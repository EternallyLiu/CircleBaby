package cn.timeface.circle.baby.support.api.exception;

/**
 * http recourse not found exception.
 *
 * Created by JieGuo on 16/9/6.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException() {
    }

    public ResourceNotFoundException(String detailMessage) {
        super(detailMessage);
    }

    public ResourceNotFoundException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public ResourceNotFoundException(Throwable throwable) {
        super(throwable);
    }
}
