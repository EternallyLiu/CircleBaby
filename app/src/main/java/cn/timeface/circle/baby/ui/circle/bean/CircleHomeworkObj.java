package cn.timeface.circle.baby.ui.circle.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import cn.timeface.circle.baby.support.api.models.base.BaseObj;

/**
 * 家长提交的作业
 * Created by lidonglin on 2017/3/14.
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class CircleHomeworkObj extends CircleSchoolTaskObj implements Parcelable {
    protected boolean homeworkId;         //家庭作业id
    protected int notations;              //老师批改标签
    protected CircleUserInfo submitter;   //提交的家长
    protected int taskId;                 //老师的作业id
    protected String teacherReview;       //老师评语

    protected CircleHomeworkObj(Parcel in) {
        super(in);
        homeworkId = in.readByte() != 0;
        notations = in.readInt();
        submitter = in.readParcelable(CircleUserInfo.class.getClassLoader());
        taskId = in.readInt();
        teacherReview = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeByte((byte) (homeworkId ? 1 : 0));
        dest.writeInt(notations);
        dest.writeParcelable(submitter, flags);
        dest.writeInt(taskId);
        dest.writeString(teacherReview);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CircleHomeworkObj> CREATOR = new Creator<CircleHomeworkObj>() {
        @Override
        public CircleHomeworkObj createFromParcel(Parcel in) {
            return new CircleHomeworkObj(in);
        }

        @Override
        public CircleHomeworkObj[] newArray(int size) {
            return new CircleHomeworkObj[size];
        }
    };

    public boolean isHomeworkId() {
        return homeworkId;
    }

    public void setHomeworkId(boolean homeworkId) {
        this.homeworkId = homeworkId;
    }

    public int getNotations() {
        return notations;
    }

    public void setNotations(int notations) {
        this.notations = notations;
    }

    public CircleUserInfo getSubmitter() {
        return submitter;
    }

    public void setSubmitter(CircleUserInfo submitter) {
        this.submitter = submitter;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getTeacherReview() {
        return teacherReview;
    }

    public void setTeacherReview(String teacherReview) {
        this.teacherReview = teacherReview;
    }
}
