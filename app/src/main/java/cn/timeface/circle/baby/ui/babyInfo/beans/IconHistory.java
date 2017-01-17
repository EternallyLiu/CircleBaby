package cn.timeface.circle.baby.ui.babyInfo.beans;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import cn.timeface.circle.baby.BuildConfig;
import cn.timeface.circle.baby.support.api.models.base.BaseObj;

/**
 * Created by wangshuai on 2017/1/12.
 */

public class IconHistory extends BaseObj implements Parcelable {


    private String age;
    private String avatar;

    public IconHistory() {
    }

    public IconHistory(String age, String avatar) {

        this.age = age;
        this.avatar = avatar;
    }

    public String getAge() {

        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getAvatar() {
        if (!TextUtils.isEmpty(avatar) && !avatar.startsWith("http"))
            avatar = BuildConfig.API_URL + avatar;
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
        dest.writeString(this.age);
        dest.writeString(this.avatar);
    }

    protected IconHistory(Parcel in) {
        this.age = in.readString();
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
