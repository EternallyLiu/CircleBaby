package cn.timeface.circle.baby.support.api.models.objs;

import java.io.Serializable;

/**
 * 小米推送 推送消息扩展数据Model
 */
public class MiPushMsgInfoObj implements Serializable {

    private int type; // 推送消息类型 参见MiPushConstant

    private String bookId; // 开放平台的
    private int bookType; // 开放平台的

    private long circleId; // 圈id
    private long circleTimeId; // circleTimelineId 圈时光id
    private long taskId; // taskId 老师布置的作业的id

    private long photoId; // 发布的照片被别人加标签 图片的id

    private long homeworkId; // 老师点评了宝宝的作业 作业的id

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

    public long getPhotoId() {
        return photoId;
    }

    public void setPhotoId(long photoId) {
        this.photoId = photoId;
    }

    public long getHomeworkId() {
        return homeworkId;
    }

    public void setHomeworkId(long homeworkId) {
        this.homeworkId = homeworkId;
    }
}
