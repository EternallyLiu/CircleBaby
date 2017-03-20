package cn.timeface.circle.baby.ui.circle.timelines.responses;

import android.os.Parcel;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;
import cn.timeface.circle.baby.ui.circle.bean.CircleTimelineObj;

/**
 * author : wangshuai Created on 2017/3/17
 * email : wangs1992321@gmail.com
 */
public class TimeLineSendResponse extends BaseResponse {

    private CircleTimelineObj circleTimeline;

    public CircleTimelineObj getCircleTimeline() {
        return circleTimeline;
    }

    public void setCircleTimeline(CircleTimelineObj circleTimeline) {
        this.circleTimeline = circleTimeline;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(this.circleTimeline, flags);
    }

    public TimeLineSendResponse() {
    }

    protected TimeLineSendResponse(Parcel in) {
        this.circleTimeline = in.readParcelable(CircleTimelineObj.class.getClassLoader());
    }

    public static final Creator<TimeLineSendResponse> CREATOR = new Creator<TimeLineSendResponse>() {
        @Override
        public TimeLineSendResponse createFromParcel(Parcel source) {
            return new TimeLineSendResponse(source);
        }

        @Override
        public TimeLineSendResponse[] newArray(int size) {
            return new TimeLineSendResponse[size];
        }
    };
}
