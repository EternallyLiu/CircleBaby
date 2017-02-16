package cn.timeface.circle.baby.support.api.exception;


import cn.timeface.circle.baby.support.api.models.base.BaseResponse;

/**
 * Created by JieGuo on 16/9/6.
 */

public class ResultException extends RuntimeException {

    private BaseResponse response;

    private int code = 0;

    public ResultException(String msg, int code) {
        this(msg);
        this.code = code;
    }

    public ResultException(String detailMessage) {
        super(detailMessage);
    }

    public ResultException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public BaseResponse getResponse() {
        return response;
    }

    public void setResponse(BaseResponse response) {
        this.response = response;
    }

    public ResultException(Throwable throwable) {
        super(throwable);
    }

    public int getCode() {
        return code;
    }
}
