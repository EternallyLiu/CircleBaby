package cn.timeface.circle.baby.support.api.models.objs;

import java.util.List;

import cn.timeface.circle.baby.support.api.models.base.BaseObj;

/**
 * Created by lidonglin on 2016/5/13.
 * 发布类
 */
//@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class PublishObj extends BaseObj {
    String content;
    List<CardObj> mediaList;
    int milestone;
    long time;                 //时间戳

    public PublishObj() {
    }

    public PublishObj(String content, List<CardObj> mediaList, int milestone, long time) {
        this.content = content;
        this.mediaList = mediaList;
        this.milestone = milestone;
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<CardObj> getMediaList() {
        return mediaList;
    }

    public void setMediaList(List<CardObj> mediaList) {
        this.mediaList = mediaList;
    }

    public int getMilestone() {
        return milestone;
    }

    public void setMilestone(int milestone) {
        this.milestone = milestone;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
