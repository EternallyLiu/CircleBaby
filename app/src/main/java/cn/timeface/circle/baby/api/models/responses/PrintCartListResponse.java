package cn.timeface.circle.baby.api.models.responses;

import java.util.List;

import cn.timeface.circle.baby.api.models.PrintCartItem;
import cn.timeface.circle.baby.api.models.base.BaseResponse;

/**
 * @author YW.SUN
 * @from 2015/5/18
 * @TODO 印刷车列表response
 */
public class PrintCartListResponse extends BaseResponse {
    private List<PrintCartItem> dataList; //返回数据集

    public List<PrintCartItem> getDataList() {
        return dataList;
    }

    public void setDataList(List<PrintCartItem> dataList) {
        this.dataList = dataList;
    }

    public float getTotalPrice() {
        float price = 0;
        for (PrintCartItem item : dataList) {
            price += item.getLittlePrice();
        }

        return price;
    }

}
