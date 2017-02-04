package cn.timeface.circle.baby.support.api.models.objs;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonObject;

/**
 * card obj
 * author : YW.SUN Created on 2017/1/12
 * email : sunyw10@gmail.com
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class CardObj implements Parcelable {
    protected long cartdId;
    protected MediaObj media;

    public long getCartdId() {
        return cartdId;
    }

    public void setCartdId(long cartdId) {
        this.cartdId = cartdId;
    }

    public MediaObj getMedia() {
        return media;
    }

    public void setMedia(MediaObj media) {
        this.media = media;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.cartdId);
        dest.writeParcelable(this.media, flags);
    }

    public CardObj() {
    }

    protected CardObj(Parcel in) {
        this.cartdId = in.readLong();
        this.media = in.readParcelable(MediaObj.class.getClassLoader());
    }

    public static final Creator<CardObj> CREATOR = new Creator<CardObj>() {
        @Override
        public CardObj createFromParcel(Parcel source) {
            return new CardObj(source);
        }

        @Override
        public CardObj[] newArray(int size) {
            return new CardObj[size];
        }
    };
}
