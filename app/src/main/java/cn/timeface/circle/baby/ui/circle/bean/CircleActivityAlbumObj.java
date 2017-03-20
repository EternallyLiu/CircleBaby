package cn.timeface.circle.baby.ui.circle.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import cn.timeface.circle.baby.support.api.models.base.BaseObj;

/**
 * 活动相册对象
 * Created by lidonglin on 2017/3/14.
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class CircleActivityAlbumObj extends BaseObj implements Parcelable {
    protected long albumId;          //相册id
    protected String albumName;     //相册名称
    protected int mediaCount;       //相册中的图片数量

    public CircleActivityAlbumObj() {
    }

    public CircleActivityAlbumObj(long albumId, String albumName, int mediaCount) {
        this.albumId = albumId;
        this.albumName = albumName;
        this.mediaCount = mediaCount;
    }

    protected CircleActivityAlbumObj(Parcel in) {
        super(in);
        albumId = in.readLong();
        albumName = in.readString();
        mediaCount = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(albumId);
        dest.writeString(albumName);
        dest.writeInt(mediaCount);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CircleActivityAlbumObj> CREATOR = new Creator<CircleActivityAlbumObj>() {
        @Override
        public CircleActivityAlbumObj createFromParcel(Parcel in) {
            return new CircleActivityAlbumObj(in);
        }

        @Override
        public CircleActivityAlbumObj[] newArray(int size) {
            return new CircleActivityAlbumObj[size];
        }
    };

    public long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public int getMediaCount() {
        return mediaCount;
    }

    public void setMediaCount(int mediaCount) {
        this.mediaCount = mediaCount;
    }
}
