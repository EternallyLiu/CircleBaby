package cn.timeface.circle.baby.ui.timelines.beans;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import cn.timeface.circle.baby.support.api.models.base.BaseObj;
import cn.timeface.circle.baby.support.api.models.objs.LocationObj;

/**
 * author : wangshuai Created on 2017/2/9
 * email : wangs1992321@gmail.com
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class NearLocationObj extends BaseObj implements Parcelable {

    private String area;
    private String areaDetail;
    private LocationObj location;

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getAreaDetail() {
        return areaDetail;
    }

    public void setAreaDetail(String areaDetail) {
        this.areaDetail = areaDetail;
    }

    public LocationObj getLocation() {
        return location;
    }

    public void setLocation(LocationObj location) {
        this.location = location;
    }

    public NearLocationObj() {
    }

    public NearLocationObj(String area, String areaDetail, LocationObj location) {
        this.area = area;
        this.areaDetail = areaDetail;
        this.location = location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NearLocationObj that = (NearLocationObj) o;

        return location != null ? location.equals(that.location) : that.location == null;

    }

    @Override
    public int hashCode() {
        int result = area != null ? area.hashCode() : 0;
        result = 31 * result + (areaDetail != null ? areaDetail.hashCode() : 0);
        result = 31 * result + (location != null ? location.hashCode() : 0);
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.area);
        dest.writeString(this.areaDetail);
        dest.writeParcelable(this.location, flags);
    }

    protected NearLocationObj(Parcel in) {
        this.area = in.readString();
        this.areaDetail = in.readString();
        this.location = in.readParcelable(LocationObj.class.getClassLoader());
    }

    public static final Parcelable.Creator<NearLocationObj> CREATOR = new Parcelable.Creator<NearLocationObj>() {
        @Override
        public NearLocationObj createFromParcel(Parcel source) {
            return new NearLocationObj(source);
        }

        @Override
        public NearLocationObj[] newArray(int size) {
            return new NearLocationObj[size];
        }
    };
}
