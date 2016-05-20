package cn.timeface.circle.baby.api.models.responses;

import java.util.List;

import cn.timeface.circle.baby.api.models.base.BaseResponse;
import cn.timeface.circle.baby.api.models.objs.SystemMsg;

/**
 * Created by lidonglin on 2016/5/3.
 */
public class SystemMsgListResponse extends BaseResponse {

    List<SystemMsg> dataList;

    public List<SystemMsg> getDataList() {
        return dataList;
    }

    public void setDataList(List<SystemMsg> dataList) {
        this.dataList = dataList;
    }
}
