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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.constants.TypeConstants;
import cn.timeface.circle.baby.support.api.models.objs.MediaObj;
import cn.timeface.circle.baby.support.api.models.objs.TimeLineObj;
import cn.timeface.circle.baby.support.api.models.objs.TimeLineWrapObj;
import cn.timeface.circle.baby.support.api.models.responses.QueryTimeLineResponse;
import cn.timeface.circle.baby.support.managers.listeners.IEventBus;
import cn.timeface.circle.baby.support.mvp.bases.BasePresenterFragment;
import cn.timeface.circle.baby.support.mvp.model.BookModel;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.growth.activities.SelectServerTimeDetailActivity;
import cn.timeface.circle.baby.ui.growth.adapters.SelectServerTimesAdapter;
import cn.timeface.circle.baby.ui.growth.events.SelectMediaEvent;
import cn.timeface.circle.baby.ui.growth.events.ServerTimePhotoAllSelectEvent;
import cn.timeface.circle.baby.views.TFStateView;
import rx.Observable;

/**
 * 时光fragment
 * author : YW.SUN Created on 2017/2/15
 * email : sunyw10@gmail.com
 */
public class ServerTimeFragment extends BasePresenterFragment implements View.OnClickListener, IEventBus {

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
    List<MediaObj> mediaObjs;

    public static ServerTimeFragment newInstance(int contentType, String userId, int bookType){
        ServerTimeFragment fragment = new ServerTimeFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("content_type", contentType);
        bundle.putInt("book_type", bookType);
        bundle.putString("user_id", userId);
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
    public static ServerTimeFragment newInstanceEdit(int contentType, String userId, int bookType, List<MediaObj> selectedMedias){
        ServerTimeFragment fragment = new ServerTimeFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("content_type", contentType);
        bundle.putInt("book_type", bookType);
        bundle.putString("user_id", userId);
        bundle.putParcelableArrayList("media_objs", (ArrayList<? extends Parcelable>) selectedMedias);
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
        if(mediaObjs == null) mediaObjs = new ArrayList<>();
        reqData();
        return view;
    }

    private void reqData(){
        stateView.setVisibility(View.VISIBLE);
        stateView.loading();
        Observable<QueryTimeLineResponse> timeResponseObservable = apiService.queryTimeLine(FastData.getBabyId(), userId, getQueryType());

        if(timeResponseObservable == null) return;
        timeResponseObservable.compose(SchedulersCompat.applyIoSchedulers())
                .doOnUnsubscribe(() -> stateView.finish())
                .subscribe(
                        response -> {
                            if(response.success()){
                                setListData(response.getDataList());
                            } else {
                                ToastUtil.showToast(response.info);
                            }
                        },
                        throwable -> {
                            Log.e(TAG, throwable.getLocalizedMessage());
                        }
                );
    }

    private void setListData(List<TimeLineWrapObj> data){
        if (serverTimesAdapter == null) {
            rvContent.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            serverTimesAdapter = new SelectServerTimesAdapter(getActivity(), data, 99, this, mediaObjs);
            rvContent.setAdapter(serverTimesAdapter);
        } else {
            serverTimesAdapter.setListData(data);
            serverTimesAdapter.notifyDataSetChanged();
        }
        llEmpty.setVisibility(serverTimesAdapter.getListData().size() > 0 ? View.GONE : View.VISIBLE);
    }

    public List<TimeLineObj> getSelectedMedias(){
        return serverTimesAdapter.getSelImgs();
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
                SelectServerTimeDetailActivity.open(getActivity(), timeLineObj);
                break;
        }
    }

    @Subscribe
    public void timePhotoAllSelectEvent(ServerTimePhotoAllSelectEvent allSelectEvent){
        //全选
        if(allSelectEvent.getAllSelect()){
            if(!serverTimesAdapter.getSelImgs().contains(allSelectEvent.getTimeLineObj())){
                serverTimesAdapter.getSelImgs().add(allSelectEvent.getTimeLineObj());
            }
        //全不选
        } else {
            //还是选中
            if(allSelectEvent.getTimeLineSelect()){
                if(!serverTimesAdapter.getSelImgs().contains(allSelectEvent.getTimeLineObj())){
                    serverTimesAdapter.getSelImgs().add(allSelectEvent.getTimeLineObj());
                }
            //没有选中
            } else {
                if(serverTimesAdapter.getSelImgs().contains(allSelectEvent.getTimeLineObj())){
                    serverTimesAdapter.getSelImgs().remove(allSelectEvent.getTimeLineObj());
                }
            }
        }

        serverTimesAdapter.notifyDataSetChanged();
    }

    @Subscribe
    public void selectMediaEvent(SelectMediaEvent selectMediaEvent){
        for(TimeLineWrapObj timeLineWrapObj : serverTimesAdapter.getListData()){
            for(TimeLineObj timeLineObj : timeLineWrapObj.getTimelineList()){
                for(MediaObj mediaObj : timeLineObj.getMediaList()){
                    if(mediaObj.equals(selectMediaEvent.getMediaObj())){
                        mediaObj.setSelected(selectMediaEvent.getSelect() ? 1 : 0);
                    }
                }
            }
        }
    }
}
