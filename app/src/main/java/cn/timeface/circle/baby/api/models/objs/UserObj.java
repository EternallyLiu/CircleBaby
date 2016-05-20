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


    protected UserObj(Parcel in) {
        avatar = in.readString();
        nickName = in.readString();
        userId = in.readString();
        relationName = in.readString();
        isCreator = in.readInt();
    }

    public static final Creator<UserObj> CREATOR = new Creator<UserObj>() {
        @Override
        public UserObj createFromParcel(Parcel in) {
            return new UserObj(in);
        }

        @Override
        public UserObj[] newArray(int size) {
            return new UserObj[size];
        }
    };

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
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userId);
        dest.writeString(this.nickName);
        dest.writeString(this.avatar);
        dest.writeSerializable(this.babyObj);
        dest.writeString(this.relationName);
        dest.writeInt(this.isCreator);
    }
}
