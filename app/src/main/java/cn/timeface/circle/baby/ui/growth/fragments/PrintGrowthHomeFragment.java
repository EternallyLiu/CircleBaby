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

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.fragments.base.BaseFragment;
import cn.timeface.circle.baby.support.utils.DeviceUtil;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.growth.adapters.PrintGrowthHomeAdapter;
import cn.timeface.circle.baby.ui.growth.beans.PrintGrowthHomeObj;

/**
 * 印成长首页
 * author : YW.SUN Created on 2017/1/11
 * email : sunyw10@gmail.com
 */
public class PrintGrowthHomeFragment extends BaseFragment implements View.OnClickListener {

    @Bind(R.id.rv_books)
    RecyclerView rvBooks;

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
                                },
                                throwable -> {
                                    Log.e(TAG, throwable.getLocalizedMessage());
                                }
                        )

        );
    }

    private void setData(List<PrintGrowthHomeObj> growthHomeObjs){
        if(growthHomeAdapter == null){
            rvBooks.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            growthHomeAdapter = new PrintGrowthHomeAdapter(getActivity(), growthHomeObjs, this);
            rvBooks.addItemDecoration(
                    new HorizontalDividerItemDecoration
                            .Builder(getActivity())
                            .colorResId(R.color.trans)
                            .size(DeviceUtil.dpToPx(getResources(), 12))
                            .build()
            );
            rvBooks.setAdapter(growthHomeAdapter);
        } else {
            growthHomeAdapter.setListData(growthHomeObjs);
            growthHomeAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.rl_root){
            // TODO: 2017/1/11 book list
        }
    }
}
