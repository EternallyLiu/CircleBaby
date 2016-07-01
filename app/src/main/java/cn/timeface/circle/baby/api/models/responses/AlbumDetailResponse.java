package cn.timeface.circle.baby.api.models.responses;

import java.util.List;

import cn.timeface.circle.baby.api.models.base.BaseResponse;
import cn.timeface.circle.baby.api.models.objs.MediaObj;

/**
 * Created by zhsheng on 2016/6/8.
 */
public class AlbumDetailResponse extends BaseResponse {
    private List<MediaObj> dataList;

    public List<MediaObj> getDataList() {
        return dataList;
    }

    public void setDataList(List<MediaObj> dataList) {
        this.dataList = dataList;
    }
}
