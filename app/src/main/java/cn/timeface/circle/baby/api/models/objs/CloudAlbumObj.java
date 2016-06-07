package cn.timeface.circle.baby.api.models.objs;

import android.os.Parcel;
import android.os.Parcelable;

import cn.timeface.circle.baby.api.models.base.BaseObj;

/**
 * Created by zhsheng on 2016/6/7.
 */
public class CloudAlbumObj extends BaseObj implements Parcelable {


    /**
     * contentInfo : 14张图片 1个视频
     * desc : 宝宝周岁相册
     * id : 23
     * imgUrl : http://img1.timeface.cn/baby/103a133ea330410ef6e9857e3cea1759.jpg
     * time : 0
     * title : 周岁
     */

    private String contentInfo;
    private String desc;
    private String id;
    private String imgUrl;
    private long time;
    private String title;

    public String getContentInfo() {
        return contentInfo;
    }

    public void setContentInfo(String contentInfo) {
        this.contentInfo = contentInfo;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.contentInfo);
        dest.writeString(this.desc);
        dest.writeString(this.id);
        dest.writeString(this.imgUrl);
        dest.writeLong(this.time);
        dest.writeString(this.title);
    }

    public CloudAlbumObj() {
    }

    protected CloudAlbumObj(Parcel in) {
        this.contentInfo = in.readString();
        this.desc = in.readString();
        this.id = in.readString();
        this.imgUrl = in.readString();
        this.time = in.readLong();
        this.title = in.readString();
    }

    public static final Parcelable.Creator<CloudAlbumObj> CREATOR = new Parcelable.Creator<CloudAlbumObj>() {
        @Override
        public CloudAlbumObj createFromParcel(Parcel source) {
            return new CloudAlbumObj(source);
        }

        @Override
        public CloudAlbumObj[] newArray(int size) {
            return new CloudAlbumObj[size];
        }
    };
}
