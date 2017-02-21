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
import cn.timeface.circle.baby.support.api.models.objs.MediaWrapObj;
import cn.timeface.circle.baby.support.api.models.responses.QueryPhotoResponse;
import cn.timeface.circle.baby.support.mvp.bases.BasePresenterFragment;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
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

    int contentType;
    String userId;
    SelectServerPhotosAdapter serverPhotosAdapter;
    String addressName;
    List<MediaWrapObj> mediaWrapObjs;

    public static ServerPhotoFragment newInstance(int contentType, String userId, String addressName, List<MediaWrapObj> mediaWrapObjs){
        ServerPhotoFragment fragment = new ServerPhotoFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("content_type", contentType);
        bundle.putString("user_id", userId);
        bundle.putString("address_name", addressName);
        bundle.putParcelableArrayList("media_wrap_objs", (ArrayList<? extends Parcelable>) mediaWrapObjs);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static ServerPhotoFragment newInstance(int contentType, String userId){
        return newInstance(contentType, userId, "", null);
    }

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
        if(contentType != TypeConstants.PHOTO_TYPE_LOCATION){
            reqData();
        } else {
            stateView.setVisibility(View.GONE);
            setListData(mediaWrapObjs);
        }
        return view;
    }

    private void reqData(){
        stateView.setVisibility(View.VISIBLE);
        stateView.loading();
        Observable<QueryPhotoResponse> photoResponseObservable = null;
        if (contentType == TypeConstants.PHOTO_TYPE_TIME) {
            photoResponseObservable = apiService.queryPhotoByTime(FastData.getBabyId());
        } else if(contentType == TypeConstants.PHOTO_TYPE_USER){
            photoResponseObservable = apiService.queryPhotoByUser(FastData.getBabyId(), userId);
        } else if(contentType == TypeConstants.PHOTO_TYPE_LABEL){
            photoResponseObservable = apiService.queryPhotoByLabel(FastData.getBabyId());
        }

        if(photoResponseObservable == null) return;
        photoResponseObservable.compose(SchedulersCompat.applyIoSchedulers())
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

    private void setListData(List<MediaWrapObj> data){
        if (serverPhotosAdapter == null) {
            rvContent.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            serverPhotosAdapter = new SelectServerPhotosAdapter(getActivity(), data, 99, contentType);
            rvContent.setAdapter(serverPhotosAdapter);
        } else {
            serverPhotosAdapter.setListData(data);
            serverPhotosAdapter.notifyDataSetChanged();
        }

        llEmpty.setVisibility(serverPhotosAdapter.getListData().size() > 0 ? View.GONE : View.VISIBLE);
    }

    public List<MediaObj> getSelectedMedias(){
        return serverPhotosAdapter.getSelImgs();
    }

    public int getContentType() {
        return contentType;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
