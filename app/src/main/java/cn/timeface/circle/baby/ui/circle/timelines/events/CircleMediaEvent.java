package cn.timeface.circle.baby.ui.circle.timelines.events;

import android.os.Parcel;
import android.os.Parcelable;

import cn.timeface.circle.baby.ui.circle.bean.CircleMediaObj;

/**
 * author : wangshuai Created on 2017/3/22
 * email : wangs1992321@gmail.com
 */
public class CircleMediaEvent implements Parcelable {

    private int type=0;//0默认编辑  1、删除
    private CircleMediaObj mediaObj;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public CircleMediaObj getMediaObj() {
        return mediaObj;
    }

    public void setMediaObj(CircleMediaObj mediaObj) {
        this.mediaObj = mediaObj;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.type);
        dest.writeParcelable(this.mediaObj, flags);
    }

    public CircleMediaEvent(int type, CircleMediaObj mediaObj) {
        this.type = type;
        this.mediaObj = mediaObj;
    }

    public CircleMediaEvent(CircleMediaObj mediaObj) {
        this.mediaObj = mediaObj;
    }

    public CircleMediaEvent() {
    }

    protected CircleMediaEvent(Parcel in) {
        this.type = in.readInt();
        this.mediaObj = in.readParcelable(CircleMediaObj.class.getClassLoader());
    }

    public static final Parcelable.Creator<CircleMediaEvent> CREATOR = new Parcelable.Creator<CircleMediaEvent>() {
        @Override
        public CircleMediaEvent createFromParcel(Parcel source) {
            return new CircleMediaEvent(source);
        }

        @Override
        public CircleMediaEvent[] newArray(int size) {
            return new CircleMediaEvent[size];
        }
    };
}
