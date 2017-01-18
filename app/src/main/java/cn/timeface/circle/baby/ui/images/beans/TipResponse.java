package cn.timeface.circle.baby.ui.images.beans;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;

/**
 * author : wangshuai Created on 2017/1/17
 * email : wangs1992321@gmail.com
 */
public class TipResponse extends BaseResponse implements Parcelable {

    private ArrayList<TipObj> recommendTips;
    private ArrayList<TipObj> historyTips;

    public ArrayList<TipObj> getRecommendTips() {
        return recommendTips;
    }

    public void setRecommendTips(ArrayList<TipObj> recommendTips) {
        this.recommendTips = recommendTips;
    }

    public ArrayList<TipObj> getHistoryTips() {
        return historyTips;
    }

    public void setHistoryTips(ArrayList<TipObj> historyTips) {
        this.historyTips = historyTips;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(this.recommendTips);
        dest.writeList(this.historyTips);
    }

    public TipResponse() {
    }

    protected TipResponse(Parcel in) {
        this.recommendTips = new ArrayList<TipObj>();
        in.readList(this.recommendTips, TipObj.class.getClassLoader());
        this.historyTips = new ArrayList<TipObj>();
        in.readList(this.historyTips, TipObj.class.getClassLoader());
    }

    public static final Parcelable.Creator<TipResponse> CREATOR = new Parcelable.Creator<TipResponse>() {
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
