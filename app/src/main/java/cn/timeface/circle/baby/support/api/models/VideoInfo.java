package cn.timeface.circle.baby.support.api.models;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ColumnIgnore;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.timeface.circle.baby.support.api.models.base.BaseObj;
import cn.timeface.circle.baby.support.api.models.db.AppDatabase;
import cn.timeface.circle.baby.support.api.models.objs.MyUploadFileObj;
import cn.timeface.circle.baby.support.oss.uploadservice.UploadFileObj;
import cn.timeface.circle.baby.ui.timelines.Utils.LogUtil;
import rx.Observable;
import rx.functions.Func0;

/**
 * Created by lidonglin on 2016/5/10.
 */
@Table(database = AppDatabase.class)
public class VideoInfo extends BaseModel implements Parcelable {
    @PrimaryKey
    String path;
    @Column
    String vedioName;
    @Column
    long duration;
    @Column
    long size;
    @ColumnIgnore
    Bitmap thumbnail;
    @Column
    String thumbmailLocalUrl;
    @Column
    String imgLocalUrl;
    @Column
    String imgObjectKey;
    @Column
    String videoObjectKey;
    @Column
    long Date;
    @Column
    int type = 0;  //类型，0、默认手机的视频资源，1、裁剪资源，2、下载保存下来的资源
    @Column
    String videoUrl;    //远程地址


    public VideoInfo() {
    }

    public VideoInfo(long duration, String imgObjectKey, String path, long date) {
        this.duration = duration;
        this.imgObjectKey = imgObjectKey;
        this.path = path;
        Date = date;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getImgLocalUrl() {
        return imgLocalUrl;
    }

    public void setImgLocalUrl(String imgLocalUrl) {
        this.imgLocalUrl = imgLocalUrl;
    }

    public String getImgObjectKey() {
        return imgObjectKey;
    }

    public void setImgObjectKey(String imgObjectKey) {
        this.imgObjectKey = imgObjectKey;
    }

    public String getVideoObjectKey() {
        if (TextUtils.isEmpty(videoObjectKey)) {
            UploadFileObj uploadFileObj = new MyUploadFileObj(path);
            videoObjectKey = uploadFileObj.getObjectKey();
            save();
        }
        return videoObjectKey;
    }

    public void setVideoObjectKey(String videoObjectKey) {
        this.videoObjectKey = videoObjectKey;
    }

    public long getDate() {
        return Date;
    }

    public void setDate(long date) {
        Date = date;
    }

    public String getVedioName() {
        return vedioName;
    }

    public void setVedioName(String vedioName) {
        this.vedioName = vedioName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long durantion) {
        this.duration = durantion;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public Bitmap getThumbnail() {
//        if (thumbnail == null)
//            thumbnail = ThumbnailUtils.createVideoThumbnail(getPath(), MediaStore.Video.Thumbnails.MINI_KIND);
        return thumbnail;
    }

    public void setThumbnail(Bitmap thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getThumbmailLocalUrl() {
        return thumbmailLocalUrl;
    }

    public void setThumbmailLocalUrl(String thumbmailLocalUrl) {
        this.thumbmailLocalUrl = thumbmailLocalUrl;
    }

    public static void saveAll(List<VideoInfo> list) {
        if (list != null && list.size() > 0)
            FlowManager.getModelAdapter(VideoInfo.class).saveAll(list);
    }

    /**
     * 依据objectKey获取VideoInfo对象
     *
     * @param url
     * @return
     */
    public static Observable<VideoInfo> findVideo(String url) {

        return Observable.just(SQLite.select().from(VideoInfo.class).where(VideoInfo_Table.videoObjectKey.eq(url)).querySingle());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VideoInfo videoInfo = (VideoInfo) o;

        if (path != null ? !path.equals(videoInfo.path) : videoInfo.path != null) return false;
        return true;

    }

    /**
     * 查询VideoInfo对象
     *
     * @param path path objectkey,imageurl等
     * @return
     */
    public static Observable<VideoInfo> findVideos(String path) {
        return Observable.defer(() -> {
            return Observable.from(SQLite.select().from(VideoInfo.class).where(VideoInfo_Table.path.eq(path)).or(VideoInfo_Table.videoObjectKey.eq(path))
                    .or(VideoInfo_Table.videoUrl.eq(path)).or(VideoInfo_Table.thumbmailLocalUrl.eq(path))
                    .or(VideoInfo_Table.imgLocalUrl.eq(path)).queryList());
        });
    }

    public boolean isThumbmail() {
        if (TextUtils.isEmpty(getThumbmailLocalUrl()))
            return false;
        else if (new File(getThumbmailLocalUrl()).exists())
            return true;
        else return false;
    }

    @Override
    public int hashCode() {
        int result = path != null ? path.hashCode() : 0;
        result = 31 * result + (vedioName != null ? vedioName.hashCode() : 0);
        result = 31 * result + (int) (duration ^ (duration >>> 32));
        result = 31 * result + (int) (size ^ (size >>> 32));
        result = 31 * result + (thumbnail != null ? thumbnail.hashCode() : 0);
        result = 31 * result + (thumbmailLocalUrl != null ? thumbmailLocalUrl.hashCode() : 0);
        result = 31 * result + (imgLocalUrl != null ? imgLocalUrl.hashCode() : 0);
        result = 31 * result + (imgObjectKey != null ? imgObjectKey.hashCode() : 0);
        result = 31 * result + (videoObjectKey != null ? videoObjectKey.hashCode() : 0);
        result = 31 * result + (int) (Date ^ (Date >>> 32));
        return result;
    }

    public static List<VideoInfo> getVideoList() {
        List<VideoInfo> infos = SQLite.select().from(VideoInfo.class).queryList();
        return infos;
    }


    public static Observable<List<VideoInfo>> getAllVideos() {
        return Observable.defer(() -> {
            LogUtil.showLog("getAllVideos=====>" + Thread.currentThread().getName());
            List<VideoInfo> infos = SQLite.select().from(VideoInfo.class).queryList();
            return Observable.just(infos);
        });
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.path);
        dest.writeString(this.vedioName);
        dest.writeLong(this.duration);
        dest.writeLong(this.size);
        dest.writeParcelable(this.thumbnail, flags);
        dest.writeString(this.thumbmailLocalUrl);
        dest.writeString(this.imgLocalUrl);
        dest.writeString(this.imgObjectKey);
        dest.writeString(this.videoObjectKey);
        dest.writeLong(this.Date);
        dest.writeInt(this.type);
    }

    protected VideoInfo(Parcel in) {
        this.path = in.readString();
        this.vedioName = in.readString();
        this.duration = in.readLong();
        this.size = in.readLong();
        this.thumbnail = in.readParcelable(Bitmap.class.getClassLoader());
        this.thumbmailLocalUrl = in.readString();
        this.imgLocalUrl = in.readString();
        this.imgObjectKey = in.readString();
        this.videoObjectKey = in.readString();
        this.Date = in.readLong();
        this.type = in.readInt();
    }

    public static final Creator<VideoInfo> CREATOR = new Creator<VideoInfo>() {
        @Override
        public VideoInfo createFromParcel(Parcel source) {
            return new VideoInfo(source);
        }

        @Override
        public VideoInfo[] newArray(int size) {
            return new VideoInfo[size];
        }
    };
}
