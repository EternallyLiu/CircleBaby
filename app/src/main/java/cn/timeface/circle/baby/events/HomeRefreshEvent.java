package cn.timeface.circle.baby.events;

/**
 * Created by lidonglin on 2016/6/22.
 */
public class HomeRefreshEvent extends TimelineEditEvent {

    private int type = 0;

    public int getType() {
        return type;
    }

    public HomeRefreshEvent setType(int type) {
        this.type = type;
        return this;
    }

    public HomeRefreshEvent() {
    }

    public HomeRefreshEvent(int timeId) {
        super(timeId);
    }
}
