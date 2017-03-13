package cn.timeface.circle.baby.ui.notebook.exceptions;

/**
 * Created by JieGuo on 16/6/22.
 */
public class PodPageNotExistException extends Exception {

    public PodPageNotExistException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public PodPageNotExistException(String detailMessage) {
        super(detailMessage);
    }
}
