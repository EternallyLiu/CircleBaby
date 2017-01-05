package cn.timeface.circle.baby.support.api.models.responses;

import java.util.List;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;
import cn.timeface.circle.baby.support.api.models.objs.MyOrderBookItem;
import cn.timeface.circle.baby.support.api.models.objs.PrintParamResponse;

/**
 * @author WXW
 * @from 2015/5/25
 * @TODO 确认订单列表response
 */
public class MyOrderConfirmListResponse extends BaseResponse {

    private int orderStatus;//订单状态
    private String orderId;//订单编号
    private long orderTime;//订单时间戳
    private String summary;//订单描述信息
    private String expressOrder;//运单号
    private String lastExpress;//最新一条快递信息
    private float coupon;//优惠券
    private int personType;//优惠类型 1:个人劵、2：全场劵、3线下券
    private int couponType;//优惠券类型
    private String couponDesc;//优惠显示信息
    private int pointsExchanged;//兑换的积分
    private float expressPrice;//运费
    private float totalPrice;//总价（优惠前总计价格）
    private float orderPrice;//订单总价

    private int addressId;//地址数据ID
    private String address;//完整收货地址
    private String contacts;//联系人名称、订单收货人
    private String contactsPhone;//联系电话、订单收货人电话
    private int points;//用户当前积分
    private int dispatch;//默认快递类型
    private int exchangeRate;//积分兑换钱得兑换率，如100

    private PrintParamResponse dispatchObject; //快递列表
    private List<MyOrderBookItem> bookList; //返回数据集

    private int from; //1：客户端 2：客户端扫描ITV二维码

    private PrintFullSiteCouponObj fullSiteCoupon;//全站优惠信息

    private int promotion;//是否有台历活动
    private float promotionFee;//台历活动减免金额
    private String discountTitle;// 优惠名称
    private float discountPrice; // 优惠价格

    public String getDiscountTitle() {
        return discountTitle;
    }

    public void setDiscountTitle(String discountTitle) {
        this.discountTitle = discountTitle;
    }

    public float getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(float discountPrice) {
        this.discountPrice = discountPrice;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getOrderStatus() {
        return orderStatus;
    }

    public float getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(float orderPrice) {
        this.orderPrice = orderPrice;
    }

    public void setOrderStatus(int orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public long getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(long orderTime) {
        this.orderTime = orderTime;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getExpressOrder() {
        return expressOrder;
    }

    public void setExpressOrder(String expressOrder) {
        this.expressOrder = expressOrder;
    }

    public String getLastExpress() {
        return lastExpress;
    }

    public void setLastExpress(String lastExpress) {
        this.lastExpress = lastExpress;
    }

    public float getCoupon() {
        return coupon;
    }

    public void setCoupon(float coupon) {
        this.coupon = coupon;
    }

    public int getPointsExchanged() {
        return pointsExchanged;
    }

    public void setPointsExchanged(int pointsExchanged) {
        this.pointsExchanged = pointsExchanged;
    }

    public float getExpressPrice() {
        return expressPrice;
    }

    public void setExpressPrice(float expressPrice) {
        this.expressPrice = expressPrice;
    }

    public float getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(float totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getAddressId() {
        return addressId;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

    public String getContactsPhone() {
        return contactsPhone;
    }

    public void setContactsPhone(String contactsPhone) {
        this.contactsPhone = contactsPhone;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getDispatch() {
        return dispatch;
    }

    public void setDispatch(int dispatch) {
        this.dispatch = dispatch;
    }

    public int getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(int exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public PrintParamResponse getDispatchObject() {
        return dispatchObject;
    }

    public void setDispatchObject(PrintParamResponse dispatchObject) {
        this.dispatchObject = dispatchObject;
    }

    public List<MyOrderBookItem> getBookList() {
        return bookList;
    }

    public void setBookList(List<MyOrderBookItem> bookList) {
        this.bookList = bookList;
    }

    public int getPersonType() {
        return personType;
    }

    public void setPersonType(int personType) {
        this.personType = personType;
    }

    public int getCouponType() {
        return couponType;
    }

    public void setCouponType(int couponType) {
        this.couponType = couponType;
    }

    public String getCouponDesc() {
        return couponDesc;
    }

    public void setCouponDesc(String couponDesc) {
        this.couponDesc = couponDesc;
    }

    public PrintFullSiteCouponObj getFullSiteCoupon() {
        return fullSiteCoupon;
    }

    public void setFullSiteCoupon(PrintFullSiteCouponObj fullSiteCoupon) {
        this.fullSiteCoupon = fullSiteCoupon;
    }

    public boolean hasPromotion() {
        return promotion == 1;
    }

    public int getPromotion() {
        return promotion;
    }

    public void setPromotion(int promotion) {
        this.promotion = promotion;
    }

    public float getPromotionFee() {
        return promotionFee;
    }

    public void setPromotionFee(float promotionFee) {
        this.promotionFee = promotionFee;
    }
}
