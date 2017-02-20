package cn.timeface.circle.baby.ui.timelines.beans;

import android.os.Parcel;
import android.os.Parcelable;

import cn.timeface.circle.baby.support.api.models.base.BaseObj;

/**
 * author : wangshuai Created on 2017/2/16
 * email : wangs1992321@gmail.com
 */
public class TimeGroupSimpleBean extends BaseObj implements Parcelable {

    private String age;                             //年龄描述
    private String dateEx;                          //时间附属信息
    private long date;
    private int allDetailsListPosition;

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getDateEx() {
        return dateEx;
    }

    public void setDateEx(String dateEx) {
        this.dateEx = dateEx;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getAllDetailsListPosition() {
        return allDetailsListPosition;
    }

    public void setAllDetailsListPosition(int allDetailsListPosition) {
        this.allDetailsListPosition = allDetailsListPosition;
    }

    public TimeGroupSimpleBean(String age, String dateEx, long date) {

        this.age = age;
        this.dateEx = dateEx;
        this.date = date;
        setBaseType(1);
    }

    public TimeGroupSimpleBean(String age, String dateEx, long date, int allDetailsListPosition) {

        this.age = age;
        this.dateEx = dateEx;
        this.date = date;
        this.allDetailsListPosition = allDetailsListPosition;
        setBaseType(1);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        TimeGroupSimpleBean bean = (TimeGroupSimpleBean) object;

        if (date != bean.date) return false;
        if (allDetailsListPosition != bean.allDetailsListPosition) return false;
        if (age != null ? !age.equals(bean.age) : bean.age != null) return false;
        return dateEx != null ? dateEx.equals(bean.dateEx) : bean.dateEx == null;

    }

    @Override
    public int hashCode() {
        int result = age != null ? age.hashCode() : 0;
        result = 31 * result + (dateEx != null ? dateEx.hashCode() : 0);
        result = 31 * result + (int) (date ^ (date >>> 32));
        result = 31 * result + allDetailsListPosition;
        return result;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.age);
        dest.writeString(this.dateEx);
        dest.writeLong(this.date);
        dest.writeInt(this.allDetailsListPosition);
    }

    protected TimeGroupSimpleBean(Parcel in) {
        this.age = in.readString();
        this.dateEx = in.readString();
        this.date = in.readLong();
        this.allDetailsListPosition = in.readInt();
    }

    public static final Parcelable.Creator<TimeGroupSimpleBean> CREATOR = new Parcelable.Creator<TimeGroupSimpleBean>() {
        @Override
        public TimeGroupSimpleBean createFromParcel(Parcel source) {
            return new TimeGroupSimpleBean(source);
        }

        @Override
        public TimeGroupSimpleBean[] newArray(int size) {
            return new TimeGroupSimpleBean[size];
        }
    };
}
