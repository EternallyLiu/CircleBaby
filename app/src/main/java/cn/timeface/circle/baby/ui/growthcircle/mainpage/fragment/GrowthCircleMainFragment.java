package cn.timeface.circle.baby.ui.growthcircle.mainpage.fragment;


import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.rayboot.widget.ratioview.RatioImageView;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.TabMainActivity;
import cn.timeface.circle.baby.events.UnreadMsgEvent;
import cn.timeface.circle.baby.fragments.base.BaseFragment;
import cn.timeface.circle.baby.support.managers.listeners.IEventBus;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.ptr.IPTRRecyclerListener;
import cn.timeface.circle.baby.support.utils.ptr.TFPTRRecyclerViewHelper;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.circle.bean.CircleTimelineObj;
import cn.timeface.circle.baby.ui.circle.bean.GrowthCircleObj;
import cn.timeface.circle.baby.ui.circle.timelines.adapter.CircleTimeLineAdapter;
import cn.timeface.circle.baby.ui.growthcircle.mainpage.dialog.CircleMoreDialog;
import cn.timeface.circle.baby.ui.timelines.Utils.JSONUtils;
import cn.timeface.circle.baby.ui.timelines.Utils.LogUtil;
import cn.timeface.circle.baby.views.TFStateView;
import rx.Subscription;

public class GrowthCircleMainFragment extends BaseFragment implements IEventBus {

    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    @Bind(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.iv_publish)
    ImageView ivPublish;

    private int currentPage = 1;
    private static final int PAGE_SIZE = 20;

    private CircleTimeLineAdapter adapter = null;

    private TFPTRRecyclerViewHelper tfptrListViewHelper;
    private boolean bottomMenuShow = true;

    private View headerView;
    private TFStateView tfStateView;

    private View footerView;
    private RatioImageView ivCircleCover;
    private TextView tvCircleName;
    private TextView tvHomework;
    private TextView tvHomeworkDetail;

    private long circleId;
    private GrowthCircleObj circleObj;

    public static GrowthCircleMainFragment newInstance() {
        return new GrowthCircleMainFragment();
    }

    public GrowthCircleMainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_circle_main_page, container, false);
        ButterKnife.bind(this, view);
        setupPTR();
        initHeaderFooter();

        circleId = FastData.getCircleId();
        setupData();

        return view;
    }

    private void setupPTR() {
        adapter = new CircleTimeLineAdapter(getActivity());
        recyclerView.setAdapter(adapter);
        IPTRRecyclerListener ptrListener = new IPTRRecyclerListener() {
            @Override
            public void onTFPullDownToRefresh(View refreshView) {
                reqData(circleId);
            }

            @Override
            public void onTFPullUpToRefresh(View refreshView) {
            }

            @Override
            public void onScrollUp(int firstVisibleItem) {
                if (bottomMenuShow) {
                    bottomMenuShow = false;
                    ObjectAnimator anim = ObjectAnimator.ofFloat(
                            ((TabMainActivity) getActivity()).getFootMenuView(),
                            "translationY",
                            0,
                            ((TabMainActivity) getActivity()).getFootMenuView().getMeasuredHeight()
                    );
                    anim.setDuration(200);
                    anim.start();
                }
            }

            @Override
            public void onScrollDown(int firstVisibleItem) {
                if (!bottomMenuShow) {
                    bottomMenuShow = true;
                    Animator anim3 = ObjectAnimator.ofFloat(
                            ((TabMainActivity) getActivity()).getFootMenuView(),
                            "translationY",
                            ((TabMainActivity) getActivity()).getFootMenuView().getMeasuredHeight(),
                            0
                    );
                    anim3.setDuration(200);
                    anim3.start();
                }
            }
        };

        tfptrListViewHelper = new TFPTRRecyclerViewHelper(getActivity(), recyclerView, swipeRefreshLayout)
                .setTFPTRMode(TFPTRRecyclerViewHelper.Mode.PULL_FORM_START)
                .tfPtrListener(ptrListener);
    }

    private void setupData() {
        tfStateView.loading();
        reqInfo(circleId);
        reqData(circleId);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.d("-------->", "-------->onHiddenChanged: " + hidden);
        if (!hidden && circleId != FastData.getCircleId()) {
            circleId = FastData.getCircleId();
            setupData();
        }
    }

    private void initHeaderFooter() {
        headerView = LayoutInflater.from(getContext()).inflate(R.layout.header_circle_dynamic_list, null);
        ivCircleCover = ButterKnife.findById(headerView, R.id.iv_circle_cover);
        tvCircleName = ButterKnife.findById(headerView, R.id.tv_circle_name);
        tvHomework = ButterKnife.findById(headerView, R.id.tv_homework);
        tvHomeworkDetail = ButterKnife.findById(headerView, R.id.tv_homework_detail);

        footerView = LayoutInflater.from(getContext()).inflate(R.layout.footer_circle_dynamic_list, null);
        tfStateView = ButterKnife.findById(footerView, R.id.tf_stateView);
        adapter.addHeader(headerView);
    }

    private void setupCircleInfo(GrowthCircleObj circleObj) {
        this.circleObj = circleObj;
        tvCircleName.setText(circleObj.getCircleName());
        Glide.with(getContext())
                .load(circleObj.getCircleCoverUrl())
                .centerCrop()
                .into(ivCircleCover);
    }

    private void reqData(long circleId) {
        Subscription s = apiService.queryCircleIndex(circleId)
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(
                        response -> {
                            tfStateView.finish();
                            tfptrListViewHelper.finishTFPTRRefresh();
                            if (response.success()) {
                                setupListData(response.getDataList());

                                if (response.getDataList() == null
                                        || response.getDataList().size() == 0) {
                                    tfStateView.empty(R.string.circle_no_dynamic);
                                }
                            } else {
                                Toast.makeText(getContext(), response.info, Toast.LENGTH_SHORT).show();
                            }
                            adapter.removeFooter(tfStateView);
                        },
                        throwable -> {
                            tfptrListViewHelper.finishTFPTRRefresh();
                            tfStateView.showException(throwable);
                            adapter.addFooter(tfStateView);
                            LogUtil.showError(throwable);
                        }
                );
        addSubscription(s);
    }

    private void reqInfo(long circleId) {
        Subscription s = apiService.queryCircleIndexInfo(circleId)
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(
                        response -> {
                            tfStateView.finish();
                            tfptrListViewHelper.finishTFPTRRefresh();
                            if (response.success()) {
                                FastData.setGrowthCircleObj(response.getGrowthCircle());
                                FastData.setCircleUserInfo(response.getUserInfo());

                                setupCircleInfo(response.getGrowthCircle());
                            } else {
                                Toast.makeText(getContext(), response.info, Toast.LENGTH_SHORT).show();
                            }
                            adapter.removeFooter(tfStateView);
                        },
                        throwable -> {
                            tfptrListViewHelper.finishTFPTRRefresh();
                            tfStateView.showException(throwable);
                            adapter.addFooter(tfStateView);
                            LogUtil.showError(throwable);
                        }
                );
        addSubscription(s);
    }

    private void setupListData(List<CircleTimelineObj> dataList) {
        LogUtil.showLog("size===" + dataList.size());
        LogUtil.showLog("circle_timeLine", JSONUtils.parse2JSONString(dataList));
        if (currentPage <= 1)
            adapter.addList(true, dataList);
        else adapter.addList(dataList);
    }

    @OnClick({R.id.tv_back, R.id.iv_more, R.id.iv_publish})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
                if (getActivity() instanceof TabMainActivity) {
                    // 切换为圈列表
//                    GrowthCircleObj.getInstance().delete();
//                    GrowthCircleObj.refreshInstance();
//                    FastData.setCircleId(0);
                    ((TabMainActivity) getActivity()).showCircleListFragment();
                } else {
                    getActivity().onBackPressed();
                }
                break;
            case R.id.iv_more:
                showMoreDialog();
                break;
            case R.id.iv_publish:

                break;
        }
    }

    private void showMoreDialog() {
        if (circleObj == null) return;
        CircleMoreDialog dialog = CircleMoreDialog.newInstance(circleObj);
        dialog.show(getChildFragmentManager(), "CircleMoreDialog");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Subscribe
    public void onEvent(UnreadMsgEvent event) {

    }

}
