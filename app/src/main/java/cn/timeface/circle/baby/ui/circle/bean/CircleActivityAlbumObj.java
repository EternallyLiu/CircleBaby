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
    protected int albumId;          //相册id
    protected String albumName;     //相册名称
    protected int mediaCount;       //相册中的图片数量

    public CircleActivityAlbumObj() {
    }

    protected CircleActivityAlbumObj(Parcel in) {
        super(in);
        albumId = in.readInt();
        albumName = in.readString();
        mediaCount = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(albumId);
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

    public int getAlbumId() {
        return albumId;
    }

    public void setAlbumId(int albumId) {
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
