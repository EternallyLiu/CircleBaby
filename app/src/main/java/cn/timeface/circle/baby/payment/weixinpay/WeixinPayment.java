package cn.timeface.circle.baby.payment.weixinpay;

import android.app.Activity;

import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import cn.timeface.circle.baby.constants.WXConstants;
import cn.timeface.circle.baby.payment.OrderInfoObj;
import cn.timeface.circle.baby.payment.Payment;
import cn.timeface.circle.baby.payment.PaymentCallback;
import cn.timeface.circle.baby.payment.PrepareOrderException;


/**
 * Created by JieGuo on 15/1/22.
 */
public class WeixinPayment implements Payment {

  public static String APP_ID = WXConstants.APP_ID;
  private static final String TAG = "WeixinPayment";
  private static final String TYPE = "weixin";

  IWXAPI api;
  private PaymentCallback callback;
  private int amount;
  private Activity activity;

  @Override
  public void requestPayment(Activity activity, OrderInfoObj orderInfo) throws PrepareOrderException {

    api = WXAPIFactory.createWXAPI(activity, APP_ID);
    api.registerApp(APP_ID);

    PayReq req = new PayReq();
    req.appId = APP_ID;


  }

  @Override
  public void callBack(PaymentCallback callback) {
    this.callback = callback;
  }

  private void requestServerOrder() {


  }

  @Override
  public void check() throws Exception {

  }
}
