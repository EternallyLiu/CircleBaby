package cn.timeface.circle.baby.fragments;


import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.adapters.MilestoneAdapter;
import cn.timeface.circle.baby.api.models.objs.FamilyMemberInfo;
import cn.timeface.circle.baby.api.models.objs.MilestoneTimeObj;
import cn.timeface.circle.baby.api.models.objs.TimeLineObj;
import cn.timeface.circle.baby.fragments.base.BaseFragment;
import cn.timeface.circle.baby.utils.ToastUtil;
import cn.timeface.circle.baby.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.views.ShareDialog;
import cn.timeface.common.utils.ShareSdkUtil;

public class MilestoneFragment extends BaseFragment{

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.content_recycler_view)
    RecyclerView contentRecyclerView;
    private MilestoneAdapter adapter;
    public FamilyMemberInfo creator;

    public MilestoneFragment() {
    }

    public static MilestoneFragment newInstance() {
        MilestoneFragment fragment = new MilestoneFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_milestone, container, false);
        ButterKnife.bind(this, view);
        setActionBar(toolbar);
        getActionBar().setTitle("里程碑");
        getActionBar().setDisplayHomeAsUpEnabled(true);


        adapter = new MilestoneAdapter(getActivity(), new ArrayList<>());
        contentRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        contentRecyclerView.setAdapter(adapter);

        reqData();

        return view;
    }

    private void reqData() {
        apiService.milestone()
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(milestoneTimeResponse -> {
                    setDataList(milestoneTimeResponse.getDataList());
                }, throwable -> {
                    Log.e(TAG, "milestone:");
                });

    }

    private void setDataList(List<MilestoneTimeObj> dataList) {
        ArrayList<MilestoneTimeObj> milestoneTimeObjs = new ArrayList<>();
        for (MilestoneTimeObj obj : dataList){
            if(!TextUtils.isEmpty(obj.getImgUrl())){
                milestoneTimeObjs.add(obj);
            }
        }
        adapter.setListData(milestoneTimeObjs);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_milestone, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        new ShareDialog(getActivity()).share("宝宝时光，让家庭充满和谐，让教育充满温馨。", "宝宝时光，让家庭充满和谐，让教育充满温馨。",
                ShareSdkUtil.getImgStrByResource(getActivity(), R.mipmap.ic_launcher),
                ShareSdkUtil.getImgStrByResource(getActivity(), R.drawable.setting_sina_share_img),
                "http://www.timeface.cn/tf_mobile/download.html");

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private List<MilestoneTimeObj> mokeData(List<MilestoneTimeObj> dataList) {
        MilestoneTimeObj item = new MilestoneTimeObj(1465193897000L, "http://img.pconline.com.cn/images/upload/upc/tx/itbbs/1402/27/c4/31612517_1393474458218_mthumb.jpg", "第一只兔子", new TimeLineObj());
        dataList.add(item);
        dataList.add(item);
        return dataList;
    }
}
