package cn.timeface.circle.baby.ui.circle.groupmembers.bean;

/**
 * Created by wangwei on 2017/3/20.
 */

public class CircleUserInfo {
    /**
     * circleAvatarUrl	成长圈用户头像	string
     circleId	成长圈id	number
     circleNickName	成长圈用户昵称	string
     circleUserId	成长圈用户id	number
     circleUserType	成长圈用户类型	number	1-创建者 2-老师 3-普通成员
     */
    private String circleAvatarUrl;
    private int circleId;
    private String circleNickName;
    private int circleUserId;
    private int circleUserType;

    public String getCircleAvatarUrl() {
        return circleAvatarUrl;
    }

    public void setCircleAvatarUrl(String circleAvatarUrl) {
        this.circleAvatarUrl = circleAvatarUrl;
    }

    public int getCircleId() {
        return circleId;
    }

    public void setCircleId(int circleId) {
        this.circleId = circleId;
    }

    public String getCircleNickName() {
        return circleNickName;
    }

    public void setCircleNickName(String circleNickName) {
        this.circleNickName = circleNickName;
    }

    public int getCircleUserId() {
        return circleUserId;
    }

    public void setCircleUserId(int circleUserId) {
        this.circleUserId = circleUserId;
    }

    public int getCircleUserType() {
        return circleUserType;
    }

    public void setCircleUserType(int circleUserType) {
        this.circleUserType = circleUserType;
    }

    public CircleUserInfo(String circleAvatarUrl, int circleId, String circleNickName, int circleUserId, int circleUserType) {
        this.circleAvatarUrl = circleAvatarUrl;
        this.circleId = circleId;

        this.circleNickName = circleNickName;
        this.circleUserId = circleUserId;
        this.circleUserType = circleUserType;
    }

    public CircleUserInfo(String circleAvatarUrl, String circleNickName, int circleUserType) {
        this.circleAvatarUrl = circleAvatarUrl;
        this.circleNickName = circleNickName;
        this.circleUserType = circleUserType;
    }
}
