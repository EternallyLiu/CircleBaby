package cn.timeface.circle.baby.support.api.models.responses;

import java.util.List;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;
import cn.timeface.circle.baby.support.api.models.objs.Milestone;

/**
 * Created by lidonglin on 2016/5/3.
 */
public class MilestoneListResponse extends BaseResponse {

    List<Milestone> dataList;

    public List<Milestone> getDataList() {
        return dataList;
    }

    public void setDataList(List<Milestone> dataList) {
        this.dataList = dataList;
    }
}
