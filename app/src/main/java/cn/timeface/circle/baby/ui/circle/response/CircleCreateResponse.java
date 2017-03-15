package cn.timeface.circle.baby.ui.circle.response;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;
import cn.timeface.circle.baby.ui.circle.bean.CircleCommentObj;

/**
 * 创建圈response
 * Created by lidonglin on 2017/3/14.
 */
public class CircleCreateResponse extends BaseResponse {
    private int circleNumber;   //创建好的圈子的圈号

    public int getCircleNumber() {
        return circleNumber;
    }

    public void setCircleNumber(int circleNumber) {
        this.circleNumber = circleNumber;
    }
}
