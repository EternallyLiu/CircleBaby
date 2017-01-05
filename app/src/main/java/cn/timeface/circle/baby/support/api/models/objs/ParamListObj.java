package cn.timeface.circle.baby.support.api.models.objs;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import cn.timeface.circle.baby.support.api.models.base.BaseObj;

/**
 * Created by lidonglin on 2016/6/28.
 */
public class ParamListObj extends BaseObj implements Parcelable {
    String key;
    String name;
    List<ValueListObj> valueList;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ValueListObj> getValueList() {
        return valueList;
    }

    public void setValueList(List<ValueListObj> valueList) {
        this.valueList = valueList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.key);
        dest.writeString(this.name);
        dest.writeTypedList(this.valueList);
    }

    public ParamListObj() {
    }

    protected ParamListObj(Parcel in) {
        this.key = in.readString();
        this.name = in.readString();
        this.valueList = in.createTypedArrayList(ValueListObj.CREATOR);
    }

    public static final Creator<ParamListObj> CREATOR = new Creator<ParamListObj>() {
        @Override
        public ParamListObj createFromParcel(Parcel source) {
            return new ParamListObj(source);
        }

        @Override
        public ParamListObj[] newArray(int size) {
            return new ParamListObj[size];
        }
    };

}
