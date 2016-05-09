package cn.timeface.circle.baby.api.models.objs;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.ArrayList;
import java.util.List;

/**
 * author: rayboot  Created on 16/3/16.
 * email : sy0725work@gmail.com
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class PhotoGroupItem implements Parcelable {
    @JsonField(name = {"time", "title"})
    String title;
    @JsonField(name = {"imgs"})
    List<ImgObj> imgObjList;

    public PhotoGroupItem(String title, List<ImgObj> imgObjList) {
        this.title = title;
        this.imgObjList = imgObjList;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public List<ImgObj> getImgObjList() {
        return imgObjList;
    }

    public void setImgObjList(List<ImgObj> imgObjList) {
        this.imgObjList = imgObjList;
    }

    public List<ImgObj> getSelectedImages() {
        List<ImgObj> images = new ArrayList<>();
        for (ImgObj item : imgObjList) {
            if (item.isSelected()) {
                images.add(item);
            }
        }
        return images;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeTypedList(imgObjList);
    }

    public PhotoGroupItem() {
    }

    protected PhotoGroupItem(Parcel in) {
        this.title = in.readString();
        this.imgObjList = in.createTypedArrayList(ImgObj.CREATOR);
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
