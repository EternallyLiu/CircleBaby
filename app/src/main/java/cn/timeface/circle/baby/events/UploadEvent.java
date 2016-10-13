package cn.timeface.circle.baby.events;

/**
 * Created by lidonglin on 2016/6/22.
 */
public class UploadEvent {
    int progress;

    public UploadEvent(int progress) {
        this.progress = progress;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
