package cn.timeface.circle.baby.ui.circle.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.ArrayList;
import java.util.List;

import cn.timeface.circle.baby.support.api.models.base.BaseObj;
import cn.timeface.circle.baby.support.api.models.objs.MediaObj;

/**
 * 需认证的教师
 * Created by lidonglin on 2017/3/17.
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class TeacherAuthObj extends BaseObj implements Parcelable {
    protected List<CircleUserInfo> agreeUserList;
    protected String circleName;
    protected CircleUserInfo teacher;

    public TeacherAuthObj() {
    }

    public TeacherAuthObj(Parcel in) {
        super(in);
        agreeUserList = in.createTypedArrayList(CircleUserInfo.CREATOR);
        circleName = in.readString();
        teacher = in.readParcelable(CircleUserInfo.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeTypedList(agreeUserList);
        dest.writeString(circleName);
        dest.writeParcelable(teacher, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TeacherAuthObj> CREATOR = new Creator<TeacherAuthObj>() {
        @Override
        public TeacherAuthObj createFromParcel(Parcel in) {
            return new TeacherAuthObj(in);
        }

        @Override
        public TeacherAuthObj[] newArray(int size) {
            return new TeacherAuthObj[size];
        }
    };

    public List<CircleUserInfo> getAgreeUserList() {
        if (agreeUserList == null) agreeUserList = new ArrayList<>(0);
        return agreeUserList;
    }

    public void setAgreeUserList(List<CircleUserInfo> agreeUserList) {
        this.agreeUserList = agreeUserList;
    }

    public String getCircleName() {
        return circleName;
    }

    public void setCircleName(String circleName) {
        this.circleName = circleName;
    }

    public CircleUserInfo getTeacher() {
        return teacher;
    }

    public void setTeacher(CircleUserInfo teacher) {
        this.teacher = teacher;
    }

    public static Creator<TeacherAuthObj> getCREATOR() {
        return CREATOR;
    }
}
