package cn.timeface.circle.baby.api.models.responses;

import java.util.List;

import cn.timeface.circle.baby.api.models.base.BaseResponse;
import cn.timeface.circle.baby.api.models.objs.FamilyMemberInfo;
import cn.timeface.circle.baby.api.models.objs.Relationship;

/**
 * Created by lidonglin on 2016/5/3.
 */
public class FamilyListResponse extends BaseResponse {

    List<FamilyMemberInfo> dataList;

    public List<FamilyMemberInfo> getDataList() {
        return dataList;
    }

    public void setDataList(List<FamilyMemberInfo> dataList) {
        this.dataList = dataList;
    }
}
