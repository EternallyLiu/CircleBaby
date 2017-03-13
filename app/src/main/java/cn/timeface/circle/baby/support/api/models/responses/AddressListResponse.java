package cn.timeface.circle.baby.support.api.models.responses;

import java.util.List;

import cn.timeface.circle.baby.support.api.models.AddressItem;
import cn.timeface.circle.baby.support.api.models.base.BaseResponse;

/**
 * Created by lidonglin on 2016/5/6.
 */
public class AddressListResponse extends BaseResponse {

    List<AddressItem> dataList;

    public List<AddressItem> getDataList() {
        return dataList;
    }

    public void setDataList(List<AddressItem> dataList) {
        this.dataList = dataList;
    }
}
