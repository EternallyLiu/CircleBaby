package cn.timeface.circle.baby.ui.circle.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonObject;

/**
 * 圈成员列表对象
 * Created by lidonglin on 2017/3/18.
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class CircleMemberObj extends CircleContentObj implements Parcelable {
    protected String babyAvatarUrl;      //该用户关联的宝宝头像s
    protected CircleUserInfo userInfo;   //用户对象

    public CircleMemberObj() {
    }

    protected CircleMemberObj(Parcel in) {
        super(in);
        babyAvatarUrl = in.readString();
        userInfo = in.readParcelable(CircleUserInfo.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(babyAvatarUrl);
        dest.writeParcelable(userInfo, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CircleMemberObj> CREATOR = new Creator<CircleMemberObj>() {
        @Override
        public CircleMemberObj createFromParcel(Parcel in) {
            return new CircleMemberObj(in);
        }

        @Override
        public CircleMemberObj[] newArray(int size) {
            return new CircleMemberObj[size];
        }
    };

    public CircleUserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(CircleUserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public String getBabyAvatarUrl() {
        return babyAvatarUrl;
    }

    public void setBabyAvatarUrl(String babyAvatarUrl) {
        this.babyAvatarUrl = babyAvatarUrl;
    }
}
