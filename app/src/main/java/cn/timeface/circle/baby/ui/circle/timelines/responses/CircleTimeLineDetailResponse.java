package cn.timeface.circle.baby.ui.circle.timelines.responses;

import android.os.Parcel;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;
import cn.timeface.circle.baby.ui.circle.bean.CircleTimelineObj;

/**
 * author : wangshuai Created on 2017/3/22
 * email : wangs1992321@gmail.com
 */
public class CircleTimeLineDetailResponse extends BaseResponse{
    private CircleTimelineObj circleTimelineInfo;

    public CircleTimelineObj getCircleTimelineInfo() {
        return circleTimelineInfo;
    }

    public void setCircleTimelineInfo(CircleTimelineObj circleTimelineInfo) {
        this.circleTimelineInfo = circleTimelineInfo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(this.circleTimelineInfo, flags);
    }

    public CircleTimeLineDetailResponse() {
    }

    protected CircleTimeLineDetailResponse(Parcel in) {
        this.circleTimelineInfo = in.readParcelable(CircleTimelineObj.class.getClassLoader());
    }

    public static final Creator<CircleTimeLineDetailResponse> CREATOR = new Creator<CircleTimeLineDetailResponse>() {
        @Override
        public CircleTimeLineDetailResponse createFromParcel(Parcel source) {
            return new CircleTimeLineDetailResponse(source);
        }

        @Override
        public CircleTimeLineDetailResponse[] newArray(int size) {
            return new CircleTimeLineDetailResponse[size];
        }
    };
}
