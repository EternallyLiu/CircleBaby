package cn.timeface.circle.baby.events;

import android.os.Parcel;
import android.os.Parcelable;

import cn.timeface.circle.baby.support.api.models.objs.TimeLineObj;

public class TimelineEditEvent implements Parcelable {
    private int timeId;
    private TimeLineObj timeLineObj;

    public TimeLineObj getTimeLineObj() {
        return timeLineObj;
    }

    public void setTimeLineObj(TimeLineObj timeLineObj) {
        this.timeLineObj = timeLineObj;
    }

    public TimelineEditEvent(TimeLineObj timeLineObj) {

        this.timeLineObj = timeLineObj;
    }

    public int getTimeId() {
        return timeId;
    }

    public void setTimeId(int timeId) {
        this.timeId = timeId;
    }

    public TimelineEditEvent() {

    }

    public TimelineEditEvent(int timeId) {

        this.timeId = timeId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.timeId);
        dest.writeParcelable(this.timeLineObj, flags);
    }

    protected TimelineEditEvent(Parcel in) {
        this.timeId = in.readInt();
        this.timeLineObj = in.readParcelable(TimeLineObj.class.getClassLoader());
    }

    public static final Creator<TimelineEditEvent> CREATOR = new Creator<TimelineEditEvent>() {
        @Override
        public TimelineEditEvent createFromParcel(Parcel source) {
            return new TimelineEditEvent(source);
        }

        @Override
        public TimelineEditEvent[] newArray(int size) {
            return new TimelineEditEvent[size];
        }
    };
}

