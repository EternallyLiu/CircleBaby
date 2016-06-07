package cn.timeface.circle.baby.api.models.objs;

import android.os.Parcel;
import android.os.Parcelable;

import cn.timeface.circle.baby.api.models.base.BaseObj;

/**
 * author: rayboot  Created on 15/12/3.
 * email : sy0725work@gmail.com
 */
public class UserObj extends BaseObj implements Parcelable {
    String avatar;
    BabyObj babyObj;
    String nickName;
    String userId;
    String relationName;
    int isCreator;

    public UserObj() {
    }

    public UserObj(String avatar, BabyObj babyObj, String nickName, String userId, String relationName, int isCreator) {
        this.avatar = avatar;
        this.babyObj = babyObj;
        this.nickName = nickName;
        this.userId = userId;
        this.relationName = relationName;
        this.isCreator = isCreator;
    }


    public String getRelationName() {
        return relationName;
    }

    public void setRelationName(String relationName) {
        this.relationName = relationName;
    }

    public int getIsCreator() {
        return isCreator;
    }

    public void setIsCreator(int isCreator) {
        this.isCreator = isCreator;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public BabyObj getBabyObj() {
        return babyObj;
    }

    public void setBabyObj(BabyObj babyObj) {
        this.babyObj = babyObj;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    @Override
    public String toString() {
        return "UserObj{" +
                "avatar='" + avatar + '\'' +
                ", babyObj=" + babyObj +
                ", nickName='" + nickName + '\'' +
                ", userId='" + userId + '\'' +
                ", relationName='" + relationName + '\'' +
                ", isCreator=" + isCreator +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.avatar);
        dest.writeParcelable(this.babyObj, flags);
        dest.writeString(this.nickName);
        dest.writeString(this.userId);
        dest.writeString(this.relationName);
        dest.writeInt(this.isCreator);
    }

    protected UserObj(Parcel in) {
        this.avatar = in.readString();
        this.babyObj = in.readParcelable(BabyObj.class.getClassLoader());
        this.nickName = in.readString();
        this.userId = in.readString();
        this.relationName = in.readString();
        this.isCreator = in.readInt();
    }

    public static final Parcelable.Creator<UserObj> CREATOR = new Parcelable.Creator<UserObj>() {
        @Override
        public UserObj createFromParcel(Parcel source) {
            return new UserObj(source);
        }

        @Override
        public UserObj[] newArray(int size) {
            return new UserObj[size];
        }
    };
}
