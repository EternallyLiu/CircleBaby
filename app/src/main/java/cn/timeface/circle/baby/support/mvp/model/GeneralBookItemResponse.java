package cn.timeface.circle.baby.support.mvp.model;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import cn.timeface.circle.baby.support.mvp.response.bases.BaseResponse;


/**
 * Created by JieGuo on 16/10/20.
 */

@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class GeneralBookItemResponse extends BaseResponse {

    private GeneralBookObj data;

    public GeneralBookObj getData() {
        return data;
    }

    public void setData(GeneralBookObj data) {
        this.data = data;
    }
}
