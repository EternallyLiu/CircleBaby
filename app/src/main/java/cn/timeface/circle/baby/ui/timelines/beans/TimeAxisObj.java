package cn.timeface.circle.baby.ui.timelines.beans;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import cn.timeface.circle.baby.support.api.models.base.BaseObj;

/**
 * Created by wangshuai on 2017/1/9.
 */

public class TimeAxisObj extends BaseObj implements Parcelable {

    private List<MonthRecord> monthRecords;
    private int year;

    public List<MonthRecord> getMonthRecords() {
        return monthRecords;
    }

    public void setMonthRecords(List<MonthRecord> monthRecords) {
        this.monthRecords = monthRecords;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(this.monthRecords);
        dest.writeInt(this.year);
    }

    public TimeAxisObj() {
    }

    protected TimeAxisObj(Parcel in) {
        this.monthRecords = new ArrayList<MonthRecord>();
        in.readList(this.monthRecords, MonthRecord.class.getClassLoader());
        this.year = in.readInt();
    }

    public static final Parcelable.Creator<TimeAxisObj> CREATOR = new Parcelable.Creator<TimeAxisObj>() {
        @Override
        public TimeAxisObj createFromParcel(Parcel source) {
            return new TimeAxisObj(source);
        }

        @Override
        public TimeAxisObj[] newArray(int size) {
            return new TimeAxisObj[size];
        }
    };
}
