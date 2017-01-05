package cn.timeface.circle.baby.support.api.models.responses;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.List;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;
import cn.timeface.circle.baby.support.api.models.objs.ImageInfoListObj;

/**
 * Created by lidonglin on 2016/7/4.
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class ImageInfoListResponse extends BaseResponse {
    private List<ImageInfoListObj> dataList;

    public List<ImageInfoListObj> getDataList() {
        return dataList;
    }

    public void setDataList(List<ImageInfoListObj> dataList) {
        this.dataList = dataList;
    }
}
