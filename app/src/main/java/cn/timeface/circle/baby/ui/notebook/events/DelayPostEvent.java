package cn.timeface.circle.baby.ui.notebook.events;

/**
 * 延迟发送的event bus event
 *
 * Created by JieGuo on 16/11/23.
 */

public class DelayPostEvent<T> {

    private T data;

    public DelayPostEvent(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }
}
