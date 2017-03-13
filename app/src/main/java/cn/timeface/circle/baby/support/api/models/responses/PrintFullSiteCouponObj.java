package cn.timeface.circle.baby.support.api.models.responses;

import cn.timeface.circle.baby.support.api.models.base.BaseObj;

/**
 * 全站优惠信息
 * Created by WXW on 2015/5/28.
 */
public class PrintFullSiteCouponObj extends BaseObj {

    private long coupon; //主键ID
    private int personType; //1:个人劵 2：全场劵 3: 线下券
    /**
     * 个人券（type=1 表示具体优惠券面值(10:10元) type=2 表示折扣券折扣值(85:八五折) type=3 表示满减券减额 type=5 表示实物奖品数量）
     * 全场券（打折类型 0:全场折扣 1:满减）
     * 11:线下优惠券
     * 21:线下折扣券
     */
    private int couponType;
    private String couponDesc; //印书券描述信息
    private float couponValue; //印书券的优惠价格
    private String discountDesc; //“还差13元，就能享受“全场满100元减20元”活动”

    public long getCoupon() {
        return coupon;
    }

    public void setCoupon(long coupon) {
        this.coupon = coupon;
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

    public float getCouponValue() {
        return couponValue;
    }

    public void setCouponValue(float couponValue) {
        this.couponValue = couponValue;
    }

    public String getDiscountDesc() {
        return discountDesc;
    }

    public void setDiscountDesc(String discountDesc) {
        this.discountDesc = discountDesc;
    }

    public String getDisableDesc(boolean isCouponCode) {
        if (personType == 2 && couponType == 0) {
            if (isCouponCode) {
                return "印书码和折扣活动不能同时享受";
            } else {
                return "印书券和折扣活动不能同时享受";
            }
        } else if (personType == 2 && couponType == 1) {
            if (isCouponCode) {
                return "印书码和满减活动不能同时享受";
            } else {
                return "印书券和满减活动不能同时享受";
            }
        }
        return "";
    }
}
