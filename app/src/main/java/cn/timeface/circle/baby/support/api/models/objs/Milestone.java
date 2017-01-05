package cn.timeface.circle.baby.support.api.models.objs;

import android.os.Parcel;
import android.os.Parcelable;

import cn.timeface.circle.baby.support.api.models.base.BaseObj;

/**
 * Created by lidonglin on 2016/5/4.
 */
public class Milestone extends BaseObj implements Parcelable {
    String milestone;
    int id;

    public Milestone(String milestone, int id) {
        this.milestone = milestone;
        this.id = id;
    }

    public String getMilestone() {
        return milestone;
    }

    public void setMilestone(String milestone) {
        this.milestone = milestone;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.milestone);
        dest.writeInt(this.id);
    }

    protected Milestone(Parcel in) {
        this.milestone = in.readString();
        this.id = in.readInt();
    }

    public static final Parcelable.Creator<Milestone> CREATOR = new Parcelable.Creator<Milestone>() {
        @Override
        public Milestone createFromParcel(Parcel source) {
            return new Milestone(source);
        }

        @Override
        public Milestone[] newArray(int size) {
            return new Milestone[size];
        }
    };
}
