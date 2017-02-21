package cn.timeface.circle.baby.support.api.models.objs;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import cn.timeface.circle.baby.support.api.models.base.BaseObj;

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
    String phoneNumber;
    String uniId;
    int babycount;
    int sendMessage;
    List<BabyObj> babies;

    public UserObj() {
    }

    public UserObj(String avatar, BabyObj babyObj, String nickName, String userId, String relationName, int isCreator, String phoneNumber, String uniId,int sendMessage) {
        this.avatar = avatar;
        this.babyObj = babyObj;
        this.nickName = nickName;
        this.userId = userId;
        this.relationName = relationName;
        this.isCreator = isCreator;
        this.phoneNumber = phoneNumber;
        this.uniId = uniId;
        this.sendMessage=sendMessage;
    }

    public int getSendMessage() {
        return sendMessage;
    }

    public void setSendMessage(int sendMessage) {
        this.sendMessage = sendMessage;
    }

    public UserObj(String avatar, BabyObj babyObj, String nickName, String userId, String relationName, int isCreator, String phoneNumber, String uniId, int babycount, int sendMessage, List<BabyObj> babies) {

        this.avatar = avatar;
        this.babyObj = babyObj;
        this.nickName = nickName;
        this.userId = userId;
        this.relationName = relationName;
        this.isCreator = isCreator;
        this.phoneNumber = phoneNumber;
        this.uniId = uniId;
        this.babycount = babycount;
        this.sendMessage = sendMessage;
        this.babies = babies;
    }

    public UserObj(String relationName) {
        this.relationName = relationName;
    }

    public int getBabycount() {
        return babycount;
    }

    public void setBabycount(int babycount) {
        this.babycount = babycount;
    }

    public List<BabyObj> getBabies() {
        return babies;
    }

    public void setBabies(List<BabyObj> babies) {
        this.babies = babies;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUniId() {
        return uniId;
    }

    public void setUniId(String uniId) {
        this.uniId = uniId;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserObj userObj = (UserObj) o;

        if (userId != null ? !userId.equals(userObj.userId) : userObj.userId != null) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = avatar != null ? avatar.hashCode() : 0;
        result = 31 * result + (babyObj != null ? babyObj.hashCode() : 0);
        result = 31 * result + (nickName != null ? nickName.hashCode() : 0);
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (relationName != null ? relationName.hashCode() : 0);
        result = 31 * result + isCreator;
        result = 31 * result + (phoneNumber != null ? phoneNumber.hashCode() : 0);
        result = 31 * result + (uniId != null ? uniId.hashCode() : 0);
        result = 31 * result + babycount;
        result = 31 * result + (babies != null ? babies.hashCode() : 0);
        return result;
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
        dest.writeString(this.phoneNumber);
        dest.writeString(this.uniId);
        dest.writeInt(this.babycount);
        dest.writeInt(this.sendMessage);
        dest.writeTypedList(this.babies);
    }

    protected UserObj(Parcel in) {
        this.avatar = in.readString();
        this.babyObj = in.readParcelable(BabyObj.class.getClassLoader());
        this.nickName = in.readString();
        this.userId = in.readString();
        this.relationName = in.readString();
        this.isCreator = in.readInt();
        this.phoneNumber = in.readString();
        this.uniId = in.readString();
        this.babycount = in.readInt();
        this.sendMessage = in.readInt();
        this.babies = in.createTypedArrayList(BabyObj.CREATOR);
    }

    public static final Creator<UserObj> CREATOR = new Creator<UserObj>() {
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
