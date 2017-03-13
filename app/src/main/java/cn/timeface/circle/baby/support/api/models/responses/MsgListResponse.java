package cn.timeface.circle.baby.support.api.models.responses;

import java.util.List;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;
import cn.timeface.circle.baby.support.api.models.objs.Msg;

/**
 * Created by lidonglin on 2016/5/3.
 */
public class MsgListResponse extends BaseResponse {

    List<Msg> dataList;

    public List<Msg> getDataList() {
        return dataList;
    }

    public void setDataList(List<Msg> dataList) {
        this.dataList = dataList;
    }
}
