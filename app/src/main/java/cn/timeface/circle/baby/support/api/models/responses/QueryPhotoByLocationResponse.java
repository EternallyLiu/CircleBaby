package cn.timeface.circle.baby.support.api.models.responses;

import java.util.List;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;
import cn.timeface.circle.baby.support.api.models.objs.MediaObj;

/**
 * 按地点查询所有的图片
 * author : YW.SUN Created on 2017/2/14
 * email : sunyw10@gmail.com
 */
public class QueryPhotoByLocationResponse extends BaseResponse {
    private List<MediaObj> dataList;

    public List<MediaObj> getDataList() {
        return dataList;
    }

    public void setDataList(List<MediaObj> dataList) {
        this.dataList = dataList;
    }
}
