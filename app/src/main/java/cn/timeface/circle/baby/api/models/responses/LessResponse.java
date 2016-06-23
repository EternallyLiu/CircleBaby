package cn.timeface.circle.baby.api.models.responses;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.List;

import cn.timeface.circle.baby.api.models.base.BaseResponse;
import cn.timeface.circle.baby.api.models.objs.PrintParamResponse;

/**
 * @author YW.SUN
 * @from 2015/5/26
 * @TODO base返回参数比较少的
 */

@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class LessResponse extends BaseResponse {
    private String orderId;//订单编号

    private List<PrintParamResponse> dataList;//获取订单配置参数
    private float price;//时光书单价
    private String printId;
    //时光书印刷状态返回
    private int printCode;//打印状态code
    private int pageCount;//时光书总页数

    public int getPrintCode() {
        return printCode;
    }

    public void setPrintCode(int printCode) {
        this.printCode = printCode;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public String getPrintId() {
        return printId;
    }

    public void setPrintId(String printId) {
        this.printId = printId;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public List<PrintParamResponse> getDataList() {
        return dataList;
    }

    public void setDataList(List<PrintParamResponse> dataList) {
        this.dataList = dataList;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}
