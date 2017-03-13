package cn.timeface.circle.baby.support.payment.timeface;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;

import org.greenrobot.eventbus.EventBus;

import java.net.URLEncoder;
import java.util.HashMap;

import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.events.PayResultEvent;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 支付宝支付Util
 *
 * @author WXW
 * @from 2015年6月1日
 * @TODO 支付宝支付Util
 */
@SuppressLint("HandlerLeak")
public class AliPayNewUtil {
    public static final String TAG = "alipay-sdk";
    private static final int RQF_PAY = 1;
    private static final int RQF_LOGIN = 2;
    //    private OrderDetailResponse orderDetail;
    private String orderId;
    private String title;
    private float totalPrice;
    private Activity activity;
    private boolean isSuccess = false;
    private String payType;
    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            Result result = new Result((String) msg.obj);

            switch (msg.what) {
                case RQF_PAY: {
                    EventBus.getDefault().post(new PayResultEvent(PayResultEvent.PayType.TB, result.getResultCode()));
//                Toast.makeText(activity, result.getResultCode(), Toast.LENGTH_SHORT).show();
//                Toast.makeText(activity, result.getResultCode().equals("9000") ? "true" : "false", Toast.LENGTH_SHORT).show();
                    if (result.getResultCode().equals("9000")) {
//                    Toast.makeText(activity, "request update", Toast.LENGTH_SHORT).show();
//                    while (!success)
//                    {
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("orderId", orderId);
                        params.put("payType", payType);

                        Subscription s = BaseAppCompatActivity.apiService.wexinPay(orderId, payType)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(
                                        response -> {
                                            AliPayNewUtil.this.isSuccess = response.success();
                                        }
                                        , throwable -> {
                                            AliPayNewUtil.this.isSuccess = false;
                                        }
                                );
                        ((BaseAppCompatActivity) activity).addSubscription(s);
                    } else if (result.getResultCode().equals("6001")) {
                        Toast.makeText(activity, activity.getString(R.string.pay_cancel), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(activity, result.getResult(), Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                case RQF_LOGIN: {
                    Toast.makeText(activity, result.getResult(), Toast.LENGTH_SHORT).show();

                }
                break;
                default:
                    break;
            }
        }
    };

    public AliPayNewUtil(Activity activity, String orderId, String title, float totalPrice, String type) {
        this.activity = activity;
        this.orderId = orderId;
        this.title = title;
        this.totalPrice = totalPrice;
        this.payType = type;
    }

    @SuppressWarnings("deprecation")
    public void pay() {
        try {
            String info = getNewOrderInfo();
            String sign = Rsa.sign(info, Keys.PRIVATE);
            sign = URLEncoder.encode(sign);
            info += "&sign=\"" + sign + "\"&" + getSignType();
            // start the pay.
            Log.i(TAG, "info = " + info);

            final String orderInfo = info;
            new Thread() {
                public void run() {
                    PayTask alipay = new PayTask(activity);
                    String result = alipay.pay(orderInfo, true);
                    Log.i(TAG, "result = " + result);
                    Message msg = new Message();
                    msg.what = RQF_PAY;
                    msg.obj = result;
                    mHandler.sendMessage(msg);
                }
            }.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getNewOrderInfo() {

        String orderInfo = "partner=" + "\"" + Keys.DEFAULT_PARTNER + "\"";
        orderInfo += "&";
        orderInfo += "seller_id=" + "\"" + Keys.DEFAULT_SELLER + "\"";
        orderInfo += "&";
        orderInfo += "out_trade_no=" + "\"" + orderId + "\"";
        orderInfo += "&";
        orderInfo += "subject=" + "\"" + title + "\"";
        orderInfo += "&";
        orderInfo += "body=" + "\"" + title + "\"";
        orderInfo += "&";
        orderInfo += "total_fee=" + "\"" + totalPrice + "\"";
//        orderInfo += "total_fee=" + "\"" + 0.01 + "\"";
        orderInfo += "&";

//        orderInfo += "notify_url=" + "\"" + "http://dev1.v5time.net/baby/babyOrder/zfbNotify" + "\"";
        orderInfo += "notify_url=" + "\"" + "http://stg2.v5time.net/baby/babyOrder/zfbNotify" + "\"";

        // 接口名称， 定值
        orderInfo += "&service=\"mobile.securitypay.pay\"";

        // 支付类型，定值
        orderInfo += "&payment_type=\"1\"";

        // 字符集，默认utf-8
        orderInfo += "&_input_charset=\"utf-8\"";

        // 超时时间 ，默认30分钟.
        // 设置未付款交易的超时时间，一旦超时，该笔交易就会自动被关闭。
        // 取值范围：1m～15d。
        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
        // 该参数数值不接受小数点，如1.5h，可转换为90m。
        // 该功能需要联系支付宝配置关闭时间。
        orderInfo += "&it_b_pay=\"30m\"";

        // 商品展示网址,客户端可不加此参数
        // orderInfo += "&show_url=\"m.alipay.com\"";
        // verify(sign, orderInfo);
        return orderInfo;
    }

    private String getSignType() {
        return "sign_type=\"RSA\"";
    }
}
