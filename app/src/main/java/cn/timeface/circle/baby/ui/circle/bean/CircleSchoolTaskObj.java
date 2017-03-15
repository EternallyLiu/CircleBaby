package cn.timeface.circle.baby.ui.circle.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonObject;

/**
 * 老师布置作业的对象
 * Created by lidonglin on 2017/3/14.
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class CircleSchoolTaskObj extends CircleContentObj implements Parcelable {
    protected int isCommit;             //当前宝宝是否已经交作业  0否，1是
    protected long taskId;              //作业id
    protected CircleUserInfo teacher;   //布置作业的老师对象

    public CircleSchoolTaskObj() {
    }

    protected CircleSchoolTaskObj(Parcel in) {
        super(in);
        isCommit = in.readInt();
        taskId = in.readLong();
        teacher = in.readParcelable(CircleUserInfo.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(isCommit);
        dest.writeLong(taskId);
        dest.writeParcelable(teacher, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CircleSchoolTaskObj> CREATOR = new Creator<CircleSchoolTaskObj>() {
        @Override
        public CircleSchoolTaskObj createFromParcel(Parcel in) {
            return new CircleSchoolTaskObj(in);
        }

        @Override
        public CircleSchoolTaskObj[] newArray(int size) {
            return new CircleSchoolTaskObj[size];
        }
    };

    public int isCommit() {
        return isCommit;
    }

    public void setCommit(int commit) {
        isCommit = commit;
    }

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public CircleUserInfo getTeacher() {
        return teacher;
    }

    public void setTeacher(CircleUserInfo teacher) {
        this.teacher = teacher;
    }
}
