package cn.timeface.circle.baby.support.payment.alipay;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;

import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.events.PayResultEvent;
import cn.timeface.circle.baby.support.api.ApiFactory;
import cn.timeface.circle.baby.support.api.models.responses.AliPayResponse;
import cn.timeface.circle.baby.support.api.services.ApiService;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by zhsheng on 2016/8/25.
 */
public class AliPay {


    private final ApiService apiService;

    public AliPay() {
        apiService = ApiFactory.getApi().getApiService();
    }

    /**
     * 支付宝
     *
     * @param orderId
     * @param activity
     * @return
     */
    public Subscription payV2(String orderId, Activity activity) {
        return apiService.aliPay(orderId)
                .map(new Func1<AliPayResponse, String>() {
                    @Override
                    public String call(AliPayResponse payResponse) {
                        return payResponse.getOrderInfo();
                    }
                })
                .map(orderInfo -> {
                    PayTask alipay = new PayTask(activity);
                    Map<String, String> result = alipay.payV2(orderInfo, true);
                    return new PayResult(result);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(payResult -> {
                    String resultStatus = payResult.getResultStatus();
                    EventBus.getDefault().post(new PayResultEvent(PayResultEvent.PayType.TB, resultStatus));
                    switch (resultStatus) {
                        case "9000":
                            Toast.makeText(activity, activity.getString(R.string.pay_success), Toast.LENGTH_SHORT).show();
                            break;
                        case "6001":
                            Toast.makeText(activity, activity.getString(R.string.pay_cancel), Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(activity, Result.sResultStatus.get(resultStatus), Toast.LENGTH_SHORT).show();
                            break;
                    }
                }, throwable -> {
                    Log.e("payV2", "payV2: "+throwable );
                    EventBus.getDefault().post(new PayResultEvent(PayResultEvent.PayType.TB, "4006"));
                    Toast.makeText(activity, activity.getString(R.string.pay_fail), Toast.LENGTH_SHORT).show();
                });
    }

}
