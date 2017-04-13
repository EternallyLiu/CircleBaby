package cn.timeface.circle.baby.support.api.models.objs;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import cn.timeface.circle.baby.support.api.models.base.BaseObj;

/**
 * Created by lidonglin on 2017/4/13.
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class MilestoneObj extends BaseObj implements Parcelable {
    String milestoneId;
    String milestoneName;

    public MilestoneObj(){}

    public MilestoneObj(String milestoneId, String milestoneName) {
        this.milestoneId = milestoneId;
        this.milestoneName = milestoneName;
    }

    public String getMilestoneId() {
        return milestoneId;
    }

    public void setMilestoneId(String milestoneId) {
        this.milestoneId = milestoneId;
    }

    public String getMilestoneName() {
        return milestoneName;
    }

    public void setMilestoneName(String milestoneName) {
        this.milestoneName = milestoneName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.milestoneId);
        dest.writeString(this.milestoneName);
    }

    protected MilestoneObj(Parcel in) {
        this.milestoneId = in.readString();
        this.milestoneName = in.readString();
    }

    public static final Creator<MilestoneObj> CREATOR = new Creator<MilestoneObj>() {
        @Override
        public MilestoneObj createFromParcel(Parcel source) {
            return new MilestoneObj(source);
        }

        @Override
        public MilestoneObj[] newArray(int size) {
            return new MilestoneObj[size];
        }
    };
}
