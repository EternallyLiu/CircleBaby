package cn.timeface.circle.baby.ui.circle.timelines.responses;

import android.os.Parcel;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;

/**
 * author : wangshuai Created on 2017/4/5
 * email : wangs1992321@gmail.com
 */
public class CircleAddBabyResponse extends BaseResponse {
    private long circleBabyId;
    private String babyName;
    private String babyAvatarUrl;

    public long getCircleBabyId() {
        return circleBabyId;
    }

    public void setCircleBabyId(long circleBabyId) {
        this.circleBabyId = circleBabyId;
    }

    public String getBabyName() {
        return babyName;
    }

    public void setBabyName(String babyName) {
        this.babyName = babyName;
    }

    public String getBabyAvatarUrl() {
        return babyAvatarUrl;
    }

    public void setBabyAvatarUrl(String babyAvatarUrl) {
        this.babyAvatarUrl = babyAvatarUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(this.circleBabyId);
        dest.writeString(this.babyName);
        dest.writeString(this.babyAvatarUrl);
    }

    public CircleAddBabyResponse() {
    }

    protected CircleAddBabyResponse(Parcel in) {
        super(in);
        this.circleBabyId = in.readLong();
        this.babyName = in.readString();
        this.babyAvatarUrl = in.readString();
    }

    public static final Creator<CircleAddBabyResponse> CREATOR = new Creator<CircleAddBabyResponse>() {
        @Override
        public CircleAddBabyResponse createFromParcel(Parcel source) {
            return new CircleAddBabyResponse(source);
        }

        @Override
        public CircleAddBabyResponse[] newArray(int size) {
            return new CircleAddBabyResponse[size];
        }
    };
}
