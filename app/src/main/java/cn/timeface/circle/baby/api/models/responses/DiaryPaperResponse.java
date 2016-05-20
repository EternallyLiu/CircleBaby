package cn.timeface.circle.baby.api.models.responses;

import java.util.List;

import cn.timeface.circle.baby.api.models.base.BaseResponse;
import cn.timeface.circle.baby.api.models.objs.Paper;

/**
 * Created by lidonglin on 2016/5/6.
 */
public class DiaryPaperResponse extends BaseResponse {
    List<Paper> dataList;

    public List<Paper> getDataList() {
        return dataList;
    }

    public void setDataList(List<Paper> dataList) {
        this.dataList = dataList;
    }
}
