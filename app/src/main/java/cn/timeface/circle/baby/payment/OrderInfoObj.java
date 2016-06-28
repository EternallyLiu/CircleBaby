package cn.timeface.circle.baby.payment;


import cn.timeface.circle.baby.api.models.base.BaseObj;

/**
 * Created by JieGuo on 1/25/16.
 */
public class OrderInfoObj extends BaseObj {

    // 订单号
    String tradeNo;

    // 商品名称，显示在账单里的名称
    String subject;

    // 商品详情
    String body;

    // 付款金额
    double price;


    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
