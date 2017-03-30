package cn.timeface.circle.baby.ui.growthcircle.mainpage.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.rayboot.widget.ratioview.RatioImageView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.support.managers.listeners.IEventBus;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.support.utils.network.NetworkError;
import cn.timeface.circle.baby.support.utils.ptr.IPTRRecyclerListener;
import cn.timeface.circle.baby.support.utils.ptr.TFPTRRecyclerViewHelper;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.circle.bean.CircleTimelineObj;
import cn.timeface.circle.baby.ui.circle.bean.GrowthCircleObj;
import cn.timeface.circle.baby.ui.circle.photo.activities.CirclePhotoActivity;
import cn.timeface.circle.baby.ui.circle.response.CircleIndexInfoResponse;
import cn.timeface.circle.baby.ui.circle.timelines.activity.HomwWorkListActivity;
import cn.timeface.circle.baby.ui.circle.timelines.activity.PublishActivity;
import cn.timeface.circle.baby.ui.circle.timelines.adapter.CircleTimeLineAdapter;
import cn.timeface.circle.baby.ui.circle.timelines.events.CircleMediaEvent;
import cn.timeface.circle.baby.ui.circle.timelines.events.CircleTimeLineEditEvent;
import cn.timeface.circle.baby.ui.growthcircle.mainpage.dialog.CircleMoreDialog;
import cn.timeface.circle.baby.ui.growthcircle.mainpage.event.CircleChangedEvent;
import cn.timeface.circle.baby.ui.images.views.DeleteDialog;
import cn.timeface.circle.baby.ui.timelines.Utils.LogUtil;
import cn.timeface.circle.baby.ui.timelines.adapters.BaseAdapter;
import cn.timeface.circle.baby.ui.timelines.adapters.EmptyItem;
import rx.Subscription;

/**
 * 圈首页
 */
public class CircleMainActivity extends BaseAppCompatActivity implements IEventBus, BaseAdapter.LoadDataFinish {

    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    @Bind(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    private int currentPage = 1;
    private static final int PAGE_SIZE = 20;

    private CircleTimeLineAdapter adapter = null;

    private TFPTRRecyclerViewHelper tfptrListViewHelper;


    private View footerView;
    private RatioImageView ivCircleCover;
    private TextView tvCircleName;
    private TextView tvHomework;
    private TextView tvHomeworkDetail;
    private RelativeLayout rlHomework;

    private long circleId;
    private GrowthCircleObj circleObj;

    public static void open(Context context) {
        context.startActivity(new Intent(context, CircleMainActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_main);
        ButterKnife.bind(this);

        setupPTR();
        initHeaderFooter();

        circleId = FastData.getCircleId();
        setupData();
    }

    private void setupPTR() {
        adapter = new CircleTimeLineAdapter(this);
        adapter.setEmptyItem(new EmptyItem(1002));
        adapter.setLoadDataFinish(this);
        recyclerView.setAdapter(adapter);

        IPTRRecyclerListener ptrListener = new IPTRRecyclerListener() {
            @Override
            public void onTFPullDownToRefresh(View refreshView) {
                currentPage = 1;
                setupData();
            }

            @Override
            public void onTFPullUpToRefresh(View refreshView) {
                ++currentPage;
                reqData(circleId);
            }

            @Override
            public void onScrollUp(int firstVisibleItem) {
            }

            @Override
            public void onScrollDown(int firstVisibleItem) {
            }
        };

        tfptrListViewHelper = new TFPTRRecyclerViewHelper(this, recyclerView, swipeRefreshLayout)
                .setTFPTRMode(TFPTRRecyclerViewHelper.Mode.BOTH)
                .tfPtrListener(ptrListener);
    }

    private void setupData() {
        adapter.getEmptyItem().setOperationType(1);
        adapter.notifyDataSetChanged();
//        reqInfo(circleId);
//        reqData(circleId);
        apiService.queryCircleIndexInfo(circleId)
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(response -> {
                    if (response.success()) {
                        FastData.setGrowthCircleObj(response.getGrowthCircle());
                        FastData.setCircleUserInfo(response.getUserInfo());
                        setupCircleInfo(response);
                        reqData(circleId);
                    } else {
                        Toast.makeText(this, response.info, Toast.LENGTH_SHORT).show();
                    }
                }, throwable -> {
                    NetworkError.showException(this, throwable);
                    LogUtil.showError(throwable);
                    ToastUtil.showToast(this, "未知错误，获取圈信息失败，请稍后重试！");
                    finish();
                });
    }

    private void initHeaderFooter() {
//        headerView = LayoutInflater.from(this).inflate(R.layout.header_circle_dynamic_list, null);
//        ivCircleCover = ButterKnife.findById(headerView, R.id.iv_circle_cover);
//        tvCircleName = ButterKnife.findById(headerView, R.id.tv_circle_name);
//        tvHomework = ButterKnife.findById(headerView, R.id.tv_homework);
//        tvHomeworkDetail = ButterKnife.findById(headerView, R.id.tv_homework_detail);
//        rlHomework = ButterKnife.findById(headerView, R.id.rl_homework);

//        footerView = LayoutInflater.from(this).inflate(R.layout.footer_circle_dynamic_list, null);
//        tfStateView = ButterKnife.findById(footerView, R.id.tf_stateView);

//        adapter.addHeader(headerView);
//        adapter.addFooter(footerView);
    }

    private void setupCircleInfo(CircleIndexInfoResponse circleIndexInfo) {
        this.circleObj = circleIndexInfo.getGrowthCircle();
        adapter.setHeaderInfo(circleIndexInfo);
        DeleteDialog deleteDialog=new DeleteDialog(this);
        deleteDialog.setMessage(String.format("大家已经帮你圈出了%d张有关%s的照片哦",circleIndexInfo.getRelateMediaCount(),FastData.getBabyObj().getRealName()));
        deleteDialog.getSubmit().setText("去看看");
        deleteDialog.setCancelTip("不看了");
//        CirclePhotoActivity.open();
//        deleteDialog.setSubmitListener(()->);
    }

    private void reqData(long circleId) {
        Subscription s = apiService.queryCircleIndex(circleId, currentPage, PAGE_SIZE)
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(
                        response -> {
                            adapter.getEmptyItem().setOperationType(0);
                            tfptrListViewHelper.finishTFPTRRefresh();
                            if (response.success()) {
                                setupListData(response.getDataList());
                            } else {
                                Toast.makeText(this, response.info, Toast.LENGTH_SHORT).show();
                            }
                        },
                        throwable -> {
                            adapter.getEmptyItem().setThrowable(throwable);
                            adapter.getEmptyItem().setOperationType(-1);
                            adapter.notifyDataSetChanged();
                            tfptrListViewHelper.finishTFPTRRefresh();
                            LogUtil.showError(throwable);
                        }
                );
        addSubscription(s);
    }


    private void setupListData(List<CircleTimelineObj> dataList) {
        if (currentPage <= 1)
            adapter.addList(true, dataList);
        else adapter.addList(dataList);
    }

    @OnClick({R.id.tv_back, R.id.iv_more, R.id.iv_publish})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                onBackPressed();
                break;
            case R.id.iv_more:
                showMoreDialog();
                break;
            case R.id.iv_publish:
                PublishActivity.open(this);
                break;
        }
    }

    private void showMoreDialog() {
        if (circleObj == null) return;
        CircleMoreDialog dialog = CircleMoreDialog.newInstance(circleObj);
        dialog.show(getSupportFragmentManager(), "CircleMoreDialog");
    }

    @Override
    public void loadFinish(int code) {
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onEvent(CircleMediaEvent event) {
        if (event.getType() == 0 && event.getMediaObj() != null) {
            for (int i = 0; i < adapter.getRealItemSize(); i++) {
                Object item = adapter.getData().get(i);
                if (item != null && item instanceof CircleTimelineObj) {
                    CircleTimelineObj timelineObj = (CircleTimelineObj) item;
                    if (timelineObj.getMediaList().contains(event.getMediaObj())) {
                        int index = timelineObj.getMediaList().indexOf(event.getMediaObj());
                        timelineObj.getMediaList().get(index).setTips(event.getMediaObj().getTips());
                        timelineObj.getMediaList().get(index).setIsFavorite(event.getMediaObj().getIsFavorite());
                        timelineObj.getMediaList().get(index).setFavoritecount(event.getMediaObj().getFavoritecount());
                        timelineObj.getMediaList().get(index).setRelateBabys(event.getMediaObj().getRelateBabys());
                    }
                }
            }
        }
    }

    @Subscribe
    public void onEvent(CircleTimeLineEditEvent event) {
        if (event.getTimelineObj() != null && event.getType() == 1) {
            adapter.deleteItem(event.getTimelineObj());
        } else if (event.getTimelineObj() != null && event.getType() == 0) {
            adapter.updateItem(event.getTimelineObj());
        } else if (event.getTimelineObj() != null && event.getType() == 2) {
//            currentPage = 1;
//            reqData(circleId);
            adapter.add(1, event.getTimelineObj());
        }
    }


    @Subscribe
    public void onEvent(CircleChangedEvent event) {
        if (event.type == CircleChangedEvent.TYPE_QUIT
                || event.type == CircleChangedEvent.TYPE_DISBANDED
                || event.type == CircleChangedEvent.TYPE_INFO_CHANGED) {
            FastData.clearCircleData();
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        FastData.clearCircleData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
