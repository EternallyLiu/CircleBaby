package cn.timeface.circle.baby.support.api.models.responses;

import java.util.List;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;
import cn.timeface.circle.baby.support.api.models.objs.CloudAlbumObj;

/**
 * Created by zhsheng on 2016/6/7.
 */
public class CloudAlbumListResponse extends BaseResponse {
    private List<CloudAlbumObj> dataList;

    public List<CloudAlbumObj> getDataList() {
        return dataList;
    }

    public void setDataList(List<CloudAlbumObj> dataList) {
        this.dataList = dataList;
    }
}
