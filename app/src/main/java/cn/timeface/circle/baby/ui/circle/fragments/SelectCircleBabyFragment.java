package cn.timeface.circle.baby.ui.circle.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.support.api.models.objs.BabyObj;
import cn.timeface.circle.baby.support.mvp.bases.BasePresenterFragment;
import cn.timeface.circle.baby.ui.circle.adapters.CircleByBabyAdapter;
import cn.timeface.circle.baby.ui.circle.bean.QueryByCircleBabyObj;

/**
 * 圈照片按圈中宝宝fragment
 * Created by lidonglin on 2017/3/18.
 */
public class SelectCircleBabyFragment extends BasePresenterFragment {

    @Bind(R.id.rv_content)
    RecyclerView rvContent;

    View.OnClickListener clickListener;
    CircleByBabyAdapter circleByBabyAdapter;
    private long circleId = 0;
    private List<QueryByCircleBabyObj> data = new ArrayList<>();

    public static SelectCircleBabyFragment newInstance(View.OnClickListener clickListener) {
        SelectCircleBabyFragment selectUserFragment = new SelectCircleBabyFragment();
        selectUserFragment.clickListener = clickListener;
        return selectUserFragment;
    }

    public SelectCircleBabyFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_user, container, false);
        ButterKnife.bind(this, view);

        reqData();
        return view;
    }

    private void reqData() {
        /*apiService.queryByCircleUser(circleId)
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(
                        response -> {
                            if(response.success()){
                                setData(response.getDataList());
                            } else {
                                ToastUtil.showToast(response.getInfo());
                            }
                        },
                        throwable -> {
                            Log.e(TAG, throwable.getLocalizedMessage());
                        }
                );*/
        setData(getData());
    }

    private void setData(List<QueryByCircleBabyObj> objs) {
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
