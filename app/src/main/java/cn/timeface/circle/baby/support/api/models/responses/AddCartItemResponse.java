package cn.timeface.circle.baby.support.api.models.responses;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.List;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;

/**
 * @author YW.SUN
 * @from 2016/1/18
 * @TODO
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class AddCartItemResponse extends BaseResponse {
    private List<String> printIds;

    public List<String> getPrintIds() {
        return printIds;
    }

    public void setPrintIds(List<String> printIds) {
        this.printIds = printIds;
    }
}
