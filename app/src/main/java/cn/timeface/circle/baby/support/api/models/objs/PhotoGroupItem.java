package cn.timeface.circle.baby.support.api.models.objs;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.List;

import cn.timeface.circle.baby.support.api.models.db.PhotoModel;

/**
 * author: rayboot  Created on 16/3/16.
 * email : sy0725work@gmail.com
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class PhotoGroupItem implements Parcelable {
    @JsonField(name = {"time", "title"})
    String title;
    @JsonField(name = {"imgs"})
    List<PhotoModel> imgList;

    PhotoGroupItem() {

    }

    public PhotoGroupItem(String title, List<PhotoModel> imgList) {
        this.title = title;
        this.imgList = imgList;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public List<PhotoModel> getImgList() {
        return imgList;
    }

    public void setImgList(List<PhotoModel> imgList) {
        this.imgList = imgList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeTypedList(imgList);
    }

    protected PhotoGroupItem(Parcel in) {
        this.title = in.readString();
        this.imgList = in.createTypedArrayList(PhotoModel.CREATOR);
    }

    public static final Creator<PhotoGroupItem> CREATOR = new Creator<PhotoGroupItem>() {
        @Override
        public PhotoGroupItem createFromParcel(Parcel source) {
            return new PhotoGroupItem(source);
        }

        @Override
        public PhotoGroupItem[] newArray(int size) {
            return new PhotoGroupItem[size];
        }
    };
}
