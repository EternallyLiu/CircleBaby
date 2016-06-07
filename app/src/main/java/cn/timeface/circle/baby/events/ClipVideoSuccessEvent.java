package cn.timeface.circle.baby.events;

/**
 * Created by lidonglin on 2016/5/31.
 */
public class ClipVideoSuccessEvent {
    public String clipVideoPath;

    public ClipVideoSuccessEvent(String clipVideoPath) {
        this.clipVideoPath = clipVideoPath;
    }

    public String getClipVideoPath() {
        return clipVideoPath;
    }

    public void setClipVideoPath(String clipVideoPath) {
        this.clipVideoPath = clipVideoPath;
    }
}
