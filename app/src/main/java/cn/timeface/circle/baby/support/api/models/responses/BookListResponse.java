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
    private int hasPic; // 是否有图片 0:否 1:是

    public List<BookObj> getDataList() {
        return dataList;
    }

    public void setDataList(List<BookObj> dataList) {
        this.dataList = dataList;
    }

    public int getHasPic() {
        return hasPic;
    }

    public void setHasPic(int hasPic) {
        this.hasPic = hasPic;
    }

    public boolean hasPic() {
        return hasPic == 1;
    }
}
