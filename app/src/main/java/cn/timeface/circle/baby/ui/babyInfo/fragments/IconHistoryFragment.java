package cn.timeface.circle.baby.ui.babyInfo.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.fragments.base.BaseFragment;
import cn.timeface.circle.baby.ui.babyInfo.adapters.IconHisAdapter;
import cn.timeface.circle.baby.ui.timelines.Utils.LogUtil;
import cn.timeface.circle.baby.ui.timelines.views.EmptyDataView;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by wangshuai on 2017/1/12.
 */

public class IconHistoryFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.list)
    RecyclerView list;
    @Bind(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefresh;
    @Bind(R.id.empty)
    EmptyDataView empty;

    private IconHisAdapter adapter = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.time_line_fragment, container, false);
        ButterKnife.bind(this, view);
        setActionBar(toolbar);
        initActionBar();
        adapter = new IconHisAdapter(getActivity());
        GridLayoutManager manager = new GridLayoutManager(getActivity(), 3, GridLayoutManager.VERTICAL, false);
        list.setLayoutManager(manager);
        list.setItemAnimator(new DefaultItemAnimator());
        list.setAdapter(adapter);
        reqData();
        title.setText(R.string.baby_icon_his_title);
        swipeRefresh.setOnRefreshListener(this);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private void reqData() {
        Subscription ss = apiService.getIconHistory().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    swipeRefresh.setRefreshing(false);
                    if (response.success()) {
                        LogUtil.showLog(response.getDataList().size() + "");
                        adapter.addList(true, response.getDataList());
                    }
                }, error -> {
                    swipeRefresh.setRefreshing(false);
                    LogUtil.showLog("error:" + error.getMessage());
                });
        addSubscription(ss);
    }

    @Override
    public void onRefresh() {
        reqData();
    }
}
