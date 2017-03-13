package cn.timeface.circle.baby.ui.timelines.beans;

import java.util.List;

import cn.timeface.circle.baby.support.mvp.response.bases.BaseResponse;

/**
 * Created by wangshuai on 2017/1/9.
 */

public class TimeAxixResponse extends BaseResponse {

    private List<TimeAxisObj> dataList;

    public List<TimeAxisObj> getDataList() {
        return dataList;
    }

    public void setDataList(List<TimeAxisObj> dataList) {
        this.dataList = dataList;
    }

}
