package cn.timeface.circle.baby.ui.circle.timelines.responses;

import android.os.Parcel;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;
import cn.timeface.circle.baby.ui.circle.bean.CircleMediaObj;

/**
 * author : wangshuai Created on 2017/3/24
 * email : wangs1992321@gmail.com
 */
public class CircleMediaResponse extends BaseResponse {
    private CircleMediaObj circleMedia;

    public CircleMediaObj getCircleMedia() {
        return circleMedia;
    }

    public void setCircleMedia(CircleMediaObj circleMedia) {
        this.circleMedia = circleMedia;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(this.circleMedia, flags);
    }

    public CircleMediaResponse() {
    }

    protected CircleMediaResponse(Parcel in) {
        this.circleMedia = in.readParcelable(CircleMediaObj.class.getClassLoader());
    }

    public static final Creator<CircleMediaResponse> CREATOR = new Creator<CircleMediaResponse>() {
        @Override
        public CircleMediaResponse createFromParcel(Parcel source) {
            return new CircleMediaResponse(source);
        }

        @Override
        public CircleMediaResponse[] newArray(int size) {
            return new CircleMediaResponse[size];
        }
    };
}
