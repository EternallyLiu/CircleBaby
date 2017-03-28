package cn.timeface.circle.baby.ui.circle.response;

import java.util.List;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;
import cn.timeface.circle.baby.ui.circle.bean.CircleHomeworkObj;

/**
 * 书中包含的所有作业列表数据
 * author : sunyanwei Created on 17-3-28
 * email : sunyanwei@timeface.cn
 */
public class CircleBookHomeworkListResponse extends BaseResponse {
    private List<CircleHomeworkObj> dataList;

    public List<CircleHomeworkObj> getDataList() {
        return dataList;
    }

    public void setDataList(List<CircleHomeworkObj> dataList) {
        this.dataList = dataList;
    }
}
