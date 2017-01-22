package cn.timeface.circle.baby.ui.timelines.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.fragments.base.BaseFragment;
import cn.timeface.circle.baby.support.api.models.objs.TimeLineGroupObj;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.timelines.Utils.LogUtil;
import cn.timeface.circle.baby.ui.timelines.adapters.TimeLineDayAdapter;
import cn.timeface.circle.baby.ui.timelines.beans.MonthRecord;
import cn.timeface.circle.baby.ui.timelines.views.MySuperRefreshLayout;

/**
 * Created by wangshuai on 2017/1/10.
 */

public class TimeLineDayFragment extends BaseFragment implements MySuperRefreshLayout.loadListener {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.content_recycler_view)
    RecyclerView contentRecyclerView;
    @Bind(R.id.swipe_refresh_layout)
    MySuperRefreshLayout swipeRefreshLayout;
    @Bind(R.id.title)
    TextView title;
    private TimeLineDayAdapter adapter;

    private int year, month;
    private int currentPage = 1;

    public static TimeLineDayFragment newInstance(MonthRecord monthRecord) {
        TimeLineDayFragment fragment = new TimeLineDayFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("data", monthRecord);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static TimeLineDayFragment newInstance() {
        TimeLineDayFragment fragment = new TimeLineDayFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        EventBus.getDefault().register(this);
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("data"))
            mMonthRecord = bundle.getParcelable("data");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ftagment_time_line_day, container, false);
        ButterKnife.bind(this, view);
        setActionBar(toolbar);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }
        title.setText(FastData.getBabyObj().getNickName()+"的成长记录");
        adapter = new TimeLineDayAdapter(getActivity());
        contentRecyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        contentRecyclerView.setLayoutManager(layoutManager);
        contentRecyclerView.setItemAnimator(new DefaultItemAnimator());
        reqData(currentPage);
//        if (mMonthRecord != null)
//            tvCount.setText(mMonthRecord.getRecordcount() + "条");
//        else tvCount.setVisibility(View.GONE);
        swipeRefreshLayout.setPullRefreshEnable(true);
        swipeRefreshLayout.setLoadMoreEnable(true);
        swipeRefreshLayout.setListener(this);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (mMonthRecord != null){
            inflater.inflate(R.menu.menu_complete, menu);
            menu.findItem(R.id.complete).setTitle(mMonthRecord.getRecordcount() + "条");
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    private MonthRecord mMonthRecord = null;

    private void reqData(int page) {
        apiService.timeline(mMonthRecord == null ? 0 : mMonthRecord.getMoth(), mMonthRecord == null ? 0 : mMonthRecord.getYear(), page, 30)
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(timelineResponse -> {
                    if (timelineResponse.success()) {
                        setDataList(timelineResponse.getDataList());
                        currentPage++;
                        swipeRefreshLayout.setRefreshing(false);
                        swipeRefreshLayout.setLoadMore(false);
                    }
                }, error -> {
                    swipeRefreshLayout.setRefreshing(false);
                    swipeRefreshLayout.setLoadMore(false);
                });
    }

    private void setDataList(List<TimeLineGroupObj> dataList) {
        LogUtil.showLog(dataList.size() + "====" + currentPage);
        ArrayList<TimeLineGroupObj> lists = new ArrayList<>();
        for (TimeLineGroupObj obj : dataList) {
            if (obj.getTimeLineList().size() > 0) {
                lists.add(obj);
            }
        }
        if (currentPage == 1) {
            adapter.addList(true, lists);
        } else {
            adapter.addList(lists);
        }
    }

    @Subscribe
    public void onEvent(MonthRecord event) {
        mMonthRecord = event;
        Log.i("test", mMonthRecord.toString());
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                currentPage = 1;
                reqData(currentPage);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void pullRefresh() {
        currentPage = 1;
        reqData(currentPage);
    }

    @Override
    public void loadMore() {
        reqData(currentPage);
    }
}
