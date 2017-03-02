package cn.timeface.circle.baby.support.api.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import cn.timeface.circle.baby.support.api.models.base.BaseObj;
import cn.timeface.circle.baby.support.api.models.objs.ImgObj;
import cn.timeface.circle.baby.support.api.models.objs.MediaObj;
import cn.timeface.circle.baby.support.api.models.objs.Milestone;
import cn.timeface.circle.baby.ui.timelines.beans.NearLocationObj;

/**
 * Created by lidonglin on 2016/5/11.
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class PhotoRecode extends BaseObj implements Parcelable {
    String title;//2016.03.02
    ArrayList<ImgObj> imgObjList = new ArrayList<>(0);
    ArrayList<MediaObj> mediaObjList;
    String content;
    Milestone mileStone;
    String time;
    NearLocationObj locationObj;

    public PhotoRecode(){}

    public PhotoRecode(String title, List<ImgObj> imgObjList) {
        this.title = title;
        setImgObjList(imgObjList);
    }

    public PhotoRecode(String title, List<ImgObj> imgObjList, ArrayList<MediaObj> mediaObjList) {
        this.title = title;
        this.mediaObjList = mediaObjList;
        setImgObjList(imgObjList);
    }

    public PhotoRecode(String title, List<ImgObj> imgObjList, ArrayList<MediaObj> mediaObjList, String content, Milestone mileStone, String time) {
        this.title = title;
        this.mediaObjList = mediaObjList;
        setImgObjList(imgObjList);
        this.content = content;
        this.mileStone = mileStone;
        this.time = time;
    }

    public NearLocationObj getLocationObj() {
        return locationObj;
    }

    public void setLocationObj(NearLocationObj locationObj) {
        this.locationObj = locationObj;
    }

    public ArrayList<MediaObj> getMediaObjList() {
        return getMediaObjList(false);
    }

    /**
     * 强行重新按照imgobjlist排序
     *
     * @param flag
     * @return
     */
    public ArrayList<MediaObj> getMediaObjList(boolean flag) {
        if (mediaObjList == null) mediaObjList = new ArrayList<>();
        if (flag || getImgObjList().size() != mediaObjList.size())
            img2Medias();
        return mediaObjList;
    }

    public ArrayList<String> getLocalPaths() {
        ArrayList<String> list = new ArrayList<>(0);
        ArrayList<MediaObj> mediaObjList = getMediaObjList();
        for (int i = 0; i < mediaObjList.size(); i++) {
            list.add(TextUtils.isEmpty(mediaObjList.get(i).getLocalPath()) ? mediaObjList.get(i).getImgUrl() : mediaObjList.get(i).getLocalPath());
        }
        return list;
    }

    public void setMediaObjList(ArrayList<MediaObj> mediaObjList) {
        this.mediaObjList = mediaObjList;
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
        this.imgObjList.clear();
        this.imgObjList.addAll(imgObjList);
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


    private void img2Medias() {
        if (getImgObjList() != null && getImgObjList().size() > 0) {
            ArrayList<MediaObj> list = new ArrayList<>();
            if (mediaObjList.size() > 0) {
                list.addAll(mediaObjList);
                mediaObjList.clear();
            }
            for (int i = 0; i < getImgObjList().size(); i++) {
                if (list.contains(getImgObjList().get(i).getMediaObj()))
                    mediaObjList.add(list.get(list.indexOf(getImgObjList().get(i).getMediaObj())));
                else mediaObjList.add(getImgObjList().get(i).getMediaObj());
            }
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.title);
        dest.writeTypedList(this.imgObjList);
        dest.writeTypedList(this.mediaObjList);
        dest.writeString(this.content);
        dest.writeParcelable(this.mileStone, flags);
        dest.writeString(this.time);
        dest.writeParcelable(this.locationObj, flags);
    }

    protected PhotoRecode(Parcel in) {
        super(in);
        this.title = in.readString();
        this.imgObjList = in.createTypedArrayList(ImgObj.CREATOR);
        this.mediaObjList = in.createTypedArrayList(MediaObj.CREATOR);
        this.content = in.readString();
        this.mileStone = in.readParcelable(Milestone.class.getClassLoader());
        this.time = in.readString();
        this.locationObj = in.readParcelable(NearLocationObj.class.getClassLoader());
    }

    public static final Creator<PhotoRecode> CREATOR = new Creator<PhotoRecode>() {
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
