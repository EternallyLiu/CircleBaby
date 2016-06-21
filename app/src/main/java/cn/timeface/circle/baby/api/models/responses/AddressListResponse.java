package cn.timeface.circle.baby.api.models.responses;

import java.util.List;

import cn.timeface.circle.baby.api.models.base.BaseResponse;
import cn.timeface.circle.baby.api.models.objs.AddressObj;
import cn.timeface.circle.baby.api.models.objs.UserObj;

/**
 * Created by lidonglin on 2016/5/6.
 */
public class AddressListResponse extends BaseResponse {

    List<AddressObj> dataList;

    public List<AddressObj> getDataList() {
        return dataList;
    }

    public void setDataList(List<AddressObj> dataList) {
        this.dataList = dataList;
    }
}
