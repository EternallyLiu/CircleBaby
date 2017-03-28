package cn.timeface.circle.baby.ui.circle.timelines.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import cn.timeface.circle.baby.support.api.models.base.BaseObj;
import cn.timeface.circle.baby.ui.circle.bean.CircleUserInfo;

/**
 * author : wangshuai Created on 2017/3/21
 * email : wangs1992321@gmail.com
 */
public class TimeLikeUserList extends BaseObj implements Parcelable {

    private List<CircleUserInfo> userInfos = new ArrayList<>(9);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TimeLikeUserList that = (TimeLikeUserList) o;

        if (getBaseType() == that.getBaseType()) return true;
        return false;
    }


    public List<CircleUserInfo> getUserInfos() {
        if (userInfos == null) userInfos = new ArrayList<>(0);
        return userInfos;
    }

    public void setUserInfos(List<CircleUserInfo> userInfos) {
        this.userInfos = userInfos;
    }

    public TimeLikeUserList(List<CircleUserInfo> userInfos) {
        this.userInfos = userInfos;
        setBaseType(-10010);
    }

    public TimeLikeUserList() {
        setBaseType(-10010);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeTypedList(this.userInfos);
    }

    protected TimeLikeUserList(Parcel in) {
        super(in);
        this.userInfos = in.createTypedArrayList(CircleUserInfo.CREATOR);
    }

    public static final Creator<TimeLikeUserList> CREATOR = new Creator<TimeLikeUserList>() {
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
