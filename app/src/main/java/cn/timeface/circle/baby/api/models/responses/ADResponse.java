package cn.timeface.circle.baby.api.models.responses;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import cn.timeface.circle.baby.api.models.base.BaseResponse;
import cn.timeface.circle.baby.api.models.objs.ADObj;

/**
 * @author SUN
 * @from 2014/11/18
 * @TODO 首页全屏广告
 */

@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class ADResponse extends BaseResponse {
    private ADObj adInfo; //广告内容

    public ADObj getAdInfo() {
        return adInfo;
    }

    public void setAdInfo(ADObj adInfo) {
        this.adInfo = adInfo;
    }
}
