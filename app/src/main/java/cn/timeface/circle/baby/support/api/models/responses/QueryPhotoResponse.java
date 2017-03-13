package cn.timeface.circle.baby.support.api.models.responses;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.List;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;
import cn.timeface.circle.baby.support.api.models.objs.MediaWrapObj;

/**
 * 按时间、发布者、地点、标签查询图片response
 * author : YW.SUN Created on 2017/2/14
 * email : sunyw10@gmail.com
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class QueryPhotoResponse extends BaseResponse {
    private List<MediaWrapObj> dataList;

    public List<MediaWrapObj> getDataList() {
        return dataList;
    }

    public void setDataList(List<MediaWrapObj> dataList) {
        this.dataList = dataList;
    }
}
