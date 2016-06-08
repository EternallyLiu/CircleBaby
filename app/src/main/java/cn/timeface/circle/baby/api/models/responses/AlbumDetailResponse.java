package cn.timeface.circle.baby.api.models.responses;

import java.util.List;

import cn.timeface.circle.baby.api.models.base.BaseResponse;
import cn.timeface.circle.baby.api.models.objs.CloudAlbumDetailObj;

/**
 * Created by zhsheng on 2016/6/8.
 */
public class AlbumDetailResponse extends BaseResponse {
    private List<CloudAlbumDetailObj> dataList;

    public List<CloudAlbumDetailObj> getDataList() {
        return dataList;
    }

    public void setDataList(List<CloudAlbumDetailObj> dataList) {
        this.dataList = dataList;
    }
}
