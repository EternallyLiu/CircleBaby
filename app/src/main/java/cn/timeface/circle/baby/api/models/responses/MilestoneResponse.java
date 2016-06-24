package cn.timeface.circle.baby.api.models.responses;

import cn.timeface.circle.baby.api.models.base.BaseResponse;
import cn.timeface.circle.baby.api.models.objs.Milestone;

/**
 * Created by lidonglin on 2016/5/3.
 */
public class MilestoneResponse extends BaseResponse {
    Milestone milestone;

    public Milestone getMilestone() {
        return milestone;
    }

    public void setMilestone(Milestone milestone) {
        this.milestone = milestone;
    }
}
