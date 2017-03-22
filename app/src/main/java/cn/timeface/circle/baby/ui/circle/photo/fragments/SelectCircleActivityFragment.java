package cn.timeface.circle.baby.ui.circle.photo.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;
import com.yqritc.recyclerviewflexibledivider.VerticalDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.support.mvp.bases.BasePresenterFragment;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.circle.bean.CircleActivityAlbumObj;
import cn.timeface.circle.baby.ui.circle.photo.adapters.CircleActivityAdapter;
import cn.timeface.circle.baby.ui.circle.photo.bean.QueryByCircleActivityObj;

/**
 * 选择圈活动fragment
 * Created by lidonglin on 2017/3/15.
 */
public class SelectCircleActivityFragment extends BasePresenterFragment implements View.OnClickListener {

    @Bind(R.id.rv_content)
    RecyclerView rvContent;

    View.OnClickListener clickListener;
    CircleActivityAdapter circleActivityAdapter;
    @Bind(R.id.et_search_activity)
    EditText etSearch;
    @Bind(R.id.iv_search)
    ImageView ivSearch;
    @Bind(R.id.rl_search)
    RelativeLayout rlSearch;
    @Bind(R.id.error_title)
    TextView tvMsg;
    @Bind(R.id.ll_no_data)
    LinearLayout llNoData;
    private List<CircleActivityAlbumObj> data = new ArrayList<>();
    private long circleId;

    public static SelectCircleActivityFragment newInstance(View.OnClickListener clickListener, long circleId) {
        SelectCircleActivityFragment selectCircleActivityFragment = new SelectCircleActivityFragment();
        selectCircleActivityFragment.clickListener = clickListener;
        Bundle bundle = new Bundle();
        bundle.putLong("circle_id", circleId);
        selectCircleActivityFragment.setArguments(bundle);
        return selectCircleActivityFragment;
    }

    public SelectCircleActivityFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_circle_activity, container, false);
        ButterKnife.bind(this, view);
        circleId = getArguments().getLong("circle_id");
        ivSearch.setOnClickListener(this);
        reqData();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        reqData();
    }

    private void reqData() {
        apiService.queryByCircleActivity(circleId)
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(
                        response -> {
                            if (response.success()) {
                                if (response.getDataList().size() > 0) {
                                    llNoData.setVisibility(View.GONE);
                                    setData(response.getDataList());
                                } else {
                                    llNoData.setVisibility(View.VISIBLE);
                                }

                            } else {
                                ToastUtil.showToast(response.getInfo());
                            }
                        },
                        throwable -> {
                            Log.e(TAG, throwable.getLocalizedMessage());
                        }
                );
    }

    private void setData(List<QueryByCircleActivityObj> AlbumObjs) {
        if (circleActivityAdapter == null) {
            rvContent.setLayoutManager(new GridLayoutManager(getActivity(), 2));
            rvContent.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity()).
                    sizeResId(R.dimen.view_space_normal).
                    color(android.R.color.transparent).
                    build());
            rvContent.addItemDecoration(new VerticalDividerItemDecoration.Builder(getActivity()).
                    sizeResId(R.dimen.view_space_normal).
                    color(android.R.color.transparent).
                    build());
            circleActivityAdapter = new CircleActivityAdapter(getActivity(), AlbumObjs, clickListener);
            rvContent.setAdapter(circleActivityAdapter);
        } else {
            circleActivityAdapter.setListData(AlbumObjs);
            circleActivityAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.iv_search) {
            String key = etSearch.getText().toString();
            apiService.searchCircleActivity(circleId, key)
                    .compose(SchedulersCompat.applyIoSchedulers())
                    .subscribe(
                            response -> {
                                if (response.success()) {
                                    if (response.getDataList().size() > 0) {
                                        setData(response.getDataList());
                                        llNoData.setVisibility(View.GONE);
                                    } else {
                                        llNoData.setVisibility(View.VISIBLE);
                                    }
                                } else {
                                    ToastUtil.showToast(response.getInfo());
                                }
                            },
                            throwable -> {
                                Log.e(TAG, throwable.getLocalizedMessage());
                            }
                    );
        }

    }
}
