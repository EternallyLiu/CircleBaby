package cn.timeface.circle.baby.ui.timelines.beans;

import android.os.Parcel;
import android.os.Parcelable;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;

/**
 * author : wangshuai Created on 2017/2/20
 * email : wangs1992321@gmail.com
 */
public class QueryLocationInfoResponse extends BaseResponse implements Parcelable {

    private NearLocationObj locationInfo;

    public NearLocationObj getLocationInfo() {
        return locationInfo;
    }

    public void setLocationInfo(NearLocationObj locationInfo) {
        this.locationInfo = locationInfo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.locationInfo, flags);
    }

    public QueryLocationInfoResponse() {
    }

    protected QueryLocationInfoResponse(Parcel in) {
        this.locationInfo = in.readParcelable(NearLocationObj.class.getClassLoader());
    }

    public static final Parcelable.Creator<QueryLocationInfoResponse> CREATOR = new Parcelable.Creator<QueryLocationInfoResponse>() {
        @Override
        public QueryLocationInfoResponse createFromParcel(Parcel source) {
            return new QueryLocationInfoResponse(source);
        }

        @Override
        public QueryLocationInfoResponse[] newArray(int size) {
            return new QueryLocationInfoResponse[size];
        }
    };
}
