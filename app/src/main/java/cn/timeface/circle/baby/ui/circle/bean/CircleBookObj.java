package cn.timeface.circle.baby.ui.circle.bean;

import com.bluelinelabs.logansquare.annotation.JsonObject;

/**
 * 成长圈书
 * Created by lidonglin on 2017/3/14.
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class CircleBookObj {
    protected String circleNumber;  //圈号

    public String getCircleNumber() {
        return circleNumber;
    }

    public void setCircleNumber(String circleNumber) {
        this.circleNumber = circleNumber;
    }
}
