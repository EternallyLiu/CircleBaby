package cn.timeface.circle.baby.ui.circle.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.ArrayList;
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
    protected int submitCount;//作业提交数量


    public HomeWorkListObj() {
    }

    public HomeWorkListObj(CircleSchoolTaskObj schoolTask, int submitCount) {
        this.schoolTask = schoolTask;
        this.submitCount = submitCount;
    }

    public int getSubmitCount() {
        return submitCount;
    }

    public void setSubmitCount(int submitCount) {
        this.submitCount = submitCount;
    }

    public List<MediaObj> getMeidaList() {
        if (meidaList == null) meidaList = new ArrayList<>(0);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HomeWorkListObj that = (HomeWorkListObj) o;

        if (getSchoolTask() == that.getSchoolTask()) return true;
        return false;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeTypedList(this.meidaList);
        dest.writeParcelable(this.schoolTask, flags);
        dest.writeInt(this.submitCount);
    }

    protected HomeWorkListObj(Parcel in) {
        super(in);
        this.meidaList = in.createTypedArrayList(MediaObj.CREATOR);
        this.schoolTask = in.readParcelable(CircleSchoolTaskObj.class.getClassLoader());
        this.submitCount = in.readInt();
    }

    public static final Creator<HomeWorkListObj> CREATOR = new Creator<HomeWorkListObj>() {
        @Override
        public HomeWorkListObj createFromParcel(Parcel source) {
            return new HomeWorkListObj(source);
        }

        @Override
        public HomeWorkListObj[] newArray(int size) {
            return new HomeWorkListObj[size];
        }
    };
}
