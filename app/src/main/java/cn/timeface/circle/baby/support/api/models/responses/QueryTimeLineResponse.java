package cn.timeface.circle.baby.support.api.models.responses;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.ArrayList;
import java.util.List;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;
import cn.timeface.circle.baby.support.api.models.objs.TimeLineObj;
import cn.timeface.circle.baby.support.api.models.objs.TimeLineWrapObj;

/**
 * 获取时光列表response
 * author : YW.SUN Created on 2017/2/14
 * email : sunyw10@gmail.com
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class QueryTimeLineResponse extends BaseResponse {
    private int currentPage;
    private int totalPage;

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    private List<TimeLineWrapObj> dataList;

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public List<TimeLineWrapObj> getDataList() {
        return dataList;
    }

    public List<TimeLineObj> getTimeLineObjs(){
        List<TimeLineObj> timeLineObjs = new ArrayList<>();
        for(TimeLineWrapObj timeLineWrapObj : dataList){
            timeLineObjs.addAll(timeLineWrapObj.getTimelineList());
        }
        return timeLineObjs;
    }

    public void setDataList(List<TimeLineWrapObj> dataList) {
        this.dataList = dataList;
    }
}
