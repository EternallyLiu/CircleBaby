package cn.timeface.circle.baby.api.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.ArrayList;
import java.util.List;

import cn.timeface.circle.baby.api.models.objs.ImgObj;
import cn.timeface.circle.baby.api.models.objs.Milestone;
import cn.timeface.circle.baby.api.services.ApiService;
import cn.timeface.circle.baby.constants.TypeConstant;
import cn.timeface.circle.baby.oss.uploadservice.UploadFileObj;

/**
 * @author wxw
 * @from 2015/12/21
 * @TODO 时光碎片
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class TimePiece
//        implements Parcelable
{

    private String content; //发布文本内容
    private Milestone mileStone; //里程碑
    private List<ImgObj> imgObjList = new ArrayList<>();
    private String time;//时间

    public TimePiece() {
    }

    public TimePiece(String content, Milestone mileStone, String time) {
        this.content = content;
        this.mileStone = mileStone;
        this.time = time;
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

    public void setImgObjList(List<ImgObj> imgObjList) {
        this.imgObjList = imgObjList;
    }

    public List<ImgObj> getImgObjList() {
        return imgObjList;
    }

    public List<UploadFileObj> getUploadFileObjs(int publishType) {
        if (imgObjList == null || imgObjList.isEmpty()) {
            return null;
        }

        List<UploadFileObj> uploadFileObjs = new ArrayList<>(imgObjList.size());
        for (ImgObj imgObj : imgObjList) {
            uploadFileObjs.add(imgObj.getmFullUriString().contains(ApiService.IMAGE_BASE_URL) ?
                    new TFUploadFile(imgObj.getmFullUriString()) :
                    new TFUploadFile(imgObj.getLocalPath(), TypeConstant.UPLOAD_FOLDER));
        }
        return uploadFileObjs;
    }

//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeString(this.content);
//        dest.writeParcelable(this.mileStone, flags);
//        dest.writeTypedList(this.imgObjList);
//        dest.writeString(this.time);
//    }
//
//    protected TimePiece(Parcel in) {
//        this.content = in.readString();
//        this.mileStone = in.readParcelable(Milestone.class.getClassLoader());
//        this.imgObjList = in.createTypedArrayList(ImgObj.CREATOR);
//        this.time = in.readString();
//    }
//
//    public static final Creator<TimePiece> CREATOR = new Creator<TimePiece>() {
//        @Override
//        public TimePiece createFromParcel(Parcel source) {
//            return new TimePiece(source);
//        }
//
//        @Override
//        public TimePiece[] newArray(int size) {
//            return new TimePiece[size];
//        }
//    };
}
