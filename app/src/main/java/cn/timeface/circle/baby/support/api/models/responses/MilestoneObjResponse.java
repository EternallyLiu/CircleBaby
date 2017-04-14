package cn.timeface.circle.baby.support.api.models.responses;

import java.util.List;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;
import cn.timeface.circle.baby.support.api.models.objs.Milestone;
import cn.timeface.circle.baby.support.api.models.objs.MilestoneObj;

/**
 * Created by lidonglin on 2017/4/13.
 */
public class MilestoneObjResponse extends BaseResponse {
    List<MilestoneObj> dataList;

    public List<MilestoneObj> getDataList() {
        return dataList;
    }

    public void setDataList(List<MilestoneObj> dataList) {
        this.dataList = dataList;
    }
}
