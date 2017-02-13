package cn.timeface.circle.baby.ui.timelines.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.tbruyelle.rxpermissions.RxPermissions;

import org.greenrobot.eventbus.EventBus;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.fragments.base.BaseFragment;
import cn.timeface.circle.baby.support.api.models.objs.LocationObj;
import cn.timeface.circle.baby.support.managers.services.SavePicInfoService;
import cn.timeface.circle.baby.support.utils.LocationHelper;
import cn.timeface.circle.baby.support.utils.ptr.IPTRRecyclerListener;
import cn.timeface.circle.baby.support.utils.ptr.TFPTRRecyclerViewHelper;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.timelines.Utils.LogUtil;
import cn.timeface.circle.baby.ui.timelines.adapters.BaseAdapter;
import cn.timeface.circle.baby.ui.timelines.adapters.NearLocationAdapter;
import cn.timeface.circle.baby.ui.timelines.beans.NearLocationObj;
import cn.timeface.circle.baby.ui.timelines.views.EmptyDataView;

/**
 * author : wangshuai Created on 2017/2/9
 * email : wangs1992321@gmail.com
 */
public class LocationListFragment extends BaseFragment implements BDLocationListener, IPTRRecyclerListener, BaseAdapter.LoadDataFinish, EmptyDataView.EmptyCallBack, BaseAdapter.OnItemClickLister {

    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.list)
    RecyclerView list;
    @Bind(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.empty)
    EmptyDataView empty;
    private LocationObj currentLocationObj = null;
    private LocationHelper locationHelper = null;
    private NearLocationAdapter adapter = null;
    private TFPTRRecyclerViewHelper helper;

    private boolean isShowLocation = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null)
            isShowLocation = bundle.getBoolean("isShowLocation", false);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_near_layout, container, false);
        ButterKnife.bind(this, view);
        setActionBar(toolbar);
        initActionBar();
        title.setText("所在位置");
        locationHelper = new LocationHelper(getActivity(), this);
        adapter = new NearLocationAdapter(getActivity());
        adapter.setLoadDataFinish(this);
        adapter.setItemClickLister(this);
        adapter.error();
        list.setAdapter(adapter);
        list.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        list.setItemAnimator(new DefaultItemAnimator());
        helper = new TFPTRRecyclerViewHelper(getActivity(), list, swipeRefreshLayout);
        helper.setTFPTRMode(TFPTRRecyclerViewHelper.Mode.PULL_FORM_START)
                .tfPtrListener(this);
        locationHelper.startLocation();
        return view;
    }

    private void startLocation() {
        if (locationHelper == null)
            locationHelper = new LocationHelper(getActivity(), this);
        RxPermissions.getInstance(getActivity()).request(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(aBoolean -> {
                    if (aBoolean) {
                        locationHelper.startLocation();
                        locationHelper.getLocationClient().requestHotSpotState();
                    }
                });
    }


    private void stopLocation() {
        if (locationHelper != null)
            locationHelper.stopLocation();
    }

    private void reqData() {
        if (currentLocationObj == null) {
            helper.finishTFPTRRefresh();
            return;
        }
        apiService.queryNearList("", currentLocationObj.getLat(), currentLocationObj.getLog())
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(response -> {
                    if (response.success()) {
                        adapter.addList(true, response.getDataList());
                        NearLocationObj nearLocationObj = new NearLocationObj("不显示位置", isShowLocation ? "显示" : null, null);
                        adapter.addList(0, nearLocationObj);
                    }
                    helper.finishTFPTRRefresh();
                }, throwable -> {
                    adapter.error();
                    helper.finishTFPTRRefresh();
                });
    }

    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        stopLocation();
        if (bdLocation != null) {
            if (currentLocationObj == null)
                currentLocationObj = new LocationObj();
            currentLocationObj.setLat(bdLocation.getLatitude());
            currentLocationObj.setLog(bdLocation.getLongitude());
            reqData();
        }
    }

    private void empty() {
        if (adapter.getRealItemSize() > 0) {
            swipeRefreshLayout.setVisibility(View.VISIBLE);
            empty.setVisibility(View.GONE);
        } else {
            swipeRefreshLayout.setVisibility(View.GONE);
            empty.setVisibility(View.VISIBLE);
            empty.setErrorDrawable(R.drawable.net_empty);
            empty.setErrorRetryText("重新加载");
            empty.setErrorText("对不起！没有获取到位置信息！");
            empty.setEmptyCallBack(this);
        }
    }

    @Override
    public void onConnectHotSpotMessage(String s, int i) {
        LogUtil.showLog(s + "------>" + i);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onTFPullDownToRefresh(View refreshView) {
        startLocation();
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

    @Override
    public void loadfinish() {
        empty();
    }

    @Override
    public void retry() {
        startLocation();
    }

    @Override
    public void onItemClick(View view, int position) {
        NearLocationObj nearLocationObj = adapter.getItem(position);
        EventBus.getDefault().post(nearLocationObj);
        getActivity().finish();
    }
}
