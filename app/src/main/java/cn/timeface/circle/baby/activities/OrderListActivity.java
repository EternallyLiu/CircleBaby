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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.timeface.refreshload.PullRefreshLoadRecyclerView;
import com.timeface.refreshload.headfoot.LoadMoreView;
import com.timeface.refreshload.headfoot.RefreshView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.adapters.OrderListAdapter;
import cn.timeface.circle.baby.api.models.objs.OrderObj;
import cn.timeface.circle.baby.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.views.DividerItemDecoration;
import cn.timeface.circle.baby.views.TFStateView;
import rx.Subscription;

public class OrderListActivity extends BaseAppCompatActivity implements View.OnClickListener {
    public static final int START_PAGE = 1;
    public int currentPage = 1;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.appbar_layout)
    AppBarLayout appbarLayout;
    @Bind(R.id.ll_no_data)
    LinearLayout llNoData;
    @Bind(R.id.rlRecyclerView)
    PullRefreshLoadRecyclerView rlRecyclerView;
    @Bind(R.id.error_retry)
    TextView errorRetry;
    @Bind(R.id.tf_stateView)
    TFStateView tfStateView;
    private OrderListAdapter orderListAdapter;
    private List<OrderObj> orderList = new ArrayList<>();

    public static void open(Context context) {
        context.startActivity(new Intent(context, OrderListActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order_list);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setupView();
        reqData(currentPage, true);

    }

    /**
     * 初始化View状态
     */
    private void setupView() {
        RecyclerView recyclerView = rlRecyclerView.getRecyclerView();
        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, LinearLayoutManager.VERTICAL, R.color.bg7);
        itemDecoration.setItemSize(getResources().getDimensionPixelOffset(R.dimen.view_space_normal));
        recyclerView.addItemDecoration(itemDecoration);
        orderListAdapter = new OrderListAdapter(this, orderList);
        rlRecyclerView.setAdapter(orderListAdapter);

        rlRecyclerView.setLoadRefreshListener(new PullRefreshLoadRecyclerView.LoadRefreshListener() {
            @Override
            public void onRefresh(PullRefreshLoadRecyclerView pullRefreshLoadRecyclerView, RefreshView refreshView) {
                reqData(1, true);
            }

            @Override
            public void onLoadMore(PullRefreshLoadRecyclerView pullRefreshLoadRecyclerView, LoadMoreView loadMoreView) {
                reqData(currentPage, false);
            }
        });
        tfStateView.setOnRetryListener(() -> reqData(1, true));
        tfStateView.loading();
        iniListener();
    }

    private void iniListener() {
        orderListAdapter.setOnItemClickListener(orderObj -> {
            Log.d(TAG, "setupView: " + orderObj.getOrderId());
            OrderDetailActivity.open(OrderListActivity.this, orderObj.getOrderId());
        });
        errorRetry.setOnClickListener(this);
    }

    /**
     * 请求网络获取订单列表
     */
    private void reqData(int page, boolean refresh) {
        Subscription subscribe = apiService.queryOrderList(20 + "", page + "")
                .compose(SchedulersCompat.applyIoSchedulers())
                .doOnTerminate(() -> rlRecyclerView.complete())
                .subscribe(myOrderListResponse -> {
                    tfStateView.finish();
                    if (myOrderListResponse.success()) {
                        currentPage += 1;
                        if (refresh) {
                            orderList.clear();
                        }
                        orderList.addAll(myOrderListResponse.getDataList());
                        orderListAdapter.notifyDataSetChanged();
                        if (orderList.size() == 0) {
                            showNoDataView(true);
                        }
                    }
                }, throwable -> {
                    Log.d(TAG, "reqOrderListData: " + throwable.getMessage());
                });
        addSubscription(subscribe);
    }

    private void showNoDataView(boolean showNoData) {
        llNoData.setVisibility(showNoData ? View.VISIBLE : View.GONE);
        rlRecyclerView.setVisibility(showNoData ? View.GONE : View.VISIBLE);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.error_retry:
                MineBookActivity.open(this);
                finish();
                break;
        }
    }
}
