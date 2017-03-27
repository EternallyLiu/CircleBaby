package cn.timeface.circle.baby.ui.growthcircle.mainpage.fragment;


import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.TabMainActivity;
import cn.timeface.circle.baby.events.ConfirmRelationEvent;
import cn.timeface.circle.baby.fragments.base.BaseFragment;
import cn.timeface.circle.baby.support.managers.listeners.IEventBus;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.ptr.IPTRRecyclerListener;
import cn.timeface.circle.baby.support.utils.ptr.TFPTRRecyclerViewHelper;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.circle.bean.GrowthCircleListObj;
import cn.timeface.circle.baby.ui.growthcircle.mainpage.activity.CircleMainActivity;
import cn.timeface.circle.baby.ui.growthcircle.mainpage.activity.CreateCircleActivity;
import cn.timeface.circle.baby.ui.growthcircle.mainpage.adapter.CircleListAdapter;
import cn.timeface.circle.baby.ui.growthcircle.mainpage.dialog.CreateCircleDialog;
import cn.timeface.circle.baby.ui.growthcircle.mainpage.event.CircleChangedEvent;
import cn.timeface.circle.baby.views.TFStateView;
import rx.Subscription;

public class GrowthCircleListFragment extends BaseFragment implements IEventBus {

    @Bind(R.id.sv_no_data)
    SwipeRefreshLayout svNoData;
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    @Bind(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.tf_stateView)
    TFStateView tfStateView;

    private TFPTRRecyclerViewHelper tfptrListViewHelper;
    private boolean bottomMenuShow = true;

    private CircleListAdapter adapter;

    public static GrowthCircleListFragment newInstance() {
        return new GrowthCircleListFragment();
    }

    public GrowthCircleListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_growth_circle, container, false);
        ButterKnife.bind(this, view);

        setupPTR();

        svNoData.setOnRefreshListener(() -> {
            svNoData.setRefreshing(false);
            tfStateView.loading();
            reqData();
        });

        tfStateView.setOnRetryListener(() -> {
            tfStateView.loading();
            reqData();
        });

        tfStateView.loading();
        reqData();

        if (FastData.getCircleId() != 0) {
            CircleMainActivity.open(getContext());
        }

        return view;
    }

    private void setupPTR() {
        IPTRRecyclerListener ptrListener = new IPTRRecyclerListener() {
            @Override
            public void onTFPullDownToRefresh(View refreshView) {
                reqData();
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

    private void reqData() {
        Subscription s = apiService.circleList()
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(
                        response -> {
                            tfStateView.finish();
                            tfptrListViewHelper.finishTFPTRRefresh();
                            if (response.success()) {
                                if (response.getDataList() == null
                                        || response.getDataList().size() == 0) {
                                    showEmptyView(true);
                                } else {
                                    showEmptyView(false);
                                    setupListData(response.getDataList());
                                }
                            } else {
                                Toast.makeText(getContext(), response.info, Toast.LENGTH_SHORT).show();
                            }
                        },
                        throwable -> {
                            tfptrListViewHelper.finishTFPTRRefresh();
                            tfStateView.showException(throwable);
                        }
                );
        addSubscription(s);
    }

    private void setupListData(List<GrowthCircleListObj> dataList) {
        if (adapter == null) {
            adapter = new CircleListAdapter(getContext(), dataList);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
            recyclerView.addItemDecoration(
                    new HorizontalDividerItemDecoration.Builder(getContext())
                            .color(Color.TRANSPARENT)
                            .sizeResId(R.dimen.view_space_small)
                            .build()
            );
            recyclerView.getItemAnimator().setChangeDuration(0);//fix 列表闪烁
            recyclerView.setAdapter(adapter);
        } else {
            adapter.setListData(dataList);
            adapter.notifyDataSetChanged();
        }
    }

    private void showEmptyView(boolean show) {
        svNoData.setVisibility(show ? View.VISIBLE : View.GONE);
        swipeRefreshLayout.setVisibility(show ? View.GONE : View.VISIBLE);
        recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void showCreateDialog() {
        CreateCircleDialog dialog = CreateCircleDialog.newInstance();
        dialog.show(getFragmentManager(), "GrowthCircleOperation");
    }

    @Override
    public void onResume() {
        // 解决某些异常情况导致FootMenu消失
        if (getActivity() instanceof TabMainActivity) {
            ((TabMainActivity) getActivity()).showFootMenu();
        }
        super.onResume();
    }

    @OnClick(R.id.iv_add)
    public void onClickAdd() {
        showCreateDialog();
    }

    @OnClick(R.id.tv_create)
    public void onClickCreate() {
        CreateCircleActivity.open(getContext());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Subscribe
    public void onEvent(CircleChangedEvent event) {
        if (event.type == CircleChangedEvent.TYPE_CREATED
                || event.type == CircleChangedEvent.TYPE_JOINED
                || event.type == CircleChangedEvent.TYPE_QUIT
                || event.type == CircleChangedEvent.TYPE_DISBANDED
                || event.type == CircleChangedEvent.TYPE_INFO_CHANGED) {
            reqData();
        }
    }

    @Subscribe
    public void onEvent(ConfirmRelationEvent event) {
        // TODO: 2017/3/23 切换宝宝，这里接收的Event不一定对
        // 切换宝宝
        reqData();
    }

}
