package cn.timeface.circle.baby.ui.circle.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.BuildConfig;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.support.mvp.bases.BasePresenterFragment;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.circle.adapters.CircleSelectSchoolTaskAdapter;
import cn.timeface.circle.baby.ui.circle.bean.CircleSchoolTaskObj;
import cn.timeface.circle.baby.views.TFStateView;

/**
 * 圈作品选择school task 界面
 * author : sunyanwei Created on 17-3-22
 * email : sunyanwei@timeface.cn
 */
public class CircleSelectSchoolTaskFragment extends BasePresenterFragment {

    @Bind(R.id.rv_content)
    RecyclerView rvContent;
    @Bind(R.id.tf_stateView)
    TFStateView stateView;

    String circleId;
    CircleSelectSchoolTaskAdapter selectSchoolTaskAdapter;

    public static CircleSelectSchoolTaskFragment newInstance(String circleId){
        CircleSelectSchoolTaskFragment fragment = new CircleSelectSchoolTaskFragment();
        Bundle bundle = new Bundle();
        bundle.putString("circle_id", circleId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_circle_select_school_task, container, false);
        ButterKnife.bind(this, view);

        circleId = getArguments().getString("circle_id");
        reqData();
        return view;
    }

    private void reqData(){
        stateView.loading();
        if(!BuildConfig.DEBUG) {
            addSubscription(
                    apiService.queryCircleTasks(circleId, 1, Integer.MAX_VALUE)
                            .compose(SchedulersCompat.applyIoSchedulers())
                            .doOnCompleted(() -> stateView.finish())
                            .subscribe(
                                    response -> {
                                        setData(response.getDataList());
                                    },
                                    throwable -> {
                                        Log.e(TAG, throwable.getLocalizedMessage());
                                    }
                            )
            );
        } else {
            List<CircleSchoolTaskObj> allBabyObjList = new ArrayList<>();
            for(int i = 0; i < 10; i++){
                CircleSchoolTaskObj babyObj = new CircleSchoolTaskObj();
                babyObj.setTitle("寒假为父母做一件事情");
                allBabyObjList.add(babyObj);
            }
            setData(allBabyObjList);
            stateView.finish();
        }
    }

    private void setData(List<CircleSchoolTaskObj> schoolTaskObjList){
        if(selectSchoolTaskAdapter == null){
            selectSchoolTaskAdapter = new CircleSelectSchoolTaskAdapter(getActivity(), schoolTaskObjList);
            rvContent.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            rvContent.addItemDecoration(
                    new HorizontalDividerItemDecoration.Builder(getActivity())
                            .size(1)
                            .colorResId(R.color.line_color)
                            .build());
            rvContent.setAdapter(selectSchoolTaskAdapter);
        } else {
            selectSchoolTaskAdapter.setListData(schoolTaskObjList);
            selectSchoolTaskAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
