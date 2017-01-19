package cn.timeface.circle.baby.support.api.models.objs;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import cn.timeface.circle.baby.support.api.models.base.BaseObj;

/**
 * media tip obj
 * author : YW.SUN Created on 2017/1/12
 * email : sunyw10@gmail.com
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class MediaTipObj extends BaseObj implements Parcelable {
    private long tipId;//标签id
    private String tipName;//标签名

    public long getTipId() {
        return tipId;
    }

    public void setTipId(long tipId) {
        this.tipId = tipId;
    }

    public String getTipName() {
        return tipName;
    }

    public void setTipName(String tipName) {
        this.tipName = tipName;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.tipId);
        dest.writeString(this.tipName);
    }

    public MediaTipObj() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MediaTipObj tipObj = (MediaTipObj) o;

        if (tipId != tipObj.tipId) return false;
        return tipName != null ? tipName.equals(tipObj.tipName) : tipObj.tipName == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (tipId ^ (tipId >>> 32));
        result = 31 * result + (tipName != null ? tipName.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "MediaTipObj{" +
                "tipId=" + tipId +
                ", tipName='" + tipName + '\'' +
                '}';
    }

    protected MediaTipObj(Parcel in) {
        this.tipId = in.readLong();
        this.tipName = in.readString();
    }

    public MediaTipObj(String tipName) {
        this.tipName = tipName;
    }

    public static final Parcelable.Creator<MediaTipObj> CREATOR = new Parcelable.Creator<MediaTipObj>() {
        @Override
        public MediaTipObj createFromParcel(Parcel source) {
            return new MediaTipObj(source);
        }

        @Override
        public MediaTipObj[] newArray(int size) {
            return new MediaTipObj[size];
        }
    };
}
