package cn.timeface.circle.baby.ui.babyInfo.beans;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;

/**
 * Created by wangshuai on 2017/1/12.
 */

public class IconHisResponse extends BaseResponse implements Parcelable {

    public List<IconHistory> getDataList() {
        return dataList;
    }

    public void setDataList(List<IconHistory> dataList) {
        this.dataList = dataList;
    }

    private List<IconHistory> dataList;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.dataList);
    }

    public IconHisResponse() {
    }

    protected IconHisResponse(Parcel in) {
        this.dataList = in.createTypedArrayList(IconHistory.CREATOR);
    }

    public static final Parcelable.Creator<IconHisResponse> CREATOR = new Parcelable.Creator<IconHisResponse>() {
        @Override
        public IconHisResponse createFromParcel(Parcel source) {
            return new IconHisResponse(source);
        }

        @Override
        public IconHisResponse[] newArray(int size) {
            return new IconHisResponse[size];
        }
    };
}
