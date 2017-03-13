package cn.timeface.circle.baby.support.payment.timeface;

import com.bluelinelabs.logansquare.annotation.JsonObject;

import cn.timeface.circle.baby.support.api.models.base.BaseResponse;

/**
 * 微信预支付response
 *
 * @author yusen (QQ:1522289706)
 * @from 2014年9月12日上午11:25:12
 * @TODO 微信预支付response
 */
@JsonObject(fieldDetectionPolicy = JsonObject.FieldDetectionPolicy.NONPRIVATE_FIELDS_AND_ACCESSORS)
public class WxPrepayResponse extends BaseResponse {
    private static final long serialVersionUID = 1L;
    private String prepayId;
    private String nonceStr;
    private String sign;
    private long timestamp;
    private String packageValue;

    public String getPrepayId() {
        return prepayId;
    }

    public void setPrepayId(String prepayId) {
        this.prepayId = prepayId;
    }

    public String getNonceStr() {
        return nonceStr;
    }

    public void setNonceStr(String nonceStr) {
        this.nonceStr = nonceStr;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getPackageValue() {
        return packageValue;
    }

    public void setPackageValue(String packageValue) {
        this.packageValue = packageValue;
    }

}
