package cn.timeface.circle.baby.ui.circle.response;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.List;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;
import cn.timeface.circle.baby.ui.circle.bean.QueryByCircleTimeObj;

/**
 * 按时间查询response
 * Created by lidonglin on 2017/3/17.
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class QueryCircleByTimeResponse extends BaseResponse {
    private List<QueryByCircleTimeObj> dataList;
    private int totalMediaCount;

    public int getTotalMediaCount() {
        return totalMediaCount;
    }

    public void setTotalMediaCount(int totalMediaCount) {
        this.totalMediaCount = totalMediaCount;
    }

    public List<QueryByCircleTimeObj> getDataList() {
        return dataList;
    }

    public void setDataList(List<QueryByCircleTimeObj> dataList) {
        this.dataList = dataList;
    }
}
