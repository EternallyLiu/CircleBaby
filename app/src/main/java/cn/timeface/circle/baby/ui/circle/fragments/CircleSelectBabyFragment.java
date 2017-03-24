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

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.support.mvp.bases.BasePresenterFragment;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.circle.activities.CircleSelectHomeWorkDetailActivity;
import cn.timeface.circle.baby.ui.circle.adapters.CircleSelectBabyAdapter;
import cn.timeface.circle.baby.ui.circle.bean.GetCircleAllBabyObj;
import cn.timeface.circle.baby.views.TFStateView;

/**
 * 选择圈内宝宝fragment
 * author : sunyanwei Created on 17-3-22
 * email : sunyanwei@timeface.cn
 */
public class CircleSelectBabyFragment extends BasePresenterFragment implements View.OnClickListener {

    @Bind(R.id.rv_content)
    RecyclerView rvContent;
    @Bind(R.id.tf_stateView)
    TFStateView stateView;

    String circleId;
    CircleSelectBabyAdapter selectBabyAdapter;

    public static CircleSelectBabyFragment newInstance(String circleId){
        CircleSelectBabyFragment fragment = new CircleSelectBabyFragment();
        Bundle bundle = new Bundle();
        bundle.putString("circle_id", circleId);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_circle_select_baby, container, false);
        ButterKnife.bind(this, view);

        circleId = getArguments().getString("circle_id");

        reqData();
        return view;
    }

    private void reqData() {
        stateView.loading();
        addSubscription(
                apiService.getCircleAllBaby(circleId, 0)
                        .compose(SchedulersCompat.applyIoSchedulers())
                        .doOnCompleted(() -> stateView.finish())
                        .subscribe(
                                response -> {
                                    if (response.success()) {
                                        setData(response.getDataList());
                                    } else {
                                        ToastUtil.showToast(response.getInfo());
                                    }
                                },
                                throwable -> {
                                    Log.e(TAG, throwable.getLocalizedMessage());
                                }
                        )
        );
    }

    private void setData(List<GetCircleAllBabyObj> allBabyObjList){
        if(selectBabyAdapter == null){
            selectBabyAdapter = new CircleSelectBabyAdapter(getActivity(), allBabyObjList, this);
            rvContent.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            rvContent.addItemDecoration(
                    new HorizontalDividerItemDecoration.Builder(getActivity())
                            .size(1)
                            .colorResId(R.color.line_color)
                            .build());
            rvContent.setAdapter(selectBabyAdapter);
        } else {
            selectBabyAdapter.setListData(allBabyObjList);
            selectBabyAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.rl_root:
                GetCircleAllBabyObj babyObj = (GetCircleAllBabyObj) view.getTag(R.string.tag_obj);
                CircleSelectHomeWorkDetailActivity.open(getActivity(), circleId, babyObj.getCircleBabyId());
                break;
        }
    }
}
