package cn.timeface.circle.baby.ui.circle.timelines.responses;

import android.os.Parcel;

import java.util.List;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;
import cn.timeface.circle.baby.ui.circle.bean.CircleActivityAlbumObj;

/**
 * author : wangshuai Created on 2017/3/16
 * email : wangs1992321@gmail.com
 */
public class ActiveSelectListResponse extends BaseResponse {

    private List<CircleActivityAlbumObj> dataList;

    public List<CircleActivityAlbumObj> getDataList() {
        return dataList;
    }

    public void setDataList(List<CircleActivityAlbumObj> dataList) {
        this.dataList = dataList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeTypedList(this.dataList);
    }

    public ActiveSelectListResponse() {
    }

    protected ActiveSelectListResponse(Parcel in) {
        this.dataList = in.createTypedArrayList(CircleActivityAlbumObj.CREATOR);
    }

    public static final Creator<ActiveSelectListResponse> CREATOR = new Creator<ActiveSelectListResponse>() {
        @Override
        public ActiveSelectListResponse createFromParcel(Parcel source) {
            return new ActiveSelectListResponse(source);
        }

        @Override
        public ActiveSelectListResponse[] newArray(int size) {
            return new ActiveSelectListResponse[size];
        }
    };
}
