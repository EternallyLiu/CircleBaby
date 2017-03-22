package cn.timeface.circle.baby.ui.circle.timelines.bean;

import android.os.Parcel;
import android.os.Parcelable;

import cn.timeface.circle.baby.ui.circle.bean.CircleUserInfo;

/**
 * author : wangshuai Created on 2017/3/21
 * email : wangs1992321@gmail.com
 */
public class TitleBean implements Parcelable {

    private String title;
    private CircleUserInfo userInfo;

    public TitleBean(String title, CircleUserInfo userInfo) {

        this.title = title;
        this.userInfo = userInfo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public CircleUserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(CircleUserInfo userInfo) {
        this.userInfo = userInfo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeParcelable(this.userInfo, flags);
    }

    public TitleBean() {
    }

    protected TitleBean(Parcel in) {
        this.title = in.readString();
        this.userInfo = in.readParcelable(CircleUserInfo.class.getClassLoader());
    }

    public static final Parcelable.Creator<TitleBean> CREATOR = new Parcelable.Creator<TitleBean>() {
        @Override
        public TitleBean createFromParcel(Parcel source) {
            return new TitleBean(source);
        }

        @Override
        public TitleBean[] newArray(int size) {
            return new TitleBean[size];
        }
    };
}
