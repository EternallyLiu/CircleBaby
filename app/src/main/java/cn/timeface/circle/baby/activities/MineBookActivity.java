package cn.timeface.circle.baby.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import cn.timeface.circle.baby.utils.ptr.IPTRRecyclerListener;
import cn.timeface.circle.baby.utils.ptr.TFPTRRecyclerViewHelper;
import cn.timeface.circle.baby.views.DividerItemDecoration;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;

public class MineBookActivity extends BaseAppCompatActivity implements View.OnClickListener ,IEventBus{
    public static final int currentPage = 1;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.iv_add)
    ImageView ivAdd;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.content_recycler_view)
    RecyclerView recyclerView;
    @Bind(R.id.swLayout)
    SwipeRefreshLayout swLayout;
    @Bind(R.id.appbar_layout)
    AppBarLayout appbarLayout;
    @Bind(R.id.error_title)
    TextView errorTitle;
    @Bind(R.id.error_retry)
    TextView errorRetry;
    @Bind(R.id.ll_no_data)
    LinearLayout llNoData;
    private MineBookAdapter adapter;
    private TFPTRRecyclerViewHelper tfptrListViewHelper;
    List<MineBookObj> bookList = new ArrayList<>();
    private FragmentManager supportFragmentManager;

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
        supportFragmentManager = getSupportFragmentManager();
        setupView();
        reqData(currentPage);

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

        adapter = new MineBookAdapter(this, new ArrayList<>(), supportFragmentManager);
        recyclerView.setAdapter(adapter);

        tfptrListViewHelper = new TFPTRRecyclerViewHelper(this, recyclerView, swLayout)
                .setTFPTRMode(TFPTRRecyclerViewHelper.Mode.BOTH)
                .tfPtrListener(new IPTRRecyclerListener() {
                    @Override
                    public void onTFPullDownToRefresh(View refreshView) {
                        reqData(1);
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
        ivAdd.setOnClickListener(this);

    }

    /**
     * 请求网络获取我的作品列表
     */
    private void reqData(int currentPage) {
        setDataList(new ArrayList<>());
    }

    private void setDataList(List<MineBookObj> dataList) {
        List<MineBookObj> mineBookObjs = mockData(dataList);

        adapter.setListData(mineBookObjs);
        adapter.notifyDataSetChanged();
        showNoDataView(mineBookObjs.size() == 0);
        tfptrListViewHelper.finishTFPTRRefresh();
//        tfptrListViewHelper.setTFPTRMode(myOrderListResponse.isLastPage() ?
//                TFPTRRecyclerViewHelper.Mode.PULL_FORM_START : TFPTRRecyclerViewHelper.Mode.BOTH);

    }

    private void showNoDataView(boolean showNoData) {
        llNoData.setVisibility(showNoData ? View.VISIBLE : View.GONE);
        swLayout.setVisibility(showNoData ? View.GONE : View.VISIBLE);
    }


    private List<MineBookObj> mockData(List<MineBookObj> bookList) {
        MineBookObj mineBookObj1 = new MineBookObj("http://static.timeface.cn/coverpage/c7f9d26b0960f6e678fe17f25421d2bc.png", 1465193897000L, 50, "测试一本书", 1, "240796762256", "Melvin");
        MineBookObj mineBookObj2 = new MineBookObj("http://static.timeface.cn/coverpage/55b9e7b678ff7ae02b9a4ef2fcf9d7b4.png", 1465193897000L, 50, "Melvin的时光书", 1, "252251922680", "Melvin");
        MineBookObj mineBookObj3 = new MineBookObj("http://static.timeface.cn/coverpage/c17a1b4718d3e413576d4e4e18ca815f.png", 1465193897000L, 50, "测试一本书2222312", 1, "261970035390", "Melvin");
        bookList.add(mineBookObj1);
        bookList.add(mineBookObj2);
        bookList.add(mineBookObj3);
        return bookList;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_add:
                FragmentBridgeActivity.open(this, "AddBookListFragment");
                break;
        }
    }

    public void doDialogItemClick(View view) {
        EventBus.getDefault().post(new CartItemClickEvent(view));
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

    @Override
    public void onEvent(Object event) {

    }
}
