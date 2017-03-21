package cn.timeface.circle.baby.ui.circle.response;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;
import cn.timeface.circle.baby.ui.circle.bean.GrowthCircleDetailObj;

/**
 * 圈资料response
 * Created by lidonglin on 2017/3/14.
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class CircleDetailResponse extends BaseResponse {
    private GrowthCircleDetailObj circleDetailInfo;
    private int isJoined; // 是否加入圈子 0-false 1-true

    public GrowthCircleDetailObj getCircleDetailInfo() {
        return circleDetailInfo;
    }

    public void setCircleDetailInfo(GrowthCircleDetailObj circleDetailInfo) {
        this.circleDetailInfo = circleDetailInfo;
    }

    public int getIsJoined() {
        return isJoined;
    }

    public void setIsJoined(int isJoined) {
        this.isJoined = isJoined;
    }

    public boolean isJoined() {
        return isJoined == 1;
    }

}
