package cn.timeface.circle.baby.support.api.models.responses;

import java.util.List;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;
import cn.timeface.circle.baby.support.api.models.objs.MediaWrapObj;

/**
 * 照片分组
 * author : YW.SUN Created on 2017/2/20
 * email : sunyw10@gmail.com
 */
public class GroupPhotoByLocationResponse extends BaseResponse {
    private List<MediaWrapObj> dataList;

    public List<MediaWrapObj> getDataList() {
        return dataList;
    }

    public void setDataList(List<MediaWrapObj> dataList) {
        this.dataList = dataList;
    }
}
