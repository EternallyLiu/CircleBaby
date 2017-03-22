package cn.timeface.circle.baby.ui.circle.groupmembers.responses;

import java.util.List;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;
import cn.timeface.circle.baby.support.api.models.objs.MediaObj;

/**
 * Created by wangwei on 2017/3/22.
 */

public class MediasResponse extends BaseResponse {
    private List<MediaObj> dataList;

    public List<MediaObj> getDataList() {
        return dataList;
    }

    public void setDataList(List<MediaObj> dataList) {
        this.dataList = dataList;
    }

    public MediasResponse(List<MediaObj> dataList) {
        this.dataList = dataList;

    }
}
