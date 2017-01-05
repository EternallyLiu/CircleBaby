package cn.timeface.circle.baby.support.api.models.objs;

import java.util.List;

import cn.timeface.circle.baby.support.api.models.base.BaseObj;

/**
 * Created by lidonglin on 2016/5/31.
 */
public class TimeLineGroupObj extends BaseObj {
    String age;                             //年龄描述
    long date;                              //创建时间
    String dateEx;                          //时间附属信息
    List<TimeLineObj> timeLineList;         //这天中的时光列表

    public TimeLineGroupObj(String age, long date, String dateEx, List<TimeLineObj> timeLineList) {
        this.age = age;
        this.date = date;
        this.dateEx = dateEx;
        this.timeLineList = timeLineList;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getDateEx() {
        return dateEx;
    }

    public void setDateEx(String dateEx) {
        this.dateEx = dateEx;
    }

    public List<TimeLineObj> getTimeLineList() {
        return timeLineList;
    }

    public void setTimeLineList(List<TimeLineObj> timeLineList) {
        this.timeLineList = timeLineList;
    }
}
