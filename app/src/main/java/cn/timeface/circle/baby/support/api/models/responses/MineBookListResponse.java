package cn.timeface.circle.baby.support.api.models.responses;

import java.util.List;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;
import cn.timeface.circle.baby.support.api.models.objs.MineBookObj;

/**
 * Created by zhsheng on 2016/7/4.
 */
public class MineBookListResponse extends BaseResponse {
    private List<MineBookObj> dataList;

    public List<MineBookObj> getDataList() {
        return dataList;
    }

    public void setDataList(List<MineBookObj> dataList) {
        this.dataList = dataList;
    }
}
