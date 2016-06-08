package cn.timeface.circle.baby.api.models;

import android.graphics.Bitmap;

import cn.timeface.circle.baby.api.models.base.BaseObj;

/**
 * Created by lidonglin on 2016/5/10.
 */
public class VideoInfo extends BaseObj{
    String vedioName;
    String path;
    long duration;
    long size;
    Bitmap thumbnail;
    String imgLocalUrl;
    String imgObjectKey;
    String videoObjectKey;
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
        return thumbnail;
    }

    public void setThumbnail(Bitmap thumbnail) {
        this.thumbnail = thumbnail;
    }
}
