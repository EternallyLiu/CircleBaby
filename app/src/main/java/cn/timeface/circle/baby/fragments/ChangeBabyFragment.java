package cn.timeface.circle.baby.fragments;


import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
import cn.timeface.circle.baby.activities.CreateBabyActivity;
import cn.timeface.circle.baby.activities.InviteCodeActivity;
import cn.timeface.circle.baby.activities.TabMainActivity;
import cn.timeface.circle.baby.adapters.ChangebabyAdapter;
import cn.timeface.circle.baby.api.models.objs.BabyListInfo;
import cn.timeface.circle.baby.fragments.base.BaseFragment;
import cn.timeface.circle.baby.utils.FastData;
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
        getActionBar().setDisplayHomeAsUpEnabled(true);


        adapter = new ChangebabyAdapter(getActivity(), new ArrayList<>());
        adapter.setOnClickListener(this);
        contentRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        contentRecyclerView.setAdapter(adapter);

        reqData();

        tvCreatebaby.setOnClickListener(this);
        tvFocusbaby.setOnClickListener(this);

        return view;
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

    private void setDataList(List<BabyListInfo> dataList) {
        adapter.getListData().clear();
        adapter.setListData(dataList);
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
            case R.id.tv_create:
                CreateBabyActivity.open(getActivity());
                break;
            case R.id.tv_focus:
                InviteCodeActivity.open(getActivity());
                break;
            case R.id.rl_baby:
                BabyListInfo info = (BabyListInfo) v.getTag(R.string.tag_ex);
                FastData.getUserInfo().setBabyObj(info.getBabyInfo());
                FastData.getUserInfo().setIsCreator(info.getOwner());

                TabMainActivity.open(getActivity());
                getActivity().finish();
                break;
        }
    }

}
