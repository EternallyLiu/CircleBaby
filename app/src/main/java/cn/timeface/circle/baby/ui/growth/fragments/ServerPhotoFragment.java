package cn.timeface.circle.baby.ui.growth.fragments;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.constants.TypeConstants;
import cn.timeface.circle.baby.events.PhotoSelectCountEvent;
import cn.timeface.circle.baby.support.api.models.objs.MediaObj;
import cn.timeface.circle.baby.support.api.models.objs.MediaWrapObj;
import cn.timeface.circle.baby.support.api.models.objs.TimeLineObj;
import cn.timeface.circle.baby.support.api.models.responses.QueryPhotoResponse;
import cn.timeface.circle.baby.support.managers.listeners.IEventBus;
import cn.timeface.circle.baby.support.mvp.bases.BasePresenterFragment;
import cn.timeface.circle.baby.support.mvp.model.BookModel;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.growth.activities.SelectServerPhotoActivity;
import cn.timeface.circle.baby.ui.growth.adapters.SelectServerPhotosAdapter;
import cn.timeface.circle.baby.views.TFStateView;
import rx.Observable;

/**
 * 按时间/发布人/标签展示图片fragment
 * author : YW.SUN Created on 2017/2/14
 * email : sunyw10@gmail.com
 */
public class ServerPhotoFragment extends BasePresenterFragment {

    @Bind(R.id.rv_content)
    RecyclerView rvContent;
    @Bind(R.id.tf_stateView)
    TFStateView stateView;
    @Bind(R.id.ll_empty)
    LinearLayout llEmpty;
//    @Bind(R.id.tv_recommend_count)
//    TextView tvRecommendCount;
//    @Bind(R.id.tv_sel_count)
//    TextView tvSelectCount;
//    @Bind(R.id.rl_photo_tip)
//    RelativeLayout rlPhotoTip;

    int contentType;
    String userId;
    SelectServerPhotosAdapter serverPhotosAdapter;
    String addressName;
    List<MediaWrapObj> mediaWrapObjs;
    List<MediaObj> mediaObjs;
    int bookType;
    int babyId;

    /**
     * 地图上选择照片后（已经包含照片信息）
     * @param contentType
     * @param userId
     * @param addressName
     * @param mediaWrapObjs
     * @return
     */
    public ServerPhotoFragment newInstance(int contentType, String userId, String addressName, List<MediaWrapObj> mediaWrapObjs, int bookType){
        ServerPhotoFragment fragment = new ServerPhotoFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("content_type", contentType);
        bundle.putString("user_id", userId);
        bundle.putString("address_name", addressName);
        bundle.putParcelableArrayList("media_wrap_objs", (ArrayList<? extends Parcelable>) mediaWrapObjs);
        bundle.putInt("book_type", bookType);
        bundle.putInt("baby_id", FastData.getBabyId());
        fragment.setArguments(bundle);
        return fragment;
    }

    /**
     * 选择照片
     * @param contentType
     * @param userId
     * @return
     */
    public ServerPhotoFragment newInstance(int contentType, String userId, int bookType){
        return newInstance(contentType, userId, "", null, bookType);
    }

    /**
     * 编辑状态
     * @param contentType
     * @param userId
     * @param selectedMedias
     * @param mediaWrapObjs
     * @return
     */
    public static ServerPhotoFragment newInstanceEdit(int contentType, String userId, String addressName, List<MediaObj> selectedMedias, List<MediaWrapObj> mediaWrapObjs, int bookType, int babyId){
        ServerPhotoFragment fragment = new ServerPhotoFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("content_type", contentType);
        bundle.putString("user_id", userId);
        bundle.putString("address_name", addressName);
        bundle.putParcelableArrayList("media_objs", (ArrayList<? extends Parcelable>) selectedMedias);
        bundle.putParcelableArrayList("media_wrap_objs", (ArrayList<? extends Parcelable>) mediaWrapObjs);
        bundle.putInt("book_type", bookType);
        bundle.putInt("baby_id", babyId);
        fragment.setArguments(bundle);
        return fragment;
    }

//    /**
//     * 编辑状态
//     * @param contentType
//     * @param userId
//     * @param selectedMedias
//     * @return
//     */
//    public ServerPhotoFragment newInstanceEdit(int contentType, String userId, List<MediaObj> selectedMedias, int bookType, int babyId){
//        return newInstanceEdit(contentType, userId, selectedMedias, new ArrayList<>(), bookType, babyId);
//    }

    public ServerPhotoFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_server_photo, container, false);
        ButterKnife.bind(this, view);
        this.contentType = getArguments().getInt("content_type", TypeConstants.PHOTO_TYPE_TIME);
        this.userId = getArguments().getString("user_id");
        this.addressName = getArguments().getString("address_name");
        this.mediaWrapObjs = getArguments().getParcelableArrayList("media_wrap_objs");
        this.mediaObjs = getArguments().getParcelableArrayList("media_objs");
        this.bookType = getArguments().getInt("book_type");
        this.babyId = getArguments().getInt("baby_id");
        if(mediaObjs == null) mediaObjs = new ArrayList<>();
        if(contentType != TypeConstants.PHOTO_TYPE_LOCATION){
            reqData();
        } else {
            stateView.setVisibility(View.GONE);
            setListData(mediaWrapObjs);
        }
        if(getActivity() instanceof SelectServerPhotoActivity) ((SelectServerPhotoActivity) getActivity()).initTips();
        return view;
    }

    private void reqData(){
        stateView.setVisibility(View.VISIBLE);
        stateView.loading();
        Observable<QueryPhotoResponse> photoResponseObservable = null;
        if (contentType == TypeConstants.PHOTO_TYPE_TIME) {
            photoResponseObservable = apiService.queryPhotoByTime(babyId);
        } else if(contentType == TypeConstants.PHOTO_TYPE_USER){
            photoResponseObservable = apiService.queryPhotoByUser(babyId, userId);
        } else if(contentType == TypeConstants.PHOTO_TYPE_LABEL){
            photoResponseObservable = apiService.queryPhotoByLabel(babyId);
        }

        if(photoResponseObservable == null) return;
        addSubscription(
                photoResponseObservable.compose(SchedulersCompat.applyIoSchedulers())
                        .doOnUnsubscribe(() -> stateView.finish())
                        .subscribe(
                                response -> {
                                    if (response.success()) {
                                        setListData(response.getDataList());
                                    } else {
                                        ToastUtil.showToast(response.info);
                                        if(getActivity() instanceof SelectServerPhotoActivity) ((SelectServerPhotoActivity) getActivity()).setPhotoTipVisibility(View.GONE);
                                    }
                                },
                                throwable -> {
                                    if(getActivity() instanceof SelectServerPhotoActivity) ((SelectServerPhotoActivity) getActivity()).setPhotoTipVisibility(View.GONE);
                                    Log.e(TAG, throwable.getLocalizedMessage());
                                }
                        )
        );
    }

    private void setListData(List<MediaWrapObj> data){
        if (serverPhotosAdapter == null) {
            rvContent.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            serverPhotosAdapter = new SelectServerPhotosAdapter(getActivity(), data, 99, contentType, mediaObjs);
            rvContent.setAdapter(serverPhotosAdapter);
        } else {
            serverPhotosAdapter.setListData(data);
            serverPhotosAdapter.notifyDataSetChanged();
        }

        llEmpty.setVisibility(serverPhotosAdapter.getListData().size() > 0 ? View.GONE : View.VISIBLE);
        if(getActivity() instanceof SelectServerPhotoActivity)
            ((SelectServerPhotoActivity) getActivity()).setPhotoTipVisibility(llEmpty.isShown() ? View.GONE : View.VISIBLE);
    }

    public List<MediaObj> getSelectedMedias(){
        return (serverPhotosAdapter == null || serverPhotosAdapter.getSelImgs() == null) ? new ArrayList<>() : serverPhotosAdapter.getSelImgs();
    }

    public int getContentType() {
        return contentType;
    }

//    @Subscribe
//    public void photoCountEvent(PhotoSelectCountEvent countEvent){
//        tvSelectCount.setText(Html.fromHtml(String.format(getString(R.string.select_server_photo_select_count), String.valueOf(countEvent.count))));
//    }

    /**
     * 设置选中的图片
     * @param mediaObjs
     */
    public void setMediaObjs(List<MediaObj> mediaObjs){
        this.mediaObjs = mediaObjs;
        if(serverPhotosAdapter != null){
            serverPhotosAdapter.setSelImgs((ArrayList<MediaObj>) mediaObjs);
            serverPhotosAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
