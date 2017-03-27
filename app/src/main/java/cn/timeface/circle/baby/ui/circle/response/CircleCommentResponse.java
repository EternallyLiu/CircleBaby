package cn.timeface.circle.baby.ui.circle.response;

import android.os.Parcel;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;
import cn.timeface.circle.baby.ui.circle.bean.CircleCommentObj;

/**
 * 圈时光评论response
 * Created by lidonglin on 2017/3/14.
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class CircleCommentResponse extends BaseResponse {
    private CircleCommentObj commentInfo;

    public CircleCommentObj getCommentInfo() {
        return commentInfo;
    }

    public void setCommentInfo(CircleCommentObj commentInfo) {
        this.commentInfo = commentInfo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(this.commentInfo, flags);
    }

    public CircleCommentResponse() {
    }

    protected CircleCommentResponse(Parcel in) {
        this.commentInfo = in.readParcelable(CircleCommentObj.class.getClassLoader());
    }

    public static final Creator<CircleCommentResponse> CREATOR = new Creator<CircleCommentResponse>() {
        @Override
        public CircleCommentResponse createFromParcel(Parcel source) {
            return new CircleCommentResponse(source);
        }

        @Override
        public CircleCommentResponse[] newArray(int size) {
            return new CircleCommentResponse[size];
        }
    };
}
