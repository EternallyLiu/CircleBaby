package cn.timeface.circle.baby.support.api.models.responses;

import java.util.List;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;
import cn.timeface.circle.baby.support.api.models.objs.BookTypeListObj;

/**
 * Created by lidonglin on 2016/6/15.
 */
public class BookTypeListResponse extends BaseResponse {
    List<BookTypeListObj> dataList;

    public List<BookTypeListObj> getDataList() {
        return dataList;
    }

    public void setDataList(List<BookTypeListObj> dataList) {
        this.dataList = dataList;
    }
}
