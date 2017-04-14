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
import cn.timeface.circle.baby.support.mvp.bases.BasePresenterFragment;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.circle.bean.CircleUserInfo;
import cn.timeface.circle.baby.ui.circle.bean.QueryByCircleUserObj;
import cn.timeface.circle.baby.ui.circle.photo.adapters.CircleByUserAdapter;

/**
 * 圈照片按发布人fragment
 * Created by lidonglin on 2017/3/17.
 */
public class SelectCircleUserFragment extends BasePresenterFragment {

    @Bind(R.id.rv_content)
    RecyclerView rvContent;

    View.OnClickListener clickListener;
    CircleByUserAdapter circleByUserAdapter;
    @Bind(R.id.ll_no_data)
    LinearLayout llNoData;
    private long circleId;
    private List<QueryByCircleUserObj> data = new ArrayList<>();

    public static SelectCircleUserFragment newInstance(View.OnClickListener clickListener, long circleId) {
        SelectCircleUserFragment selectUserFragment = new SelectCircleUserFragment();
        selectUserFragment.clickListener = clickListener;
        Bundle bundle = new Bundle();
        bundle.putLong("circle_id", circleId);
        selectUserFragment.setArguments(bundle);
        return selectUserFragment;
    }

    public SelectCircleUserFragment() {
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
        apiService.queryByCircleUser(circleId)
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

    private void setData(List<QueryByCircleUserObj> objs) {
        if(objs.size() > 0){
            llNoData.setVisibility(View.GONE);
        }else{
            llNoData.setVisibility(View.VISIBLE);
            return;
        }
        if (circleByUserAdapter == null) {
            rvContent.setLayoutManager(new GridLayoutManager(getActivity(), 2));
            circleByUserAdapter = new CircleByUserAdapter(getActivity(), objs, clickListener);
            rvContent.setAdapter(circleByUserAdapter);
        } else {
            circleByUserAdapter.setListData(objs);
            circleByUserAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public List<QueryByCircleUserObj> getData() {
        data.clear();
        CircleUserInfo user = new CircleUserInfo("http://img1.timeface.cn/baby/d8e132d581eb8bb1b1c6a5a80b81d2f1.jpg", "豆豆的妈妈", 0, 0, 0);
        QueryByCircleUserObj queryByCircleUserObj = new QueryByCircleUserObj(user, 100);
        data.add(queryByCircleUserObj);
        data.add(queryByCircleUserObj);
        data.add(queryByCircleUserObj);
        data.add(queryByCircleUserObj);
        return data;
    }
}
