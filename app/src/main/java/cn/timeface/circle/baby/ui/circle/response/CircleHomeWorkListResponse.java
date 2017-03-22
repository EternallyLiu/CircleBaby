package cn.timeface.circle.baby.ui.circle.response;

import java.util.List;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;
import cn.timeface.circle.baby.ui.circle.bean.CircleHomeWorkExWrapperObj;

/**
 * author : sunyanwei Created on 17-3-22
 * email : sunyanwei@timeface.cn
 */
public class CircleHomeWorkListResponse extends BaseResponse {
    private List<CircleHomeWorkExWrapperObj> dataList;

    public List<CircleHomeWorkExWrapperObj> getDataList() {
        return dataList;
    }

    public void setDataList(List<CircleHomeWorkExWrapperObj> dataList) {
        this.dataList = dataList;
    }
}
