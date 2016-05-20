package cn.timeface.circle.baby.api.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.timeface.circle.baby.api.models.base.BaseObj;
import cn.timeface.circle.baby.api.models.objs.ImgObj;
import cn.timeface.circle.baby.api.models.objs.Milestone;

/**
 * Created by lidonglin on 2016/5/11.
 */
public class PhotoRecode extends BaseObj implements Parcelable{
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
        dest.writeTypedList(imgObjList);
        dest.writeString(this.content);
        dest.writeSerializable(this.mileStone);
        dest.writeString(this.time);
    }

    protected PhotoRecode(Parcel in) {
        title = in.readString();
        imgObjList = in.createTypedArrayList(ImgObj.CREATOR);
        content = in.readString();
        mileStone = (Milestone) in.readSerializable();
        time = in.readString();
    }

    public static final Creator<PhotoRecode> CREATOR = new Creator<PhotoRecode>() {
        @Override
        public PhotoRecode createFromParcel(Parcel in) {
            return new PhotoRecode(in);
        }

        @Override
        public PhotoRecode[] newArray(int size) {
            return new PhotoRecode[size];
        }
    };

}
