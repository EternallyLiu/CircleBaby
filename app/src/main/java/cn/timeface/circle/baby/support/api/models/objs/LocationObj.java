package cn.timeface.circle.baby.support.api.models.objs;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonObject;

/**
 * location obj
 * author : YW.SUN Created on 2017/1/12
 * email : sunyw10@gmail.com
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class LocationObj implements Parcelable {
    private double lat;
    private double log;

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLog() {
        return log;
    }

    public void setLog(double log) {
        this.log = log;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.lat);
        dest.writeDouble(this.log);
    }

    public LocationObj() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LocationObj that = (LocationObj) o;

        if (Double.compare(that.lat, lat) != 0) return false;
        return Double.compare(that.log, log) == 0;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(lat);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(log);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    protected LocationObj(Parcel in) {
        this.lat = in.readDouble();
        this.log = in.readDouble();
    }

    public static final Creator<LocationObj> CREATOR = new Creator<LocationObj>() {
        @Override
        public LocationObj createFromParcel(Parcel source) {
            return new LocationObj(source);
        }

        @Override
        public LocationObj[] newArray(int size) {
            return new LocationObj[size];
        }
    };
}
