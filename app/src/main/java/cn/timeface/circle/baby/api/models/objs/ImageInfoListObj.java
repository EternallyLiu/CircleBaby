package cn.timeface.circle.baby.api.models.objs;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import cn.timeface.circle.baby.api.models.base.BaseObj;

/**
 * Created by lidonglin on 2016/7/4.
 */
public class ImageInfoListObj extends BaseObj implements Parcelable {
    List<MediaObj> mediaList;
    int timeId;


    public List<MediaObj> getMediaList() {
        return mediaList;
    }

    public void setMediaList(List<MediaObj> mediaList) {
        this.mediaList = mediaList;
    }

    public int getTimeId() {
        return timeId;
    }

    public void setTimeId(int timeId) {
        this.timeId = timeId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.mediaList);
        dest.writeInt(this.timeId);
    }

    public ImageInfoListObj() {
    }

    protected ImageInfoListObj(Parcel in) {
        this.mediaList = in.createTypedArrayList(MediaObj.CREATOR);
        this.timeId = in.readInt();
    }

    public static final Creator<ImageInfoListObj> CREATOR = new Creator<ImageInfoListObj>() {
        @Override
        public ImageInfoListObj createFromParcel(Parcel source) {
            return new ImageInfoListObj(source);
        }

        @Override
        public ImageInfoListObj[] newArray(int size) {
            return new ImageInfoListObj[size];
        }
    };

}