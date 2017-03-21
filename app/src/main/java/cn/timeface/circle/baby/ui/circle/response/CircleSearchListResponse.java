package cn.timeface.circle.baby.ui.circle.response;

import java.util.List;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;
import cn.timeface.circle.baby.ui.circle.bean.GrowthCircleObj;

/**
 * 查找圈子response
 * Created by lidonglin on 2017/3/14.
 */
public class CircleSearchListResponse extends BaseResponse {
    private List<GrowthCircleObj> dataList;

    public List<GrowthCircleObj> getDataList() {
        return dataList;
    }

    public void setDataList(List<GrowthCircleObj> dataList) {
        this.dataList = dataList;
    }
}
