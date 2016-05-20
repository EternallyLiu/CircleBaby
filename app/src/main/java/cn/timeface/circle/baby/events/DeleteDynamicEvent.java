package cn.timeface.circle.baby.events;

/**
 * Created by tao on 2016/3/23.
 */
public class DeleteDynamicEvent {
    public DeleteDynamicEvent(int id) {
        this.id = id;
    }

    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
