package cn.timeface.circle.baby.ui.growth.responses;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.List;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;
import cn.timeface.circle.baby.ui.growth.beans.PrintGrowthHomeObj;

/**
 * 印成长首页response
 * author : YW.SUN Created on 2017/1/11
 * email : sunyw10@gmail.com
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class PrintGrowthHomeResponse extends BaseResponse {
    private List<PrintGrowthHomeObj> dataList;

    public List<PrintGrowthHomeObj> getDataList() {
        return dataList;
    }

    public void setDataList(List<PrintGrowthHomeObj> dataList) {
        this.dataList = dataList;
    }
}
