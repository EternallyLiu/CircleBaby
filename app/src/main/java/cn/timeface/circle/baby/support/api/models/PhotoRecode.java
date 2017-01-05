package cn.timeface.circle.baby.support.api.models;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.List;

import cn.timeface.circle.baby.support.api.models.base.BaseObj;
import cn.timeface.circle.baby.support.api.models.objs.ImgObj;
import cn.timeface.circle.baby.support.api.models.objs.Milestone;

/**
 * Created by lidonglin on 2016/5/11.
 */
public class PhotoRecode extends BaseObj implements Parcelable {
    String title;//2016.03.02
    List<ImgObj> imgObjList;
    String content;
    Milestone mileStone;
    String time;

    public PhotoRecode(String title, List<ImgObj> imgObjList) {
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Milestone getMileStone() {
        return mileStone;
    }

    public void setMileStone(Milestone mileStone) {
        this.mileStone = mileStone;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeTypedList(this.imgObjList);
        dest.writeString(this.content);
        dest.writeParcelable(this.mileStone, flags);
        dest.writeString(this.time);
    }

    protected PhotoRecode(Parcel in) {
        this.title = in.readString();
        this.imgObjList = in.createTypedArrayList(ImgObj.CREATOR);
        this.content = in.readString();
        this.mileStone = in.readParcelable(Milestone.class.getClassLoader());
        this.time = in.readString();
    }

    public static final Parcelable.Creator<PhotoRecode> CREATOR = new Parcelable.Creator<PhotoRecode>() {
        @Override
        public PhotoRecode createFromParcel(Parcel source) {
            return new PhotoRecode(source);
        }

        @Override
        public PhotoRecode[] newArray(int size) {
            return new PhotoRecode[size];
        }
    };
}
