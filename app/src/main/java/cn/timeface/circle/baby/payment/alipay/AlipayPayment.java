package cn.timeface.circle.baby.payment.alipay;

import android.app.Activity;
import android.util.Log;

import com.alipay.sdk.app.PayTask;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import cn.timeface.circle.baby.payment.OrderInfoObj;
import cn.timeface.circle.baby.payment.Payment;
import cn.timeface.circle.baby.payment.PaymentCallback;
import cn.timeface.circle.baby.payment.PrepareOrderException;
import cn.timeface.circle.baby.payment.extras.SignUtils;

/**
 * Created by JieGuo on 1/22/16.
 */
public class AlipayPayment implements Payment {

    private static final String TAG = "AlipayPayment";

    // 商户PID
    public static final String PARTNER = "2088002597202423";
    // 商户收款账号
    public static final String SELLER = "ahea@163.com";
    // 商户私钥，pkcs8格式
    public static final String RSA_PRIVATE =
            "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJwXH9rWUvLtXvbl"
                    + "pHWv6301QZCM+uZD4TeORoLSvm8VSjaM5ZNan/TQ8leKqk1DJBzF7DhbuAqM4NuM"
                    + "r5SgftOlAEWJI0Ylx3q90Uvllpq5sdcpsW3jhVJg8EcE27l0LH6yTuSYBLkSI4uB"
                    + "xR9vJ9qneCkuEKG+3KHKscGvInx1AgMBAAECgYBdonmXe05TMBXxohygBKINgC8O"
                    + "maPBEiM+gnjF7coTNQBJ7Qei95BQ+i8GWMaEhqxZHlnwDQVAPvZ8fc6uKAEr4Ujt"
                    + "OAgtDPuGgwX8vf86gPQ6wSYOIKMuzDgPyYnbVja7hTuOfDb85+QNif6xkh3JVxOR"
                    + "vQ8pXUTX9SnPMnl6GQJBAMsYMvg5dFQ3vY/pnxslJIm1FTeUK633p3Lg9IrOcXb6"
                    + "5NtCEto9dWHnjc93Nsy0wR3Y/gS5Ca6XQmjv4lPzZ+8CQQDEwFkYI4TefWsdmLPw"
                    + "0r4qk+8+ubMn/NAJj0AhJM8HF077Z31dUV6nb/U3ziBehiS4EfN7iikxZqpRdB7o"
                    + "hZ3bAkBMDc3Ygrt7ZjxIjjYU1j3ui69cVtJcnWdJb9Bjwpde9OmK6h1hOK6icTH7"
                    + "xSryUaYX5VCKuDhV9zLZVSuuQHJlAkBKQ5QdgWKonExvKnFZCCLRbW9TjMJr6IgZ"
                    + "46FAIWWndovQZxqxu4Hvz1mOy9X598YqWFRAIEE2LVtCTYNRHwYbAkEAlrqqGi4N"
                    + "bnA+rUQk1psJNlGeK0L2RRm+YPjKDC79uMDZJF4cAkt/VUDxUzcc975o/oL7dnoF" + "L1khmYHYPTmOeg==";
    // 支付宝公钥
    public static final String RSA_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdy"
            + "PuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBV"
            + "OrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";

    PayTask payTask;
    String payInfo;
    OrderInfoObj orderInfoObj;

    @Override
    public void requestPayment(Activity activity, OrderInfoObj orderInfoObj) throws PrepareOrderException {

        this.orderInfoObj = orderInfoObj;

        try {
            payInfo = getSinOrderInfo();
        } catch (UnsupportedEncodingException e) {
            throw new PrepareOrderException("编码参数错误");
        }

        payTask = new PayTask(activity);

        new Thread(() -> {

            String result = payTask.pay(payInfo, true);

            Observable.just(result)
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .subscribe(s -> {
                        Log.d(TAG, "requestPayment: " + result);
                    });

        }).start();
    }

    @Override
    public void callBack(PaymentCallback callback) {

    }

    @Override
    public void check() throws Exception {

    }

    private String getOrderInfo() throws PrepareOrderException {
        if (orderInfoObj == null) {
            throw new PrepareOrderException("支付参数错误.");
        }

        StringBuilder builder = new StringBuilder();
        // 签约合作者身份ID
        builder.append("partner=" + "\"" + PARTNER + "\"");

        // 签约卖家支付宝账号
        builder.append("&seller_id=" + "\"" + SELLER + "\"");

        // 商户网站唯一订单号
        builder.append("&out_trade_no=" + "\"" + orderInfoObj.getTradeNo() + "\"");

        // 商品名称
        builder.append("&subject=" + "\"" + orderInfoObj.getSubject() + "\"");

        // 商品详情
        builder.append("&body=" + "\"" + orderInfoObj.getBody() + "\"");

        // 商品金额
        builder.append("&total_fee=" + "\"" + orderInfoObj.getPrice() + "\"");

        // 服务器异步通知页面路径
        // TODO: 1/25/16 异步通知服务器地址需要找接口方要
        builder.append("&notify_url=" + "\"" + "http://dev1.v5time.net/baby/babyOrder/zfbNotify" + "\"");

        // 服务接口名称， 固定值
        builder.append("&service=\"mobile.securitypay.pay\"");

        // 支付类型， 固定值
        builder.append("&payment_type=\"1\"");

        // 参数编码， 固定值
        builder.append("&_input_charset=\"utf-8\"");

        // 设置未付款交易的超时时间
        // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
        // 取值范围：1m～15d。
        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
        // 该参数数值不接受小数点，如1.5h，可转换为90m。
        builder.append("&it_b_pay=\"30m\"");

        // extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
        // orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

        // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
        builder.append("&return_url=\"m.alipay.com\"");

        // 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
        // orderInfo += "&paymethod=\"expressGateway\"";

        return builder.toString();
    }

    private String getSinOrderInfo() throws UnsupportedEncodingException, PrepareOrderException {
        String sign = SignUtils.sign(getOrderInfo(), RSA_PRIVATE);
        sign = URLEncoder.encode(sign, "UTF-8");

        return getOrderInfo() + "&sign=\"" + sign + "\"&" + getSignType();
    }

    private String getSignType() {
        return "sign_type=\"RSA\"";
    }
}
