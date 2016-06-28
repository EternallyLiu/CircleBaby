package cn.timeface.circle.baby.payment;


import cn.timeface.circle.baby.payment.alipay.AlipayPayment;
import cn.timeface.circle.baby.payment.weixinpay.WeixinPayment;

/**
 * Created by JieGuo on 1/22/16.
 */
public class PaymentFactory {

    private PaymentFactory() {
    }

    public static Payment newInstance(int type) {

        switch (type) {
            case Payment.ALIPAY:

                return new AlipayPayment();

            case Payment.WEIXIN_PAY:

                return new WeixinPayment();

            default:
                return new AlipayPayment();
        }
    }
}
