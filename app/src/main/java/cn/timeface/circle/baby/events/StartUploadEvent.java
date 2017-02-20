package cn.timeface.circle.baby.events;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by lidonglin on 2016/6/22.
 */
public class StartUploadEvent implements Parcelable {
    private int timeId;

    public int getTimeId() {
        return timeId;
    }

    public void setTimeId(int timeId) {
        this.timeId = timeId;
    }

    public StartUploadEvent() {

    }

    public StartUploadEvent(int timeId) {

        this.timeId = timeId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.timeId);
    }

    protected StartUploadEvent(Parcel in) {
        this.timeId = in.readInt();
    }

    public static final Parcelable.Creator<StartUploadEvent> CREATOR = new Parcelable.Creator<StartUploadEvent>() {
        @Override
        public StartUploadEvent createFromParcel(Parcel source) {
            return new StartUploadEvent(source);
        }

        @Override
        public StartUploadEvent[] newArray(int size) {
            return new StartUploadEvent[size];
        }
    };
}
