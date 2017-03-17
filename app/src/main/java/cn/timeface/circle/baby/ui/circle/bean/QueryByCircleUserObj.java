package cn.timeface.circle.baby.ui.circle.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import cn.timeface.circle.baby.support.api.models.base.BaseObj;
import cn.timeface.circle.baby.support.api.models.objs.BabyObj;

/**
 * Created by lidonglin on 2017/3/17.
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class QueryByCircleUserObj extends BaseObj implements Parcelable {
    protected CircleUserInfo userInfo;
    protected int mediaCount;           //该用户发布的照片数量

    public QueryByCircleUserObj() {
    }

    public QueryByCircleUserObj(Parcel in) {
        super(in);
        userInfo = in.readParcelable(CircleUserInfo.class.getClassLoader());
        mediaCount = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(userInfo, flags);
        dest.writeInt(mediaCount);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<QueryByCircleUserObj> CREATOR = new Creator<QueryByCircleUserObj>() {
        @Override
        public QueryByCircleUserObj createFromParcel(Parcel in) {
            return new QueryByCircleUserObj(in);
        }

        @Override
        public QueryByCircleUserObj[] newArray(int size) {
            return new QueryByCircleUserObj[size];
        }
    };

    public CircleUserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(CircleUserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public int getMediaCount() {
        return mediaCount;
    }

    public void setMediaCount(int mediaCount) {
        this.mediaCount = mediaCount;
    }

    public static Creator<QueryByCircleUserObj> getCREATOR() {
        return CREATOR;
    }
}
