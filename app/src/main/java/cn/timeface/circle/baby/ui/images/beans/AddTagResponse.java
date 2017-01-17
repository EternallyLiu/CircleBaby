package cn.timeface.circle.baby.ui.images.beans;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;

/**
 * author : wangshuai Created on 2017/1/17
 * email : wangs1992321@gmail.com
 */
public class AddTagResponse extends BaseResponse implements Parcelable {

    private List<TipObj> tips;

    public AddTagResponse() {
    }

    public AddTagResponse(List<TipObj> tips) {

        this.tips = tips;
    }

    public List<TipObj> getTips() {
        return tips;
    }

    public void setTips(List<TipObj> tips) {
        this.tips = tips;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.tips);
    }

    protected AddTagResponse(Parcel in) {
        this.tips = in.createTypedArrayList(TipObj.CREATOR);
    }

    public static final Parcelable.Creator<AddTagResponse> CREATOR = new Parcelable.Creator<AddTagResponse>() {
        @Override
        public AddTagResponse createFromParcel(Parcel source) {
            return new AddTagResponse(source);
        }

        @Override
        public AddTagResponse[] newArray(int size) {
            return new AddTagResponse[size];
        }
    };
}
