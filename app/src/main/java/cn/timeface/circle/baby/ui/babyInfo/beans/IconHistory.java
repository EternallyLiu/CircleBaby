package cn.timeface.circle.baby.ui.babyInfo.beans;

import android.os.Parcel;
import android.os.Parcelable;

import cn.timeface.circle.baby.support.api.models.base.BaseObj;

/**
 * Created by wangshuai on 2017/1/12.
 */

public class IconHistory extends BaseObj implements Parcelable {

    private int age;
    private String avatar;

    public IconHistory() {
    }

    public IconHistory(int age, String avatar) {

        this.age = age;
        this.avatar = avatar;
    }

    public int getAge() {

        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.age);
        dest.writeString(this.avatar);
    }

    protected IconHistory(Parcel in) {
        this.age = in.readInt();
        this.avatar = in.readString();
    }

    public static final Parcelable.Creator<IconHistory> CREATOR = new Parcelable.Creator<IconHistory>() {
        @Override
        public IconHistory createFromParcel(Parcel source) {
            return new IconHistory(source);
        }

        @Override
        public IconHistory[] newArray(int size) {
            return new IconHistory[size];
        }
    };
}
