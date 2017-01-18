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

    /**
     * 这个属性是为了做一个标记，在时间选择的时候判断是否被展开
     */
    private boolean isSelected=false;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
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

    public TimeAxisObj() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.monthRecords);
        dest.writeInt(this.year);
        dest.writeByte(this.isSelected ? (byte) 1 : (byte) 0);
    }

    protected TimeAxisObj(Parcel in) {
        this.monthRecords = in.createTypedArrayList(MonthRecord.CREATOR);
        this.year = in.readInt();
        this.isSelected = in.readByte() != 0;
    }

    public static final Creator<TimeAxisObj> CREATOR = new Creator<TimeAxisObj>() {
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
