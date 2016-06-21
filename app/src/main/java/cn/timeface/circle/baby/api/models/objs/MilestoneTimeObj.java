package cn.timeface.circle.baby.api.models.objs;

import cn.timeface.circle.baby.api.models.base.BaseObj;

/**
 * Created by lidonglin on 2016/5/4.
 */
public class MilestoneTimeObj extends BaseObj {
    long date;
    String imgUrl;
    String milestone;
    TimeLineObj timeInfo;

    public MilestoneTimeObj(long date, String imgUrl, String milestone, TimeLineObj timeInfo) {
        this.date = date;
        this.imgUrl = imgUrl;
        this.milestone = milestone;
        this.timeInfo = timeInfo;
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

    public TimeLineObj getTimeInfo() {
        return timeInfo;
    }

    public void setTimeInfo(TimeLineObj timeInfo) {
        this.timeInfo = timeInfo;
    }
}
