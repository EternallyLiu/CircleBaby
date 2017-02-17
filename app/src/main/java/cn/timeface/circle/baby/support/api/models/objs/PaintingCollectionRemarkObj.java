package cn.timeface.circle.baby.support.api.models.objs;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * author : YW.SUN Created on 2017/2/16
 * email : sunyw10@gmail.com
 */
public class PaintingCollectionRemarkObj implements Parcelable {
    private String name;
    private String age;

    public PaintingCollectionRemarkObj(String name, String age) {
        this.name = name;
        this.age = age;
    }

    public PaintingCollectionRemarkObj() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.age);
    }

    protected PaintingCollectionRemarkObj(Parcel in) {
        this.name = in.readString();
        this.age = in.readString();
    }

    public static final Parcelable.Creator<PaintingCollectionRemarkObj> CREATOR = new Parcelable.Creator<PaintingCollectionRemarkObj>() {
        @Override
        public PaintingCollectionRemarkObj createFromParcel(Parcel source) {
            return new PaintingCollectionRemarkObj(source);
        }

        @Override
        public PaintingCollectionRemarkObj[] newArray(int size) {
            return new PaintingCollectionRemarkObj[size];
        }
    };
}
