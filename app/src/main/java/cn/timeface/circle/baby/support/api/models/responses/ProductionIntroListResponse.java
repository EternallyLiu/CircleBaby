package cn.timeface.circle.baby.support.api.models.responses;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.List;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;
import cn.timeface.circle.baby.support.api.models.objs.MediaObj;

/**
 * @author wxw
 * @from 2017/2/21
 * 印品介绍
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class ProductionIntroListResponse extends BaseResponse {

    private String html;
    private List<MediaObj> dataList;

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public List<MediaObj> getDataList() {
        return dataList;
    }

    public void setDataList(List<MediaObj> dataList) {
        this.dataList = dataList;
    }
}
