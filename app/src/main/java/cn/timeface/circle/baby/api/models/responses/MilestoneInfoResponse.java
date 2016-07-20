package cn.timeface.circle.baby.api.models.responses;

import java.util.List;

import cn.timeface.circle.baby.api.models.base.BaseResponse;
import cn.timeface.circle.baby.api.models.objs.TimeLineObj;

/**
 * Created by lidonglin on 2016/5/6.
 */
public class MilestoneInfoResponse extends BaseResponse {

    List<TimeLineObj> dataList;

    public List<TimeLineObj> getDataList() {
        return dataList;
    }

    public void setDataList(List<TimeLineObj> dataList) {
        this.dataList = dataList;
    }
}
