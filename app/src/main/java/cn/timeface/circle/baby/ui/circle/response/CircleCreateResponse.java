package cn.timeface.circle.baby.ui.circle.response;

import android.os.Parcel;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;

/**
 * 创建圈response
 * Created by lidonglin on 2017/3/14.
 */
public class CircleCreateResponse extends BaseResponse {

    private long circleId; // 创建好的圈子的圈号

    public long getCircleId() {
        return circleId;
    }

    public void setCircleId(long circleId) {
        this.circleId = circleId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(this.circleId);
    }

    public CircleCreateResponse() {
    }

    protected CircleCreateResponse(Parcel in) {
        this.circleId = in.readLong();
    }

    public static final Creator<CircleCreateResponse> CREATOR = new Creator<CircleCreateResponse>() {
        @Override
        public CircleCreateResponse createFromParcel(Parcel source) {
            return new CircleCreateResponse(source);
        }

        @Override
        public CircleCreateResponse[] newArray(int size) {
            return new CircleCreateResponse[size];
        }
    };
}
