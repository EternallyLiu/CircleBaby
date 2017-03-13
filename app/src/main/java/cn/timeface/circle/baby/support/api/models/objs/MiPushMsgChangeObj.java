package cn.timeface.circle.baby.support.api.models.objs;

import java.io.Serializable;

/**
 * 小米推送 透传消息 更新未读消息数量
 */
public class MiPushMsgChangeObj implements Serializable {

    private String msgCount;
    private String userId;
    private String targetId;
    private String type;

    public String getMsgCount() {
        return msgCount;
    }

    public int getUnReadMsgCount() {
        int count = 0;
        try {
            count = Integer.valueOf(msgCount);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return count;
    }

    public void setMsgCount(String msgCount) {
        this.msgCount = msgCount;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
