package cn.timeface.circle.baby.api.models.responses;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.List;

import cn.timeface.circle.baby.api.models.DistrictModel;
import cn.timeface.circle.baby.api.models.base.BaseResponse;

/**
 * @author SunYanwei (QQ:707831837)
 * @from 2014年9月11日下午2:53:11
 * @TODO 地区列表数据
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class DistrictListResponse extends BaseResponse {

    /**
     * serial
     */
    private static final long serialVersionUID = 1L;

    private List<DistrictModel> dataList;

    public List<DistrictModel> getDataList() {
        return dataList;
    }

    public void setDataList(List<DistrictModel> dataList) {
        this.dataList = dataList;
    }


}
