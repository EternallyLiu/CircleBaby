package cn.timeface.circle.baby.ui.circle.response;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.List;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;
import cn.timeface.circle.baby.ui.circle.bean.CircleSchoolTaskObj;
import cn.timeface.circle.baby.ui.circle.bean.CircleTimelineObj;
import cn.timeface.circle.baby.ui.circle.bean.CircleUserInfo;
import cn.timeface.circle.baby.ui.circle.bean.GrowthCircleObj;

/**
 * 圈时光发布response
 * Created by lidonglin on 2017/3/17.
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class CirclePublishResponse extends BaseResponse {
    private CircleTimelineObj circleTimeline;

    public CircleTimelineObj getCircleTimeline() {
        return circleTimeline;
    }

    public void setCircleTimeline(CircleTimelineObj circleTimeline) {
        this.circleTimeline = circleTimeline;
    }
}
