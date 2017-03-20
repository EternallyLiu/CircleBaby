package cn.timeface.circle.baby.ui.circle.timelines.events;

import android.os.Parcel;
import android.os.Parcelable;

import cn.timeface.circle.baby.ui.circle.bean.CircleActivityAlbumObj;

/**
 * author : wangshuai Created on 2017/3/16
 * email : wangs1992321@gmail.com
 */
public class ActiveSelectEvent implements Parcelable {
    private CircleActivityAlbumObj activityAlbumObj;
    private int type = 0;//0正常传值 -1、表示传null（不显示活动）

    public ActiveSelectEvent(CircleActivityAlbumObj activityAlbumObj) {
        this.activityAlbumObj = activityAlbumObj;
        this.type = 0;
    }

    public ActiveSelectEvent(CircleActivityAlbumObj activityAlbumObj, int type) {

        this.activityAlbumObj = activityAlbumObj;
        this.type = type;
    }

    public CircleActivityAlbumObj getActivityAlbumObj() {
        return activityAlbumObj;
    }

    public void setActivityAlbumObj(CircleActivityAlbumObj activityAlbumObj) {
        this.activityAlbumObj = activityAlbumObj;
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
        dest.writeParcelable(this.activityAlbumObj, flags);
        dest.writeInt(this.type);
    }

    public ActiveSelectEvent() {
    }

    protected ActiveSelectEvent(Parcel in) {
        this.activityAlbumObj = in.readParcelable(CircleActivityAlbumObj.class.getClassLoader());
        this.type = in.readInt();
    }

    public static final Parcelable.Creator<ActiveSelectEvent> CREATOR = new Parcelable.Creator<ActiveSelectEvent>() {
        @Override
        public ActiveSelectEvent createFromParcel(Parcel source) {
            return new ActiveSelectEvent(source);
        }

        @Override
        public ActiveSelectEvent[] newArray(int size) {
            return new ActiveSelectEvent[size];
        }
    };
}
