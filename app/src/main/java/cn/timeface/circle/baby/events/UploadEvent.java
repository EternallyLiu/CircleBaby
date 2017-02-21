package cn.timeface.circle.baby.events;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by lidonglin on 2016/6/22.
 */
public class UploadEvent implements Parcelable {
    int progress;
    private int timeId;
    private boolean isComplete=false;

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    public UploadEvent(int progress, int timeId, boolean isComplete) {
        this.progress = progress;
        this.timeId = timeId;
        this.isComplete = isComplete;
    }

    public UploadEvent(int timeId, boolean isComplete) {
        this.timeId = timeId;
        this.isComplete = isComplete;
    }

    public int getTimeId() {
        return timeId;
    }

    public void setTimeId(int timeId) {
        this.timeId = timeId;
    }

    public UploadEvent(int progress, int timeId) {

        this.progress = progress;
        this.timeId = timeId;
    }

    public UploadEvent(int progress) {
        this.progress = progress;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.progress);
        dest.writeInt(this.timeId);
        dest.writeByte(this.isComplete ? (byte) 1 : (byte) 0);
    }

    protected UploadEvent(Parcel in) {
        this.progress = in.readInt();
        this.timeId = in.readInt();
        this.isComplete = in.readByte() != 0;
    }

    public static final Creator<UploadEvent> CREATOR = new Creator<UploadEvent>() {
        @Override
        public UploadEvent createFromParcel(Parcel source) {
            return new UploadEvent(source);
        }

        @Override
        public UploadEvent[] newArray(int size) {
            return new UploadEvent[size];
        }
    };
}
