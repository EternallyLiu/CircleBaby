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
public class RelateBabyObj extends BaseObj implements Parcelable {
    protected int babyId;       //宝宝id
    protected String babyName;  //宝宝姓名

    public RelateBabyObj() {
    }

    protected RelateBabyObj(Parcel in) {
        super(in);
        babyId = in.readInt();
        babyName = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(babyId);
        dest.writeString(babyName);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RelateBabyObj> CREATOR = new Creator<RelateBabyObj>() {
        @Override
        public RelateBabyObj createFromParcel(Parcel in) {
            return new RelateBabyObj(in);
        }

        @Override
        public RelateBabyObj[] newArray(int size) {
            return new RelateBabyObj[size];
        }
    };

    public int getBabyId() {
        return babyId;
    }

    public void setBabyId(int babyId) {
        this.babyId = babyId;
    }

    public String getBabyName() {
        return babyName;
    }

    public void setBabyName(String babyName) {
        this.babyName = babyName;
    }
}
