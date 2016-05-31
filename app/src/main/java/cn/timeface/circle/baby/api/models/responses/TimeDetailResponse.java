package cn.timeface.circle.baby.api.models.responses;

import cn.timeface.circle.baby.api.models.base.BaseResponse;
import cn.timeface.circle.baby.api.models.objs.TimeLineObj;

/**
 * Created by lidonglin on 2016/5/31.
 */
public class TimeDetailResponse extends BaseResponse {
    TimeLineObj timeInfo;

    public TimeLineObj getTimeInfo() {
        return timeInfo;
    }

    public void setTimeInfo(TimeLineObj timeInfo) {
        this.timeInfo = timeInfo;
    }
}
