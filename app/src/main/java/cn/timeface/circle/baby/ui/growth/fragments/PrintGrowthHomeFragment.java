package cn.timeface.circle.baby.ui.growth.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.TabMainActivity;
import cn.timeface.circle.baby.events.HomeRefreshEvent;
import cn.timeface.circle.baby.fragments.base.BaseFragment;
import cn.timeface.circle.baby.support.managers.listeners.IEventBus;
import cn.timeface.circle.baby.support.utils.DeviceUtil;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.growth.activities.BookListActivity;
import cn.timeface.circle.baby.ui.growth.activities.ProductionListActivityDelegate;
import cn.timeface.circle.baby.ui.growth.adapters.PrintGrowthHomeAdapter;
import cn.timeface.circle.baby.ui.growth.beans.PrintGrowthHomeObj;
import cn.timeface.circle.baby.views.TFStateView;

/**
 * 印成长首页
 * author : YW.SUN Created on 2017/1/11
 * email : sunyw10@gmail.com
 */
public class PrintGrowthHomeFragment extends BaseFragment implements View.OnClickListener, IEventBus {

    @Bind(R.id.rv_books)
    RecyclerView rvBooks;
    @Bind(R.id.tf_stateView)
    TFStateView stateView;

    PrintGrowthHomeAdapter growthHomeAdapter;

    public static PrintGrowthHomeFragment newInstance(String param) {
        return new PrintGrowthHomeFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View vFragment = inflater.inflate(R.layout.fragment_print_growth_home, container, false);
        ButterKnife.bind(this, vFragment);
        reqPrintGrowthDHome();
        return vFragment;
    }

    private void reqPrintGrowthDHome() {
        stateView.setVisibility(View.VISIBLE);
        stateView.loading();
        addSubscription(
                apiService.printGrowthHome()
                        .compose(SchedulersCompat.applyIoSchedulers())
                        .subscribe(
                                response -> {
                                    if(response.success()){
                                        setData(response.getDataList());
                                    } else {
                                        Toast.makeText(getActivity(), response.info, Toast.LENGTH_SHORT).show();
                                    }
                                    stateView.finish();
                                },
                                throwable -> {
                                    stateView.showException(throwable);
                                    Log.e(TAG, throwable.getLocalizedMessage());
                                }
                        )

        );
    }

    private void setData(List<PrintGrowthHomeObj> growthHomeObjs){
        if(growthHomeAdapter == null){
            rvBooks.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            growthHomeAdapter = new PrintGrowthHomeAdapter(getActivity(), growthHomeObjs, this);
//            rvBooks.addItemDecoration(
//                    new HorizontalDividerItemDecoration
//                            .Builder(getActivity())
//                            .colorResId(R.color.trans)
//                            .size(DeviceUtil.dpToPx(getResources(), 12))
//                            .build()
//            );
            rvBooks.setAdapter(growthHomeAdapter);
        } else {
            growthHomeAdapter.setListData(growthHomeObjs);
            growthHomeAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // 解决某些异常情况导致FootMenu消失
        if (getActivity() instanceof TabMainActivity) {
            ((TabMainActivity) getActivity()).showFootMenu();
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.rl_root){
            PrintGrowthHomeObj printGrowthHomeObj = (PrintGrowthHomeObj) view.getTag(R.string.tag_obj);
            ProductionListActivityDelegate.dispatchProductionList(getActivity(), printGrowthHomeObj.getBookType());
        }
    }

    @Subscribe
    public void homeRefreshEvent(HomeRefreshEvent refreshEvent){
        //切换宝宝操作
        if(refreshEvent.getType() == 1001){
            growthHomeAdapter.notifyDataSetChanged();
        }
    }
}
