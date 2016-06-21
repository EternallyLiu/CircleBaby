package cn.timeface.circle.baby.api.models.objs;

import java.util.List;

import cn.timeface.circle.baby.api.models.base.BaseObj;

/**
 * Created by zhsheng on 2016/6/20.
 */
public class OrderObj extends BaseObj {
    private String orderId; // 订单编号
    private long createtime;// 订单创建时间
    private float totalPrice; // 订单总价
    private float expressFee; // 运费价格
    private int orderStatus; // 订单状态
    private int payType; // 支付方式
    private int pointsExchanged; // 兑换的积分

    private List<MyOrderBookItem> bookList; //返回数据集

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public long getCreatetime() {
        return createtime;
    }

    public void setCreatetime(long createtime) {
        this.createtime = createtime;
    }

    public float getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(float totalPrice) {
        this.totalPrice = totalPrice;
    }

    public float getExpressFee() {
        return expressFee;
    }

    public void setExpressFee(float expressFee) {
        this.expressFee = expressFee;
    }

    public int getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(int orderStatus) {
        this.orderStatus = orderStatus;
    }

    public int getPayType() {
        return payType;
    }

    public void setPayType(int payType) {
        this.payType = payType;
    }

    public int getPointsExchanged() {
        return pointsExchanged;
    }

    public void setPointsExchanged(int pointsExchanged) {
        this.pointsExchanged = pointsExchanged;
    }

    public List<MyOrderBookItem> getBookList() {
        return bookList;
    }

    public void setBookList(List<MyOrderBookItem> bookList) {
        this.bookList = bookList;
    }

    public int getBookCount() {
        int bookCount = 0;
        if (bookList != null && bookList.size() > 0) {
            for (MyOrderBookItem item : bookList) {
                int split = item.getChildNum();
                if (item.getPrintList() != null) {
                    for (PrintPropertyPriceObj priceObj : item.getPrintList()) {
                        if (split > 0) {
                            bookCount += priceObj.getNum() * split;
                        } else {
                            bookCount += priceObj.getNum();
                        }
                    }
                }
            }
        }
        return bookCount;
    }
}
