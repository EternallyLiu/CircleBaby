package cn.timeface.circle.baby.api.models.responses;

import java.util.List;

import cn.timeface.circle.baby.api.models.base.BaseResponse;
import cn.timeface.circle.baby.api.models.objs.BabyObj;
import cn.timeface.circle.baby.api.models.objs.CardBookSizeObj;

/**
 * Created by lidonglin on 2016/7/7.
 */
public class CardBookSizeResponse extends BaseResponse {
    List<CardBookSizeObj> dataList;

    public List<CardBookSizeObj> getDataList() {
        return dataList;
    }

    public void setDataList(List<CardBookSizeObj> dataList) {
        this.dataList = dataList;
    }
}
