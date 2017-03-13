package cn.timeface.circle.baby.support.mvp.model;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.List;

import cn.timeface.circle.baby.support.mvp.response.bases.BaseResponse;


/**
 * Created by JieGuo on 16/10/19.
 */

@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class GeneralBookResponse extends BaseResponse {

    List<GeneralBookObj> data;

    public List<GeneralBookObj> getData() {
        return data;
    }

    public void setData(List<GeneralBookObj> data) {
        this.data = data;
    }
}
