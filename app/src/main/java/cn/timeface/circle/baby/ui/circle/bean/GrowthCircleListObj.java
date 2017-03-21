package cn.timeface.circle.baby.ui.circle.bean;

import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonObject;

/**
 * 圈列表
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class GrowthCircleListObj extends GrowthCircleObj implements Parcelable {

    private GrowthCircleObj circleInfo;
    private CircleUserInfo userInfo;

    public GrowthCircleObj getCircleInfo() {
        return circleInfo;
    }

    public void setCircleInfo(GrowthCircleObj circleInfo) {
        this.circleInfo = circleInfo;
    }

    public CircleUserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(CircleUserInfo userInfo) {
        this.userInfo = userInfo;
    }
}
