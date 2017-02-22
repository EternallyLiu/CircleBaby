package cn.timeface.circle.baby.support.api.models.responses;

import java.util.List;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;
import cn.timeface.circle.baby.support.api.models.objs.MediaObj;

/**
 * 查询书中包含的图片response
 * author : YW.SUN Created on 2017/2/21
 * email : sunyw10@gmail.com
 */
public class QuerySelectedPhotoResponse extends BaseResponse {
    private List<MediaObj> dataList;

    public List<MediaObj> getDataList() {
        return dataList;
    }

    public void setDataList(List<MediaObj> dataList) {
        this.dataList = dataList;
    }
}
