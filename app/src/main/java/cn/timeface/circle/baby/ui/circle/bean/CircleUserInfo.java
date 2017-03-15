package cn.timeface.circle.baby.ui.circle.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import cn.timeface.circle.baby.support.api.models.base.BaseObj;

/**
 * 圈用户对象
 * Created by lidonglin on 2017/3/14.
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class CircleUserInfo extends BaseObj implements Parcelable {
    protected String circleAvatarUrl; //成长圈用户头像
    protected String circleNickName;  //成长圈用户昵称
    protected long circleId;          //成长圈id
    protected long circleUserId;      //成长圈用户id
    protected int circleUserType;     //成长圈用户类型  1-创建者 2-老师 3-普通成员

    public CircleUserInfo() {
    }

    protected CircleUserInfo(Parcel in) {
        super(in);
        circleAvatarUrl = in.readString();
        circleNickName = in.readString();
        circleId = in.readLong();
        circleUserId = in.readLong();
        circleUserType = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(circleAvatarUrl);
        dest.writeString(circleNickName);
        dest.writeLong(circleId);
        dest.writeLong(circleUserId);
        dest.writeInt(circleUserType);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CircleUserInfo> CREATOR = new Creator<CircleUserInfo>() {
        @Override
        public CircleUserInfo createFromParcel(Parcel in) {
            return new CircleUserInfo(in);
        }

        @Override
        public CircleUserInfo[] newArray(int size) {
            return new CircleUserInfo[size];
        }
    };

    public String getCircleAvatarUrl() {
        return circleAvatarUrl;
    }

    public void setCircleAvatarUrl(String circleAvatarUrl) {
        this.circleAvatarUrl = circleAvatarUrl;
    }

    public String getCircleNickName() {
        return circleNickName;
    }

    public void setCircleNickName(String circleNickName) {
        this.circleNickName = circleNickName;
    }

    public long getCircleId() {
        return circleId;
    }

    public void setCircleId(long circleId) {
        this.circleId = circleId;
    }

    public long getCircleUserId() {
        return circleUserId;
    }

    public void setCircleUserId(long circleUserId) {
        this.circleUserId = circleUserId;
    }

    public int getCircleUserType() {
        return circleUserType;
    }

    public void setCircleUserType(int circleUserType) {
        this.circleUserType = circleUserType;
    }
}
