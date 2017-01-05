package cn.timeface.circle.baby.support.api.models.responses;


import java.util.List;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;
import cn.timeface.circle.baby.support.api.models.objs.PrintParamResponse;

/**获取订单配置参数
 * Created by lidonglin on 2016/6/28.
 */
public class ParamListResponse extends BaseResponse {
    List<PrintParamResponse> dataList;

    public List<PrintParamResponse> getDataList() {
        return dataList;
    }

    public void setDataList(List<PrintParamResponse> dataList) {
        this.dataList = dataList;
    }
}
