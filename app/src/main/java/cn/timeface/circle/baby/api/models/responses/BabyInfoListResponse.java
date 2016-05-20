package cn.timeface.circle.baby.api.models.responses;

import java.util.List;

import cn.timeface.circle.baby.api.models.base.BaseResponse;
import cn.timeface.circle.baby.api.models.objs.BabyListInfo;
import cn.timeface.circle.baby.api.models.objs.BabyObj;

/**
 * Created by lidonglin on 2016/5/6.
 */
public class BabyInfoListResponse extends BaseResponse {

    List<BabyListInfo> dataList;

    public List<BabyListInfo> getDataList() {
        return dataList;
    }

    public void setDataList(List<BabyListInfo> dataList) {
        this.dataList = dataList;
    }
}
