package cn.timeface.circle.baby.ui.circle.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import cn.timeface.circle.baby.support.api.models.base.BaseObj;

/**
 * 获取圈内的所有宝宝对象
 * Created by lidonglin on 2017/3/17.
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class GetCircleAllBabyObj extends BaseObj implements Parcelable {
    protected String babyAvatarUrl;
    protected String babyName;
    private long selectUserId;          //圈中宝宝的用户ID
    private long circleBabyId;
    private int isCurrUserBaby;
    private int selected;

    public GetCircleAllBabyObj() {
    }

    public GetCircleAllBabyObj(String babyAvatarUrl, String babyName, long circleBabyId, int selected) {
        this.babyAvatarUrl = babyAvatarUrl;
        this.babyName = babyName;
        this.circleBabyId = circleBabyId;
        this.selected = selected;
    }

    public long getCircleBabyId() {
        return circleBabyId;
    }

    public void setCircleBabyId(long circleBabyId) {
        this.circleBabyId = circleBabyId;
    }

    public int getIsCurrUserBaby() {
        return isCurrUserBaby;
    }

    public void setIsCurrUserBaby(int isCurrUserBaby) {
        this.isCurrUserBaby = isCurrUserBaby;
    }

    public int getSelected() {
        return selected;
    }

    public void setSelected(int selected) {
        this.selected = selected;
    }

    public String getBabyAvatarUrl() {
        return babyAvatarUrl;
    }

    public void setBabyAvatarUrl(String babyAvatarUrl) {
        this.babyAvatarUrl = babyAvatarUrl;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GetCircleAllBabyObj that = (GetCircleAllBabyObj) o;

        if (getBaseType() == that.getBaseType() && getCircleBabyId() == that.getCircleBabyId()) {
            return true;
        }
        return false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.babyAvatarUrl);
        dest.writeString(this.babyName);
        dest.writeLong(this.selectUserId);
        dest.writeLong(this.circleBabyId);
        dest.writeInt(this.isCurrUserBaby);
        dest.writeInt(this.selected);
    }

    protected GetCircleAllBabyObj(Parcel in) {
        super(in);
        this.babyAvatarUrl = in.readString();
        this.babyName = in.readString();
        this.selectUserId = in.readLong();
        this.circleBabyId = in.readLong();
        this.isCurrUserBaby = in.readInt();
        this.selected = in.readInt();
    }

    public static final Creator<GetCircleAllBabyObj> CREATOR = new Creator<GetCircleAllBabyObj>() {
        @Override
        public GetCircleAllBabyObj createFromParcel(Parcel source) {
            return new GetCircleAllBabyObj(source);
        }

        @Override
        public GetCircleAllBabyObj[] newArray(int size) {
            return new GetCircleAllBabyObj[size];
        }
    };
}
