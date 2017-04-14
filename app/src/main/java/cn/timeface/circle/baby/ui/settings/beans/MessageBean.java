package cn.timeface.circle.baby.ui.settings.beans;

import android.content.Context;
import android.text.TextUtils;

import com.wechat.photopicker.fragment.BigImageFragment;

import java.util.Map;

import cn.timeface.circle.baby.activities.FragmentBridgeActivity;
import cn.timeface.circle.baby.activities.MyPODActivity;
import cn.timeface.circle.baby.activities.OrderDetailActivity;
import cn.timeface.circle.baby.constants.MiPushConstant;
import cn.timeface.circle.baby.support.api.ApiFactory;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.circle.timelines.activity.CircleTimeLineDetailActivitiy;
import cn.timeface.circle.baby.ui.circle.timelines.activity.HomeWorkActivity;
import cn.timeface.circle.baby.ui.circle.timelines.activity.SchoolTaskDetailActivity;
import cn.timeface.circle.baby.ui.circle.timelines.activity.TeacherAuthoActivity;
import cn.timeface.circle.baby.ui.growthcircle.mainpage.activity.CircleMainActivity;
import cn.timeface.circle.baby.ui.kiths.KithFragment;
import cn.timeface.circle.baby.ui.timelines.fragments.TimeFaceDetailFragment;

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
    private Map<String, String> info;

    public Map<String, String> getInfo() {
        return info;
    }

    public void setInfo(Map<String, String> info) {
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

    public String getInfoValue(String key) {
        if (getInfo() != null && getInfo().containsKey(key)) return getInfo().get(key);
        else return null;
    }

    private void skipUI(Context context) {
        switch (getIdentifier()) {
            case MiPushConstant.PUSH_TYPE_CIRCLE_NEW_MEMBER:
                CircleMainActivity.open(context, Long.valueOf(getInfoValue("circleId")));
                break;
            case MiPushConstant.PUSH_TYPE_CIRCLE_TEACHER_NEW_PRODUCTION:
            case MiPushConstant.PUSH_TYPE_CIRCLE_PRODUCTION_REFERENCED:
            case MiPushConstant.PUSH_TYPE_CIRCLE_NEW_SCHOOL_BOOK:
                MyPODActivity.open(context, getInfoValue("bookId"), Integer.valueOf(getInfoValue("bookType")));
                break;
            case MiPushConstant.PUSH_TYPE_CIRCLE_TEACHER_NEW_TIME_LINE:
            case MiPushConstant.PUSH_TYPE_CIRCLE_NEW_COMMENTS:
            case MiPushConstant.PUSH_TYPE_CIRCLE_NEW_GOOD:
                CircleTimeLineDetailActivitiy.open(context, Long.valueOf(getInfoValue("circleTimeId")));
                break;
            case MiPushConstant.PUSH_TYPE_CIRCLE_NEW_TEACHER_AUTHORIZATION:
                TeacherAuthoActivity.open(context, Long.valueOf(getInfoValue("circleId")));
                break;
            case MiPushConstant.PUSH_TYPE_CIRCLE_NEW_SCHOOL_TASK:
                SchoolTaskDetailActivity.open(context, Long.valueOf(getInfoValue("taskId")));
                break;
            case MiPushConstant.PUSH_TYPE_CIRCLE_NEW_PHOTO_TAGGED:
            case MiPushConstant.PUSH_TYPE_CIRCLE_NEW_PHOTO_LIKED:
                BigImageFragment.open(context, Long.valueOf(getInfoValue("mediaId")), true, false);
                break;
            case MiPushConstant.PUSH_TYPE_CIRCLE_HOMEWORK_COMMENTS:
                HomeWorkActivity.open(context, Long.valueOf(getInfoValue("homeworkId")));
                break;
            case MiPushConstant.PUSH_TYPE_CIRCLE_MEMBER_REMOVED:

                break;

            //----------------------------系统消息---------------------
            case MiPushConstant.NOTICE_TYPE_ORDER_DELIVERED:
            case MiPushConstant.NOTICE_TYPE_ORDER_DELIVERING:
            case MiPushConstant.NOTICE_TYPE_ORDER_CHECK_NOT_PASS:
            case MiPushConstant.NOTICE_TYPE_ORDER_PAY_SUCCESS:
            case MiPushConstant.NOTICE_TYPE_ORDER_NOT_PAY:
                OrderDetailActivity.open(context, getInfoValue("uId"));
                break;
            case MiPushConstant.NOTICE_TYPE_NEW_ACTIVITY:
                FragmentBridgeActivity.openWebViewFragment(context,getInfoValue("url"),getTitle());
                break;
            case MiPushConstant.NOTICE_TYPE_NORMAL_MSG:
                break;

            //-----------------------------亲友圈----------------------------
            case MiPushConstant.PUSH_TYPE_NEW_MEMBER:
                FragmentBridgeActivity.open(context, KithFragment.class.getSimpleName());
                break;
            case MiPushConstant.PUSH_TYPE_TIME_GOOD:
            case MiPushConstant.PUSH_TYPE_TIME_COMMENT:
            case MiPushConstant.PUSH_TYPE_NEW_TIME_PUBLISH:
                TimeFaceDetailFragment.open(context, Integer.valueOf(getInfoValue("timeId")));
                break;
        }
    }

    public void skip(Context context) {
        if (!TextUtils.isEmpty(getInfoValue("circleId"))) {
            long circleId = Long.valueOf(getInfoValue("circleId"));
            if (circleId != FastData.getCircleId())
                ApiFactory.getApi().getApiService().queryCircleIndexInfo(circleId)
                    .compose(SchedulersCompat.applyIoSchedulers())
                    .subscribe(circleIndexInfoResponse -> {
                        if (circleIndexInfoResponse.success()) {
                            FastData.setGrowthCircleObj(circleIndexInfoResponse.getGrowthCircle());
                            FastData.setCircleUserInfo(circleIndexInfoResponse.getUserInfo());
                            skipUI(context);
                        }
                    }, throwable -> {
                    });
            else skipUI(context);
        } else skipUI(context);

    }

}
