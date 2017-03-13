package cn.timeface.circle.baby.ui.growth.fragments;

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
import cn.timeface.circle.baby.support.api.models.objs.MediaObj;
import cn.timeface.circle.baby.support.api.models.objs.TimeLineObj;
import cn.timeface.circle.baby.support.api.models.objs.TimeLineWrapObj;
import cn.timeface.circle.baby.support.api.models.responses.QueryTimeLineResponse;
import cn.timeface.circle.baby.support.mvp.bases.BasePresenterFragment;
import cn.timeface.circle.baby.support.mvp.model.BookModel;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.growth.activities.SelectServerTimeActivity;
import cn.timeface.circle.baby.ui.growth.activities.SelectServerTimeDetailActivity;
import cn.timeface.circle.baby.ui.growth.adapters.SelectServerTimesAdapter;
import cn.timeface.circle.baby.views.TFStateView;
import rx.Observable;

/**
 * 时光fragment
 * author : YW.SUN Created on 2017/2/15
 * email : sunyw10@gmail.com
 */
public class ServerTimeFragment extends BasePresenterFragment implements View.OnClickListener {

    @Bind(R.id.rv_content)
    RecyclerView rvContent;
    @Bind(R.id.tf_stateView)
    TFStateView stateView;
    @Bind(R.id.ll_empty)
    LinearLayout llEmpty;

    int contentType;
    int bookType;
    String userId;
    SelectServerTimesAdapter serverTimesAdapter;
    List<MediaObj> mediaObjs = new ArrayList<>();//选中的照片(编辑的话是书中包含的所有照片)
    List<String> timeIds = new ArrayList<>();//选中的时光id（编辑的时候所有选中的时光）
    List<TimeLineObj> timeLineObjs = new ArrayList<>();//选中的时光（根据时光反解出来的时光）(当前页面选中的时光)
    int babyId;

    public ServerTimeFragment newInstance(int contentType, String userId, int bookType){
        ServerTimeFragment fragment = new ServerTimeFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("content_type", contentType);
        bundle.putInt("book_type", bookType);
        bundle.putString("user_id", userId);
        bundle.putInt("baby_id", FastData.getBabyId());
        fragment.setArguments(bundle);
        return fragment;
    }

    /**
     * 编辑状态
     * @param contentType
     * @param userId
     * @param selectedMedias
     * @return
     */
    public static ServerTimeFragment newInstanceEdit(int contentType, String userId, int bookType, List<MediaObj> selectedMedias, List<String> selectedTimeIds, int babyId, List<TimeLineObj> selectedTimeLines){
        ServerTimeFragment fragment = new ServerTimeFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("content_type", contentType);
        bundle.putInt("book_type", bookType);
        bundle.putString("user_id", userId);
        bundle.putParcelableArrayList("media_objs", (ArrayList<? extends Parcelable>) selectedMedias);
        bundle.putStringArrayList("time_ids", (ArrayList<String>) selectedTimeIds);
        bundle.putParcelableArrayList("time_lines", (ArrayList<? extends Parcelable>) selectedTimeLines);
        bundle.putInt("baby_id", babyId);
        fragment.setArguments(bundle);
        return fragment;
    }

    public ServerTimeFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_server_time, container, false);
        ButterKnife.bind(this, view);
        this.contentType = getArguments().getInt("content_type", TypeConstants.PHOTO_TYPE_TIME);
        this.userId = getArguments().getString("user_id");
        this.bookType = getArguments().getInt("book_type", 0);
        this.mediaObjs = getArguments().getParcelableArrayList("media_objs");
        this.timeIds = getArguments().getStringArrayList("time_ids");
        this.timeLineObjs = getArguments().getParcelableArrayList("time_lines");
        this.babyId = getArguments().getInt("baby_id");
        if(mediaObjs == null) mediaObjs = new ArrayList<>();
        if(timeIds == null) timeIds = new ArrayList<>();
        rvContent.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if(Math.abs(dy) > 5 && getActivity() instanceof SelectServerTimeActivity){
                    ((SelectServerTimeActivity) getActivity()).setSelectContentShow(false);
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        reqData();
        return view;
    }

    private void reqData(){
        Observable<QueryTimeLineResponse> timeResponseObservable = null;
        if(contentType == TypeConstants.PHOTO_TYPE_TIME){
            timeResponseObservable = apiService.queryTimeLineByTime(babyId, String.valueOf(getQueryType()));
        } else {
            timeResponseObservable = apiService.queryTimeLineByMember(babyId, userId, getQueryType());
        }

        if(timeResponseObservable == null) return;
        timeResponseObservable.compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(
                        response -> {
                            if(response.success()){
                                setListData(response.getDataList());
                            } else {
                                ToastUtil.showToast(response.info);
                            }
                        },
                        throwable -> {
                            stateView.showException(throwable);
                            Log.e(TAG, throwable.getLocalizedMessage());
                        }
                );
    }

    private void setListData(List<TimeLineWrapObj> data){
        if (serverTimesAdapter == null) {
            rvContent.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            serverTimesAdapter = new SelectServerTimesAdapter(getActivity(), data, 99, this, mediaObjs, timeLineObjs);
            rvContent.setAdapter(serverTimesAdapter);
        } else {
            serverTimesAdapter.setListData(data);
            serverTimesAdapter.notifyDataSetChanged();
        }
        llEmpty.setVisibility(serverTimesAdapter.getListData().size() > 0 ? View.GONE : View.VISIBLE);
        stateView.finish();
        if(getActivity() instanceof SelectServerTimeActivity) ((SelectServerTimeActivity) getActivity()).setPhotoTipVisibility(View.VISIBLE);
    }

    public int getQueryType(){
        switch (bookType) {
            //成长语录
            case BookModel.BOOK_TYPE_GROWTH_QUOTATIONS:
                return TypeConstants.QUOTATION;

            default:
                return -1;
        }
    }

    public void doAllSelImg(){
        if(serverTimesAdapter != null)serverTimesAdapter.doAllSelImg();
    }

    public void doAllUnSelImg(){
        if(serverTimesAdapter != null)serverTimesAdapter.doAllUnSelImg();
    }

    /**
     * 该页面内容是否都全部选中
     * @return
     */
    public boolean isAllSelect(){
        return serverTimesAdapter != null ? serverTimesAdapter.isAllSelect() : false;
    }

    /**
     * 设置选中的时光
     * @param timeLineObjs
     */
    public void setTimeLineObjs(List<TimeLineObj> timeLineObjs) {
        this.timeLineObjs = timeLineObjs;
        if(serverTimesAdapter != null){
            serverTimesAdapter.setSelImgs((ArrayList<TimeLineObj>) timeLineObjs);
            serverTimesAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 设置选中的图片
     * @param mediaObjs
     */
    public void setMediaObjs(List<MediaObj> mediaObjs){
        this.mediaObjs = mediaObjs;
        if(serverTimesAdapter != null){
            serverTimesAdapter.setSelImgs((ArrayList<TimeLineObj>) timeLineObjs);
            serverTimesAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //跳到时光详情页面
            case R.id.ll_time_root:
                TimeLineObj timeLineObj = (TimeLineObj) view.getTag(R.string.tag_obj);
                SelectServerTimeDetailActivity.open(getActivity(), timeLineObj, mediaObjs);
                break;
        }
    }
}
