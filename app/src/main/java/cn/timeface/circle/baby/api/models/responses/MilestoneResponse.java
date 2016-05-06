package cn.timeface.circle.baby.api.models.responses;

import java.util.List;

import cn.timeface.circle.baby.api.models.base.BaseResponse;
import cn.timeface.circle.baby.api.models.objs.Milestone;
import cn.timeface.circle.baby.api.models.objs.Relationship;

/**
 * Created by lidonglin on 2016/5/3.
 */
public class MilestoneResponse extends BaseResponse {

    List<Milestone> dataList;

    public List<Milestone> getDataList() {
        return dataList;
    }

    public void setDataList(List<Milestone> dataList) {
        this.dataList = dataList;
    }
}
