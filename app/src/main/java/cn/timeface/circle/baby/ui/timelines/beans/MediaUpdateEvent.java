package cn.timeface.circle.baby.ui.timelines.beans;

import android.os.Parcel;
import android.os.Parcelable;

import cn.timeface.circle.baby.support.api.models.objs.MediaObj;

/**
 * author : wangshuai Created on 2017/2/4
 * email : wangs1992321@gmail.com
 */
public class MediaUpdateEvent implements Parcelable {

    private int allDetailsListPosition;
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

    public void setMediaObj(MediaObj mediaObj) {
        this.mediaObj = mediaObj;
    }

    public MediaUpdateEvent(int allDetailsListPosition, MediaObj mediaObj) {
        this.allDetailsListPosition = allDetailsListPosition;
        this.mediaObj = mediaObj;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.allDetailsListPosition);
        dest.writeParcelable(this.mediaObj, flags);
    }

    protected MediaUpdateEvent(Parcel in) {
        this.allDetailsListPosition = in.readInt();
        this.mediaObj = in.readParcelable(MediaObj.class.getClassLoader());
    }

    public static final Parcelable.Creator<MediaUpdateEvent> CREATOR = new Parcelable.Creator<MediaUpdateEvent>() {
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
