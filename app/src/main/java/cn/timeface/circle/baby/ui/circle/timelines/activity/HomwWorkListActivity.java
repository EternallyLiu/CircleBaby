package cn.timeface.circle.baby.ui.circle.timelines.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.timeface.circle.baby.App;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.constants.MiPushConstant;
import cn.timeface.circle.baby.support.managers.listeners.IEventBus;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.support.utils.ptr.IPTRRecyclerListener;
import cn.timeface.circle.baby.support.utils.ptr.TFPTRRecyclerViewHelper;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.circle.bean.HomeWorkListObj;
import cn.timeface.circle.baby.ui.circle.timelines.adapter.SchoolTaskAdapter;
import cn.timeface.circle.baby.ui.circle.timelines.bean.CircleHomeWorkHeader;
import cn.timeface.circle.baby.ui.circle.timelines.events.HomeWorkListEvent;
import cn.timeface.circle.baby.ui.circle.timelines.events.SchoolTaskEvent;
import cn.timeface.circle.baby.ui.growthcircle.mainpage.event.CirclePassThroughMessageEvent;
import cn.timeface.circle.baby.ui.timelines.Utils.LogUtil;
import cn.timeface.circle.baby.ui.timelines.adapters.BaseAdapter;
import cn.timeface.circle.baby.ui.timelines.adapters.EmptyItem;
import rx.subjects.PublishSubject;

/**
 * author : wangshuai Created on 2017/3/23
 * email : wangs1992321@gmail.com
 */
public class HomwWorkListActivity extends BaseAppCompatActivity implements IEventBus, BaseAdapter.OnItemClickLister {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.content_recycler_view)
    RecyclerView contentRecyclerView;
    @Bind(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefresh;
    @Bind(R.id.iv_dot)
    ImageView ivDot;


    private SchoolTaskAdapter adapter = null;
    private TFPTRRecyclerViewHelper helper;

    private int currentPage = 1;
    private static final int PAGE_SIZE = 20;
    private CircleHomeWorkHeader homeWorkHeader;
    private long clickToolBarLastTime = 0;

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
        toolbar.setTitle(getString(R.string.circle_home_work));
        init();
        reqData();
    }

    private void init() {
        toolbar.setOnClickListener(v -> {
            if (System.currentTimeMillis() - clickToolBarLastTime <= 500 && adapter != null && adapter.getRealItemSize() > 0 && contentRecyclerView != null)
                contentRecyclerView.scrollToPosition(0);
            clickToolBarLastTime = System.currentTimeMillis();
        });
        adapter = new SchoolTaskAdapter(this);
        adapter.setItemClickLister(this);
        adapter.setEmptyItem(new EmptyItem(1004));
        adapter.getEmptyItem().setOperationType(1);
        adapter.notifyDataSetChanged();
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
                .doOnNext(homeWorkListResponse -> adapter.getEmptyItem().setOperationType(2))
                .subscribe(homeWorkListResponse -> {
                    adapter.getEmptyItem().setOperationType(0);
                    if (homeWorkListResponse.success()) {
                        if (homeWorkHeader == null)
                            homeWorkHeader = new CircleHomeWorkHeader(homeWorkListResponse.getGrowthCircle(), homeWorkListResponse.getHasTeacherCertification(), homeWorkListResponse.getLastSubmitHomework());
                        else {
                            homeWorkHeader.setGrowthCircle(homeWorkListResponse.getGrowthCircle());
                            homeWorkHeader.setHasTeacherCertification(homeWorkListResponse.getHasTeacherCertification());
                            homeWorkHeader.setLastSubmitHomework(homeWorkListResponse.getLastSubmitHomework());
                        }
                        ivDot.setVisibility(homeWorkHeader.getHasTeacherCertification() == 1 ? View.VISIBLE : View.GONE);
                        if (currentPage == 1) {
                            adapter.addList(true, homeWorkListResponse.getDataList(), 0, homeWorkHeader);
                        } else {
                            adapter.updateItem(homeWorkHeader);
                            adapter.addList(homeWorkListResponse.getDataList());
                        }
                    } else ToastUtil.showToast(this, homeWorkListResponse.getInfo());
                    adapter.setHashSubmit(homeWorkHeader.getLastSubmitHomework() != null && homeWorkHeader.getLastSubmitHomework().getSubmitter() != null);
                }, throwable -> {
                    adapter.getEmptyItem().setThrowable(throwable);
                    adapter.getEmptyItem().setOperationType(-1);
                    adapter.notifyDataSetChanged();
                    helper.finishTFPTRRefresh();
                    LogUtil.showError(throwable);
                }));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(CirclePassThroughMessageEvent event) {
        if (event.type == MiPushConstant.TYPE_CIRCLE_COMMIT_HOMEWORK || event.type == MiPushConstant.TYPE_CIRCLE_SCHOOL_TASK) {
            currentPage = 1;
            reqData();
        }else if (event.type==MiPushConstant.TYPE_CIRCLE_TEACHER_AUTHORIZATION){
            ivDot.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        ButterKnife.unbind(this);
        super.onDestroy();
    }

    @Subscribe
    public void onEvent(SchoolTaskEvent event) {
        currentPage = 1;
        reqData();
    }


    @Override
    public void onItemClick(View view, int position) {
        if (position < 1) return;
        HomeWorkListObj item = adapter.getItem(position);
        if (item != null) {
            SchoolTaskDetailActivity.open(this, item.getSchoolTask().getTaskId());
//            PublishActivity.open(this, new CircleHomeworkObj(item.getSchoolTask().getTaskId(), item.getSchoolTask().getTitle()));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home_work_list,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.teacher_confirm:
                TeacherAuthoActivity.open(this);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
