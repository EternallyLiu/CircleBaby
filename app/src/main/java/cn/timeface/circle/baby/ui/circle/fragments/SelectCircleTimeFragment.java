package cn.timeface.circle.baby.ui.circle.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import cn.timeface.circle.baby.ui.circle.adapters.CircleByTimeAdapter;
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
    private long circleId = 0;
    private List<QueryByCircleTimeObj> data = new ArrayList<>();

    public static SelectCircleTimeFragment newInstance(View.OnClickListener clickListener) {
        SelectCircleTimeFragment selectUserFragment = new SelectCircleTimeFragment();
        selectUserFragment.clickListener = clickListener;
        return selectUserFragment;
    }

    public SelectCircleTimeFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_circle_bytime, container, false);
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

    public List<QueryByCircleTimeObj> getData() {
        data.clear();
        ArrayList<CirclePhotoMonthObj> monthObjs = new ArrayList<>();
        monthObjs.add(new CirclePhotoMonthObj(18, "http://img1.timeface.cn/baby/d8e132d581eb8bb1b1c6a5a80b81d2f1.jpg", "08月"));
        monthObjs.add(new CirclePhotoMonthObj(19, "http://img1.timeface.cn/baby/d8e132d581eb8bb1b1c6a5a80b81d2f1.jpg", "09月"));
        monthObjs.add(new CirclePhotoMonthObj(20, "http://img1.timeface.cn/baby/d8e132d581eb8bb1b1c6a5a80b81d2f1.jpg", "10月"));
        data.add(new QueryByCircleTimeObj(57, monthObjs, "2016年"));

        ArrayList<CirclePhotoMonthObj> monthObjs2 = new ArrayList<>();
        monthObjs2.add(new CirclePhotoMonthObj(1, "http://img1.timeface.cn/baby/d8e132d581eb8bb1b1c6a5a80b81d2f1.jpg", "01月"));
        monthObjs2.add(new CirclePhotoMonthObj(2, "http://img1.timeface.cn/baby/d8e132d581eb8bb1b1c6a5a80b81d2f1.jpg", "02月"));
        monthObjs2.add(new CirclePhotoMonthObj(3, "http://img1.timeface.cn/baby/d8e132d581eb8bb1b1c6a5a80b81d2f1.jpg", "03月"));
        data.add(new QueryByCircleTimeObj(6, monthObjs2, "2015年"));
        return data;
    }
}
