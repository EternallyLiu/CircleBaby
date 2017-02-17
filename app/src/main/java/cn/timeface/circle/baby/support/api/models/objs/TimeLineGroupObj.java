package cn.timeface.circle.baby.support.api.models.objs;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import cn.timeface.circle.baby.support.api.models.base.BaseObj;

/**
 * Created by lidonglin on 2016/5/31.
 */
public class TimeLineGroupObj extends BaseObj implements Cloneable, Parcelable {
    String age;                             //年龄描述
    String dateEx;                          //时间附属信息
    long date;                              //创建时间
    List<TimeLineObj> timeLineList;         //这天中的时光列表

    @Override
    public TimeLineGroupObj clone() throws CloneNotSupportedException {
        return (TimeLineGroupObj) super.clone();
    }

    public TimeLineGroupObj(String age, long date, String dateEx, List<TimeLineObj> timeLineList) {
        this.age = age;
        this.date = date;
        this.dateEx = dateEx;
        this.timeLineList = timeLineList;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getDateEx() {
        return dateEx;
    }

    public void setDateEx(String dateEx) {
        this.dateEx = dateEx;
    }

    public List<TimeLineObj> getTimeLineList() {
        if (timeLineList==null)timeLineList=new ArrayList<>(0);
        return timeLineList;
    }

    public void setTimeLineList(List<TimeLineObj> timeLineList) {
        this.timeLineList = timeLineList;
    }

    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TimeLineGroupObj that = (TimeLineGroupObj) o;

        if (date != that.date) return false;
        if (age != null ? !age.equals(that.age) : that.age != null) return false;
        if (dateEx != null ? !dateEx.equals(that.dateEx) : that.dateEx != null) return false;
        return timeLineList != null ? timeLineList.equals(that.timeLineList) : that.timeLineList == null;

    }

    @Override
    public int hashCode() {
        int result = age != null ? age.hashCode() : 0;
        result = 31 * result + (dateEx != null ? dateEx.hashCode() : 0);
        result = 31 * result + (int) (date ^ (date >>> 32));
        result = 31 * result + (timeLineList != null ? timeLineList.hashCode() : 0);
        return result;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.age);
        dest.writeString(this.dateEx);
        dest.writeLong(this.date);
        dest.writeTypedList(this.timeLineList);
    }

    protected TimeLineGroupObj(Parcel in) {
        this.age = in.readString();
        this.dateEx = in.readString();
        this.date = in.readLong();
        this.timeLineList = in.createTypedArrayList(TimeLineObj.CREATOR);
    }

    public static final Parcelable.Creator<TimeLineGroupObj> CREATOR = new Parcelable.Creator<TimeLineGroupObj>() {
        @Override
        public TimeLineGroupObj createFromParcel(Parcel source) {
            return new TimeLineGroupObj(source);
        }

        @Override
        public TimeLineGroupObj[] newArray(int size) {
            return new TimeLineGroupObj[size];
        }
    };
}
