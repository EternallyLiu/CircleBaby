package cn.timeface.circle.baby.api.models.responses;

import java.util.List;

import cn.timeface.circle.baby.api.models.base.BaseResponse;
import cn.timeface.circle.baby.api.models.objs.MilestoneTimeObj;

/**
 * Created by lidonglin on 2016/5/6.
 */
public class MilestoneTimeResponse extends BaseResponse {

    List<MilestoneTimeObj> dataList;

    public List<MilestoneTimeObj> getDataList() {
        return dataList;
    }

    public void setDataList(List<MilestoneTimeObj> dataList) {
        this.dataList = dataList;
    }
}
