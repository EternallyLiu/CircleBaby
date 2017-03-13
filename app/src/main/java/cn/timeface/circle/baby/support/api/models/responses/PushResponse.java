package cn.timeface.circle.baby.support.api.models.responses;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.List;

import cn.timeface.circle.baby.support.api.models.PushItem;
import cn.timeface.circle.baby.support.api.models.base.BaseResponse;

/**
 * @author rayboot
 * @from 14-3-6 9:15
 * @TODO 消息推送数据结构
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class PushResponse extends BaseResponse {
    private static final long serialVersionUID = 1L;

    private List<PushItem> datas;

    public List<PushItem> getDatas() {
        return datas;
    }

    public void setDatas(List<PushItem> datas) {
        this.datas = datas;
    }
}
