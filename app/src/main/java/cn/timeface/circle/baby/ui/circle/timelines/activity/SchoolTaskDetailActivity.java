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
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.support.utils.ptr.IPTRRecyclerListener;
import cn.timeface.circle.baby.support.utils.ptr.TFPTRRecyclerViewHelper;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.circle.activities.CircleSelectServeHomeWorksActivity;
import cn.timeface.circle.baby.ui.circle.bean.CircleHomeworkObj;
import cn.timeface.circle.baby.ui.circle.bean.CircleSchoolTaskDetailObj;
import cn.timeface.circle.baby.ui.circle.bean.CircleSchoolTaskObj;
import cn.timeface.circle.baby.ui.circle.timelines.adapter.SchoolTaskDetailAdapter;
import cn.timeface.circle.baby.ui.timelines.adapters.BaseAdapter;

/**
 * author : wangshuai Created on 2017/3/25
 * email : wangs1992321@gmail.com
 */
public class SchoolTaskDetailActivity extends BaseAppCompatActivity implements BaseAdapter.OnItemClickLister {

    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.content_recycler_view)
    RecyclerView contentRecyclerView;
    @Bind(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefresh;
    @Bind(R.id.iv_submit_task)
    ImageView ivSubmitTask;

    private SchoolTaskDetailAdapter adapter = null;
    private TFPTRRecyclerViewHelper helper;
    private int currentPage = 1;

    private static final int PAGE_SIZE = 20;

    private CircleSchoolTaskObj currentTaskObj = null;

    public static void open(Context context, CircleSchoolTaskObj task) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(CircleSchoolTaskObj.class.getSimpleName(), task);
        context.startActivity(new Intent(context, SchoolTaskDetailActivity.class).putExtras(bundle));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schooltask_detail);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        title.setText(R.string.activity_look_homework);
        init();
        reqData();
    }

    private void init() {
        currentTaskObj = getIntent().getParcelableExtra(CircleSchoolTaskObj.class.getSimpleName());
        ivSubmitTask.setVisibility(currentTaskObj.getIsCommit() == 0 ? View.VISIBLE : View.GONE);
        adapter = new SchoolTaskDetailAdapter(this);
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

    private void setDataList(CircleSchoolTaskDetailObj taskObj) {
        if (currentPage == 1) {
            adapter.addList(true, taskObj.getHomeworkList(), 0, currentTaskObj);
        } else {
            adapter.addList(taskObj.getHomeworkList());
        }
        if (currentTaskObj.getIsCommit() != taskObj.getIsCommit()) {
            currentTaskObj.setIsCommit(taskObj.getIsCommit());
            ivSubmitTask.setVisibility(currentTaskObj.getIsCommit() != 0 ? View.VISIBLE : View.GONE);
            adapter.updateItem(currentTaskObj);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_complete, menu);
        menu.findItem(R.id.complete).setTitle("制作家校成长册");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.complete) {
            CircleSelectServeHomeWorksActivity.open(this, String.valueOf(FastData.getCircleId()));
        }
        return super.onOptionsItemSelected(item);
    }

    private void reqData() {
        addSubscription(apiService.teacherHomeworkDetal(currentTaskObj.getTaskId(), currentPage, PAGE_SIZE)
                .compose(SchedulersCompat.applyIoSchedulers())
                .doOnNext(homeWorkDetailResponse -> helper.finishTFPTRRefresh())
                .subscribe(homeWorkDetailResponse -> {
                    if (homeWorkDetailResponse.success()) {
                        setDataList(homeWorkDetailResponse.getSchoolTaskDetailObj());
                    } else ToastUtil.showToast(this, homeWorkDetailResponse.getInfo());
                }, throwable -> {
                    helper.finishTFPTRRefresh();
                    if (currentPage == 1) {
                        adapter.addList(true, new ArrayList(), 0, currentTaskObj);
                    }
                }));
    }

    @Override
    protected void onDestroy() {
        ButterKnife.unbind(this);
        super.onDestroy();
    }

    @Override
    public void onItemClick(View view, int position) {
        if (position > 0) {
            HomeWorkActivity.open(this, adapter.getItem(position));
        }
    }

    @OnClick(R.id.iv_submit_task)
    public void onViewClicked() {
        PublishActivity.open(this, new CircleHomeworkObj(currentTaskObj.getTaskId(), currentTaskObj.getTitle()));
    }
}
