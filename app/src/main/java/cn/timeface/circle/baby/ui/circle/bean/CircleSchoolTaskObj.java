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
    protected int selectCount;

    public CircleSchoolTaskObj() {
    }

    public CircleSchoolTaskObj(int isCommit, long taskId) {
        this.isCommit = isCommit;
        this.taskId = taskId;
    }

    public int getSelectCount() {
        return selectCount;
    }

    public void setSelectCount(int selectCount) {
        this.selectCount = selectCount;
    }

    public int getIsCommit() {
        return isCommit;
    }

    public void setIsCommit(int isCommit) {
        this.isCommit = isCommit;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CircleSchoolTaskObj that = (CircleSchoolTaskObj) o;

        if (taskId > 0 && taskId == that.taskId) return true;
        return false;

    }

    @Override
    public int hashCode() {
        int result = isCommit;
        result = 31 * result + (int) (taskId ^ (taskId >>> 32));
        result = 31 * result + (teacher != null ? teacher.hashCode() : 0);
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.isCommit);
        dest.writeLong(this.taskId);
        dest.writeParcelable(this.teacher, flags);
    }

    protected CircleSchoolTaskObj(Parcel in) {
        super(in);
        this.isCommit = in.readInt();
        this.taskId = in.readLong();
        this.teacher = in.readParcelable(CircleUserInfo.class.getClassLoader());
    }

    public static final Creator<CircleSchoolTaskObj> CREATOR = new Creator<CircleSchoolTaskObj>() {
        @Override
        public CircleSchoolTaskObj createFromParcel(Parcel source) {
            return new CircleSchoolTaskObj(source);
        }

        @Override
        public CircleSchoolTaskObj[] newArray(int size) {
            return new CircleSchoolTaskObj[size];
        }
    };
}
