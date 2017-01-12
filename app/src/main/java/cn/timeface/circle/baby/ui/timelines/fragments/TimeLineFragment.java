package cn.timeface.circle.baby.ui.timelines.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.FragmentBridgeActivity;
import cn.timeface.circle.baby.fragments.base.BaseFragment;
import cn.timeface.circle.baby.support.api.models.base.BaseObj;
import cn.timeface.circle.baby.ui.timelines.adapters.BaseAdapter;
import cn.timeface.circle.baby.ui.timelines.adapters.TimeLineSelectAdapter;
import cn.timeface.circle.baby.ui.timelines.beans.MonthRecord;
import cn.timeface.circle.baby.ui.timelines.beans.TimeAxisObj;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by wangshuai on 2017/1/10.
 */

public class TimeLineFragment extends BaseFragment implements BaseAdapter.OnItemClickLister {
    @Bind(R.id.tv_mine)
    TextView tvMine;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.list)
    RecyclerView list;
    @Bind(R.id.back)
    ImageView back;
    private TimeLineSelectAdapter adapter = null;

    @OnClick(R.id.back)
    public void click(View view){
        switch (view.getId()){
            case R.id.back:
                getActivity().finish();
                break;
        }
    }

    public static TimeLineFragment newInstance() {
        TimeLineFragment fragment = new TimeLineFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.time_line_fragment, container, false);
        ButterKnife.bind(this, view);
        setActionBar(toolbar);
        adapter = new TimeLineSelectAdapter(getActivity());
        list.setAdapter(adapter);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        list.setLayoutManager(manager);
//        list.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        list.setItemAnimator(new DefaultItemAnimator());
        adapter.setItemClickLister(this);
        reqData();
        return view;
    }

    private void reqData() {
        Subscription ss = apiService.getTimeAxisList().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(timeAxixResponse -> {
                    Log.d("test", timeAxixResponse.getErrorCode() + "--");
                    if (timeAxixResponse.success()) {
                        if (timeAxixResponse.getDataList() != null && timeAxixResponse.getDataList().size() > 0) {
                            Log.d("test", "datalistSize==" + timeAxixResponse.getDataList().size());
                            adapter.addList(true, timeAxixResponse.getDataList());
                        }
                    }
                }, error -> {
                    Log.d("test", "error:" + error.getMessage());
                });
        addSubscription(ss);
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onItemClick(View view, int position) {
        BaseObj item = adapter.getItem(position);
        if (item instanceof TimeAxisObj) {
            TimeAxisObj time = (TimeAxisObj) item;
            time.setSelected(!time.isSelected());
            adapter.updateItem(position, item);
            if (!time.isSelected())
                adapter.deleteItem(position + 1, ((TimeAxisObj) item).getMonthRecords().size());
            else
                adapter.addList(position + 1, time.getMonthRecords());
        } else if (item instanceof MonthRecord) {
            MonthRecord mon = (MonthRecord) item;
            Bundle bundle=new Bundle();
            bundle.putParcelable("data",mon);
            FragmentBridgeActivity.open(getContext(),"TimeLineDayFragment","",bundle);
        }
    }
}
