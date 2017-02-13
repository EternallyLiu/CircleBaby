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
    protected long cardId;
    protected MediaObj media;
    protected int select;

    public boolean select(){
        return select == 1;
    }

    public int getSelect() {
        return select;
    }

    public void setSelect(int select) {
        this.select = select;
    }

    public long getCardId() {
        return cardId;
    }

    public void setCartdId(long cartdId) {
        this.cardId = cartdId;
    }

    public MediaObj getMedia() {
        return media;
    }

    public void setMedia(MediaObj media) {
        this.media = media;
    }

    public CardObj() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.cardId);
        dest.writeParcelable(this.media, flags);
        dest.writeInt(this.select);
    }

    protected CardObj(Parcel in) {
        this.cardId = in.readLong();
        this.media = in.readParcelable(MediaObj.class.getClassLoader());
        this.select = in.readInt();
    }

    @Override
    public boolean equals(Object obj) {
        CardObj other = (CardObj) obj;
        return cardId == other.cardId;
    }
}
