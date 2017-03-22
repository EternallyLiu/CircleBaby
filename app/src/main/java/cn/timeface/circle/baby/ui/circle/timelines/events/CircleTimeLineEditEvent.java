package cn.timeface.circle.baby.ui.circle.timelines.events;

import android.os.Parcel;
import android.os.Parcelable;

import cn.timeface.circle.baby.ui.circle.bean.CircleTimelineObj;

/**
 * author : wangshuai Created on 2017/3/22
 * email : wangs1992321@gmail.com
 */
public class CircleTimeLineEditEvent implements Parcelable {

    private int type = 0;//0默认编辑 1、删除 2、发布
    private CircleTimelineObj timelineObj;

    public CircleTimeLineEditEvent(CircleTimelineObj timelineObj) {
        this.timelineObj = timelineObj;
    }

    public CircleTimeLineEditEvent(int type, CircleTimelineObj timelineObj) {

        this.type = type;
        this.timelineObj = timelineObj;
    }

    public CircleTimelineObj getTimelineObj() {

        return timelineObj;
    }

    public void setTimelineObj(CircleTimelineObj timelineObj) {
        this.timelineObj = timelineObj;
    }

    public int getType() {

        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.type);
        dest.writeParcelable(this.timelineObj, flags);
    }

    protected CircleTimeLineEditEvent(Parcel in) {
        this.type = in.readInt();
        this.timelineObj = in.readParcelable(CircleTimelineObj.class.getClassLoader());
    }

    public static final Parcelable.Creator<CircleTimeLineEditEvent> CREATOR = new Parcelable.Creator<CircleTimeLineEditEvent>() {
        @Override
        public CircleTimeLineEditEvent createFromParcel(Parcel source) {
            return new CircleTimeLineEditEvent(source);
        }

        @Override
        public CircleTimeLineEditEvent[] newArray(int size) {
            return new CircleTimeLineEditEvent[size];
        }
    };
}
