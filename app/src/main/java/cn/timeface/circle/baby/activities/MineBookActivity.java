package cn.timeface.circle.baby.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.timeface.refreshload.PullRefreshLoadRecyclerView;
import com.timeface.refreshload.headfoot.LoadMoreView;
import com.timeface.refreshload.headfoot.RefreshView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.adapters.MineBookAdapter;
import cn.timeface.circle.baby.api.models.objs.MineBookObj;
import cn.timeface.circle.baby.dialogs.CartPrintPropertyDialog;
import cn.timeface.circle.baby.events.CartBuyNowEvent;
import cn.timeface.circle.baby.events.CartItemClickEvent;
import cn.timeface.circle.baby.managers.listeners.IEventBus;
import cn.timeface.circle.baby.views.DividerItemDecoration;
import cn.timeface.circle.baby.views.TFStateView;

public class MineBookActivity extends BaseAppCompatActivity implements IEventBus {
    public int currentPage = 1;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.rlRecyclerView)
    PullRefreshLoadRecyclerView rlRecyclerView;
    @Bind(R.id.appbar_layout)
    AppBarLayout appbarLayout;
    @Bind(R.id.error_title)
    TextView errorTitle;
    @Bind(R.id.error_retry)
    TextView errorRetry;
    @Bind(R.id.ll_no_data)
    LinearLayout llNoData;
    @Bind(R.id.tf_stateView)
    TFStateView tfStateView;
    private MineBookAdapter adapter;
    ArrayList<MineBookObj> bookList = new ArrayList<>();

    public static void open(Context context) {
        context.startActivity(new Intent(context, MineBookActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_minebook);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("我的作品");
        setupView();
        reqData(currentPage, true);

    }

    /**
     * 初始化View状态
     */
    private void setupView() {
        RecyclerView recyclerView = rlRecyclerView.getRecyclerView();
        DividerItemDecoration itemDecoration =
                new DividerItemDecoration(this, LinearLayoutManager.VERTICAL, R.color.bg7);
        itemDecoration.setItemSize(getResources().getDimensionPixelOffset(R.dimen.view_space_normal));
        recyclerView.addItemDecoration(itemDecoration);
        adapter = new MineBookAdapter(this, bookList, getSupportFragmentManager());
        rlRecyclerView.setAdapter(adapter);
        rlRecyclerView.setLoadRefreshListener(new PullRefreshLoadRecyclerView.LoadRefreshListener() {
            @Override
            public void onRefresh(PullRefreshLoadRecyclerView pullRefreshLoadRecyclerView, RefreshView refreshView) {
                reqData(currentPage, true);
            }

            @Override
            public void onLoadMore(PullRefreshLoadRecyclerView pullRefreshLoadRecyclerView, LoadMoreView loadMoreView) {
                reqData(currentPage, false);
            }
        });
        tfStateView.setOnRetryListener(() -> reqData(1, true));
        tfStateView.loading();
    }

    /**
     * 请求网络获取我的作品列表
     */
    private void reqData(int page, boolean refresh) {

        if (refresh) {
            bookList.clear();
        }
        mockData();
        adapter.notifyDataSetChanged();
        tfStateView.finish();
        rlRecyclerView.complete();
      /*  Subscription subscribe = apiService.getBabyBookList(PullRefreshLoadRecyclerView.REQ_PAGE, page)
                .compose(SchedulersCompat.applyIoSchedulers())
                .doOnTerminate(() -> rlRecyclerView.complete())
                .subscribe(mineBookListResponse -> {
                    tfStateView.finish();
                    if (mineBookListResponse.success()) {
                        currentPage += 1;
                        if (refresh) {
                            bookList.clear();
                        }
                         bookList.addAll(mineBookListResponse.getDataList());
                        adapter.notifyDataSetChanged();
                    } else {
                        ToastUtil.showToast(mineBookListResponse.getInfo());
                    }
                }, throwable -> {
                    tfStateView.showException(throwable);
                    Log.d(TAG, "reqData: " + throwable.getMessage());
                });
        addSubscription(subscribe);*/
    }


    private List<MineBookObj> mockData() {
        MineBookObj mineBookObj1;
        for (int i = 0; i < 5; i++) {
            mineBookObj1 = new MineBookObj("http://static.timeface.cn/coverpage/c7f9d26b0960f6e678fe17f25421d2bc.png", 1465193897000L, 50, "测试一本书", 1, "240796762256", "Melvin");
            bookList.add(mineBookObj1);
        }
        return bookList;
    }


    public void doDialogItemClick(View view) {
        EventBus.getDefault().post(new CartItemClickEvent(view));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_mine_book, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add) {
            FragmentBridgeActivity.open(this, "AddBookListFragment");
        }
        return super.onOptionsItemSelected(item);
    }

    @Subscribe
    public void onEvent(CartBuyNowEvent event) {
        if (event != null &&
                event.requestCode == CartPrintPropertyDialog.REQUEST_CODE_MINETIME) {
            if (event.response.success()) {
                MyOrderConfirmActivity.open(this, event.response.getOrderId());
            } else {
                Toast.makeText(this, event.response.getInfo(), Toast.LENGTH_SHORT).show();
            }
        }
    }

}
