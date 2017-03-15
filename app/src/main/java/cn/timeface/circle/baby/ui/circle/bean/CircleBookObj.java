package cn.timeface.circle.baby.ui.circle.bean;

import com.bluelinelabs.logansquare.annotation.JsonObject;

/**
 * 成长圈书
 * Created by lidonglin on 2017/3/14.
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class CircleBookObj {
    protected int circleNumber;  //圈号

    public int getCircleNumber() {
        return circleNumber;
    }

    public void setCircleNumber(int circleNumber) {
        this.circleNumber = circleNumber;
    }
}
