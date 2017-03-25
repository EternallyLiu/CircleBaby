package cn.timeface.circle.baby.ui.circle.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * author : sunyanwei Created on 17-3-22
 * email : sunyanwei@timeface.cn
 */
public class CircleHomeworkExObj implements Parcelable {
    private CircleHomeworkObj homework;
    private String schoolTaskName;

    public CircleHomeworkObj getHomework() {
        return homework;
    }

    public void setHomework(CircleHomeworkObj homework) {
        this.homework = homework;
    }

    public String getSchoolTaskName() {
        return schoolTaskName;
    }

    public void setSchoolTaskName(String schoolTaskName) {
        this.schoolTaskName = schoolTaskName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.homework, flags);
        dest.writeString(this.schoolTaskName);
    }

    public CircleHomeworkExObj() {
    }

    protected CircleHomeworkExObj(Parcel in) {
        this.homework = in.readParcelable(CircleHomeworkObj.class.getClassLoader());
        this.schoolTaskName = in.readString();
    }

    public static final Parcelable.Creator<CircleHomeworkExObj> CREATOR = new Parcelable.Creator<CircleHomeworkExObj>() {
        @Override
        public CircleHomeworkExObj createFromParcel(Parcel source) {
            return new CircleHomeworkExObj(source);
        }

        @Override
        public CircleHomeworkExObj[] newArray(int size) {
            return new CircleHomeworkExObj[size];
        }
    };
}
