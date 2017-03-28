package cn.timeface.circle.baby.ui.circle.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import cn.timeface.circle.baby.support.api.models.base.BaseObj;

/**
 * 图片关联的宝宝
 * Created by lidonglin on 2017/3/14.
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class RelateBabyObj extends GetCircleAllBabyObj implements Parcelable {
    protected long babyId;       //宝宝id

    public RelateBabyObj() {
    }

    public RelateBabyObj(long babyId, String babyName) {
        this.babyId = babyId;
        setBabyName(babyName);
    }

    public RelateBabyObj(Parcel in, long babyId, String babyName) {
        super(in);
        this.babyId = babyId;
        setBabyName(babyName);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RelateBabyObj babyObj = (RelateBabyObj) o;

        if (babyId != babyObj.babyId) return false;
        return getBabyName() != null ? getBabyName().equals(babyObj.getBabyName()) : babyObj.getBabyName() == null;

    }

    public long getBabyId() {
        return babyId;
    }

    public void setBabyId(long babyId) {
        this.babyId = babyId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(this.babyId);
    }

    protected RelateBabyObj(Parcel in) {
        super(in);
        this.babyId = in.readLong();
    }

    public static final Creator<RelateBabyObj> CREATOR = new Creator<RelateBabyObj>() {
        @Override
        public RelateBabyObj createFromParcel(Parcel source) {
            return new RelateBabyObj(source);
        }

        @Override
        public RelateBabyObj[] newArray(int size) {
            return new RelateBabyObj[size];
        }
    };
}
