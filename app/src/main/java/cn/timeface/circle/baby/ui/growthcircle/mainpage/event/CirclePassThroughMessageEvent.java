package cn.timeface.circle.baby.ui.growthcircle.mainpage.event;

import cn.timeface.circle.baby.ui.circle.bean.CircleHomeworkObj;
import cn.timeface.circle.baby.ui.circle.bean.CircleSchoolTaskObj;
import cn.timeface.circle.baby.ui.circle.bean.CircleUserInfo;
import cn.timeface.circle.baby.ui.circle.bean.GrowthCircleObj;

/**
 * 收到圈子透传消息
 */
public class CirclePassThroughMessageEvent {

    public int type; // 透传消息类型 参见MiPushConstant

    public GrowthCircleObj circleObj; // 被圈主移出了圈、圈被解散
    public CircleUserInfo circleUserInfo; // 成为老师身份、取消老师身份
    public CircleSchoolTaskObj schoolTaskObj; // 新的作业
    public CircleHomeworkObj homeworkObj; // 最近提交作业

    public CirclePassThroughMessageEvent(int type) {
        this.type = type;
    }

}
