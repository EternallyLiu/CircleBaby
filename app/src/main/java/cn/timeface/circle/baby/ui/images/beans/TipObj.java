package cn.timeface.circle.baby.ui.images.beans;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import cn.timeface.circle.baby.support.api.models.base.BaseObj;

/**
 * author : wangshuai Created on 2017/1/17
 * email : wangs1992321@gmail.com
 */
@JsonObject
public class TipObj extends BaseObj implements Parcelable {
    /**
     * tipId : 1
     * tipName : 元旦
     */


    @JsonField
    private int tipId;
    @JsonField
    private String tipName;

    public int getTipId() {
        return tipId;
    }

    public void setTipId(int tipId) {
        this.tipId = tipId;
    }

    public String getTipName() {
        return tipName;
    }

    public void setTipName(String tipName) {
        this.tipName = tipName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.tipId);
        dest.writeString(this.tipName);
    }

    public TipObj() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TipObj tipObj = (TipObj) o;

        if (tipId != tipObj.tipId) return false;
        return tipName != null ? tipName.equals(tipObj.tipName) : tipObj.tipName == null;
    }

    @Override
    public int hashCode() {
        int result = tipId;
        result = 31 * result + (tipName != null ? tipName.hashCode() : 0);
        return result;
    }

    public TipObj(String tipName) {
        this.tipName = tipName;
    }

    protected TipObj(Parcel in) {
        this.tipId = in.readInt();
        this.tipName = in.readString();
    }

    public static final Parcelable.Creator<TipObj> CREATOR = new Parcelable.Creator<TipObj>() {
        @Override
        public TipObj createFromParcel(Parcel source) {
            return new TipObj(source);
        }

        @Override
        public TipObj[] newArray(int size) {
            return new TipObj[size];
        }
    };
}
