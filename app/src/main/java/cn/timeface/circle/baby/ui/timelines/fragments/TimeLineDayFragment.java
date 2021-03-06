package cn.timeface.circle.baby.ui.timelines.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;
import com.yqritc.recyclerviewflexibledivider.VerticalDividerItemDecoration;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.events.DeleteTimeLineEvent;
import cn.timeface.circle.baby.events.TimelineEditEvent;
import cn.timeface.circle.baby.fragments.base.BaseFragment;
import cn.timeface.circle.baby.support.api.models.base.BaseObj;
import cn.timeface.circle.baby.support.api.models.objs.MediaObj;
import cn.timeface.circle.baby.support.api.models.objs.TimeLineGroupObj;
import cn.timeface.circle.baby.support.api.models.objs.TimeLineObj;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.ptr.IPTRRecyclerListener;
import cn.timeface.circle.baby.support.utils.ptr.TFPTRRecyclerViewHelper;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.timelines.Utils.LogUtil;
import cn.timeface.circle.baby.ui.timelines.adapters.BaseAdapter;
import cn.timeface.circle.baby.ui.timelines.adapters.TimeLineDayAdapter;
import cn.timeface.circle.baby.ui.timelines.adapters.TimeLineGroupListAdapter;
import cn.timeface.circle.baby.ui.timelines.adapters.ViewHolder;
import cn.timeface.circle.baby.ui.timelines.beans.MediaUpdateEvent;
import cn.timeface.circle.baby.ui.timelines.beans.MonthRecord;
import cn.timeface.circle.baby.ui.timelines.beans.TimeGroupSimpleBean;
import cn.timeface.circle.baby.ui.timelines.views.EmptyDataView;
import cn.timeface.circle.baby.ui.timelines.views.MySuperRefreshLayout;

/**
 * Created by wangshuai on 2017/1/10.
 */

public class TimeLineDayFragment extends BaseFragment implements BaseAdapter.LoadDataFinish, IPTRRecyclerListener, EmptyDataView.EmptyCallBack {


    @Bind(R.id.back_up)
    ImageView backUp;
    @Bind(R.id.ll_menu)
    Toolbar toolbar;
    @Bind(R.id.content_recycler_view)
    RecyclerView contentRecyclerView;
    @Bind(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.empty)
    EmptyDataView empty;
    private TimeLineGroupListAdapter adapter;

    private int year, month;
    private int currentPage = 1;
    private TFPTRRecyclerViewHelper helper;

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
        toolbar.setTitle(FastData.getBabyObj().getNickName() + "的成长记录");
        adapter = new TimeLineGroupListAdapter(getActivity());
        adapter.setIntentCalender(false);
        adapter.setLoadDataFinish(this);
        contentRecyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        contentRecyclerView.setLayoutManager(layoutManager);
        contentRecyclerView.addItemDecoration(
                new HorizontalDividerItemDecoration.Builder(getActivity())
                        .color(getResources().getColor(R.color.bg30))
                        .sizeResId(R.dimen.view_space_normal).build());
        contentRecyclerView.getItemAnimator().setChangeDuration(0);//fix 列表闪烁
        reqData(currentPage);
//        if (mMonthRecord != null)
//            tvCount.setText(mMonthRecord.getRecordcount() + "条");
//        else tvCount.setVisibility(View.GONE);
        empty.setErrorDrawable(R.drawable.net_empty);
        empty.setErrorRetryText("重新加载");
        empty.setErrorText("对不起！没有加载到数据！");
        empty.setEmptyCallBack(this);
        empty.setVisibility(View.GONE);
        swipeRefreshLayout.setVisibility(View.VISIBLE);
        helper = new TFPTRRecyclerViewHelper(getActivity(), contentRecyclerView, swipeRefreshLayout);
        helper.setTFPTRMode(TFPTRRecyclerViewHelper.Mode.BOTH);
        helper.tfPtrListener(this);
        backUp.setOnClickListener(v -> {
            if (contentRecyclerView != null && adapter.getRealItemSize() > 0)
                contentRecyclerView.scrollToPosition(0);
        });
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (mMonthRecord != null) {
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
                        if (timelineResponse.getCurrentPage() <= timelineResponse.getTotalPage())
                            currentPage++;
                    } else adapter.error();
                }, error -> {
                    if (adapter != null)
                        adapter.error();
                });
    }

    private void setDataList(List<TimeLineGroupObj> dataList) {
        ArrayList<BaseObj> list = new ArrayList<>();
        for (TimeLineGroupObj groupObj : dataList) {
            TimeGroupSimpleBean bean = new TimeGroupSimpleBean(groupObj.getAge(), groupObj.getDateEx(), groupObj.getDate());
            list.add(bean);
            for (TimeLineObj timeLineObj : groupObj.getTimeLineList())
                list.add(timeLineObj);
        }
        if (currentPage == 1) {
            adapter.addList(true, list);
        } else {
            adapter.addList(list);
        }
    }

    @Subscribe
    public void onEvent(MonthRecord event) {
        mMonthRecord = event;
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
    public void onTFPullDownToRefresh(View refreshView) {
        currentPage = 1;
        reqData(currentPage);
    }

    @Override
    public void onTFPullUpToRefresh(View refreshView) {
        reqData(currentPage);
    }

    @Override
    public void onScrollUp(int firstVisibleItem) {
        backUp.setVisibility(firstVisibleItem > 0 ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onScrollDown(int firstVisibleItem) {
        backUp.setVisibility(firstVisibleItem > 0 ? View.VISIBLE : View.GONE);
    }

    @Override
    public void loadFinish(int code) {
        if (helper != null)
            helper.finishTFPTRRefresh();
        if (adapter.getRealItemSize() <= 0) {
            if (empty!=null)empty.setVisibility(View.VISIBLE);
            if (swipeRefreshLayout!=null)swipeRefreshLayout.setVisibility(View.GONE);
        } else {
            if (empty!=null)empty.setVisibility(View.GONE);
            if (swipeRefreshLayout!=null)swipeRefreshLayout.setVisibility(View.VISIBLE);
        }
    }

    @Subscribe
    public void onEvent(MediaUpdateEvent mediaUpdateEvent) {
        if (mediaUpdateEvent.getAllDetailsListPosition() < 0)
            return;
        TimeLineObj timeLineObj = adapter.getItem(mediaUpdateEvent.getAllDetailsListPosition());
        boolean flag = timeLineObj.getMediaList().contains(mediaUpdateEvent.getMediaObj());
        LogUtil.showLog("flag:" + flag);
        if (flag) {
            int index = timeLineObj.getMediaList().indexOf(mediaUpdateEvent.getMediaObj());
            timeLineObj.getMediaList().get(index).setTips(mediaUpdateEvent.getMediaObj().getTips());
            timeLineObj.getMediaList().get(index).setIsFavorite(mediaUpdateEvent.getMediaObj().getIsFavorite());
            timeLineObj.getMediaList().get(index).setFavoritecount(mediaUpdateEvent.getMediaObj().getFavoritecount());
        }
    }

    @Subscribe
    public void onEvent(TimelineEditEvent event) {
        if (event instanceof DeleteTimeLineEvent) {
            adapter.deleteTimeLine(event.getTimeId());
        }
    }

    @Subscribe
    public void onEvent(TimeLineObj timeLineObj) {
        adapter.updateItem(timeLineObj);
    }

    @Override
    public void retry() {
        currentPage = 1;
        reqData(currentPage);

    }
}
