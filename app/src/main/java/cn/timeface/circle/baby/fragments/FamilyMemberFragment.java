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
import cn.timeface.circle.baby.activities.FragmentBridgeActivity;
import cn.timeface.circle.baby.adapters.FamilyMemberAdapter;
import cn.timeface.circle.baby.adapters.FamilyMemberAdapter2;
import cn.timeface.circle.baby.api.models.objs.FamilyMemberInfo;
import cn.timeface.circle.baby.api.models.objs.UserObj;
import cn.timeface.circle.baby.fragments.base.BaseFragment;
import cn.timeface.circle.baby.utils.DateUtil;
import cn.timeface.circle.baby.utils.FastData;
import cn.timeface.circle.baby.utils.GlideUtil;
import cn.timeface.circle.baby.utils.Remember;
import cn.timeface.circle.baby.utils.rxutils.SchedulersCompat;
import de.hdodenhof.circleimageview.CircleImageView;

public class FamilyMemberFragment extends BaseFragment implements View.OnClickListener {


    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.tv_invite)
    TextView tvInvite;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.iv_avatar)
    CircleImageView ivAvatar;
    @Bind(R.id.tv_relation)
    TextView tvRelation;
    @Bind(R.id.tv_count)
    TextView tvCount;
    @Bind(R.id.tv_time)
    TextView tvTime;
    @Bind(R.id.content_recycler_view)
    RecyclerView contentRecyclerView;
    @Bind(R.id.content_recycler_view2)
    RecyclerView contentRecyclerView2;
    private FamilyMemberAdapter adapter;
    public FamilyMemberInfo creator;
    public List<String> relationNames = new ArrayList<>();
    private FamilyMemberAdapter2 adapter2;

    public FamilyMemberFragment() {
    }

    public static FamilyMemberFragment newInstance() {
        FamilyMemberFragment fragment = new FamilyMemberFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        relationNames.add("爸爸");
        relationNames.add("妈妈");
        relationNames.add("爷爷");
        relationNames.add("奶奶");
        relationNames.add("外公");
        relationNames.add("外婆");
        relationNames.add("其他成员");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_family, container, false);
        ButterKnife.bind(this, view);
        setActionBar(toolbar);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        tvTitle.setText(FastData.getBabyName() + "一家");
        adapter = new FamilyMemberAdapter(getActivity(), new ArrayList<>());
        contentRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        contentRecyclerView.setAdapter(adapter);

        reqData();

        tvInvite.setOnClickListener(this);

        return view;
    }

    private void initFoot() {
        adapter2 = new FamilyMemberAdapter2(getActivity(), relationNames);
        contentRecyclerView2.setLayoutManager(new LinearLayoutManager(getActivity()));
        contentRecyclerView2.setAdapter(adapter2);
    }

    private void reqData() {
        apiService.queryBabyFamilyLoginInfoList()
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(familyListResponse -> {
                    setDataList(familyListResponse.getDataList());
                }, throwable -> {
                    Log.e(TAG, "queryBabyFamilyList:");
                });

    }

    private void setDataList(List<FamilyMemberInfo> dataList) {
        for (FamilyMemberInfo info : dataList) {
            String relationName = info.getUserInfo().getRelationName();
            if (relationNames.contains(relationName)) {
                relationNames.remove(relationName);
            }
            if (info.getUserInfo().getIsCreator() == 1) {
                creator = info;
            }
        }
        GlideUtil.displayImage(creator.getUserInfo().getAvatar(), ivAvatar);
        tvRelation.setText(creator.getUserInfo().getRelationName());
        tvCount.setText("来过" + creator.getCount() + "次");
        tvTime.setText("最近：" + DateUtil.formatDate(creator.getTime()));
        dataList.remove(creator);
        adapter.setListData(dataList);
        adapter.notifyDataSetChanged();
        initFoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_invite:
                FragmentBridgeActivity.openInviteFragment(getActivity(), "");
                break;
        }
    }
}
