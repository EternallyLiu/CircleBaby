package cn.timeface.circle.baby.support.payment;

/**
 * Created by JieGuo on 1/22/16.
 */
public interface PaymentCallback {

    void onSuccess();

    /**
     * 在路上，等支付系统的异步通知确认结果
     */
    void onJourney();

    void onFailure();
}
