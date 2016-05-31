package cn.timeface.circle.baby.api.models.responses;

import java.util.List;

import cn.timeface.circle.baby.api.models.base.BaseResponse;
import cn.timeface.circle.baby.api.models.objs.MediaObj;
import cn.timeface.circle.baby.api.models.objs.TimeLineGroupObj;

/**
 * Created by lidonglin on 2016/5/6.
 */
public class TimelineResponse extends BaseResponse {
    List<TimeLineGroupObj> dataList;
    int currentPage;
    int totalPage;

    public List<TimeLineGroupObj> getDataList() {
        return dataList;
    }

    public void setDataList(List<TimeLineGroupObj> dataList) {
        this.dataList = dataList;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }
}
