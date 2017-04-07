package cn.timeface.circle.baby.ui.circle.bean;

import android.os.Parcel;
import android.os.Parcelable;

import cn.timeface.circle.baby.ui.circle.groupmembers.bean.CircleBabyBriefObj;

/**
 * Created by wangwei on 2017/4/5.
 */

public class MemberDataObj implements Parcelable {
    private CircleUserInfo userInfo;
    private CircleBabyBriefObj babyBrief;

    public CircleUserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(CircleUserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public CircleBabyBriefObj getBabyBrief() {
        return babyBrief;
    }

    public void setBabyBrief(CircleBabyBriefObj babyBrief) {
        this.babyBrief = babyBrief;
    }

    public MemberDataObj(CircleUserInfo userInfo, CircleBabyBriefObj babyBrief) {
        this.userInfo = userInfo;
        this.babyBrief = babyBrief;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.userInfo, flags);
        dest.writeParcelable(this.babyBrief, flags);
    }

    protected MemberDataObj(Parcel in) {
        this.userInfo = in.readParcelable(CircleUserInfo.class.getClassLoader());
        this.babyBrief = in.readParcelable(CircleBabyBriefObj.class.getClassLoader());
    }

    public static final Creator<MemberDataObj> CREATOR = new Creator<MemberDataObj>() {
        @Override
        public MemberDataObj createFromParcel(Parcel source) {
            return new MemberDataObj(source);
        }

        @Override
        public MemberDataObj[] newArray(int size) {
            return new MemberDataObj[size];
        }
    };
}
