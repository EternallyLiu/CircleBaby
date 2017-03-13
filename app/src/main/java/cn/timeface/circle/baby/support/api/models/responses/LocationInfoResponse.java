package cn.timeface.circle.baby.support.api.models.responses;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;
import cn.timeface.circle.baby.support.api.models.objs.LocationInfoObj;

/**
 * 反解位置坐标信息response
 * author : YW.SUN Created on 2017/2/21
 * email : sunyw10@gmail.com
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class LocationInfoResponse extends BaseResponse {
    private LocationInfoObj locationInfo;

    public LocationInfoObj getLocationInfo() {
        return locationInfo;
    }

    public void setLocationInfo(LocationInfoObj locationInfo) {
        this.locationInfo = locationInfo;
    }
}
