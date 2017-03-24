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
    private int circleBabyId;

    public GetCircleAllBabyObj() {
    }

    public GetCircleAllBabyObj(Parcel in) {
        super(in);
        babyAvatarUrl = in.readString();
        babyName = in.readString();
        selectUserId = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(babyAvatarUrl);
        dest.writeString(babyName);
        dest.writeLong(selectUserId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<GetCircleAllBabyObj> CREATOR = new Creator<GetCircleAllBabyObj>() {
        @Override
        public GetCircleAllBabyObj createFromParcel(Parcel in) {
            return new GetCircleAllBabyObj(in);
        }

        @Override
        public GetCircleAllBabyObj[] newArray(int size) {
            return new GetCircleAllBabyObj[size];
        }
    };

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

    public static Creator<GetCircleAllBabyObj> getCREATOR() {
        return CREATOR;
    }

    public int getCircleBabyId() {
        return circleBabyId;
    }

    public void setCircleBabyId(int circleBabyId) {
        this.circleBabyId = circleBabyId;
    }
}
