package cn.timeface.circle.baby.ui.images.beans;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;
import cn.timeface.circle.baby.support.api.models.objs.MediaTipObj;

/**
 * author : wangshuai Created on 2017/1/17
 * email : wangs1992321@gmail.com
 */
public class TipResponse extends BaseResponse implements Parcelable {

    private ArrayList<MediaTipObj> recommendTips;
    private ArrayList<MediaTipObj> historyTips;

    public ArrayList<MediaTipObj> getRecommendTips() {
        return recommendTips;
    }

    public void setRecommendTips(ArrayList<MediaTipObj> recommendTips) {
        this.recommendTips = recommendTips;
    }

    public ArrayList<MediaTipObj> getHistoryTips() {
        return historyTips;
    }

    public void setHistoryTips(ArrayList<MediaTipObj> historyTips) {
        this.historyTips = historyTips;
    }

    public TipResponse() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.recommendTips);
        dest.writeTypedList(this.historyTips);
    }

    protected TipResponse(Parcel in) {
        this.recommendTips = in.createTypedArrayList(MediaTipObj.CREATOR);
        this.historyTips = in.createTypedArrayList(MediaTipObj.CREATOR);
    }

    public static final Creator<TipResponse> CREATOR = new Creator<TipResponse>() {
        @Override
        public TipResponse createFromParcel(Parcel source) {
            return new TipResponse(source);
        }

        @Override
        public TipResponse[] newArray(int size) {
            return new TipResponse[size];
        }
    };
}
