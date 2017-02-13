package cn.timeface.circle.baby.ui.timelines.beans;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;

/**
 * author : wangshuai Created on 2017/2/9
 * email : wangs1992321@gmail.com
 */
public class NearLocalResponse extends BaseResponse implements Parcelable {

    private List<NearLocationObj> dataList;

    public List<NearLocationObj> getDataList() {
        return dataList;
    }

    public void setDataList(List<NearLocationObj> dataList) {
        this.dataList = dataList;
    }

    public NearLocalResponse(List<NearLocationObj> dataList) {
        this.dataList = dataList;
    }

    public NearLocalResponse() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.dataList);
    }

    protected NearLocalResponse(Parcel in) {
        this.dataList = in.createTypedArrayList(NearLocationObj.CREATOR);
    }

    public static final Parcelable.Creator<NearLocalResponse> CREATOR = new Parcelable.Creator<NearLocalResponse>() {
        @Override
        public NearLocalResponse createFromParcel(Parcel source) {
            return new NearLocalResponse(source);
        }

        @Override
        public NearLocalResponse[] newArray(int size) {
            return new NearLocalResponse[size];
        }
    };
}
