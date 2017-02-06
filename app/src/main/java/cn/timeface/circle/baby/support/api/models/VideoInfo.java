package cn.timeface.circle.baby.support.api.models;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ColumnIgnore;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.ArrayList;
import java.util.List;

import cn.timeface.circle.baby.support.api.models.base.BaseObj;
import cn.timeface.circle.baby.support.api.models.db.AppDatabase;
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

    public VideoInfo() {
    }

    public VideoInfo(long duration, String imgObjectKey, String path, long date) {
        this.duration = duration;
        this.imgObjectKey = imgObjectKey;
        this.path = path;
        Date = date;
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

    public static Observable<List<VideoInfo>> getAllVideos() {
        return Observable.defer(new Func0<Observable<List<VideoInfo>>>() {
            @Override
            public Observable<List<VideoInfo>> call() {
                LogUtil.showLog("getAllVideos=====>" + Thread.currentThread().getName());
                List<VideoInfo> infos = SQLite.select().from(VideoInfo.class).queryList();
                return Observable.just(infos);
            }
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
