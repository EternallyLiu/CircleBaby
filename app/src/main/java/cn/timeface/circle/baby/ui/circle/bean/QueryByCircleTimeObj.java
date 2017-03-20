package cn.timeface.circle.baby.ui.circle.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.List;

import cn.timeface.circle.baby.support.api.models.base.BaseObj;

/**
 * Created by lidonglin on 2017/3/17.
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class QueryByCircleTimeObj extends BaseObj implements Parcelable {
    protected int mediaCount;                       //这一年中的所有图片
    protected List<CirclePhotoMonthObj> monthList;
    protected String year;                          //年份

    public QueryByCircleTimeObj() {
    }

    public QueryByCircleTimeObj(int mediaCount, List<CirclePhotoMonthObj> monthList, String year) {
        this.mediaCount = mediaCount;
        this.monthList = monthList;
        this.year = year;
    }

    public QueryByCircleTimeObj(Parcel in) {
        super(in);
        mediaCount = in.readInt();
        monthList = in.createTypedArrayList(CirclePhotoMonthObj.CREATOR);
        year = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(mediaCount);
        dest.writeTypedList(monthList);
        dest.writeString(year);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<QueryByCircleTimeObj> CREATOR = new Creator<QueryByCircleTimeObj>() {
        @Override
        public QueryByCircleTimeObj createFromParcel(Parcel in) {
            return new QueryByCircleTimeObj(in);
        }

        @Override
        public QueryByCircleTimeObj[] newArray(int size) {
            return new QueryByCircleTimeObj[size];
        }
    };

    public List<CirclePhotoMonthObj> getMonthList() {
        return monthList;
    }

    public void setMonthList(List<CirclePhotoMonthObj> monthList) {
        this.monthList = monthList;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public int getMediaCount() {
        return mediaCount;
    }

    public void setMediaCount(int mediaCount) {
        this.mediaCount = mediaCount;
    }

    public static Creator<QueryByCircleTimeObj> getCREATOR() {
        return CREATOR;
    }
}
