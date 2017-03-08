package cn.timeface.circle.baby.support.api.models.objs;

import java.io.Serializable;

/**
 * 小米推送 透传消息
 */
public class MiPushMsgObj implements Serializable {

    private MiPushMsgChangeObj msgChange;

    public MiPushMsgChangeObj getMsgChange() {
        return msgChange;
    }

    public void setMsgChange(MiPushMsgChangeObj msgChange) {
        this.msgChange = msgChange;
    }
}
