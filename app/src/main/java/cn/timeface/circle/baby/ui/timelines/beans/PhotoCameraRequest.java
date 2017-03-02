package cn.timeface.circle.baby.ui.timelines.beans;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * author : wangshuai Created on 2017/3/2
 * email : wangs1992321@gmail.com
 */
public class PhotoCameraRequest implements Parcelable {
    private String identifiers;

    public PhotoCameraRequest() {
    }

    public PhotoCameraRequest(String identifiers) {

        this.identifiers = identifiers;
    }

    public String getIdentifiers() {

        return identifiers;
    }

    public void setIdentifiers(String identifiers) {
        this.identifiers = identifiers;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.identifiers);
    }

    protected PhotoCameraRequest(Parcel in) {
        this.identifiers = in.readString();
    }

    public static final Parcelable.Creator<PhotoCameraRequest> CREATOR = new Parcelable.Creator<PhotoCameraRequest>() {
        @Override
        public PhotoCameraRequest createFromParcel(Parcel source) {
            return new PhotoCameraRequest(source);
        }

        @Override
        public PhotoCameraRequest[] newArray(int size) {
            return new PhotoCameraRequest[size];
        }
    };
}
