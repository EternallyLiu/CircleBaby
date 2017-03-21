package cn.timeface.circle.baby.ui.circle.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.List;

import cn.timeface.circle.baby.support.api.models.base.BaseObj;
import cn.timeface.circle.baby.support.api.models.objs.BabyObj;
import cn.timeface.circle.baby.support.api.models.objs.MediaObj;

/**
 * Created by lidonglin on 2017/3/17.
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class GetCirclePhotoByUserObj extends BaseObj implements Parcelable {
    protected List<MediaObj> mediaList;
    protected String date;                  //发布的时间

    public GetCirclePhotoByUserObj() {
    }

    public GetCirclePhotoByUserObj(List<MediaObj> mediaList, String date) {
        this.mediaList = mediaList;
        this.date = date;
    }

    public GetCirclePhotoByUserObj(Parcel in) {
        super(in);
        mediaList = in.createTypedArrayList(MediaObj.CREATOR);
        date = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeTypedList(mediaList);
        dest.writeString(date);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<GetCirclePhotoByUserObj> CREATOR = new Creator<GetCirclePhotoByUserObj>() {
        @Override
        public GetCirclePhotoByUserObj createFromParcel(Parcel in) {
            return new GetCirclePhotoByUserObj(in);
        }

        @Override
        public GetCirclePhotoByUserObj[] newArray(int size) {
            return new GetCirclePhotoByUserObj[size];
        }
    };

    public List<MediaObj> getMediaList() {
        return mediaList;
    }

    public void setMediaList(List<MediaObj> mediaList) {
        this.mediaList = mediaList;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public static Creator<GetCirclePhotoByUserObj> getCREATOR() {
        return CREATOR;
    }
}
