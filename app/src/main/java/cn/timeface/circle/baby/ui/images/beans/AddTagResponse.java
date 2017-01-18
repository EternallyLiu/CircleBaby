package cn.timeface.circle.baby.ui.images.beans;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;
import cn.timeface.circle.baby.support.api.models.objs.MediaTipObj;

/**
 * author : wangshuai Created on 2017/1/17
 * email : wangs1992321@gmail.com
 */
public class AddTagResponse extends BaseResponse implements Parcelable {

    private List<MediaTipObj> tips;

    public AddTagResponse() {
    }

    public List<MediaTipObj> getTips() {
        return tips;
    }

    public void setTips(List<MediaTipObj> tips) {
        this.tips = tips;
    }

    public AddTagResponse(List<MediaTipObj> tips) {
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
        this.tips = in.createTypedArrayList(MediaTipObj.CREATOR);
    }

    public static final Creator<AddTagResponse> CREATOR = new Creator<AddTagResponse>() {
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
