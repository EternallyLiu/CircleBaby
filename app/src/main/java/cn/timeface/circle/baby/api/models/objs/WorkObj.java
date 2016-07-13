package cn.timeface.circle.baby.api.models.objs;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import cn.timeface.circle.baby.api.models.base.BaseObj;

/**
 * Created by lidonglin on 2016/6/15.
 */
public class WorkObj extends BaseObj implements Parcelable {
    String coverTitle;
    String description;
    int id;
    List<MediaObj> imgList;
    String title;
    int type;


    public String getCoverTitle() {
        return coverTitle;
    }

    public void setCoverTitle(String coverTitle) {
        this.coverTitle = coverTitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<MediaObj> getImgList() {
        return imgList;
    }

    public void setImgList(List<MediaObj> imgList) {
        this.imgList = imgList;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.coverTitle);
        dest.writeString(this.description);
        dest.writeInt(this.id);
        dest.writeTypedList(this.imgList);
        dest.writeString(this.title);
        dest.writeInt(this.type);
    }

    public WorkObj() {
    }

    protected WorkObj(Parcel in) {
        this.coverTitle = in.readString();
        this.description = in.readString();
        this.id = in.readInt();
        this.imgList = in.createTypedArrayList(MediaObj.CREATOR);
        this.title = in.readString();
        this.type = in.readInt();
    }

    public static final Creator<WorkObj> CREATOR = new Creator<WorkObj>() {
        @Override
        public WorkObj createFromParcel(Parcel source) {
            return new WorkObj(source);
        }

        @Override
        public WorkObj[] newArray(int size) {
            return new WorkObj[size];
        }
    };
}
