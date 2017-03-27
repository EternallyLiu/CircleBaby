package cn.timeface.circle.baby.ui.circle.response;

import java.util.List;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;
import cn.timeface.circle.baby.ui.circle.groupmembers.bean.CircleBabyBriefObj;

/**
 * 查询绑定了家长的宝宝列表
 * author : sunyanwei Created on 17-3-25
 * email : sunyanwei@timeface.cn
 */
public class QueryBindingBabyResponse extends BaseResponse {
    private List<CircleBabyBriefObj> dataList;

    public List<CircleBabyBriefObj> getDataList() {
        return dataList;
    }

    public void setDataList(List<CircleBabyBriefObj> dataList) {
        this.dataList = dataList;
    }
}
