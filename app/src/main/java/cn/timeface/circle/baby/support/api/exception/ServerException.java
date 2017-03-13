package cn.timeface.circle.baby.support.api.exception;

/**
 * Created by JieGuo on 16/9/6.
 */

public class ServerException extends RuntimeException {


    public ServerException() {
    }

    public ServerException(String detailMessage) {
        super(detailMessage);
    }

    public ServerException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public ServerException(Throwable throwable) {
        super(throwable);
    }
}
