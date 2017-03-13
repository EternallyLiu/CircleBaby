package cn.timeface.circle.baby.support.api.models.base;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * author: rayboot  Created on 15/12/3.
 * email : sy0725work@gmail.com
 */
public class BaseObj implements Parcelable {
    private int baseType = 0;

    public int getBaseType() {
        return baseType;
    }

    public void setBaseType(int baseType) {
        this.baseType = baseType;
    }

    public BaseObj() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.baseType);
    }

    protected BaseObj(Parcel in) {
        this.baseType = in.readInt();
    }

}

