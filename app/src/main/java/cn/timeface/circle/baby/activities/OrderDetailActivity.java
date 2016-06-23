package cn.timeface.circle.baby.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
/*        FastData.setUserId("550010434672");
        Api.changeApiBaseUrl("http://stg2.v5time.net/tfmobile/");*/
        Subscription subscribe = apiService.findOrderDetail(orderId)
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(listResponse -> {
                    List<MyOrderBookItem> bookList = listResponse.getBookList();
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

                break;
            case R.id.order_action_cancel_btn:
                //取消订单

                break;
        }
    }
}
