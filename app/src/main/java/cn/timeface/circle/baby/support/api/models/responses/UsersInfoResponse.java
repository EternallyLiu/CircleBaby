package cn.timeface.circle.baby.support.api.models.responses;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.List;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;
import cn.timeface.circle.baby.support.api.models.objs.UserWrapObj;

/**
 * author : YW.SUN Created on 2017/2/15
 * email : sunyw10@gmail.com
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class UsersInfoResponse extends BaseResponse {
    private List<UserWrapObj> dataList;

    public List<UserWrapObj> getDataList() {
        return dataList;
    }

    public void setDataList(List<UserWrapObj> dataList) {
        this.dataList = dataList;
    }
}
