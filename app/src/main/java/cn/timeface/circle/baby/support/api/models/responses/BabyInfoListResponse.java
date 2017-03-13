package cn.timeface.circle.baby.support.api.models.responses;

import java.util.List;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;
import cn.timeface.circle.baby.support.api.models.objs.UserObj;

/**
 * Created by lidonglin on 2016/5/6.
 */
public class BabyInfoListResponse extends BaseResponse {

    List<UserObj> dataList;

    public List<UserObj> getDataList() {
        return dataList;
    }

    public void setDataList(List<UserObj> dataList) {
        this.dataList = dataList;
    }
}