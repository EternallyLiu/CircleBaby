package cn.timeface.circle.baby.ui.circle.timelines.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import cn.timeface.circle.baby.ui.circle.bean.CircleUserInfo;

/**
 * author : wangshuai Created on 2017/3/21
 * email : wangs1992321@gmail.com
 */
public class TimeLikeUserList implements Parcelable {

    private List<CircleUserInfo> userInfos=new ArrayList<>(9);

    public List<CircleUserInfo> getUserInfos() {
        return userInfos;
    }

    public void setUserInfos(List<CircleUserInfo> userInfos) {
        this.userInfos = userInfos;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.userInfos);
    }

    public TimeLikeUserList(List<CircleUserInfo> userInfos) {
        this.userInfos = userInfos;
    }

    public TimeLikeUserList() {
    }

    protected TimeLikeUserList(Parcel in) {
        this.userInfos = in.createTypedArrayList(CircleUserInfo.CREATOR);
    }

    public static final Parcelable.Creator<TimeLikeUserList> CREATOR = new Parcelable.Creator<TimeLikeUserList>() {
        @Override
        public TimeLikeUserList createFromParcel(Parcel source) {
            return new TimeLikeUserList(source);
        }

        @Override
        public TimeLikeUserList[] newArray(int size) {
            return new TimeLikeUserList[size];
        }
    };
}
