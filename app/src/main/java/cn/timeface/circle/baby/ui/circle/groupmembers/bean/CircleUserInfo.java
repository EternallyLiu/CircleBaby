package cn.timeface.circle.baby.ui.circle.groupmembers.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by wangwei on 2017/3/20.
 */

public class CircleUserInfo implements Parcelable {
    /**
     * circleAvatarUrl	成长圈用户头像	string
     circleId	成长圈id	number
     circleNickName	成长圈用户昵称	string
     circleUserId	成长圈用户id	number
     circleUserType	成长圈用户类型	number	1-创建者 2-老师 3-普通成员
     */
    private String circleAvatarUrl;
    private int circleId;
    private String circleNickName;
    private int circleUserId;
    private int circleUserType;

    public String getCircleAvatarUrl() {
        return circleAvatarUrl;
    }

    public void setCircleAvatarUrl(String circleAvatarUrl) {
        this.circleAvatarUrl = circleAvatarUrl;
    }

    public int getCircleId() {
        return circleId;
    }

    public void setCircleId(int circleId) {
        this.circleId = circleId;
    }

    public String getCircleNickName() {
        return circleNickName;
    }

    public void setCircleNickName(String circleNickName) {
        this.circleNickName = circleNickName;
    }

    public int getCircleUserId() {
        return circleUserId;
    }

    public void setCircleUserId(int circleUserId) {
        this.circleUserId = circleUserId;
    }

    public int getCircleUserType() {
        return circleUserType;
    }

    public void setCircleUserType(int circleUserType) {
        this.circleUserType = circleUserType;
    }

    public CircleUserInfo(String circleAvatarUrl, int circleId, String circleNickName, int circleUserId, int circleUserType) {
        this.circleAvatarUrl = circleAvatarUrl;
        this.circleId = circleId;

        this.circleNickName = circleNickName;
        this.circleUserId = circleUserId;
        this.circleUserType = circleUserType;
    }

    public CircleUserInfo(String circleAvatarUrl, String circleNickName, int circleUserType) {
        this.circleAvatarUrl = circleAvatarUrl;
        this.circleNickName = circleNickName;
        this.circleUserType = circleUserType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.circleAvatarUrl);
        dest.writeInt(this.circleId);
        dest.writeString(this.circleNickName);
        dest.writeInt(this.circleUserId);
        dest.writeInt(this.circleUserType);
    }

    protected CircleUserInfo(Parcel in) {
        this.circleAvatarUrl = in.readString();
        this.circleId = in.readInt();
        this.circleNickName = in.readString();
        this.circleUserId = in.readInt();
        this.circleUserType = in.readInt();
    }

    public static final Parcelable.Creator<CircleUserInfo> CREATOR = new Parcelable.Creator<CircleUserInfo>() {
        @Override
        public CircleUserInfo createFromParcel(Parcel source) {
            return new CircleUserInfo(source);
        }

        @Override
        public CircleUserInfo[] newArray(int size) {
            return new CircleUserInfo[size];
        }
    };
}
