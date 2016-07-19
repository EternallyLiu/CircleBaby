package cn.timeface.circle.baby.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.wbtech.ums.UmsAgent;

import org.greenrobot.eventbus.Subscribe;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.adapters.MyOrderBookAdapter;
import cn.timeface.circle.baby.api.Api;
import cn.timeface.circle.baby.api.models.objs.MyOrderBookItem;
import cn.timeface.circle.baby.api.models.responses.MyOrderConfirmListResponse;
import cn.timeface.circle.baby.constants.LogConstant;
import cn.timeface.circle.baby.constants.TypeConstant;
import cn.timeface.circle.baby.dialogs.SelectPayWayDialog;
import cn.timeface.circle.baby.events.PayResultEvent;
import cn.timeface.circle.baby.payment.OrderInfoObj;
import cn.timeface.circle.baby.payment.PrepareOrderException;
import cn.timeface.circle.baby.payment.alipay.AlipayPayment;
import cn.timeface.circle.baby.payment.timeface.AliPayNewUtil;
import cn.timeface.circle.baby.utils.FastData;
import cn.timeface.circle.baby.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.views.OrderDetailFootView;
import cn.timeface.circle.baby.views.OrderDetailHeaderView;
import cn.timeface.circle.baby.views.dialog.TFProgressDialog;
import rx.Subscription;

public class OrderDetailActivity extends BaseAppCompatActivity {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.appbar_layout)
    AppBarLayout appbarLayout;
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    List<MyOrderBookItem> listData = new ArrayList<>();
    private OrderDetailHeaderView detailHeaderView;
    private String orderId;
    private MyOrderBookAdapter orderBookAdapter;
    private OrderDetailFootView detailFootView;
    private MyOrderConfirmListResponse listResponse;
    private float orderPrice;
    private TFProgressDialog progressDialog ;
    private int orderStatus;
    private String orderSummary;
    private Timer timer = new Timer(true);
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (progressDialog != null) progressDialog.dismiss();
                    PaySuccessActivity.open(OrderDetailActivity.this, orderId,
                            orderPrice, orderStatus, orderSummary);
                    finish();
                    break;
                case 2:
                    if (orderStatus == TypeConstant.STATUS_CHECKING) {
                        timer.cancel();
                        progressDialog.dismiss();
                        PaySuccessActivity.open(OrderDetailActivity.this, orderId,
                                orderPrice, orderStatus, orderSummary);
                        finish();
                    }
                    break;
            }
        }
    };

    public static void open(Context context, String orderId) {
        Intent intent = new Intent(context, OrderDetailActivity.class);
        intent.putExtra("orderId", orderId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        orderId = getIntent().getStringExtra("orderId");
        setupView();
        reqOrderListData();

    }

    private void setupView() {
        progressDialog = new TFProgressDialog(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        detailHeaderView = new OrderDetailHeaderView(this);
        orderBookAdapter = new MyOrderBookAdapter(this, listData);
        detailFootView = new OrderDetailFootView(this);
        orderBookAdapter.addHeader(detailHeaderView);
        orderBookAdapter.addFooter(detailFootView);
        recyclerView.setAdapter(orderBookAdapter);
    }

    private void reqOrderListData() {

        Subscription subscribe = apiService.findOrderDetail(orderId)
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(listResponse -> {
                    this.listResponse = listResponse;
                    List<MyOrderBookItem> bookList = listResponse.getBookList();
                    listData.clear();
                    listData.addAll(bookList);
                    detailHeaderView.setupViewData(listResponse);
                    detailFootView.setupViewData(listResponse);
                    orderBookAdapter.notifyDataSetChanged();
                }, throwable -> {

                });
        addSubscription(subscribe);
    }

    public void clickBtn(View view) {
        switch (view.getId()) {
            case R.id.order_action_btn:
                //去支付
                doPay();
                break;
            case R.id.order_action_cancel_btn:
                //取消订单
                apiService.cancelOrder(orderId)
                        .compose(SchedulersCompat.applyIoSchedulers())
                        .subscribe(response -> {
                            if (response.success()) {
                                reqOrderListData();
                            }
                        }, throwable -> {
                        });
                break;
        }
    }

    /**
     * 支付
     */
    private void doPay() {
        orderPrice = listResponse.getOrderPrice();
        UmsAgent.onEvent(this, LogConstant.IMMEDIATELY_PAY);
        progressDialog.setMessage(R.string.begin_payoff);
        Log.i("------->", "orderPrice:" + orderPrice + "-->getPayTitle:" + getPayTitle());
//        if (orderPrice == 0) {//积分支付
//            reqPayByPoint();
//        } else {// 现金支付
        final SelectPayWayDialog dialog = new SelectPayWayDialog(0);
        dialog.setClickListener(new SelectPayWayDialog.ClickListener() {
            @Override
            public void okClick(int payType) {
                dialog.dismiss();
                progressDialog.show();
                switch (payType) {
                    // 支付宝
                    case 1:
                        orderPrice = 0.01f;//一分钱测试支付
//                            if (isUsePoint || !TextUtils.isEmpty(getUseCouponId())) {
//                                //4混合支付(支付宝)
//                                new AliPayNewUtil(MyOrderConfirmActivity.this, orderId, getPayTitle(), orderPrice, "4").pay();
//                            } else {
//                                //2支付宝支付
                        System.out.println("2支付宝支付==========="+orderId);
                        new AliPayNewUtil(OrderDetailActivity.this, orderId, getPayTitle(), orderPrice, "2").pay();
//                            }
                        break;

                    // 微信
//                        case 2:
                            /*if (isUsePoint || !TextUtils.isEmpty(getUseCouponId())) {
                                new WxUtil(MyOrderConfirmActivity.this, orderId, FastData.getUserId(), "3").pay();
                            } else {
                                new WxUtil(MyOrderConfirmActivity.this, orderId, FastData.getUserId(), "1").pay();
                            }
                            break;*/

                    //翼支付
//                        case 3:
                           /* new EPayUtil(MyOrderConfirmActivity.this, orderId).pay();
                            break;*/
                }
            }

            @Override
            public void cancelClick() {
                dialog.dismiss();
                    /*OrderDetailCartActivity.open(MyOrderConfirmActivity.this, orderId, TypeConstant.STATUS_NOT_PAY);*/
                OrderDetailActivity.open(OrderDetailActivity.this,orderId);
                finish();
            }
        });
        dialog.setCancelable(false);
        dialog.show(getSupportFragmentManager(), "dialog");
//        }
    }

    /**
     * 获取支付时显示的商品名称
     */
    private String getPayTitle() {
        List<MyOrderBookItem> dataList = listResponse.getBookList();
        if (dataList != null && dataList.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (MyOrderBookItem item : dataList) {
                sb.append(item.getBookName() + ",");
            }
            return sb.length() > 1 ? sb.substring(0, sb.lastIndexOf(",")) : "";//去掉最后一个逗号
        }
        return "";
    }

    @Subscribe
    public void onEvent(PayResultEvent event) {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }

        if (event.type != null && (event.type).equals(PayResultEvent.PayType.WX)) {
            if ((event.resultCode).equals("-1")) {
                Toast.makeText(this, getString(R.string.pay_fail), Toast.LENGTH_SHORT).show();
            } else if ((event.resultCode).equals("-2")) {
                Toast.makeText(this, getString(R.string.pay_cancel), Toast.LENGTH_SHORT).show();
            }
        }

        if (event.paySuccess()) {
            Toast.makeText(this, getString(R.string.pay_success), Toast.LENGTH_SHORT).show();
            reqConfirmOrder();
        }
    }

    /**
     * 确认订单支付结果
     */
    private void reqConfirmOrder() {
        progressDialog.setMessage(getString(R.string.pay_result_confirm_begin));
        progressDialog.show();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("确认订单支付结果==========="+orderId);
                Subscription s = apiService.findOrderDetail(orderId)
                        .compose(SchedulersCompat.applyIoSchedulers())
                        .subscribe(response -> {
                            if (response.success()) {
                                orderStatus = response.getOrderStatus();
                                orderSummary = response.getSummary();
                                mHandler.sendEmptyMessage(2);
                            }
                        });
                addSubscription(s);
            }
        }, 0, 2 * 1000);
    }



}
