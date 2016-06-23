package cn.timeface.circle.baby.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.adapters.OrderListAdapter;
import cn.timeface.circle.baby.api.Api;
import cn.timeface.circle.baby.api.models.objs.OrderObj;
import cn.timeface.circle.baby.utils.FastData;
import cn.timeface.circle.baby.utils.ptr.IPTRRecyclerListener;
import cn.timeface.circle.baby.utils.ptr.TFPTRRecyclerViewHelper;
import cn.timeface.circle.baby.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.views.DividerItemDecoration;
import rx.Subscription;
import rx.functions.Action0;

public class OrderListActivity extends BaseAppCompatActivity {
    public static final int START_PAGE = 1;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.appbar_layout)
    AppBarLayout appbarLayout;
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    @Bind(R.id.swLayout)
    SwipeRefreshLayout swLayout;
    @Bind(R.id.ll_no_data)
    LinearLayout llNoData;
    private OrderListAdapter orderListAdapter;
    private List<OrderObj> orderList;
    private TFPTRRecyclerViewHelper tfptrListViewHelper;

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
        reqOrderListData(START_PAGE);

    }

    /**
     * 初始化View状态
     */
    private void setupView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration itemDecoration =
                new DividerItemDecoration(this, LinearLayoutManager.VERTICAL, R.color.bg7);
        itemDecoration.setItemSize(getResources().getDimensionPixelOffset(R.dimen.view_space_normal));
        recyclerView.addItemDecoration(itemDecoration);
        orderList = new ArrayList<>();
        orderListAdapter = new OrderListAdapter(this, orderList);
        recyclerView.setAdapter(orderListAdapter);

        tfptrListViewHelper = new TFPTRRecyclerViewHelper(this, recyclerView, swLayout)
                .setTFPTRMode(TFPTRRecyclerViewHelper.Mode.BOTH)
                .tfPtrListener(new IPTRRecyclerListener() {
                    @Override
                    public void onTFPullDownToRefresh(View refreshView) {
                        reqOrderListData(START_PAGE);
                    }

                    @Override
                    public void onTFPullUpToRefresh(View refreshView) {

                    }

                    @Override
                    public void onScrollUp(int firstVisibleItem) {

                    }

                    @Override
                    public void onScrollDown(int firstVisibleItem) {

                    }
                });
        orderListAdapter.setOnItemClickListener(orderObj -> {
            Log.d(TAG, "setupView: " + orderObj.getOrderId());
            OrderDetailActivity.open(OrderListActivity.this, orderObj.getOrderId());
        });

    }

    /**
     * 请求网络获取订单列表
     *
     * @param currentPage
     */
    private void reqOrderListData(int currentPage) {
   /*     FastData.setUserId("550010434672");
        Api.changeApiBaseUrl("http://stg2.v5time.net/tfmobile/");*/
        Subscription subscribe = apiService.queryOrderList(20 + "", currentPage + "")
                .compose(SchedulersCompat.applyIoSchedulers())
                .doOnTerminate(() -> tfptrListViewHelper.finishTFPTRRefresh())
                .subscribe(myOrderListResponse -> {
                    List<OrderObj> dataList = myOrderListResponse.getDataList();
                    if (currentPage == START_PAGE) {
                        orderList.clear();
                    }
                    orderList.addAll(dataList);
                    orderListAdapter.notifyDataSetChanged();
                    showNoDataView(orderList.size() == 0);
                    tfptrListViewHelper.setTFPTRMode(myOrderListResponse.isLastPage() ?
                            TFPTRRecyclerViewHelper.Mode.PULL_FORM_START : TFPTRRecyclerViewHelper.Mode.BOTH);
                }, throwable -> {
                    Log.d(TAG, "reqOrderListData: " + throwable.getMessage());
                });
        addSubscription(subscribe);
    }

    private void showNoDataView(boolean showNoData) {
        llNoData.setVisibility(showNoData ? View.VISIBLE : View.GONE);
        swLayout.setVisibility(showNoData ? View.GONE : View.VISIBLE);
    }


}
