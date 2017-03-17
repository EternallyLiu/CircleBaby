package cn.timeface.circle.baby.ui.circle.response;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;
import cn.timeface.circle.baby.ui.circle.bean.CircleTimelineObj;

/**
 * 查找圈子response
 * Created by lidonglin on 2017/3/14.
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class UpdateTimeLineResponse extends BaseResponse {
    private CircleTimelineObj circleTimelineInfo;

    public CircleTimelineObj getCircleTimelineInfo() {
        return circleTimelineInfo;
    }

    public void setCircleTimelineInfo(CircleTimelineObj circleTimelineInfo) {
        this.circleTimelineInfo = circleTimelineInfo;
    }
}
