package cn.timeface.circle.baby.fragments;


import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.CreateBabyActivity;
import cn.timeface.circle.baby.activities.InviteCodeActivity;
import cn.timeface.circle.baby.activities.TabMainActivity;
import cn.timeface.circle.baby.adapters.ChangebabyAdapter;
import cn.timeface.circle.baby.api.models.objs.BabyObj;
import cn.timeface.circle.baby.api.models.objs.UserObj;
import cn.timeface.circle.baby.events.HomeRefreshEvent;
import cn.timeface.circle.baby.fragments.base.BaseFragment;
import cn.timeface.circle.baby.utils.FastData;
import cn.timeface.circle.baby.utils.Remember;
import cn.timeface.circle.baby.utils.rxutils.SchedulersCompat;

public class ChangeBabyFragment extends BaseFragment implements View.OnClickListener {


    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.tv_createbaby)
    TextView tvCreatebaby;
    @Bind(R.id.tv_focusbaby)
    TextView tvFocusbaby;
    @Bind(R.id.content_recycler_view)
    RecyclerView contentRecyclerView;
    private ChangebabyAdapter adapter;


    public ChangeBabyFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_changebaby, container, false);
        ButterKnife.bind(this, view);
        setActionBar(toolbar);
        ActionBar actionBar = getActionBar();
        if(actionBar!=null){
            actionBar.setTitle("选择宝宝");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        adapter = new ChangebabyAdapter(getActivity(), new ArrayList<>());
        adapter.setOnClickListener(this);
        contentRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        contentRecyclerView.setAdapter(adapter);

        reqData();

        tvCreatebaby.setOnClickListener(this);
        tvFocusbaby.setOnClickListener(this);

        return view;
    }

    @Override
    public void onResume() {
        reqData();
        super.onResume();
    }

    private void reqData() {
        apiService.queryBabyInfoList()
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(babyInfoListResponse -> {
                    setDataList(babyInfoListResponse.getDataList());
                }, throwable -> {
                    Log.e(TAG, "queryBabyInfoList:");
                });

    }

    private void setDataList(List<UserObj> dataList) {
        ArrayList<UserObj> userObjs = new ArrayList<>();
        for (UserObj user : dataList){
            if(!TextUtils.isEmpty(user.getUserId())){
                userObjs.add(user);
            }
        }
        adapter.setListData(userObjs);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_createbaby:
                CreateBabyActivity.open(getActivity(),false);
                break;
            case R.id.tv_focusbaby:
                InviteCodeActivity.open(getActivity());
                break;
            case R.id.rl_baby:
                UserObj info = (UserObj) v.getTag(R.string.tag_ex);
                FastData.setUserInfo(info);
                Gson gson = new Gson();
                FastData.putString("userObj", gson.toJson(info));
                apiService.updateLoginInfo()
                        .compose(SchedulersCompat.applyIoSchedulers())
                        .subscribe(response -> {
                            if (response.success()) {
                                Remember.putBoolean("showtimelinehead", true);
                                EventBus.getDefault().post(new HomeRefreshEvent());
                                getActivity().finish();
                            }
                        }, throwable -> {
                            Log.e(TAG, "updateLoginInfo:");
                        });
                break;
        }
    }

}
