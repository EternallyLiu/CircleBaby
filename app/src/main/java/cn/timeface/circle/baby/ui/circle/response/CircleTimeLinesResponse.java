package cn.timeface.circle.baby.ui.circle.response;

import java.util.List;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;
import cn.timeface.circle.baby.ui.circle.bean.CircleTimeLineWrapperObj;

/**
 * 查询时光列表response
 * author : sunyanwei Created on 17-3-20
 * email : sunyanwei@timeface.cn
 */
public class CircleTimeLinesResponse extends BaseResponse {
    private List<CircleTimeLineWrapperObj> dataList;

    public List<CircleTimeLineWrapperObj> getDataList() {
        return dataList;
    }

    public void setDataList(List<CircleTimeLineWrapperObj> dataList) {
        this.dataList = dataList;
    }
}
