package cn.timeface.circle.baby.ui.circle.timelines.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.constants.MiPushConstant;
import cn.timeface.circle.baby.support.api.ApiFactory;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.support.utils.ptr.IPTRRecyclerListener;
import cn.timeface.circle.baby.support.utils.ptr.TFPTRRecyclerViewHelper;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.circle.adapters.BaseEmptyAdapter;
import cn.timeface.circle.baby.ui.circle.timelines.adapter.TearcherOpproverAdapter;
import cn.timeface.circle.baby.ui.growthcircle.mainpage.event.CirclePassThroughMessageEvent;
import cn.timeface.circle.baby.ui.timelines.Utils.JSONUtils;
import cn.timeface.circle.baby.ui.timelines.Utils.LogUtil;
import cn.timeface.circle.baby.ui.timelines.adapters.BaseAdapter;
import cn.timeface.circle.baby.ui.timelines.adapters.EmptyItem;
import cn.timeface.circle.baby.views.TFStateView;

/**
 * author : wangshuai Created on 2017/3/27
 * email : wangs1992321@gmail.com
 */
public class TeacherAuthoActivity extends BaseAppCompatActivity implements BaseAdapter.LoadDataFinish {

    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.content_recycler_view)
    RecyclerView contentRecyclerView;
    @Bind(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefresh;

    private TearcherOpproverAdapter adapter;
    private TFPTRRecyclerViewHelper helper;
    private View footerView;

    public static void open(Context context) {
        context.startActivity(new Intent(context, TeacherAuthoActivity.class));
    }

    public static void open(Context context, long circleId) {
        if (circleId > 0 && FastData.getCircleId() <= 0)
            ApiFactory.getApi().getApiService().queryCircleIndexInfo(circleId)
                    .compose(SchedulersCompat.applyIoSchedulers())
                    .subscribe(circleIndexInfoResponse -> {
                        if (circleIndexInfoResponse.success()) {
                            FastData.setGrowthCircleObj(circleIndexInfoResponse.getGrowthCircle());
                            FastData.setCircleUserInfo(circleIndexInfoResponse.getUserInfo());
                            open(context);
                        }
                    }, throwable -> {
                    });
        else
            open(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tearcher_author);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        title.setText(R.string.activity_teacher_author_title);
        init();
    }

    private void init() {
        adapter = new TearcherOpproverAdapter(this);
        contentRecyclerView.setAdapter(adapter);
        adapter.setLoadDataFinish(this);
        adapter.setEmptyItem(new EmptyItem(BaseEmptyAdapter.EMPTY_CODE));
        adapter.getEmptyItem().setOperationType(1);
        adapter.notifyDataSetChanged();
        contentRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        helper = new TFPTRRecyclerViewHelper(this, contentRecyclerView, swipeRefresh);
        helper.setTFPTRMode(TFPTRRecyclerViewHelper.Mode.PULL_FORM_START)
                .tfPtrListener(new IPTRRecyclerListener() {
                    @Override
                    public void onTFPullDownToRefresh(View refreshView) {
                        reqData();
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
        reqData();
    }

    private void reqData() {
        addSubscription(apiService.checkTeacherList(FastData.getCircleId()).compose(SchedulersCompat.applyIoSchedulers())
                .doOnNext(teacherAuthObjQueryCirclePhotoResponse -> helper.finishTFPTRRefresh())
                .doOnNext(teacherAuthObjQueryCirclePhotoResponse -> adapter.getEmptyItem().setOperationType(2))
                .subscribe(teacherAuthObjQueryCirclePhotoResponse -> {
                    LogUtil.showLog("result:" + JSONUtils.parse2JSONString(teacherAuthObjQueryCirclePhotoResponse));
                    if (teacherAuthObjQueryCirclePhotoResponse.success()) {
                        adapter.addList(true, teacherAuthObjQueryCirclePhotoResponse.getDataList());
                    } else
                        ToastUtil.showToast(this, teacherAuthObjQueryCirclePhotoResponse.getInfo());
                    adapter.getEmptyItem().setOperationType(0);
                }, throwable -> {
                    LogUtil.showError(throwable);
                    adapter.getEmptyItem().setThrowable(throwable);
                    adapter.getEmptyItem().setOperationType(-1);
                    helper.finishTFPTRRefresh();
                }));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(CirclePassThroughMessageEvent event) {
        if (event.type == MiPushConstant.TYPE_CIRCLE_TEACHER_AUTHORIZATION) {
            reqData();
        }
    }

    @Override
    protected void onDestroy() {
        ButterKnife.unbind(this);
        super.onDestroy();
    }

    @Override
    public void loadFinish(int code) {
    }
}
