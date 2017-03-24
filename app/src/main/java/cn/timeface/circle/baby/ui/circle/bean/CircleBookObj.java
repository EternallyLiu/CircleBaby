package cn.timeface.circle.baby.ui.circle.bean;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import cn.timeface.circle.baby.support.api.models.objs.BookObj;

/**
 * 成长圈书
 * Created by lidonglin on 2017/3/14.
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class CircleBookObj extends BookObj{
    protected int circleId;  //圈号

    public int getCircleNumber() {
        return circleId;
    }

    public void setCircleNumber(int circleNumber) {
        this.circleId = circleNumber;
    }
}
