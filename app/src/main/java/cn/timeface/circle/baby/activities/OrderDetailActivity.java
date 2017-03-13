package cn.timeface.circle.baby.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.adapters.MyOrderBookAdapter;
import cn.timeface.circle.baby.constants.TypeConstant;
import cn.timeface.circle.baby.dialogs.SelectPayWayDialog;
import cn.timeface.circle.baby.events.OrderListRefreshEvent;
import cn.timeface.circle.baby.events.PayResultEvent;
import cn.timeface.circle.baby.support.api.models.objs.MyOrderBookItem;
import cn.timeface.circle.baby.support.api.models.responses.MyOrderConfirmListResponse;
import cn.timeface.circle.baby.support.managers.listeners.IEventBus;
import cn.timeface.circle.baby.support.payment.alipay.AliPay;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.views.OrderDetailFootView;
import cn.timeface.circle.baby.views.OrderDetailHeaderView;
import cn.timeface.circle.baby.views.dialog.TFProgressDialog;
import rx.Observable;
import rx.Subscription;

public class OrderDetailActivity extends BaseAppCompatActivity implements IEventBus {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.appbar_layout)
    AppBarLayout appbarLayout;
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    List<MyOrderBookItem> listData = new ArrayList<>();
    @Bind(R.id.order_total_price_tv)
    TextView orderTotalPriceTv;
    @Bind(R.id.order_action_cancel_btn)
    Button orderActionCancelBtn;
    @Bind(R.id.order_action_btn)
    Button orderActionBtn;
    private OrderDetailHeaderView detailHeaderView;
    private String orderId;
    private MyOrderBookAdapter orderBookAdapter;
    private OrderDetailFootView detailFootView;
    private MyOrderConfirmListResponse listResponse;
    private float orderPrice;
    private TFProgressDialog progressDialog;
    private int orderStatus;
    private String orderSummary;

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
        progressDialog = TFProgressDialog.getInstance("");
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
//                    listData.clear();
//                    listData.addAll(bookList);
                    orderBookAdapter.setListData(bookList);
                    detailHeaderView.setupViewData(listResponse);
                    detailFootView.setupViewData(listResponse);


                    // 配送中 || 已送达
                    if (listResponse.getOrderStatus() == 0 || listResponse.getOrderStatus() == 3) {
                        // 商家优惠码现场配送不显示运单号、物流信息
//                        if (listResponse.getOrderStatus() == 5) {
                        orderActionBtn.setText(getResources().getString(R.string.show_order));
                        orderActionBtn.setVisibility(View.VISIBLE);
                        orderActionCancelBtn.setVisibility(View.GONE);
                        orderActionBtn.setBackgroundResource(R.color.bg_color1);
//                        }
                    }
                    // 待确认(未支付)
                    if (listResponse.getOrderStatus() == TypeConstant.STATUS_NOT_PAY) {
                        orderActionBtn.setVisibility(View.VISIBLE);
                        orderActionCancelBtn.setVisibility(View.VISIBLE);
                        orderActionBtn.setText(getResources().getString(R.string.payoff_at_once));
                        orderActionBtn.setBackgroundResource(R.drawable.selector_red_btn_bg);
                    } else if (listResponse.getOrderStatus() == TypeConstant.STATUS_CLOSED || listResponse.getOrderStatus() == TypeConstant.STATUS_DELIVERY_SUCCESS) {
                        //已关闭
                        orderActionBtn.setVisibility(View.GONE);
                        orderActionCancelBtn.setVisibility(View.GONE);
                    }
                    orderTotalPriceTv.setText(String.format(getResources().getString(R.string.total_price), listResponse.getOrderPrice()));


                    orderBookAdapter.notifyDataSetChanged();
                }, throwable -> {

                });
        addSubscription(subscribe);
    }

    public void clickBtn(View view) {
        switch (view.getId()) {
            case R.id.order_action_btn:
                if (listResponse.getOrderStatus() == 0 || listResponse.getOrderStatus() == 3 || listResponse.getOrderStatus() == 5) {
                    //确认收货
                    apiService.receipt(orderId)
                            .compose(SchedulersCompat.applyIoSchedulers())
                            .subscribe(response -> {
                                if (response.success()) {
                                    setupView();
                                    reqOrderListData();
                                    EventBus.getDefault().post(new OrderListRefreshEvent());
                                }
                            });

                } else if (listResponse.getOrderStatus() == TypeConstant.STATUS_NOT_PAY) {
                    //去支付
                    doPay();
                }
                break;
            case R.id.order_action_cancel_btn:
                //取消订单
                apiService.cancelOrder(orderId)
                        .compose(SchedulersCompat.applyIoSchedulers())
                        .subscribe(response -> {
                            if (response.success()) {
                                setupView();
                                reqOrderListData();
                                EventBus.getDefault().post(new OrderListRefreshEvent());
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
        progressDialog.setTvMessage(getString(R.string.begin_payoff));
        Log.i("------->", "orderPrice:" + orderPrice + "-->getPayTitle:" + getPayTitle());
//        if (orderPrice == 0) {//积分支付
//            reqPayByPoint();
//        } else {// 现金支付
        final SelectPayWayDialog dialog = new SelectPayWayDialog(0);
        dialog.setClickListener(new SelectPayWayDialog.ClickListener() {
            @Override
            public void okClick(int payType) {
                dialog.dismiss();
                progressDialog.show(getSupportFragmentManager(), "");
                switch (payType) {
                    // 支付宝
                    case 1:
//                        orderPrice = 0.01f;//一分钱测试支付
//                            if (isUsePoint || !TextUtils.isEmpty(getUseCouponId())) {
//                                //4混合支付(支付宝)
//                                new AliPayNewUtil(MyOrderConfirmActivity.this, orderId, getPayTitle(), orderPrice, "4").pay();
//                            } else {
//                                //2支付宝支付
//                        new AliPayNewUtil(OrderDetailActivity.this, orderId, getPayTitle(), orderPrice, "2").pay();

                        addSubscription(new AliPay().payV2(orderId, OrderDetailActivity.this));
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
                }
            }

            @Override
            public void cancelClick() {
                dialog.dismiss();
                    /*OrderDetailCartActivity.open(MyOrderConfirmActivity.this, orderId, TypeConstant.STATUS_NOT_PAY);*/
//                OrderDetailActivity.open(OrderDetailActivity.this, orderId);
//                finish();
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

//        if (event.type != null && (event.type).equals(PayResultEvent.PayType.WX)) {
//            if ((event.resultCode).equals("-1")) {
//                Toast.makeText(this, getString(R.string.pay_fail), Toast.LENGTH_SHORT).show();
//            } else if ((event.resultCode).equals("-2")) {
//                Toast.makeText(this, getString(R.string.pay_cancel), Toast.LENGTH_SHORT).show();
//            }
//        }

        if (event.paySuccess()) {
            Toast.makeText(this, getString(R.string.pay_success), Toast.LENGTH_SHORT).show();
            reqConfirmOrder();
        }
    }

    /**
     * 确认订单支付结果
     */
    private void reqConfirmOrder() {
        progressDialog.setTvMessage(getString(R.string.pay_result_confirm_begin));
        progressDialog.show(getSupportFragmentManager(), "");

        Subscription s = Observable.interval(1, 1, TimeUnit.SECONDS) //延时1s ，每间隔1s，时间单位
                .flatMap(aLong -> apiService.findOrderDetail(orderId))
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(
                        response -> {
                            if (response.success()) {
//                                    orderDetail = response;
                                orderStatus = response.getOrderStatus();
                                orderSummary = response.getSummary();

                                if (orderStatus == TypeConstant.STATUS_CHECKING) {
                                    if (progressDialog != null) {
                                        progressDialog.dismiss();
                                    }
                                    PaySuccessActivity.open(this, orderId, orderPrice, orderStatus, orderSummary);
                                    finish();
                                }
                            }
                        },
                        throwable -> {
                            Log.e(TAG, "reqConfirmOrder: ", throwable);
                        }
                );
        addSubscription(s);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        OrderListActivity.open(OrderDetailActivity.this);
        finish();
    }
}
