package cn.timeface.circle.baby.api.models.objs;

import android.os.Parcel;
import android.os.Parcelable;

import cn.timeface.circle.baby.api.models.base.BaseObj;

/**
 * Created by zhsheng on 2016/6/8.
 */
public class CloudAlbumDetailObj extends BaseObj implements Parcelable {
    private String id;
    private String content;
    private int h;
    private int w;
    private String imgUrl;
    private int length;
    private String localPath;
    private long photographTime;
    private String videoUrl;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }

    public int getW() {
        return w;
    }

    public void setW(int w) {
        this.w = w;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public long getPhotographTime() {
        return photographTime;
    }

    public void setPhotographTime(long photographTime) {
        this.photographTime = photographTime;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public CloudAlbumDetailObj() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.content);
        dest.writeInt(this.h);
        dest.writeInt(this.w);
        dest.writeString(this.imgUrl);
        dest.writeInt(this.length);
        dest.writeString(this.localPath);
        dest.writeLong(this.photographTime);
        dest.writeString(this.videoUrl);
    }

    protected CloudAlbumDetailObj(Parcel in) {
        this.id = in.readString();
        this.content = in.readString();
        this.h = in.readInt();
        this.w = in.readInt();
        this.imgUrl = in.readString();
        this.length = in.readInt();
        this.localPath = in.readString();
        this.photographTime = in.readLong();
        this.videoUrl = in.readString();
    }

    public static final Creator<CloudAlbumDetailObj> CREATOR = new Creator<CloudAlbumDetailObj>() {
        @Override
        public CloudAlbumDetailObj createFromParcel(Parcel source) {
            return new CloudAlbumDetailObj(source);
        }

        @Override
        public CloudAlbumDetailObj[] newArray(int size) {
            return new CloudAlbumDetailObj[size];
        }
    };
}
