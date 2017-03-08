package cn.timeface.circle.baby.events;

/**
 * Created by lidonglin on 2016/6/22.
 */
public class UnreadMsgEvent {

    public int unReadMsgCount;

    public UnreadMsgEvent() {
    }

    public UnreadMsgEvent(int unReadMsgCount) {
        this.unReadMsgCount = unReadMsgCount;
    }
}
