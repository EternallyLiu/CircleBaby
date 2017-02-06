package cn.timeface.circle.baby.events;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * author : wangshuai Created on 2017/2/6
 * email : wangs1992321@gmail.com
 */
public class MediaLoadComplete implements Parcelable {

    private int type=-1;//1、视频；2、图片

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public MediaLoadComplete(int type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.type);
    }

    protected MediaLoadComplete(Parcel in) {
        this.type = in.readInt();
    }

    public static final Parcelable.Creator<MediaLoadComplete> CREATOR = new Parcelable.Creator<MediaLoadComplete>() {
        @Override
        public MediaLoadComplete createFromParcel(Parcel source) {
            return new MediaLoadComplete(source);
        }

        @Override
        public MediaLoadComplete[] newArray(int size) {
            return new MediaLoadComplete[size];
        }
    };
}
