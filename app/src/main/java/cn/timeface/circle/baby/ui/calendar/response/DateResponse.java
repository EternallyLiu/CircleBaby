package cn.timeface.circle.baby.ui.calendar.response;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.List;

import cn.timeface.circle.baby.support.mvp.response.bases.BaseResponse;
import cn.timeface.circle.baby.ui.calendar.bean.DateObj;


/**
 * Created by JieGuo on 16/9/29.
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class DateResponse extends BaseResponse {

    private List<DateObj> data;

    public List<DateObj> getData() {
        return data;
    }

    public void setData(List<DateObj> data) {
        this.data = data;
    }
}
