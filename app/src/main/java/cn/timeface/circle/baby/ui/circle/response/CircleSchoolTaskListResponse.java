package cn.timeface.circle.baby.ui.circle.response;

import java.util.List;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;
import cn.timeface.circle.baby.ui.circle.bean.CircleSchoolTaskObj;

/**
 * 获取书中所有老师布置的作业列表
 * author : sunyanwei Created on 17-3-22
 * email : sunyanwei@timeface.cn
 */
public class CircleSchoolTaskListResponse extends BaseResponse {
    private List<CircleSchoolTaskObj> dataList;

    public List<CircleSchoolTaskObj> getDataList() {
        return dataList;
    }

    public void setDataList(List<CircleSchoolTaskObj> dataList) {
        this.dataList = dataList;
    }
}
