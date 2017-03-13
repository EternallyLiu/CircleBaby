package cn.timeface.circle.baby.ui.timelines.beans;

import android.os.Parcel;
import android.os.Parcelable;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;
import cn.timeface.circle.baby.support.api.models.objs.TimeLineObj;

/**
 * author : wangshuai Created on 2017/2/20
 * email : wangs1992321@gmail.com
 */
public class SendTimeLineResponse extends BaseResponse implements Parcelable {
    private TimeLineObj timeInfo;

    public TimeLineObj getTimeInfo() {
        return timeInfo;
    }

    public void setTimeInfo(TimeLineObj timeInfo) {
        this.timeInfo = timeInfo;
    }

    public SendTimeLineResponse() {

    }

    public SendTimeLineResponse(TimeLineObj timeInfo) {

        this.timeInfo = timeInfo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.timeInfo, flags);
    }

    protected SendTimeLineResponse(Parcel in) {
        this.timeInfo = in.readParcelable(TimeLineObj.class.getClassLoader());
    }

    public static final Parcelable.Creator<SendTimeLineResponse> CREATOR = new Parcelable.Creator<SendTimeLineResponse>() {
        @Override
        public SendTimeLineResponse createFromParcel(Parcel source) {
            return new SendTimeLineResponse(source);
        }

        @Override
        public SendTimeLineResponse[] newArray(int size) {
            return new SendTimeLineResponse[size];
        }
    };
}
