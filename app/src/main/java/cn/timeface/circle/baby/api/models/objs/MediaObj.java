package cn.timeface.circle.baby.api.models.objs;

import cn.timeface.circle.baby.api.models.base.BaseObj;

/**
 * Created by lidonglin on 2016/5/9.
 */
public class MediaObj extends BaseObj {
    String content;         //图片描述
    int h;                  //长度
    int w;                  //宽度
    int id;
    String imgUrl;          //如果是视频则是视频某一帧图片
    long length;             //视频长度
    String localPath;       //图片本地路径
    long photographTime;     //照片or视频的拍摄时间
    String videoUrl;        //视频url

    //图片
    public MediaObj(String content,String imgUrl, int w , int h ,long photographTime) {
        this.content = content;
        this.imgUrl = imgUrl;
        this.w = w;
        this.h = h;
        this.photographTime = photographTime;
    }

    //视频
    public MediaObj(String imgUrl, long length, String videoUrl, long photographTime) {
        this.imgUrl = imgUrl;
        this.length = length;
        this.videoUrl = videoUrl;
        this.photographTime = photographTime;
    }


    public MediaObj(String content, int h, int w, int id, String imgUrl, long length, String localPath, long photographTime, String videoUrl) {
        this.content = content;
        this.h = h;
        this.w = w;
        this.id = id;
        this.imgUrl = imgUrl;
        this.length = length;
        this.localPath = localPath;
        this.photographTime = photographTime;
        this.videoUrl = videoUrl;
    }

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
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
}
