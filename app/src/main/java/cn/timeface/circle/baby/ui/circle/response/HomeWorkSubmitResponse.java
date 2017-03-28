package cn.timeface.circle.baby.ui.circle.response;

import android.os.Parcel;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;
import cn.timeface.circle.baby.ui.circle.bean.CircleHomeworkObj;

/**
 * 家长提交作业的详情response
 * Created by lidonglin on 2017/3/17.
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class HomeWorkSubmitResponse extends BaseResponse {
    private CircleHomeworkObj homework;

    public CircleHomeworkObj getCircleTimeline() {
        return homework;
    }

    public CircleHomeworkObj getHomework() {
        return homework;
    }

    public void setHomework(CircleHomeworkObj homework) {
        this.homework = homework;
    }

    public void setCircleTimeline(CircleHomeworkObj homework) {
        this.homework = homework;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(this.homework, flags);
    }

    public HomeWorkSubmitResponse() {
    }

    protected HomeWorkSubmitResponse(Parcel in) {
        super(in);
        this.homework = in.readParcelable(CircleHomeworkObj.class.getClassLoader());
    }

    public static final Creator<HomeWorkSubmitResponse> CREATOR = new Creator<HomeWorkSubmitResponse>() {
        @Override
        public HomeWorkSubmitResponse createFromParcel(Parcel source) {
            return new HomeWorkSubmitResponse(source);
        }

        @Override
        public HomeWorkSubmitResponse[] newArray(int size) {
            return new HomeWorkSubmitResponse[size];
        }
    };
}
