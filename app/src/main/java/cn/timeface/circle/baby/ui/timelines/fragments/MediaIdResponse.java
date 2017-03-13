package cn.timeface.circle.baby.ui.timelines.fragments;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;

/**
 * author : wangshuai Created on 2017/2/14
 * email : wangs1992321@gmail.com
 */
public class MediaIdResponse extends BaseResponse implements Parcelable {
    private List<String> dataList;

    public List<String> getDataList() {
        return dataList;
    }

    public void setDataList(List<String> dataList) {
        this.dataList = dataList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(this.dataList);
    }

    public MediaIdResponse() {
    }

    protected MediaIdResponse(Parcel in) {
        this.dataList = in.createStringArrayList();
    }

    public static final Parcelable.Creator<MediaIdResponse> CREATOR = new Parcelable.Creator<MediaIdResponse>() {
        @Override
        public MediaIdResponse createFromParcel(Parcel source) {
            return new MediaIdResponse(source);
        }

        @Override
        public MediaIdResponse[] newArray(int size) {
            return new MediaIdResponse[size];
        }
    };
}
