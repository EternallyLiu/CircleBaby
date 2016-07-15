package cn.timeface.circle.baby.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.adapters.MyOrderBookAdapter;
import cn.timeface.circle.baby.api.Api;
import cn.timeface.circle.baby.api.models.objs.MyOrderBookItem;
import cn.timeface.circle.baby.api.models.responses.MyOrderConfirmListResponse;
import cn.timeface.circle.baby.payment.OrderInfoObj;
import cn.timeface.circle.baby.payment.PrepareOrderException;
import cn.timeface.circle.baby.payment.alipay.AlipayPayment;
import cn.timeface.circle.baby.payment.timeface.AliPayNewUtil;
import cn.timeface.circle.baby.utils.FastData;
import cn.timeface.circle.baby.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.views.OrderDetailFootView;
import cn.timeface.circle.baby.views.OrderDetailHeaderView;
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

    private void doPay() {
        float orderPrice = listResponse.getOrderPrice();
        orderPrice = (float) 0.01;
        new AliPayNewUtil(OrderDetailActivity.this, orderId, getPayTitle(), orderPrice, "2").pay();
//        OrderInfoObj orderInfoObj = new OrderInfoObj();
//        orderInfoObj.setTradeNo(orderId);
//        orderInfoObj.setPrice(listResponse.getOrderPrice());
//        orderInfoObj.setSubject(getPayTitle());
//        orderInfoObj.setBody(getPayTitle());
//        try {
//            new AlipayPayment().requestPayment(this, orderInfoObj);
//        } catch (PrepareOrderException e) {
//            Log.e(TAG, "startPayment: ", e);
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
}
