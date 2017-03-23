package cn.timeface.circle.baby.ui.circle.timelines.responses;

import android.os.Parcel;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;
import cn.timeface.circle.baby.ui.circle.bean.CircleSchoolTaskObj;

/**
 * author : wangshuai Created on 2017/3/22
 * email : wangs1992321@gmail.com
 */
public class CircleSchoolTaskResponse extends BaseResponse{

    private CircleSchoolTaskObj schoolTask;

    public CircleSchoolTaskObj getSchoolTask() {
        return schoolTask;
    }

    public void setSchoolTask(CircleSchoolTaskObj schoolTask) {
        this.schoolTask = schoolTask;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(this.schoolTask, flags);
    }

    public CircleSchoolTaskResponse() {
    }

    protected CircleSchoolTaskResponse(Parcel in) {
        this.schoolTask = in.readParcelable(CircleSchoolTaskObj.class.getClassLoader());
    }

    public static final Creator<CircleSchoolTaskResponse> CREATOR = new Creator<CircleSchoolTaskResponse>() {
        @Override
        public CircleSchoolTaskResponse createFromParcel(Parcel source) {
            return new CircleSchoolTaskResponse(source);
        }

        @Override
        public CircleSchoolTaskResponse[] newArray(int size) {
            return new CircleSchoolTaskResponse[size];
        }
    };
}
