package cn.timeface.circle.baby.ui.circle.timelines.bean;

import android.os.Parcel;
import android.os.Parcelable;

import cn.timeface.circle.baby.ui.circle.bean.CircleHomeworkObj;
import cn.timeface.circle.baby.ui.circle.bean.GrowthCircleObj;

/**
 * author : wangshuai Created on 2017/3/23
 * email : wangs1992321@gmail.com
 */
public class CircleHomeWorkHeader implements Parcelable {

    private GrowthCircleObj growthCircle;             //圈对象
    private int hasTeacherCertification;              //是否有老师认证消息 1- 有 0 - 无
    private CircleHomeworkObj lastSubmitHomework;     //最新提交的作业

    public CircleHomeWorkHeader(GrowthCircleObj growthCircle, int hasTeacherCertification, CircleHomeworkObj lastSubmitHomework) {
        this.growthCircle = growthCircle;
        this.hasTeacherCertification = hasTeacherCertification;
        this.lastSubmitHomework = lastSubmitHomework;
    }

    public GrowthCircleObj getGrowthCircle() {

        return growthCircle;
    }

    public void setGrowthCircle(GrowthCircleObj growthCircle) {
        this.growthCircle = growthCircle;
    }

    public int getHasTeacherCertification() {
        return hasTeacherCertification;
    }

    public void setHasTeacherCertification(int hasTeacherCertification) {
        this.hasTeacherCertification = hasTeacherCertification;
    }

    public CircleHomeworkObj getLastSubmitHomework() {
        return lastSubmitHomework;
    }

    public void setLastSubmitHomework(CircleHomeworkObj lastSubmitHomework) {
        this.lastSubmitHomework = lastSubmitHomework;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.growthCircle, flags);
        dest.writeInt(this.hasTeacherCertification);
        dest.writeParcelable(this.lastSubmitHomework, flags);
    }

    public CircleHomeWorkHeader() {
    }

    protected CircleHomeWorkHeader(Parcel in) {
        this.growthCircle = in.readParcelable(GrowthCircleObj.class.getClassLoader());
        this.hasTeacherCertification = in.readInt();
        this.lastSubmitHomework = in.readParcelable(CircleHomeworkObj.class.getClassLoader());
    }

    public static final Parcelable.Creator<CircleHomeWorkHeader> CREATOR = new Parcelable.Creator<CircleHomeWorkHeader>() {
        @Override
        public CircleHomeWorkHeader createFromParcel(Parcel source) {
            return new CircleHomeWorkHeader(source);
        }

        @Override
        public CircleHomeWorkHeader[] newArray(int size) {
            return new CircleHomeWorkHeader[size];
        }
    };
}
