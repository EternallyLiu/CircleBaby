package cn.timeface.circle.baby.support.payment;

import android.app.Activity;

/**
 * Created by JieGuo on 1/22/16.
 */
public interface Payment {

    public static final int ALIPAY = 0;     // 支付宝
    public static final int WEIXIN_PAY = 1; // 微信支付


    /**
     * 发起支付请求
     *
     * @param orderInfoObj
     */
    void requestPayment(Activity activity, OrderInfoObj orderInfoObj) throws PrepareOrderException;

    /**
     * 返回结果
     *
     * @param callback 回调
     */
    void callBack(PaymentCallback callback);

    void check() throws Exception;
}
