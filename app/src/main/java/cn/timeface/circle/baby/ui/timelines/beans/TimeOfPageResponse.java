package cn.timeface.circle.baby.ui.timelines.beans;

import android.os.Parcel;
import android.os.Parcelable;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;

/**
 * author : wangshuai Created on 2017/2/20
 * email : wangs1992321@gmail.com
 */
public class TimeOfPageResponse extends BaseResponse implements Parcelable {
    private int pageNo;

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.pageNo);
    }

    public TimeOfPageResponse() {
    }

    protected TimeOfPageResponse(Parcel in) {
        this.pageNo = in.readInt();
    }

    public static final Parcelable.Creator<TimeOfPageResponse> CREATOR = new Parcelable.Creator<TimeOfPageResponse>() {
        @Override
        public TimeOfPageResponse createFromParcel(Parcel source) {
            return new TimeOfPageResponse(source);
        }

        @Override
        public TimeOfPageResponse[] newArray(int size) {
            return new TimeOfPageResponse[size];
        }
    };
}
