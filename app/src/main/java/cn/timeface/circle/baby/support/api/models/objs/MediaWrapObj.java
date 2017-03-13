package cn.timeface.circle.baby.support.api.models.objs;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.List;

/**
 * media wrap obj
 * author : YW.SUN Created on 2017/2/14
 * email : sunyw10@gmail.com
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class MediaWrapObj implements Parcelable {
    private String date;
    private String address;
    private List<MediaObj> mediaList;
    private int mediaCount;
    private MediaTipObj tip;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<MediaObj> getMediaList() {
        return mediaList;
    }

    public void setMediaList(List<MediaObj> mediaList) {
        this.mediaList = mediaList;
    }

    public int getMediaCount() {
        return mediaCount;
    }

    public void setMediaCount(int mediaCount) {
        this.mediaCount = mediaCount;
    }

    public MediaTipObj getTip() {
        return tip;
    }

    public void setTip(MediaTipObj tip) {
        this.tip = tip;
    }

    public MediaWrapObj() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.date);
        dest.writeString(this.address);
        dest.writeTypedList(this.mediaList);
        dest.writeInt(this.mediaCount);
        dest.writeParcelable(this.tip, flags);
    }

    protected MediaWrapObj(Parcel in) {
        this.date = in.readString();
        this.address = in.readString();
        this.mediaList = in.createTypedArrayList(MediaObj.CREATOR);
        this.mediaCount = in.readInt();
        this.tip = in.readParcelable(MediaTipObj.class.getClassLoader());
    }

    public static final Creator<MediaWrapObj> CREATOR = new Creator<MediaWrapObj>() {
        @Override
        public MediaWrapObj createFromParcel(Parcel source) {
            return new MediaWrapObj(source);
        }

        @Override
        public MediaWrapObj[] newArray(int size) {
            return new MediaWrapObj[size];
        }
    };
}
