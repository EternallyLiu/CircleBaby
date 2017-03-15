package cn.timeface.circle.baby.ui.circle.response;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;
import cn.timeface.circle.baby.ui.circle.bean.GrowthCricleDetailObj;

/**
 * 圈资料response
 * Created by lidonglin on 2017/3/14.
 */
public class CircleDetailResponse extends BaseResponse {
    private GrowthCricleDetailObj circleDetailInfo;   //创建好的圈子的圈号

    public GrowthCricleDetailObj getCircleDetailInfo() {
        return circleDetailInfo;
    }

    public void setCircleDetailInfo(GrowthCricleDetailObj circleDetailInfo) {
        this.circleDetailInfo = circleDetailInfo;
    }
}
