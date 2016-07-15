package cn.timeface.circle.baby.events;

/**
 * Created by lidonglin on 2016/5/31.
 */
public class ClipVideoSuccessEvent {
    public String clipVideoPath;
    public int duration;

    public ClipVideoSuccessEvent(String clipVideoPath, int duration) {
        this.clipVideoPath = clipVideoPath;
        this.duration = duration;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getClipVideoPath() {
        return clipVideoPath;
    }

    public void setClipVideoPath(String clipVideoPath) {
        this.clipVideoPath = clipVideoPath;
    }
}
