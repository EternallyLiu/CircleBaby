package cn.timeface.circle.baby.ui.circle.fragments;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.constants.TypeConstants;
import cn.timeface.circle.baby.support.mvp.bases.BasePresenterFragment;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.circle.activities.CircleSelectServerTimeDetailActivity;
import cn.timeface.circle.baby.ui.circle.activities.CircleSelectServerTimesActivity;
import cn.timeface.circle.baby.ui.circle.adapters.CircleSelectServerTimesAdapter;
import cn.timeface.circle.baby.ui.circle.bean.CircleMediaObj;
import cn.timeface.circle.baby.ui.circle.bean.CircleTimeLineExObj;
import cn.timeface.circle.baby.ui.circle.bean.CircleTimeLineWrapperObj;
import cn.timeface.circle.baby.ui.timelines.Utils.LogUtil;
import cn.timeface.circle.baby.views.StateView;
import cn.timeface.circle.baby.views.TFStateView;

/**
 * 圈时光fragment
 * author : sunyanwei Created on 17-3-20
 * email : sunyanwei@timeface.cn
 */
public class CircleServerTimeFragment extends BasePresenterFragment implements View.OnClickListener {
    @Bind(R.id.rv_content)
    RecyclerView rvContent;
    @Bind(R.id.tf_stateView)
    TFStateView stateView;
    @Bind(R.id.ll_empty)
    LinearLayout llEmpty;

    private String circleId;
    int contentType; //1-全部 2-按我发布的 3-按@我宝宝的
    CircleSelectServerTimesAdapter serverTimesAdapter;
    List<CircleMediaObj> mediaObjs = new ArrayList<>();//选中的照片(编辑的话是书中包含的所有照片)
    List<CircleTimeLineExObj> timeLineObjs = new ArrayList<>();//选中的时光（根据时光反解出来的时光）(当前页面选中的时光)

    public static CircleServerTimeFragment newInstance(
            int contentType,
            String circleId,
            List<CircleMediaObj> selectedMedias,
            List<CircleTimeLineExObj> selectedTimeLines){
        CircleServerTimeFragment fragment = new CircleServerTimeFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("content_type", contentType);
        bundle.putString("circle_id", circleId);
        bundle.putParcelableArrayList("media_objs", (ArrayList<? extends Parcelable>) selectedMedias);
        bundle.putParcelableArrayList("time_lines", (ArrayList<? extends Parcelable>) selectedTimeLines);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_server_time, container, false);
        ButterKnife.bind(this, view);
        this.contentType = getArguments().getInt("content_type", TypeConstants.PHOTO_TYPE_TIME);
        this.mediaObjs = getArguments().getParcelableArrayList("media_objs");
        this.timeLineObjs = getArguments().getParcelableArrayList("time_lines");
        this.circleId = getArguments().getString("circle_id");
        if(mediaObjs == null) mediaObjs = new ArrayList<>();
        stateView.setOnRetryListener(() -> reqData());
//        rvContent.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                if(Math.abs(dy) > 5 && getActivity() instanceof CircleSelectServerTimesActivity){
//                    ((CircleSelectServerTimesActivity) getActivity()).setSelectContentShow(false);
//                }
//                super.onScrolled(recyclerView, dx, dy);
//            }
//        });

        reqData();
        return view;
    }

    private void reqData(){
        stateView.loading();
        addSubscription(
                apiService.queryCircleTimeLines(circleId, contentType)
                        .compose(SchedulersCompat.applyIoSchedulers())
                        .doOnCompleted(() -> stateView.finish())
                        .subscribe(
                                response -> {
                                    if (response.success()) {
                                        setListData(response.getDataList());
                                    } else {
                                        ToastUtil.showToast(response.info);
                                    }
                                },
                                throwable -> {
                                    stateView.showException(throwable);
                                    LogUtil.showError(throwable);
                                }
                        )
        );
    }

    private void setListData(List<CircleTimeLineWrapperObj> data){
        if (serverTimesAdapter == null) {
            rvContent.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            serverTimesAdapter = new CircleSelectServerTimesAdapter(getActivity(), data, Integer.MAX_VALUE, this, mediaObjs, timeLineObjs);
            rvContent.setAdapter(serverTimesAdapter);
        } else {
            serverTimesAdapter.setListData(data);
            serverTimesAdapter.notifyDataSetChanged();
        }
        llEmpty.setVisibility(serverTimesAdapter.getListData().size() > 0 ? View.GONE : View.VISIBLE);
        stateView.finish();

        if(getActivity() instanceof CircleSelectServerTimesActivity)
            ((CircleSelectServerTimesActivity) getActivity()).setPhotoTipVisibility(llEmpty.isShown() ? View.GONE : View.VISIBLE);
    }

    /**
     * 设置选中的时光
     * @param timeLineObjs
     */
    public void setTimeLineObjs(List<CircleTimeLineExObj> timeLineObjs) {
        this.timeLineObjs = timeLineObjs;
        if(serverTimesAdapter != null){
            serverTimesAdapter.setSelImgs((ArrayList<CircleTimeLineExObj>) timeLineObjs);
            serverTimesAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 设置选中的图片
     * @param mediaObjs
     */
    public void setMediaObjs(List<CircleMediaObj> mediaObjs){
        this.mediaObjs = mediaObjs;
        if(serverTimesAdapter != null){
            serverTimesAdapter.setSelImgs((ArrayList<CircleTimeLineExObj>) timeLineObjs);
            serverTimesAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 该页面内容是否都全部选中
     * @return
     */
    public boolean isAllSelect(){
        return serverTimesAdapter != null ? serverTimesAdapter.isAllSelect() : false;
    }

    public void doAllSelImg(){
        if(serverTimesAdapter != null)serverTimesAdapter.doAllSelImg();
    }

    public void doAllUnSelImg(){
        if(serverTimesAdapter != null)serverTimesAdapter.doAllUnSelImg();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //跳到时光详情页面
            case R.id.ll_time_root:
                CircleTimeLineExObj timeLineExObj = (CircleTimeLineExObj) view.getTag(R.string.tag_obj);
                CircleSelectServerTimeDetailActivity.open(getActivity(), timeLineExObj, mediaObjs);
                break;
        }
    }
}