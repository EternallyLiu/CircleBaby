package cn.timeface.circle.baby.support.api.models.responses;

import android.os.Parcel;

import java.util.ArrayList;
import java.util.List;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;
import cn.timeface.circle.baby.support.api.models.objs.UserObj;

/**
 * Created by lidonglin on 2016/5/6.
 */
public class BabyInfoListResponse extends BaseResponse {

    List<UserObj> dataList;

    public List<UserObj> getDataList() {
        if (dataList == null)
            dataList = new ArrayList<>(0);
        return dataList;
    }

    public void setDataList(List<UserObj> dataList) {
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

    public BabyInfoListResponse() {
    }

    protected BabyInfoListResponse(Parcel in) {
        super(in);
        this.dataList = in.createTypedArrayList(UserObj.CREATOR);
    }

    public static final Creator<BabyInfoListResponse> CREATOR = new Creator<BabyInfoListResponse>() {
        @Override
        public BabyInfoListResponse createFromParcel(Parcel source) {
            return new BabyInfoListResponse(source);
        }

        @Override
        public BabyInfoListResponse[] newArray(int size) {
            return new BabyInfoListResponse[size];
        }
    };
}
