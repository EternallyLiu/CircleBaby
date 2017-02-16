package cn.timeface.circle.baby.ui.growth.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.support.api.models.objs.UserWrapObj;
import cn.timeface.circle.baby.support.mvp.bases.BasePresenterFragment;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.growth.adapters.UsersAdapter;

/**
 * 选择用户fragment
 * author : YW.SUN Created on 2017/2/15
 * email : sunyw10@gmail.com
 */
public class SelectUserFragment extends BasePresenterFragment {

    @Bind(R.id.rv_content)
    RecyclerView rvContent;

    View.OnClickListener clickListener;
    UsersAdapter usersAdapter;

    public static SelectUserFragment newInstance(View.OnClickListener clickListener){
        SelectUserFragment selectUserFragment = new SelectUserFragment();
        selectUserFragment.clickListener = clickListener;
        return selectUserFragment;
    }

    public SelectUserFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_user, container, false);
        ButterKnife.bind(this, view);

        reqData();
        return view;
    }

    private void reqData(){
        apiService.queryUsers()
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
                );
    }

    private void setData(List<UserWrapObj> userWrapObjs){
        if(usersAdapter == null){
            rvContent.setLayoutManager(new GridLayoutManager(getActivity(), 3));
            usersAdapter = new UsersAdapter(getActivity(), userWrapObjs, clickListener);
            rvContent.setAdapter(usersAdapter);
        } else {
            usersAdapter.setListData(userWrapObjs);
            usersAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
