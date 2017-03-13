package cn.timeface.circle.baby.support.api.models;

import android.text.TextUtils;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import cn.timeface.circle.baby.support.api.models.base.BaseModule;
import cn.timeface.circle.baby.support.utils.Utils;


/**
 * @author wxw
 * @from 2016/1/12
 * @TODO
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class CouponItem extends BaseModule {

    private int id;//印书券id
    private String imgUrl;//印书券图片url
    private long expired;//有效期时间
    private int couponType;//个人券（type=1表示具体优惠券面值(10:10元) type=2表示折扣券折扣值(85:八五折) type=3表示满减券减额 type=5表示实物奖品数量） 全场券（打折类型，0:全场折扣，1:满减'） 11-线下优惠券 21-线下折扣券
    private int type;//印书券类型 0：正常 1：即将过期 2：已使用 3：已过期
    private int personType;//个人印书券类型 1:个人劵、2：全场劵。3: 线下券 5: 商家优惠码
    private String discount; //10.0元（优惠劵） 85（折扣券） 100_20（满减券）
    private String couponDesc; //印书券显示信息
    private String couponCode; //线下印书券 存放具体的8位code码
    private int spotDispatch; //现场配送

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImgUrl() {
        return TextUtils.isEmpty(imgUrl) ? null : imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public long getExpired() {
        return expired;
    }

    public void setExpired(long expired) {
        this.expired = expired;
    }

    public int getCouponType() {
        return couponType;
    }

    public void setCouponType(int couponType) {
        this.couponType = couponType;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getPersonType() {
        return personType;
    }

    public void setPersonType(int personType) {
        this.personType = personType;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getCouponDesc() {
        return couponDesc;
    }

    public void setCouponDesc(String couponDesc) {
        this.couponDesc = couponDesc;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public int getSpotDispatch() {
        return spotDispatch;
    }

    public void setSpotDispatch(int spotDispatch) {
        this.spotDispatch = spotDispatch;
    }

    public float getDiscountFloat(float bookPrice) {
        float discountFloat = getDiscountFloat();
        //根据订单总价和折扣率计算优惠的金额
        if (personType == 1 && couponType == 2
                || personType == 2 && couponType == 0
                || personType == 3 && couponType == 21
                || personType == 5 && couponType == 2) {
//            discountFloat = bookPrice * (100 - discountFloat) * 0.01f;
            discountFloat = Utils.floatRound(bookPrice * (100 - discountFloat) * 0.01f);
        }
        if (!canDiscount(bookPrice)) { //根据订单总价计算是否符合满减条件
            return 0;
        }
        return discountFloat;
    }

    public float getDiscountFloat() {
        float discountFloat = 0f;

        if (TextUtils.isEmpty(discount)) {
            return discountFloat;
        }

        try {
            switch (personType) {
                case 1://个人劵
                    switch (couponType) {
                        case 1://表示具体优惠券面值
                            discountFloat = Float.valueOf(discount);
                            break;
                        case 2://表示折扣券折扣值(85:八五折)
                            discountFloat = Float.valueOf(discount);// 注意：此处返回的是折扣率
                            break;
                        case 3://表示满减券减额 100_20（满减券）
                            String[] str = discount.split("_");
                            if (str.length > 1) {
                                discountFloat = Float.valueOf(str[1]);
                            }
                            break;
                        case 5://表示实物奖品数量

                            break;
                    }
                    break;
                case 2://全场劵
                    switch (couponType) {
                        case 0://全场折扣
                            discountFloat = Float.valueOf(discount);// 注意：此处返回的是折扣率
                            break;
                        case 1://满减
                            String[] str = discount.split("_");
                            if (str.length > 1) {
                                discountFloat = Float.valueOf(str[1]);
                            }
                            break;
                    }
                    break;
                case 3://线下券
                    switch (couponType) {
                        case 11://线下优惠券
                            discountFloat = Float.valueOf(discount);
                            break;
                        case 21://线下折扣券
                            discountFloat = Float.valueOf(discount);// 注意：此处返回的是折扣率
                            break;
                    }
                    break;
                case 5://商家优惠码
                    switch (couponType) {
                        case 1://表示具体优惠券面值
                            discountFloat = Float.valueOf(discount);
                            break;
                        case 2://表示折扣券折扣值(85:八五折)
                            discountFloat = Float.valueOf(discount);// 注意：此处返回的是折扣率
                            break;
                        case 3://表示满减券减额 100_20（满减券）
                            String[] str = discount.split("_");
                            if (str.length > 1) {
                                discountFloat = Float.valueOf(str[1]);
                            }
                            break;
                    }
                    break;
            }
        } catch (NumberFormatException e) {
        }
        return discountFloat;
    }

    /**
     * 根据订单总价计算是否符合满减条件
     */
    public boolean canDiscount(float bookPrice) {
        if (personType == 1 && couponType == 3 || personType == 2 && couponType == 1
                || personType == 5 && couponType == 3) {
            try {
                //满减券 100_20（满减券）
                String[] str = discount.split("_");
                if (str.length > 1) {
                    float money = Float.valueOf(str[0]);
                    return bookPrice >= money;//订单总价未达到满减条件
                }
            } catch (NumberFormatException e) {
            }
            return false;
        }
        return true;
    }

    public String getDiscountString() {
        if (!TextUtils.isEmpty(couponDesc)) {
            return couponDesc;
        }

        if (personType == 1 && couponType == 1
                || personType == 3 && couponType == 11
                || personType == 5 && couponType == 1) {
            return getDiscountFloat() + "元";
        }
        return couponDesc;
    }

    // 是否为现场配送
    public boolean isSpotDispatch() {
        return spotDispatch != 0;
    }
}
