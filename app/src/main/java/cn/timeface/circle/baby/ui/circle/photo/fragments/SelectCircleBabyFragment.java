package cn.timeface.circle.baby.ui.circle.photo.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
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
import cn.timeface.circle.baby.support.api.models.objs.BabyObj;
import cn.timeface.circle.baby.support.mvp.bases.BasePresenterFragment;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.circle.bean.QueryByCircleBabyObj;
import cn.timeface.circle.baby.ui.circle.photo.adapters.CircleByBabyAdapter;

/**
 * 圈照片按圈中宝宝fragment
 * Created by lidonglin on 2017/3/18.
 */
public class SelectCircleBabyFragment extends BasePresenterFragment {

    @Bind(R.id.rv_content)
    RecyclerView rvContent;

    View.OnClickListener clickListener;
    CircleByBabyAdapter circleByBabyAdapter;
    @Bind(R.id.ll_no_data)
    LinearLayout llNoData;
    private long circleId;
    private List<QueryByCircleBabyObj> data = new ArrayList<>();

    public static SelectCircleBabyFragment newInstance(View.OnClickListener clickListener, long circleId) {
        SelectCircleBabyFragment selectUserFragment = new SelectCircleBabyFragment();
        selectUserFragment.clickListener = clickListener;
        Bundle bundle = new Bundle();
        bundle.putLong("circle_id", circleId);
        selectUserFragment.setArguments(bundle);
        return selectUserFragment;
    }

    public SelectCircleBabyFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_user, container, false);
        ButterKnife.bind(this, view);
        circleId = getArguments().getLong("circle_id");
        reqData();
        return view;
    }

    private void reqData() {
        apiService.queryByCircleBaby(circleId)
                .compose(SchedulersCompat.applyIoSchedulers())
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
                );
//        setData(getData());
    }

    private void setData(List<QueryByCircleBabyObj> objs) {
        if(objs.size()>0){
            llNoData.setVisibility(View.VISIBLE);
            return;
        }
        if (circleByBabyAdapter == null) {
            rvContent.setLayoutManager(new GridLayoutManager(getActivity(), 2));
            circleByBabyAdapter = new CircleByBabyAdapter(getActivity(), objs, clickListener);
            rvContent.setAdapter(circleByBabyAdapter);
        } else {
            circleByBabyAdapter.setListData(objs);
            circleByBabyAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public List<QueryByCircleBabyObj> getData() {
        data.clear();
        BabyObj babyObj = new BabyObj();
        babyObj.setAvatar("http://img1.timeface.cn/baby/d8e132d581eb8bb1b1c6a5a80b81d2f1.jpg");
        babyObj.setName("豆豆");
        QueryByCircleBabyObj queryByCircleBabyObj = new QueryByCircleBabyObj(babyObj, 100);
        data.add(queryByCircleBabyObj);
        data.add(queryByCircleBabyObj);
        data.add(queryByCircleBabyObj);
        data.add(queryByCircleBabyObj);
        return data;
    }
}
