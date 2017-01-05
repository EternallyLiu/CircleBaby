package cn.timeface.circle.baby.support.api.models.objs;

import android.os.Parcel;
import android.os.Parcelable;

import cn.timeface.circle.baby.support.api.models.base.BaseObj;

/**
 * Created by lidonglin on 2016/4/28.
 */
public class BabyObj extends BaseObj implements Parcelable {
    String age;
    String avatar;
    int babyId;
    long bithday;
    String blood;
    String constellation;
    int gender;
    String name;

    public BabyObj() {
    }

    public BabyObj(String age, String avatar, int babyId, long bithday, String blood, String constellation, int gender, String name) {
        this.age = age;
        this.avatar = avatar;
        this.babyId = babyId;
        this.bithday = bithday;
        this.blood = blood;
        this.constellation = constellation;
        this.gender = gender;
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getBabyId() {
        return babyId;
    }

    public void setBabyId(int babyId) {
        this.babyId = babyId;
    }

    public long getBithday() {
        return bithday;
    }

    public void setBithday(long bithday) {
        this.bithday = bithday;
    }

    public String getBlood() {
        return blood;
    }

    public void setBlood(String blood) {
        this.blood = blood;
    }

    public String getConstellation() {
        return constellation;
    }

    public void setConstellation(String constellation) {
        this.constellation = constellation;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "BabyObj{" +
                "age='" + age + '\'' +
                ", avatar='" + avatar + '\'' +
                ", babyId=" + babyId +
                ", bithday=" + bithday +
                ", blood='" + blood + '\'' +
                ", constellation='" + constellation + '\'' +
                ", gender=" + gender +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.age);
        dest.writeString(this.avatar);
        dest.writeInt(this.babyId);
        dest.writeLong(this.bithday);
        dest.writeString(this.blood);
        dest.writeString(this.constellation);
        dest.writeInt(this.gender);
        dest.writeString(this.name);
    }

    protected BabyObj(Parcel in) {
        this.age = in.readString();
        this.avatar = in.readString();
        this.babyId = in.readInt();
        this.bithday = in.readLong();
        this.blood = in.readString();
        this.constellation = in.readString();
        this.gender = in.readInt();
        this.name = in.readString();
    }

    public static final Parcelable.Creator<BabyObj> CREATOR = new Parcelable.Creator<BabyObj>() {
        @Override
        public BabyObj createFromParcel(Parcel source) {
            return new BabyObj(source);
        }

        @Override
        public BabyObj[] newArray(int size) {
            return new BabyObj[size];
        }
    };
}
