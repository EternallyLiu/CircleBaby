package cn.timeface.circle.baby.ui.circle.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.List;

import cn.timeface.circle.baby.support.api.models.base.BaseObj;
import cn.timeface.circle.baby.support.api.models.objs.MediaObj;

/**
 * 获取圈作业列表对象
 * Created by lidonglin on 2017/3/17.
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class HomeWorkListObj extends BaseObj implements Parcelable {
    protected List<MediaObj> meidaList;        //该作业下作品图片
    protected CircleSchoolTaskObj schoolTask;  //老师的作业

    public HomeWorkListObj() {
    }

    public HomeWorkListObj(Parcel in) {
        super(in);
        meidaList = in.createTypedArrayList(MediaObj.CREATOR);
        schoolTask = in.readParcelable(CircleSchoolTaskObj.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeTypedList(meidaList);
        dest.writeParcelable(schoolTask, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<HomeWorkListObj> CREATOR = new Creator<HomeWorkListObj>() {
        @Override
        public HomeWorkListObj createFromParcel(Parcel in) {
            return new HomeWorkListObj(in);
        }

        @Override
        public HomeWorkListObj[] newArray(int size) {
            return new HomeWorkListObj[size];
        }
    };

    public List<MediaObj> getMeidaList() {
        return meidaList;
    }

    public void setMeidaList(List<MediaObj> meidaList) {
        this.meidaList = meidaList;
    }

    public CircleSchoolTaskObj getSchoolTask() {
        return schoolTask;
    }

    public void setSchoolTask(CircleSchoolTaskObj schoolTask) {
        this.schoolTask = schoolTask;
    }

    public static Creator<HomeWorkListObj> getCREATOR() {
        return CREATOR;
    }
}