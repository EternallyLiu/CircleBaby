package cn.timeface.circle.baby.ui.circle.timelines.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.support.managers.listeners.IEventBus;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.support.utils.ptr.IPTRRecyclerListener;
import cn.timeface.circle.baby.support.utils.ptr.TFPTRRecyclerViewHelper;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.circle.bean.CircleHomeworkObj;
import cn.timeface.circle.baby.ui.circle.bean.HomeWorkListObj;
import cn.timeface.circle.baby.ui.circle.timelines.adapter.SchoolTaskAdapter;
import cn.timeface.circle.baby.ui.timelines.Utils.LogUtil;
import cn.timeface.circle.baby.ui.timelines.adapters.BaseAdapter;

/**
 * author : wangshuai Created on 2017/3/23
 * email : wangs1992321@gmail.com
 */
public class HomwWorkListActivity extends BaseAppCompatActivity implements BaseAdapter.OnItemClickLister {

    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.content_recycler_view)
    RecyclerView contentRecyclerView;
    @Bind(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefresh;

    private SchoolTaskAdapter adapter = null;
    private TFPTRRecyclerViewHelper helper;

    private int currentPage = 1;
    private static final int PAGE_SIZE = 20;

    public static void open(Context context) {
        context.startActivity(new Intent(context, HomwWorkListActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homework_list);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        title.setText(R.string.activity_homework_title);
        init();
        reqData();
    }

    private void init() {
        adapter = new SchoolTaskAdapter(this);
        adapter.setItemClickLister(this);
        contentRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        helper = new TFPTRRecyclerViewHelper(this, contentRecyclerView, swipeRefresh);
        helper.setTFPTRMode(TFPTRRecyclerViewHelper.Mode.PULL_FORM_START)
                .tfPtrListener(new IPTRRecyclerListener() {
                    @Override
                    public void onTFPullDownToRefresh(View refreshView) {
                        currentPage = 1;
                        reqData();
                    }

                    @Override
                    public void onTFPullUpToRefresh(View refreshView) {
                        currentPage++;
                        reqData();
                    }

                    @Override
                    public void onScrollUp(int firstVisibleItem) {
                    }

                    @Override
                    public void onScrollDown(int firstVisibleItem) {
                    }
                });
        contentRecyclerView.setAdapter(adapter);
    }

    private void reqData() {
        addSubscription(apiService.homeWorkList(FastData.getCircleId(), currentPage, PAGE_SIZE)
                .compose(SchedulersCompat.applyIoSchedulers())
                .doOnNext(homeWorkListResponse -> helper.finishTFPTRRefresh())
                .subscribe(homeWorkListResponse -> {
                    if (homeWorkListResponse.success()) {
                        if (currentPage == 1) {
                            adapter.addList(true, homeWorkListResponse.getDataList());
                        } else adapter.addList(homeWorkListResponse.getDataList());
                    } else ToastUtil.showToast(this, homeWorkListResponse.getInfo());
                }, throwable -> {
                    helper.finishTFPTRRefresh();
                    LogUtil.showError(throwable);
                }));
    }

    @Override
    protected void onDestroy() {
        ButterKnife.unbind(this);
        super.onDestroy();
    }

    @Override
    public void onItemClick(View view, int position) {
        HomeWorkListObj item = adapter.getItem(position);
        if (item != null) {
            PublishActivity.open(this, new CircleHomeworkObj(item.getSchoolTask().getTaskId(), item.getSchoolTask().getTitle()));
        }
    }
}
