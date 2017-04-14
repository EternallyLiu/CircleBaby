package cn.timeface.circle.baby.support.api.models.objs;

import java.io.Serializable;

import cn.timeface.circle.baby.constants.MiPushConstant;

/**
 * 小米推送 推送消息扩展数据Model
 */
public class MiPushMsgInfoObj implements Serializable {

    private int type; // 推送消息类型 参见MiPushConstant

    /*-------------成长圈圈推送-------------*/
    private String bookId; // 开放平台的
    private int bookType; // 开放平台的

    private long circleId; // 圈id
    private long circleTimeId; // circleTimelineId 圈时光id
    private long taskId; // taskId 老师布置的作业的id

    private long mediaId; // 发布的照片被别人加标签 图片的id

    private long homeworkId; // 老师点评了宝宝的作业 作业的id

    /*-------------亲友圈推送-------------*/
    private int timeId; // 时光Id
    private int mediaType; // 类型 1：照片 2 ：视频
    private int mediaUrl;

    public MiPushMsgInfoObj(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public int getBookType() {
        return bookType;
    }

    public void setBookType(int bookType) {
        this.bookType = bookType;
    }

    public long getCircleId() {
        return circleId;
    }

    public void setCircleId(long circleId) {
        this.circleId = circleId;
    }

    public long getCircleTimeId() {
        return circleTimeId;
    }

    public void setCircleTimeId(long circleTimeId) {
        this.circleTimeId = circleTimeId;
    }

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public long getMediaId() {
        return mediaId;
    }

    public void setMediaId(long mediaId) {
        this.mediaId = mediaId;
    }

    public long getHomeworkId() {
        return homeworkId;
    }

    public void setHomeworkId(long homeworkId) {
        this.homeworkId = homeworkId;
    }


    public int getTimeId() {
        return timeId;
    }

    public void setTimeId(int timeId) {
        this.timeId = timeId;
    }

    public int getMediaType() {
        return mediaType;
    }

    public void setMediaType(int mediaType) {
        this.mediaType = mediaType;
    }

    public int getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(int mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    /**
     * 是否为亲友圈推送（跳转逻辑与成长圈推送不同）
     */
    public boolean isKithPush() {
        return type == MiPushConstant.PUSH_TYPE_NEW_MEMBER ||
                type == MiPushConstant.PUSH_TYPE_TIME_GOOD ||
                type == MiPushConstant.PUSH_TYPE_TIME_COMMENT ||
                type == MiPushConstant.PUSH_TYPE_NEW_TIME_PUBLISH;
    }
}
