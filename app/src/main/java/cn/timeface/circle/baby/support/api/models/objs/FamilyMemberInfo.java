package cn.timeface.circle.baby.support.api.models.objs;

import android.os.Parcel;
import android.os.Parcelable;

import cn.timeface.circle.baby.support.api.models.base.BaseObj;

/**
 * Created by lidonglin on 2016/5/16.
 */
public class FamilyMemberInfo extends BaseObj implements Parcelable {
    int piccount;
    int attention;
    int count;
    long time;
    UserObj userInfo;

    public int getPiccount() {
        return piccount;
    }

    public void setPiccount(int piccount) {
        this.piccount = piccount;
    }

    public int getAttention() {
        return attention;
    }

    public void setAttention(int attention) {
        this.attention = attention;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public UserObj getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserObj userInfo) {
        this.userInfo = userInfo;
    }

    public FamilyMemberInfo(int piccount, int attention, int count, long time, UserObj userInfo) {
        this.piccount = piccount;
        this.attention = attention;
        this.count = count;
        this.time = time;
        this.userInfo = userInfo;
    }

    public FamilyMemberInfo(UserObj userInfo) {
        this.userInfo = userInfo;
    }

    public FamilyMemberInfo() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FamilyMemberInfo that = (FamilyMemberInfo) o;

        if (piccount != that.piccount) return false;
        if (attention != that.attention) return false;
        if (count != that.count) return false;
        if (time != that.time) return false;
        return userInfo != null ? userInfo.equals(that.userInfo) : that.userInfo == null;

    }

    @Override
    public int hashCode() {
        int result = piccount;
        result = 31 * result + attention;
        result = 31 * result + count;
        result = 31 * result + (int) (time ^ (time >>> 32));
        result = 31 * result + (userInfo != null ? userInfo.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "FamilyMemberInfo{" +
                "piccount=" + piccount +
                ", attention=" + attention +
                ", count=" + count +
                ", time=" + time +
                ", userInfo=" + userInfo +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.piccount);
        dest.writeInt(this.attention);
        dest.writeInt(this.count);
        dest.writeLong(this.time);
        dest.writeParcelable(this.userInfo, flags);
    }

    protected FamilyMemberInfo(Parcel in) {
        this.piccount = in.readInt();
        this.attention = in.readInt();
        this.count = in.readInt();
        this.time = in.readLong();
        this.userInfo = in.readParcelable(UserObj.class.getClassLoader());
    }

    public static final Parcelable.Creator<FamilyMemberInfo> CREATOR = new Parcelable.Creator<FamilyMemberInfo>() {
        @Override
        public FamilyMemberInfo createFromParcel(Parcel source) {
            return new FamilyMemberInfo(source);
        }

        @Override
        public FamilyMemberInfo[] newArray(int size) {
            return new FamilyMemberInfo[size];
        }
    };
}
