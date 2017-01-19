package cn.timeface.circle.baby.ui.images.beans;

import android.os.Parcel;
import android.os.Parcelable;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;

/**
 * author : wangshuai Created on 2017/1/19
 * email : wangs1992321@gmail.com
 */
public class LikeResponse extends BaseResponse implements Parcelable {

    private int favoritecount;

    public int getFavoritecount() {
        return favoritecount;
    }

    public void setFavoritecount(int favoritecount) {
        this.favoritecount = favoritecount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.favoritecount);
    }

    public LikeResponse() {
    }

    protected LikeResponse(Parcel in) {
        this.favoritecount = in.readInt();
    }

    public static final Parcelable.Creator<LikeResponse> CREATOR = new Parcelable.Creator<LikeResponse>() {
        @Override
        public LikeResponse createFromParcel(Parcel source) {
            return new LikeResponse(source);
        }

        @Override
        public LikeResponse[] newArray(int size) {
            return new LikeResponse[size];
        }
    };
}
