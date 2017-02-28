package cn.timeface.circle.baby.events;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import cn.timeface.circle.baby.support.api.models.objs.MediaObj;

/**
 * Created by lidonglin on 2016/6/22.
 */
public class TimeEditPhotoDeleteEvent implements Parcelable {
    public int position;
    public String url;
    private int allDetailsListPosition;
    private MediaObj mediaObj;

    public TimeEditPhotoDeleteEvent(int position, String url) {
        this.position = position;
        this.url = url;
    }

    public TimeEditPhotoDeleteEvent(MediaObj mediaObj, int allDetailsListPosition,int position) {
        this.mediaObj = mediaObj;
        this.position=position;
        this.allDetailsListPosition = allDetailsListPosition;
    }

    public int getAllDetailsListPosition() {

        return allDetailsListPosition;
    }

    public void setAllDetailsListPosition(int allDetailsListPosition) {
        this.allDetailsListPosition = allDetailsListPosition;
    }

    public TimeEditPhotoDeleteEvent(MediaObj mediaObj) {
        this.mediaObj = mediaObj;
    }

    public MediaObj getMediaObj() {

        return mediaObj;
    }

    public void setMediaObj(MediaObj mediaObj) {
        this.mediaObj = mediaObj;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getUrl() {
        if (TextUtils.isEmpty(url) && mediaObj != null)
            url = !TextUtils.isEmpty(mediaObj.getImgUrl()) ? mediaObj.getImgUrl() : mediaObj.getLocalPath();
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.position);
        dest.writeString(this.url);
        dest.writeInt(this.allDetailsListPosition);
        dest.writeParcelable(this.mediaObj, flags);
    }

    protected TimeEditPhotoDeleteEvent(Parcel in) {
        this.position = in.readInt();
        this.url = in.readString();
        this.allDetailsListPosition = in.readInt();
        this.mediaObj = in.readParcelable(MediaObj.class.getClassLoader());
    }

    public static final Creator<TimeEditPhotoDeleteEvent> CREATOR = new Creator<TimeEditPhotoDeleteEvent>() {
        @Override
        public TimeEditPhotoDeleteEvent createFromParcel(Parcel source) {
            return new TimeEditPhotoDeleteEvent(source);
        }

        @Override
        public TimeEditPhotoDeleteEvent[] newArray(int size) {
            return new TimeEditPhotoDeleteEvent[size];
        }
    };
}
