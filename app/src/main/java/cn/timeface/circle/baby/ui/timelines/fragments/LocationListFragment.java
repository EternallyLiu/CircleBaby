package cn.timeface.circle.baby.ui.timelines.fragments;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.tbruyelle.rxpermissions.RxPermissions;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

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
import cn.timeface.circle.baby.ui.images.views.DeleteDialog;
import cn.timeface.circle.baby.ui.timelines.Utils.LogUtil;
import cn.timeface.circle.baby.ui.timelines.Utils.PermissionUtils;
import cn.timeface.circle.baby.ui.timelines.adapters.BaseAdapter;
import cn.timeface.circle.baby.ui.timelines.adapters.NearLocationAdapter;
import cn.timeface.circle.baby.ui.timelines.beans.ContentType;
import cn.timeface.circle.baby.ui.timelines.beans.NearLocationObj;
import cn.timeface.circle.baby.ui.timelines.views.EmptyDataView;
import cn.timeface.circle.baby.ui.timelines.views.LocationSearchDialog;

/**
 * author : wangshuai Created on 2017/2/9
 * email : wangs1992321@gmail.com
 */
public class LocationListFragment extends BaseFragment implements BDLocationListener, IPTRRecyclerListener, BaseAdapter.LoadDataFinish, EmptyDataView.EmptyCallBack, BaseAdapter.OnItemClickLister, LocationSearchDialog.SearchCallBack, DialogInterface.OnDismissListener ,DeleteDialog.SubmitListener{

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
    private DeleteDialog deleteDialog;


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
        startLocation();
        return view;
    }

    private void startLocation() {
        if (locationHelper == null)
            locationHelper = new LocationHelper(getActivity(), this);
        // TODO: 2/16/17 fix it  in test version
//        RxPermissions.getInstance(getActivity()).requestEach(
//                Manifest.permission.ACCESS_COARSE_LOCATION,
//                Manifest.permission.ACCESS_FINE_LOCATION,
//                Manifest.permission.READ_PHONE_STATE,
//                Manifest.permission.READ_EXTERNAL_STORAGE,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                .subscribe(permission -> {
//                    if (permission.name.equals(Manifest.permission.ACCESS_COARSE_LOCATION)
//                            || permission.name.equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
//                        if (permission.granted) {
//                            locationHelper.startLocation();
//                            locationHelper.getLocationClient().requestHotSpotState();
//                        }else {
//                            showRequestPermissionDialog();
//                        }
//                    }
//                });
    }

    private void showRequestPermissionDialog(){
        if (deleteDialog == null) {
            deleteDialog = new DeleteDialog(getActivity());
            deleteDialog.setTitle("提示");
            String contentMessage = "未获取GPS定位权限，请前往设置相关权限!";


            SpannableStringBuilder builder = new SpannableStringBuilder(contentMessage).append(" ");
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.RED);
            AbsoluteSizeSpan span = new AbsoluteSizeSpan((int) getResources().getDimension(R.dimen.text_large));
            //标红宝宝名字
            builder.setSpan(span, 0, contentMessage.length(),
                    Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            builder.setSpan(colorSpan, 0, contentMessage.length(),
                    Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            //关键提示语加粗
            StyleSpan styleSpan = new StyleSpan(Typeface.BOLD);
            builder.setSpan(styleSpan, 0, contentMessage.length(),
                    Spannable.SPAN_EXCLUSIVE_INCLUSIVE);


            deleteDialog.setMessage(builder);
            deleteDialog.setSubmitListener(this);
        }
        deleteDialog.show();
    }


    private void stopLocation() {
        if (locationHelper != null)
            locationHelper.stopLocation();
    }

    private String keywoard = null;

    private void reqData() {
        if (currentLocationObj == null) {
            helper.finishTFPTRRefresh();
            return;
        }
        apiService.queryNearList(keywoard, currentLocationObj.getLat(), currentLocationObj.getLog())
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(response -> {
                    if (response.success()) {
                        List<ContentType> list = new ArrayList<>();
                        for (int i = 0; i < response.getDataList().size(); i++) {
                            list.add(new ContentType(0, response.getDataList().get(i)));
                        }
                        adapter.addList(true, list);
                        NearLocationObj nearLocationObj = new NearLocationObj("不显示位置", isShowLocation ? "显示" : null, null);
                        adapter.addList(0, new ContentType(0, nearLocationObj));
                        adapter.addList(0, new ContentType(1, null));
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
        ContentType item = adapter.getItem(position);
        if (item.getType() == 0) {
            NearLocationObj nearLocationObj = (NearLocationObj) item.getItem();
            EventBus.getDefault().post(nearLocationObj);
            getActivity().finish();
        } else if (item.getType() == 1) {
            LocationSearchDialog dialog = new LocationSearchDialog(getActivity());
            dialog.setSearchCallBack(this);
            dialog.setOnDismissListener(this);
            dialog.show();
            adapter.deleteItem(item);
            list.scrollToPosition(0);
        }
    }

    @Override
    public void searchCall(String text) {
        keywoard = text;
        startLocation();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        adapter.addList(0, new ContentType(1, null));
    }

    @Override
    public void submit() {
        PermissionUtils.skipSpec(getActivity());
//        Intent localIntent = new Intent();
//        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        if (Build.VERSION.SDK_INT >= 9) {
//            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
//            localIntent.setData(Uri.fromParts("package", "cn.timeface.circle.baby", null));
//        } else if (Build.VERSION.SDK_INT <= 8) {
//            localIntent.setAction(Intent.ACTION_VIEW);
//            localIntent.setClassName("com.android.settings","com.android.settings.InstalledAppDetails");
//            localIntent.putExtra("com.android.settings.ApplicationPkgName", "cn.timeface.circle.baby");
//        }
//        startActivity(localIntent);


//        Uri packageURI = Uri.parse("package:" + "cn.timeface.circle.baby");
//        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,packageURI);
//        startActivity(intent);

//        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//        startActivity(intent);

    }
}
