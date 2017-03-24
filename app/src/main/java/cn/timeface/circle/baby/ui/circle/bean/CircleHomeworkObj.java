package cn.timeface.circle.baby.ui.circle.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.List;

import cn.timeface.circle.baby.support.api.models.objs.BabyObj;

/**
 * 家长提交的作业
 * Created by lidonglin on 2017/3/14.
 */
public class CircleHomeworkObj extends CircleSchoolTaskObj implements Parcelable {
    protected long homeworkId;             //家庭作业id
    protected List<String> notations;      //老师批改标签
    protected CircleUserInfo submitter;    //提交的家长
    protected String teacherReview;        //老师评语
    private BabyObj babyInfo;

    public BabyObj getBabyInfo() {
        return babyInfo;
    }

    public void setBabyInfo(BabyObj babyInfo) {
        this.babyInfo = babyInfo;
    }

    public CircleHomeworkObj() {
    }

    protected CircleHomeworkObj(Parcel in) {
        super(in);
        homeworkId = in.readLong();
        notations = in.createStringArrayList();
        submitter = in.readParcelable(CircleUserInfo.class.getClassLoader());
        teacherReview = in.readString();
    public CircleHomeworkObj(long taskId, String title) {
        this.taskId = taskId;
        setTitle(title);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(homeworkId);
        dest.writeStringList(notations);
        dest.writeParcelable(submitter, flags);
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


    public long getHomeworkId() {
        return homeworkId;
    }

    public void setHomeworkId(long homeworkId) {
        this.homeworkId = homeworkId;
    }

    public List<String> getNotations() {
        return notations;
    }

    public void setNotations(List<String> notations) {
        this.notations = notations;
    }

    public CircleUserInfo getSubmitter() {
        return submitter;
    }

    public void setSubmitter(CircleUserInfo submitter) {
        this.submitter = submitter;
    }

    public String getTeacherReview() {
        return teacherReview;
    }

    public void setTeacherReview(String teacherReview) {
        this.teacherReview = teacherReview;
    }
}
