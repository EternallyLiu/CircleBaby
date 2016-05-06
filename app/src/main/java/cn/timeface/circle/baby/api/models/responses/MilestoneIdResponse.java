package cn.timeface.circle.baby.api.models.responses;

import cn.timeface.circle.baby.api.models.base.BaseResponse;

/**
 * Created by lidonglin on 2016/5/3.
 */
public class MilestoneIdResponse extends BaseResponse {
    int milestoneId;

    public int getMilestoneId() {
        return milestoneId;
    }

    public void setMilestoneId(int milestoneId) {
        this.milestoneId = milestoneId;
    }
}
