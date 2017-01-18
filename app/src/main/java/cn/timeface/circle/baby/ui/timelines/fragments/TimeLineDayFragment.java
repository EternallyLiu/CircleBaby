package cn.timeface.circle.baby.ui.timelines.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import cn.timeface.circle.baby.support.utils.Remember;
import cn.timeface.circle.baby.support.utils.ptr.TFPTRRecyclerViewHelper;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.timelines.adapters.TimeLineDayAdapter;
import cn.timeface.circle.baby.ui.timelines.beans.MonthRecord;
import cn.timeface.circle.baby.ui.timelines.views.SuperSwipeRefreshLayout;

/**
 * Created by wangshuai on 2017/1/10.
 */

public class TimeLineDayFragment extends BaseFragment {

    @Bind(R.id.back)
    ImageView back;
    @Bind(R.id.tv_mine)
    TextView tvMine;
    @Bind(R.id.tv_count)
    TextView tvCount;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.content_recycler_view)
    RecyclerView contentRecyclerView;
    @Bind(R.id.swipe_refresh_layout)
    SuperSwipeRefreshLayout swipeRefreshLayout;
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
        adapter = new TimeLineDayAdapter(getActivity());
        contentRecyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        contentRecyclerView.setLayoutManager(layoutManager);
        contentRecyclerView.setItemAnimator(new DefaultItemAnimator());
        reqData(currentPage);
        if (mMonthRecord!=null)
            tvCount.setText(mMonthRecord.getRecordcount()+"条");
        else tvCount.setVisibility(View.GONE);
        return view;
    }

    @OnClick(R.id.back)
    public void click(View view) {
        switch (view.getId()) {
            case R.id.back:
                getActivity().finish();
                break;
        }
    }

    private MonthRecord mMonthRecord = null;

    private void reqData(int page) {
        apiService.timeline(mMonthRecord == null ? 0 : mMonthRecord.getMoth(), mMonthRecord == null ? 0 : mMonthRecord.getYear(), page, 30)
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(timelineResponse -> {
                    if (timelineResponse.success()) {
                        currentPage++;
                        setDataList(timelineResponse.getDataList());
                    }
                }, error -> {
                });
    }

    private void setDataList(List<TimeLineGroupObj> dataList) {
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
}
