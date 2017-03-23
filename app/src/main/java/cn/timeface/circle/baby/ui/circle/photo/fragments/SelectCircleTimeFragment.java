package cn.timeface.circle.baby.ui.circle.photo.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.support.mvp.bases.BasePresenterFragment;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.circle.photo.adapters.CircleByTimeAdapter;
import cn.timeface.circle.baby.ui.circle.bean.CirclePhotoMonthObj;
import cn.timeface.circle.baby.ui.circle.bean.QueryByCircleTimeObj;

/**
 * 圈照片按时间fragment
 * Created by lidonglin on 2017/3/17.
 */
public class SelectCircleTimeFragment extends BasePresenterFragment {

    @Bind(R.id.rv_content)
    RecyclerView rvContent;

    View.OnClickListener clickListener;
    CircleByTimeAdapter circleByTimeAdapter;
    @Bind(R.id.tv_count)
    TextView tvCount;
    private long circleId;
    private List<QueryByCircleTimeObj> data = new ArrayList<>();

    public static SelectCircleTimeFragment newInstance(View.OnClickListener clickListener, long circleId) {
        SelectCircleTimeFragment selectUserFragment = new SelectCircleTimeFragment();
        selectUserFragment.clickListener = clickListener;
        Bundle bundle = new Bundle();
        bundle.putLong("circle_id", circleId);
        selectUserFragment.setArguments(bundle);
        return selectUserFragment;
    }

    public SelectCircleTimeFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_circle_bytime, container, false);
        ButterKnife.bind(this, view);
        circleId = getArguments().getLong("circle_id");
        reqData();
        return view;
    }

    private void reqData() {
        apiService.queryByCircleTime(circleId)
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(
                        response -> {
                            if (response.success()) {
                                tvCount.setText(getString(R.string.circlephoto_count, response.getTotalMediaCount()));
                                setData(response.getDataList());
                            } else {
                                ToastUtil.showToast(response.getInfo());
                            }
                        },
                        throwable -> {
                            Log.e(TAG, throwable.getLocalizedMessage());
                        }
                );
    }

    private void setData(List<QueryByCircleTimeObj> objs) {
        if (circleByTimeAdapter == null) {
            rvContent.setLayoutManager(new LinearLayoutManager(getActivity()));
            circleByTimeAdapter = new CircleByTimeAdapter(getActivity(), objs, clickListener);
            rvContent.setAdapter(circleByTimeAdapter);
        } else {
            circleByTimeAdapter.setListData(objs);
            circleByTimeAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

}
