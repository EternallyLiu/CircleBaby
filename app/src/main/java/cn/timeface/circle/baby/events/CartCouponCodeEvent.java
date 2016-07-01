package cn.timeface.circle.baby.events;

/**
 * @author WXW
 * @from 2015/6/5
 * @TODO 扫描到印书码
 */
public class CartCouponCodeEvent {
    public String couponCode;

    public CartCouponCodeEvent(String couponCode) {
        this.couponCode = couponCode;
    }
}
