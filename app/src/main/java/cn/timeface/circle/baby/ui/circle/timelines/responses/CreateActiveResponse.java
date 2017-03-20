package cn.timeface.circle.baby.ui.circle.timelines.responses;

import android.os.Parcel;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;
import cn.timeface.circle.baby.ui.circle.bean.CircleActivityAlbumObj;

/**
 * author : wangshuai Created on 2017/3/16
 * email : wangs1992321@gmail.com
 */
public class CreateActiveResponse extends BaseResponse {

    private CircleActivityAlbumObj activityAlbum;

    public CircleActivityAlbumObj getActivityAlbum() {
        return activityAlbum;
    }

    public void setActivityAlbum(CircleActivityAlbumObj activityAlbum) {
        this.activityAlbum = activityAlbum;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(this.activityAlbum, flags);
    }

    public CreateActiveResponse() {
    }

    protected CreateActiveResponse(Parcel in) {
        this.activityAlbum = in.readParcelable(CircleActivityAlbumObj.class.getClassLoader());
    }

    public static final Creator<CreateActiveResponse> CREATOR = new Creator<CreateActiveResponse>() {
        @Override
        public CreateActiveResponse createFromParcel(Parcel source) {
            return new CreateActiveResponse(source);
        }

        @Override
        public CreateActiveResponse[] newArray(int size) {
            return new CreateActiveResponse[size];
        }
    };
}
