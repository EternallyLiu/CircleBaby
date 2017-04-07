package cn.timeface.circle.baby.ui.circle.response;

import android.os.Parcel;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;
import cn.timeface.circle.baby.ui.circle.bean.CircleMediaObj;

/**
 * 获取图片信息
 */
public class QueryCirclePhotoByIdResponse extends BaseResponse {

    private CircleMediaObj circleMedia;

    public CircleMediaObj getCircleMedia() {
        return circleMedia;
    }

    public void setCircleMedia(CircleMediaObj circleMedia) {
        this.circleMedia = circleMedia;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(this.circleMedia, flags);
    }

    public QueryCirclePhotoByIdResponse() {
    }

    protected QueryCirclePhotoByIdResponse(Parcel in) {
        super(in);
        this.circleMedia = in.readParcelable(CircleMediaObj.class.getClassLoader());
    }

    public static final Creator<QueryCirclePhotoByIdResponse> CREATOR = new Creator<QueryCirclePhotoByIdResponse>() {
        @Override
        public QueryCirclePhotoByIdResponse createFromParcel(Parcel source) {
            return new QueryCirclePhotoByIdResponse(source);
        }

        @Override
        public QueryCirclePhotoByIdResponse[] newArray(int size) {
            return new QueryCirclePhotoByIdResponse[size];
        }
    };
}
