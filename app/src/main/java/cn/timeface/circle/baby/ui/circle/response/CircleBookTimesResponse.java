package cn.timeface.circle.baby.ui.circle.response;

import java.util.List;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;
import cn.timeface.circle.baby.ui.circle.bean.CircleTimeLineExObj;

/**
 * 查询圈时光书中包含的时光
 * author : sunyanwei Created on 17-3-27
 * email : sunyanwei@timeface.cn
 */
public class CircleBookTimesResponse extends BaseResponse {
    private List<CircleTimeLineExObj> dataList;

    public List<CircleTimeLineExObj> getDataList() {
        return dataList;
    }

    public void setDataList(List<CircleTimeLineExObj> dataList) {
        this.dataList = dataList;
    }
}
