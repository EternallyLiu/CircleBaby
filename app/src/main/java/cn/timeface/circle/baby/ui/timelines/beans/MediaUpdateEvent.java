package cn.timeface.circle.baby.ui.timelines.beans;

import android.os.Parcel;
import android.os.Parcelable;

import cn.timeface.circle.baby.support.api.models.objs.MediaObj;

/**
 * author : wangshuai Created on 2017/2/4
 * email : wangs1992321@gmail.com
 */
public class MediaUpdateEvent implements Parcelable {

    private int allDetailsListPosition=-1;
    private MediaObj mediaObj;

    public int getAllDetailsListPosition() {
        return allDetailsListPosition;
    }

    public void setAllDetailsListPosition(int allDetailsListPosition) {
        this.allDetailsListPosition = allDetailsListPosition;
    }

    public MediaObj getMediaObj() {
        return mediaObj;
    }

    private int index;

    public MediaUpdateEvent(MediaObj mediaObj, int index) {
        this.mediaObj = mediaObj;
        this.index = index;
    }

    public int getIndex() {

        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setMediaObj(MediaObj mediaObj) {
        this.mediaObj = mediaObj;
    }

    public MediaUpdateEvent(int allDetailsListPosition, MediaObj mediaObj) {
        this.allDetailsListPosition = allDetailsListPosition;
        this.mediaObj = mediaObj;
    }

    public MediaUpdateEvent(int index) {
        this.index = index;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.allDetailsListPosition);
        dest.writeParcelable(this.mediaObj, flags);
        dest.writeInt(this.index);
    }

    protected MediaUpdateEvent(Parcel in) {
        this.allDetailsListPosition = in.readInt();
        this.mediaObj = in.readParcelable(MediaObj.class.getClassLoader());
        this.index = in.readInt();
    }

    public static final Creator<MediaUpdateEvent> CREATOR = new Creator<MediaUpdateEvent>() {
        @Override
        public MediaUpdateEvent createFromParcel(Parcel source) {
            return new MediaUpdateEvent(source);
        }

        @Override
        public MediaUpdateEvent[] newArray(int size) {
            return new MediaUpdateEvent[size];
        }
    };
}
