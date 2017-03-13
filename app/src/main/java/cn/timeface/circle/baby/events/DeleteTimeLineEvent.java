package cn.timeface.circle.baby.events;

/**
 * Created by lidonglin on 2016/6/22.
 */
public class DeleteTimeLineEvent extends TimelineEditEvent{
    public DeleteTimeLineEvent() {
    }

    public DeleteTimeLineEvent(int timeId) {
        super(timeId);
    }
}
