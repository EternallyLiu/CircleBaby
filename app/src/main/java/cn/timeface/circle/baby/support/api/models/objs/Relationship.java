package cn.timeface.circle.baby.support.api.models.objs;

import android.os.Parcel;
import android.os.Parcelable;

import cn.timeface.circle.baby.support.api.models.base.BaseObj;

/**
 * Created by lidonglin on 2016/5/4.
 */
public class Relationship extends BaseObj implements Parcelable {
    String relationName;
    int relationId;

    public Relationship(String relationName, int relationId) {
        this.relationName = relationName;
        this.relationId = relationId;
    }

    public String getRelationName() {
        return relationName;
    }

    public void setRelationName(String relationName) {
        this.relationName = relationName;
    }

    public int getRelationId() {
        return relationId;
    }

    public void setRelationId(int relationId) {
        this.relationId = relationId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.relationName);
        dest.writeInt(this.relationId);
    }

    protected Relationship(Parcel in) {
        this.relationName = in.readString();
        this.relationId = in.readInt();
    }

    public static final Parcelable.Creator<Relationship> CREATOR = new Parcelable.Creator<Relationship>() {
        @Override
        public Relationship createFromParcel(Parcel source) {
            return new Relationship(source);
        }

        @Override
        public Relationship[] newArray(int size) {
            return new Relationship[size];
        }
    };
}
