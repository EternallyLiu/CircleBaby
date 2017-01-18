package cn.timeface.circle.baby.support.api.models.responses;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.List;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;
import cn.timeface.circle.baby.support.api.models.objs.BookObj;

/**
 * book list response
 * author : YW.SUN Created on 2017/1/12
 * email : sunyw10@gmail.com
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class BookListResponse extends BaseResponse {
    private List<BookObj> dataList;

    public List<BookObj> getDataList() {
        return dataList;
    }

    public void setDataList(List<BookObj> dataList) {
        this.dataList = dataList;
    }
}
