package cn.timeface.circle.baby.support.api.models.objs;

import android.os.Parcel;
import android.os.Parcelable;

import cn.timeface.circle.baby.support.api.models.base.BaseObj;

/**
 * Created by lidonglin on 2016/6/28.
 */
public class ValueListObj extends BaseObj implements Parcelable {
    String show;
    String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getShow() {
        return show;
    }

    public void setShow(String show) {
        this.show = show;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.show);
        dest.writeString(this.value);
    }

    public ValueListObj() {
    }

    protected ValueListObj(Parcel in) {
        this.show = in.readString();
        this.value = in.readString();
    }

    public static final Creator<ValueListObj> CREATOR = new Creator<ValueListObj>() {
        @Override
        public ValueListObj createFromParcel(Parcel source) {
            return new ValueListObj(source);
        }

        @Override
        public ValueListObj[] newArray(int size) {
            return new ValueListObj[size];
        }
    };

}
