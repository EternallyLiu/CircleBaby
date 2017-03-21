package cn.timeface.circle.baby.ui.circle.groupmembers.bean;

/**
 * Created by wangwei on 2017/3/20.
 */

public class MenemberInfo {
    /**
     * babyAvatarUrl	该用户关联的宝宝头像	string
     * babyName	关联的宝宝姓名	string
     * leaveMessage	留言信息	string
     * userInfo	用户对象	object	CircleUserInfo
     */
    private String babyAvatarUrl;
    private String babyName;
    private String leaveMessage;
    private int babyId;
    private CircleUserInfo userInfo;

    public MenemberInfo(String babyAvatarUrl, int babyId, cn.timeface.circle.baby.ui.circle.groupmembers.bean.CircleUserInfo circleUserInfo) {
        this.babyAvatarUrl = babyAvatarUrl;
        this.babyId = babyId;
        userInfo = circleUserInfo;
    }

    public MenemberInfo(String babyAvatarUrl, String babyName, String leaveMessage, int babyId, cn.timeface.circle.baby.ui.circle.groupmembers.bean.CircleUserInfo circleUserInfo) {
        this.babyAvatarUrl = babyAvatarUrl;
        this.babyName = babyName;

        this.leaveMessage = leaveMessage;
        this.babyId = babyId;
        userInfo = circleUserInfo;
    }

    public String getBabyAvatarUrl() {
        return babyAvatarUrl;
    }

    public void setBabyAvatarUrl(String babyAvatarUrl) {
        this.babyAvatarUrl = babyAvatarUrl;
    }

    public String getBabyName() {
        return babyName;
    }

    public void setBabyName(String babyName) {
        this.babyName = babyName;
    }

    public String getLeaveMessage() {
        return leaveMessage;
    }

    public void setLeaveMessage(String leaveMessage) {
        this.leaveMessage = leaveMessage;
    }

    public cn.timeface.circle.baby.ui.circle.groupmembers.bean.CircleUserInfo getCircleUserInfo() {
        return userInfo;
    }

    public void setCircleUserInfo(cn.timeface.circle.baby.ui.circle.groupmembers.bean.CircleUserInfo circleUserInfo) {
        userInfo = circleUserInfo;
    }
}