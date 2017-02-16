package cn.timeface.circle.baby.support.api.models.objs;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.List;

/**
 * timeline obj 包裹类
 * author : YW.SUN Created on 2017/2/14
 * email : sunyw10@gmail.com
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class TimeLineWrapObj {
    private String date;
    private List<TimeLineObj> timelineList;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<TimeLineObj> getTimelineList() {
        return timelineList;
    }

    public void setTimelineList(List<TimeLineObj> timelineList) {
        this.timelineList = timelineList;
    }
}
