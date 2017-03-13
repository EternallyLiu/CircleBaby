package cn.timeface.circle.baby.support.payment.weixinpay;

import com.google.gson.annotations.SerializedName;

/**
 * Created by baziii on 15/6/3.
 */
public class WxParams {

  @SerializedName("appid")
  public String appid;
  @SerializedName("mch_id")
  public String partnerId;
  @SerializedName("prepay_id")
  public String prepayId;
  @SerializedName("request_params")
  public String packageValue="Sign=WXPay";
  @SerializedName("nonce_str")
  public String nonceStr;
  @SerializedName("time_stamp")
  public String timeStamp;
  @SerializedName("payment_sign")
  public String signParams;
}
