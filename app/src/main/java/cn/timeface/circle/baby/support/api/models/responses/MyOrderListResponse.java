package cn.timeface.circle.baby.support.api.models.responses;

import java.util.List;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;
import cn.timeface.circle.baby.support.api.models.objs.OrderObj;

/**
 * @author WXW
 * @from 2015/5/18
 * @TODO 订单列表response
 */
public class MyOrderListResponse extends BaseResponse {
    private int currentPage; // 当前页码
    private int totalPage; // 总页数
    private List<OrderObj> dataList; //返回数据集

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public List<OrderObj> getDataList() {
        return dataList;
    }

    public void setDataList(List<OrderObj> dataList) {
        this.dataList = dataList;
    }

    public boolean isLastPage() {
        return currentPage >= totalPage;
    }
}
