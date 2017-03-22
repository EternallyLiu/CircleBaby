package cn.timeface.circle.baby.ui.circle.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import cn.timeface.circle.baby.support.api.models.base.BaseObj;

/**
 * Created by lidonglin on 2017/3/17.
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class CirclePhotoMonthObj extends BaseObj implements Parcelable {
    protected int mediaCount;           //该月中所有的图片
    protected String mediaUrl;          //该月第一张图片的url
    protected String month;             //月份
    protected String year;              //年

    public CirclePhotoMonthObj() {
    }

    public CirclePhotoMonthObj(int mediaCount, String year) {
        this.mediaCount = mediaCount;
        this.year = year;
    }

    public CirclePhotoMonthObj(int mediaCount, String mediaUrl, String month) {
        this.mediaCount = mediaCount;
        this.mediaUrl = mediaUrl;
        this.month = month;
    }

    public CirclePhotoMonthObj(Parcel in) {
        super(in);
        mediaCount = in.readInt();
        mediaUrl = in.readString();
        month = in.readString();
        year = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(mediaCount);
        dest.writeString(mediaUrl);
        dest.writeString(month);
        dest.writeString(year);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CirclePhotoMonthObj> CREATOR = new Creator<CirclePhotoMonthObj>() {
        @Override
        public CirclePhotoMonthObj createFromParcel(Parcel in) {
            return new CirclePhotoMonthObj(in);
        }

        @Override
        public CirclePhotoMonthObj[] newArray(int size) {
            return new CirclePhotoMonthObj[size];
        }
    };

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public int getMediaCount() {
        return mediaCount;
    }

    public void setMediaCount(int mediaCount) {
        this.mediaCount = mediaCount;
    }

    public static Creator<CirclePhotoMonthObj> getCREATOR() {
        return CREATOR;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }
}
