package cn.timeface.circle.baby.ui.circle.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;
import com.yqritc.recyclerviewflexibledivider.VerticalDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.support.api.models.objs.UserWrapObj;
import cn.timeface.circle.baby.support.mvp.bases.BasePresenterFragment;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.circle.adapters.CircleActivityAdapter;
import cn.timeface.circle.baby.ui.circle.bean.CircleActivityAlbumObj;
import cn.timeface.circle.baby.ui.growth.adapters.UsersAdapter;

/**
 * 选择圈活动fragment
 * Created by lidonglin on 2017/3/15.
 */
public class SelectCircleActivityFragment extends BasePresenterFragment {

    @Bind(R.id.rv_content)
    RecyclerView rvContent;

    View.OnClickListener clickListener;
    CircleActivityAdapter circleActivityAdapter;
    @Bind(R.id.et_search_activity)
    EditText etSearch;
    private List<CircleActivityAlbumObj> data = new ArrayList<>();

    public static SelectCircleActivityFragment newInstance(View.OnClickListener clickListener) {
        SelectCircleActivityFragment selectCircleActivityFragment = new SelectCircleActivityFragment();
        selectCircleActivityFragment.clickListener = clickListener;
        return selectCircleActivityFragment;
    }

    public SelectCircleActivityFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_circle_activity, container, false);
        ButterKnife.bind(this, view);

        reqData();
        return view;
    }

    private void reqData() {
        /*apiService.queryUsers()
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
                );*/
        setData(getData());
    }

    private void setData(List<CircleActivityAlbumObj> AlbumObjs) {
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

    public List<CircleActivityAlbumObj> getData() {
        data.clear();
        data.add(new CircleActivityAlbumObj(0,"小博士学习班",108));
        data.add(new CircleActivityAlbumObj(1,"小学士学习班",109));
        data.add(new CircleActivityAlbumObj(2,"小硕士学习班",110));
        data.add(new CircleActivityAlbumObj(3,"小博士后学习班",111));
        return data;
    }
}
