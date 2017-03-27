package cn.timeface.circle.baby.ui.circle.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.ArrayList;
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

    public CircleHomeworkObj(long taskId, String title) {
        this.taskId = taskId;
        setTitle(title);
    }

    public long getHomeworkId() {
        return homeworkId;
    }

    public void setHomeworkId(long homeworkId) {
        this.homeworkId = homeworkId;
    }

    public List<String> getNotations() {
        if (notations==null)
            notations=new ArrayList<>(0);
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(this.homeworkId);
        dest.writeStringList(this.notations);
        dest.writeParcelable(this.submitter, flags);
        dest.writeString(this.teacherReview);
        dest.writeParcelable(this.babyInfo, flags);
    }

    protected CircleHomeworkObj(Parcel in) {
        super(in);
        this.homeworkId = in.readLong();
        this.notations = in.createStringArrayList();
        this.submitter = in.readParcelable(CircleUserInfo.class.getClassLoader());
        this.teacherReview = in.readString();
        this.babyInfo = in.readParcelable(BabyObj.class.getClassLoader());
    }

    public static final Creator<CircleHomeworkObj> CREATOR = new Creator<CircleHomeworkObj>() {
        @Override
        public CircleHomeworkObj createFromParcel(Parcel source) {
            return new CircleHomeworkObj(source);
        }

        @Override
        public CircleHomeworkObj[] newArray(int size) {
            return new CircleHomeworkObj[size];
        }
    };
}
