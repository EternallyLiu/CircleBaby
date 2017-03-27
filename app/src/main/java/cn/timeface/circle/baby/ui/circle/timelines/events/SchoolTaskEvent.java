package cn.timeface.circle.baby.ui.circle.timelines.events;

import cn.timeface.circle.baby.ui.circle.bean.CircleSchoolTaskObj;

/**
 * author : wangshuai Created on 2017/3/27
 * email : wangs1992321@gmail.com
 */
public class SchoolTaskEvent {

    public static final int SCHOOLTASK_DELETE = 1;
    public static final int SCHOOLTASK_UPDATE = 2;
    public static final int SCHOOLTASK_NEW_HOMEWORK = 0;

    private int type;
    private CircleSchoolTaskObj schoolTaskObj;

    public SchoolTaskEvent(CircleSchoolTaskObj schoolTaskObj) {
        this.schoolTaskObj = schoolTaskObj;
    }

    public SchoolTaskEvent(int type, CircleSchoolTaskObj schoolTaskObj) {

        this.type = type;
        this.schoolTaskObj = schoolTaskObj;
    }

    public int getType() {

        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public CircleSchoolTaskObj getSchoolTaskObj() {
        return schoolTaskObj;
    }

    public void setSchoolTaskObj(CircleSchoolTaskObj schoolTaskObj) {
        this.schoolTaskObj = schoolTaskObj;
    }
}
