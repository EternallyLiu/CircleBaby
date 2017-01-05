package cn.timeface.circle.baby.support.api.models.responses;

import java.util.List;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;
import cn.timeface.circle.baby.support.api.models.objs.ImageInfoListObj;

/**
 * Created by zhsheng on 2016/6/8.
 */
public class AlbumDetailResponse extends BaseResponse {
    private List<ImageInfoListObj> dataList;

    public List<ImageInfoListObj> getDataList() {
        return dataList;
    }

    public void setDataList(List<ImageInfoListObj> dataList) {
        this.dataList = dataList;
    }
}
