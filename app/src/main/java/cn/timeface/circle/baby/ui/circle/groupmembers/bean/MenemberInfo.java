package cn.timeface.circle.baby.ui.circle.groupmembers.bean;

import android.os.Parcel;
import android.os.Parcelable;

import cn.timeface.circle.baby.ui.circle.bean.CircleUserInfo;

/**
 * Created by wangwei on 2017/3/20.
 */

public class MenemberInfo implements Parcelable {
    /**
     * babyAvatarUrl	该用户关联的宝宝头像	string
     * babyName	关联的宝宝姓名	string
     * leaveMessage	留言信息	string
     * userInfo	用户对象	object	CircleUserInfo
     */
    private CircleBabyBriefObj babyBrief;
    private String leaveMessage;
    private CircleUserInfo userInfo;

    public MenemberInfo(CircleBabyBriefObj babyBrief, String leaveMessage, CircleUserInfo userInfo) {
        this.babyBrief = babyBrief;
        this.leaveMessage = leaveMessage;
        this.userInfo = userInfo;
    }

    public CircleBabyBriefObj getBabyBrief() {
        return babyBrief;
    }

    public void setBabyBrief(CircleBabyBriefObj babyBrief) {
        this.babyBrief = babyBrief;
    }

    public String getLeaveMessage() {
        return leaveMessage;
    }

    public void setLeaveMessage(String leaveMessage) {
        this.leaveMessage = leaveMessage;
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
        dest.writeParcelable(this.babyBrief, flags);
        dest.writeString(this.leaveMessage);
        dest.writeParcelable(this.userInfo, flags);
    }

    protected MenemberInfo(Parcel in) {
        this.babyBrief = in.readParcelable(CircleBabyBriefObj.class.getClassLoader());
        this.leaveMessage = in.readString();
        this.userInfo = in.readParcelable(CircleUserInfo.class.getClassLoader());
    }

    public static final Creator<MenemberInfo> CREATOR = new Creator<MenemberInfo>() {
        @Override
        public MenemberInfo createFromParcel(Parcel source) {
            return new MenemberInfo(source);
        }

        @Override
        public MenemberInfo[] newArray(int size) {
            return new MenemberInfo[size];
        }
    };
}
