package cn.timeface.circle.baby.ui.circle.photo.fragments;

import android.os.Bundle;
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
import cn.timeface.circle.baby.support.api.models.objs.MediaWrapObj;
import cn.timeface.circle.baby.support.api.models.responses.QueryPhotoResponse;
import cn.timeface.circle.baby.support.mvp.bases.BasePresenterFragment;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.circle.bean.GetCirclePhotoByUserObj;
import cn.timeface.circle.baby.ui.circle.photo.adapters.CirclePhotosAdapter;
import cn.timeface.circle.baby.ui.circle.response.QueryCirclePhotoResponse;
import cn.timeface.circle.baby.ui.growth.activities.SelectServerPhotoActivity;
import cn.timeface.circle.baby.ui.timelines.Utils.LogUtil;
import cn.timeface.circle.baby.views.TFStateView;
import rx.Observable;

/**
 * 圈照片展示fragment
 * Created by lidonglin on 2017/3/20.
 */
public class CirclePhotoFragment extends BasePresenterFragment {

    @Bind(R.id.rv_content)
    RecyclerView rvContent;
    @Bind(R.id.tf_stateView)
    TFStateView stateView;
    @Bind(R.id.ll_empty)
    LinearLayout llEmpty;

    private int contentType;
    private long userId;
    private CirclePhotosAdapter circlePhotosAdapter;
    private int babyId;
    private long circleId;
    private long activityAlbumId;
    private String year;
    private String month;

    /**
     * 按发布者
     * @param contentType
     * @param circleId
     * @param userId
     * @return
     */
    public static CirclePhotoFragment newInstance(int contentType, long circleId, long userId) {
        CirclePhotoFragment fragment = new CirclePhotoFragment();
        Bundle bundle = new Bundle();
        bundle.putLong("circle_id",circleId);
        bundle.putInt("content_type", contentType);
        bundle.putLong("user_id", userId);
        fragment.setArguments(bundle);
        return fragment;
    }

    /**
     * 按@宝宝
     * @param contentType
     * @param circleId
     * @param babyId
     * @return
     */
    public static CirclePhotoFragment newInstance(int contentType, long circleId, int babyId) {
        CirclePhotoFragment fragment = new CirclePhotoFragment();
        Bundle bundle = new Bundle();
        bundle.putLong("circle_id",circleId);
        bundle.putInt("content_type", contentType);
        bundle.putInt("baby_id", babyId);
        fragment.setArguments(bundle);
        return fragment;
    }

    /**
     * 按时间
     * @param contentType
     * @param circleId
     * @param year
     * @param month
     * @return
     */
    public static CirclePhotoFragment newInstance(int contentType, long circleId, String year, String month) {
        CirclePhotoFragment fragment = new CirclePhotoFragment();
        Bundle bundle = new Bundle();
        bundle.putLong("circle_id",circleId);
        bundle.putInt("content_type", contentType);
        bundle.putString("year", year);
        bundle.putString("month", month);
        fragment.setArguments(bundle);
        return fragment;
    }

    /**
     * 按活动
     * @param contentType
     * @param activityAlbumId
     * @return
     */
    public static CirclePhotoFragment newInstance(int contentType, long activityAlbumId) {
        CirclePhotoFragment fragment = new CirclePhotoFragment();
        Bundle bundle = new Bundle();
        bundle.putLong("activityAlbumId",activityAlbumId);
        bundle.putInt("content_type", contentType);
        fragment.setArguments(bundle);
        return fragment;
    }

    public CirclePhotoFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_server_photo, container, false);
        ButterKnife.bind(this, view);
        circleId =   getArguments().getLong("circle_id");
        contentType = getArguments().getInt("content_type", TypeConstants.PHOTO_TYPE_TIME);
        userId = getArguments().getLong("user_id");
        babyId = getArguments().getInt("baby_id");
        activityAlbumId = getArguments().getLong("activityAlbumId");
        year = getArguments().getString("year");
        month = getArguments().getString("month");
        reqData();
        rvContent.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (Math.abs(dy) > 5 && getActivity() instanceof SelectServerPhotoActivity) {
                    ((SelectServerPhotoActivity) getActivity()).showSelectContentType(false);
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        return view;
    }

    private void reqData() {
        stateView.setVisibility(View.VISIBLE);
        stateView.loading();
        Observable<QueryCirclePhotoResponse<GetCirclePhotoByUserObj>> photoResponseObservable = null;
        if (contentType == TypeConstants.PHOTO_TYPE_ACTIVITY) {
            photoResponseObservable = apiService.getCirclePhotoByActivity(activityAlbumId);
        } else if (contentType == TypeConstants.PHOTO_TYPE_USER) {
            photoResponseObservable = apiService.getCirclePhotoByUser(circleId, userId);
        } else if (contentType == TypeConstants.PHOTO_TYPE_ATBABY) {
            photoResponseObservable = apiService.getCirclePhotoByAtBaby(circleId,babyId);
        }else if(contentType == TypeConstants.PHOTO_TYPE_TIME){
            photoResponseObservable = apiService.getCirclePhotoByYearMonth(circleId,year,month);
        }

        if (photoResponseObservable == null) return;
        addSubscription(
                photoResponseObservable.compose(SchedulersCompat.applyIoSchedulers())
                        .doOnUnsubscribe(() -> stateView.finish())
                        .subscribe(
                                response -> {
                                    if (response.success()) {
                                        List<GetCirclePhotoByUserObj> dataList = response.getDataList();
//                                        List<GetCirclePhotoByUserObj> objs = new ArrayList<>();
//                                        for (MediaWrapObj obj : dataList) {
//                                            objs.add(new GetCirclePhotoByUserObj(obj.getMediaList(), obj.getDate()));
//                                        }
                                        setListData(dataList);
                                    } else {
                                        ToastUtil.showToast(response.info);
                                    }
                                },
                                throwable -> {
                                    LogUtil.showError(throwable);
                                }
                        )
        );
    }

    private void setListData(List<GetCirclePhotoByUserObj> data) {
        if (circlePhotosAdapter == null) {
            rvContent.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            circlePhotosAdapter = new CirclePhotosAdapter(getActivity(), data, Integer.MAX_VALUE, contentType);
            rvContent.setAdapter(circlePhotosAdapter);
        } else {
            circlePhotosAdapter.setListData(data);
            circlePhotosAdapter.notifyDataSetChanged();
        }

        llEmpty.setVisibility(circlePhotosAdapter.getListData().size() > 0 ? View.GONE : View.VISIBLE);
        if (getActivity() instanceof SelectServerPhotoActivity)
            ((SelectServerPhotoActivity) getActivity()).setPhotoTipVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
