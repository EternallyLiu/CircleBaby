package cn.timeface.circle.baby.ui.circle.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 老师作业的详情
 * Created by lidonglin on 2017/3/14.
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class CircleSchoolTaskDetailObj extends CircleSchoolTaskObj implements Parcelable {
    protected List<CircleHomeworkObj> homeworkList;         //家长提交的作业列表

    public CircleSchoolTaskDetailObj() {
    }

    protected CircleSchoolTaskDetailObj(Parcel in) {
        super(in);
        homeworkList = in.createTypedArrayList(CircleHomeworkObj.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeTypedList(homeworkList);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CircleSchoolTaskDetailObj> CREATOR = new Creator<CircleSchoolTaskDetailObj>() {
        @Override
        public CircleSchoolTaskDetailObj createFromParcel(Parcel in) {
            return new CircleSchoolTaskDetailObj(in);
        }

        @Override
        public CircleSchoolTaskDetailObj[] newArray(int size) {
            return new CircleSchoolTaskDetailObj[size];
        }
    };

    public List<CircleHomeworkObj> getHomeworkList() {
        if (homeworkList==null)homeworkList=new ArrayList<>(0);
        return homeworkList;
    }

    public void setHomeworkList(List<CircleHomeworkObj> homeworkList) {
        this.homeworkList = homeworkList;
    }
}
