package cn.timeface.circle.baby.ui.circle.timelines.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * author : wangshuai Created on 2017/3/20
 * email : wangs1992321@gmail.com
 */
public class CircleBabyObj implements Parcelable {

    private String babyAvatarUrl;
    private long babyId;
    private String babyName;
    private long selectUserId;

    public String getBabyAvatarUrl() {
        return babyAvatarUrl;
    }

    public void setBabyAvatarUrl(String babyAvatarUrl) {
        this.babyAvatarUrl = babyAvatarUrl;
    }

    public long getBabyId() {
        return babyId;
    }

    public void setBabyId(long babyId) {
        this.babyId = babyId;
    }

    public String getBabyName() {
        return babyName;
    }

    public void setBabyName(String babyName) {
        this.babyName = babyName;
    }

    public long getSelectUserId() {
        return selectUserId;
    }

    public void setSelectUserId(long selectUserId) {
        this.selectUserId = selectUserId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.babyAvatarUrl);
        dest.writeLong(this.babyId);
        dest.writeString(this.babyName);
        dest.writeLong(this.selectUserId);
    }

    public CircleBabyObj() {
    }

    protected CircleBabyObj(Parcel in) {
        this.babyAvatarUrl = in.readString();
        this.babyId = in.readLong();
        this.babyName = in.readString();
        this.selectUserId = in.readLong();
    }

    public static final Parcelable.Creator<CircleBabyObj> CREATOR = new Parcelable.Creator<CircleBabyObj>() {
        @Override
        public CircleBabyObj createFromParcel(Parcel source) {
            return new CircleBabyObj(source);
        }

        @Override
        public CircleBabyObj[] newArray(int size) {
            return new CircleBabyObj[size];
        }
    };
}
