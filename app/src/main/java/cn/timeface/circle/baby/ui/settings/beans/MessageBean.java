package cn.timeface.circle.baby.ui.settings.beans;

import android.content.Context;

import com.wechat.photopicker.fragment.BigImageFragment;

import java.util.Map;

import cn.timeface.circle.baby.activities.MyPODActivity;
import cn.timeface.circle.baby.constants.MiPushConstant;
import cn.timeface.circle.baby.ui.circle.timelines.activity.CircleTimeLineDetailActivitiy;
import cn.timeface.circle.baby.ui.circle.timelines.activity.HomeWorkActivity;
import cn.timeface.circle.baby.ui.circle.timelines.activity.SchoolTaskDetailActivity;
import cn.timeface.circle.baby.ui.circle.timelines.activity.TeacherAuthoActivity;
import cn.timeface.circle.baby.ui.growthcircle.mainpage.activity.CircleMainActivity;

/**
 * author : wangshuai Created on 2017/4/13
 * email : wangs1992321@gmail.com
 */
public class MessageBean {

    /**
     * content : 范饼饼的爷爷发布了一条新动态，赶快去看看吧~
     * identifier : 2001
     * imgUrl :
     * info : {"circleTimeId":268,"beginString":"今晚最后二个圈子"}
     * isRead : 0
     * messageId : 3010
     * time : 1491990706504
     * title : 发布圈动态
     */

    private String content;
    private int identifier;
    private String imgUrl;
    private int isRead;
    private int messageId;
    private long time;
    private String title;
    private Map<String, Object> info;

    public Map<String, Object> getInfo() {
        return info;
    }

    public void setInfo(Map<String, Object> info) {
        this.info = info;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getIdentifier() {
        return identifier;
    }

    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public int getIsRead() {
        return isRead;
    }

    public void setIsRead(int isRead) {
        this.isRead = isRead;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public <T extends Object> T getInfoValue(String key) {
        if (getInfo() != null && getInfo().containsKey(key)) return (T) getInfo().get(key);
        else return null;
    }

    public void skip(Context context) {
        switch (getIdentifier()) {
            case MiPushConstant.PUSH_TYPE_CIRCLE_NEW_MEMBER:
                CircleMainActivity.open(context, getInfoValue("circleId"));
                break;
            case MiPushConstant.PUSH_TYPE_CIRCLE_TEACHER_NEW_PRODUCTION:
            case MiPushConstant.PUSH_TYPE_CIRCLE_PRODUCTION_REFERENCED:
            case MiPushConstant.PUSH_TYPE_CIRCLE_NEW_SCHOOL_BOOK:
                MyPODActivity.open(context, getInfoValue("bookId"), getInfoValue("bookType"));
                break;
            case MiPushConstant.PUSH_TYPE_CIRCLE_TEACHER_NEW_TIME_LINE:
            case MiPushConstant.PUSH_TYPE_CIRCLE_NEW_COMMENTS:
            case MiPushConstant.PUSH_TYPE_CIRCLE_NEW_GOOD:
                CircleTimeLineDetailActivitiy.open(context, getInfoValue("circleTimeId"));
                break;
            case MiPushConstant.PUSH_TYPE_CIRCLE_NEW_TEACHER_AUTHORIZATION:
                TeacherAuthoActivity.open(context, getInfoValue("circleId"));
                break;
            case MiPushConstant.PUSH_TYPE_CIRCLE_NEW_SCHOOL_TASK:
                SchoolTaskDetailActivity.open(context, getInfoValue("taskId"));
                break;
            case MiPushConstant.PUSH_TYPE_CIRCLE_NEW_PHOTO_TAGGED:
            case MiPushConstant.PUSH_TYPE_CIRCLE_NEW_PHOTO_LIKED:
                BigImageFragment.open(context, getInfoValue("mediaId"), true, false);
                break;
            case MiPushConstant.PUSH_TYPE_CIRCLE_HOMEWORK_COMMENTS:
                HomeWorkActivity.open(context, getInfoValue("homeworkId"));
                break;
            case MiPushConstant.PUSH_TYPE_CIRCLE_MEMBER_REMOVED:

                break;
        }
    }

}
