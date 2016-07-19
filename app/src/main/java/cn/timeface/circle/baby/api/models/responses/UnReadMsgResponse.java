package cn.timeface.circle.baby.api.models.responses;

import cn.timeface.circle.baby.api.models.base.BaseResponse;

/**
 * Created by lidonglin on 2016/5/3.
 */
public class UnReadMsgResponse extends BaseResponse {
    int unreadMessageCount;

    public int getUnreadMessageCount() {
        return unreadMessageCount;
    }

    public void setUnreadMessageCount(int unreadMessageCount) {
        this.unreadMessageCount = unreadMessageCount;
    }
}
