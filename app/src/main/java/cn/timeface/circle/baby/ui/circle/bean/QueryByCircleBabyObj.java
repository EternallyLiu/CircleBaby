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
public class QueryByCircleBabyObj extends BaseObj implements Parcelable {
    protected BabyObj babyInfo;
    protected int mediaCount;       //圈中该宝宝的照片数量

    public QueryByCircleBabyObj() {
    }

    public QueryByCircleBabyObj(Parcel in) {
        super(in);
        babyInfo = in.readParcelable(BabyObj.class.getClassLoader());
        mediaCount = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(babyInfo, flags);
        dest.writeInt(mediaCount);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<QueryByCircleBabyObj> CREATOR = new Creator<QueryByCircleBabyObj>() {
        @Override
        public QueryByCircleBabyObj createFromParcel(Parcel in) {
            return new QueryByCircleBabyObj(in);
        }

        @Override
        public QueryByCircleBabyObj[] newArray(int size) {
            return new QueryByCircleBabyObj[size];
        }
    };

    public BabyObj getBabyInfo() {
        return babyInfo;
    }

    public void setBabyInfo(BabyObj babyInfo) {
        this.babyInfo = babyInfo;
    }

    public int getMediaCount() {
        return mediaCount;
    }

    public void setMediaCount(int mediaCount) {
        this.mediaCount = mediaCount;
    }


}
