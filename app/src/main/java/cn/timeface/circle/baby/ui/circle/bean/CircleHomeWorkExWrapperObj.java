package cn.timeface.circle.baby.ui.circle.bean;

import java.util.List;

/**
 * author : sunyanwei Created on 17-3-22
 * email : sunyanwei@timeface.cn
 */
public class CircleHomeWorkExWrapperObj {
    private String date;
    private List<CircleHomeworkExObj> homeworkList;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<CircleHomeworkExObj> getHomeworkList() {
        return homeworkList;
    }

    public void setHomeworkList(List<CircleHomeworkExObj> homeworkList) {
        this.homeworkList = homeworkList;
    }
}
