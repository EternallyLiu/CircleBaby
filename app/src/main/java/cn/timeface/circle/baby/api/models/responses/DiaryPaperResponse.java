package cn.timeface.circle.baby.api.models.responses;

import java.util.List;

import cn.timeface.circle.baby.api.models.base.BaseResponse;
import cn.timeface.circle.baby.api.models.objs.TemplateObj;

/**
 * Created by lidonglin on 2016/5/6.
 */
public class DiaryPaperResponse extends BaseResponse {
    List<TemplateObj> dataList;

    public List<TemplateObj> getDataList() {
        return dataList;
    }

    public void setDataList(List<TemplateObj> dataList) {
        this.dataList = dataList;
    }
}
