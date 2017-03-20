package cn.timeface.circle.baby.ui.circle.bean;

import java.util.List;

/**
 * circle timeline obj wrapper
 * author : sunyanwei Created on 17-3-20
 * email : sunyanwei@timeface.cn
 */
public class CircleTimeLineWrapperObj {
    private String date;
    private List<CircleTimeLineExObj> timelineList;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<CircleTimeLineExObj> getTimelineList() {
        return timelineList;
    }

    public void setTimelineList(List<CircleTimeLineExObj> timelineList) {
        this.timelineList = timelineList;
    }
}
