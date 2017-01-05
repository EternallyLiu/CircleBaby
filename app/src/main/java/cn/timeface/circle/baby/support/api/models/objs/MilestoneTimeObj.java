package cn.timeface.circle.baby.support.api.models.objs;

import cn.timeface.circle.baby.support.api.models.base.BaseObj;

/**
 * Created by lidonglin on 2016/5/4.
 */
public class MilestoneTimeObj extends BaseObj {
    long date;
    String imgUrl;
    String milestone;
    int milestoneId;
    int isRead;
    int timelineCount;

    public MilestoneTimeObj(long date, String imgUrl, String milestone, int milestoneId) {
        this.date = date;
        this.imgUrl = imgUrl;
        this.milestone = milestone;
        this.milestoneId = milestoneId;
    }

    public int getTimelineCount() {
        return timelineCount;
    }

    public void setTimelineCount(int timelineCount) {
        this.timelineCount = timelineCount;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getMilestone() {
        return milestone;
    }

    public void setMilestone(String milestone) {
        this.milestone = milestone;
    }

    public int getMilestoneId() {
        return milestoneId;
    }

    public void setMilestoneId(int milestoneId) {
        this.milestoneId = milestoneId;
    }

    public int getIsRead() {
        return isRead;
    }

    public void setIsRead(int isRead) {
        this.isRead = isRead;
    }
}
