package cn.timeface.circle.baby.support.api.models.responses;

import java.util.List;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;
import cn.timeface.circle.baby.support.api.models.objs.RecommendObj;
import cn.timeface.circle.baby.support.api.models.objs.TimeLineGroupObj;

/**
 * Created by lidonglin on 2016/5/6.
 */
public class TimelineResponse extends BaseResponse {
    List<TimeLineGroupObj> dataList;
    int currentPage;
    int totalPage;
    RecommendObj recommendCard;

    public RecommendObj getRecommendCard() {
        return recommendCard;
    }

    public void setRecommendCard(RecommendObj recommendCard) {
        this.recommendCard = recommendCard;
    }

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
