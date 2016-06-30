package cn.timeface.circle.baby.api.models.responses;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.List;

import cn.timeface.circle.baby.api.models.base.BaseResponse;
import cn.timeface.circle.baby.api.models.objs.PrintParamResponse;

/**
 * @author WXW
 * @from 2015/5/27
 * @TODO 快递列表response
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class PrintDispatchListResponse extends BaseResponse {
    private List<PrintParamResponse> dataList; //返回数据集

    public List<PrintParamResponse> getDataList() {
        return dataList;
    }

    public void setDataList(List<PrintParamResponse> dataList) {
        this.dataList = dataList;
    }

    public PrintParamResponse getProperty(String key) {
        for (PrintParamResponse printParam : dataList) {
            if (key.equals(printParam.getKey())) {
                return printParam;
            }
        }
        return null;
    }
}
