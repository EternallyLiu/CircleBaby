package cn.timeface.circle.baby.fragments;


import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.FragmentBridgeActivity;
import cn.timeface.circle.baby.api.models.objs.FamilyMemberInfo;
import cn.timeface.circle.baby.fragments.base.BaseFragment;
import cn.timeface.circle.baby.utils.DateUtil;
import cn.timeface.circle.baby.utils.FastData;
import cn.timeface.circle.baby.utils.GlideUtil;
import cn.timeface.circle.baby.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.views.ShareDialog;
import cn.timeface.common.utils.ShareSdkUtil;
import de.hdodenhof.circleimageview.CircleImageView;

public class FamilyMemberFragment extends BaseFragment {


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
    @Bind(R.id.iv_cover_bg)
    ImageView ivCoverBg;
    @Bind(R.id.iv_creator)
    ImageView ivCreator;
    @Bind(R.id.ll_familymember)
    LinearLayout llFamilymember;
    @Bind(R.id.ll_familymember_none)
    LinearLayout llFamilymemberNone;
    public FamilyMemberInfo creator;
    public List<String> relationNames = new ArrayList<>();

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
        setHasOptionsMenu(true);
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
        ActionBar actionBar = getActionBar();
        if(actionBar!=null){
            actionBar.setTitle(FastData.getBabyName() + "一家");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        reqData();
        return view;
    }

    private void initFoot() {
        for(String s : relationNames){
            View view = initView2(s);
            llFamilymemberNone.addView(view);
        }
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
            }else {
                View view = initView(info);
                llFamilymember.addView(view);
            }
        }
        GlideUtil.displayImage(creator.getUserInfo().getAvatar(), ivAvatar);
        tvRelation.setText(creator.getUserInfo().getRelationName());
        tvCount.setText("来过" + creator.getCount() + "次");
        tvTime.setText("最近：" + DateUtil.formatDate(creator.getTime()));
        initFoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private View initView(FamilyMemberInfo info) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.item_family_member, null);
        ImageView ivAvatar = (ImageView) view.findViewById(R.id.iv_avatar);
        TextView tvName = (TextView) view.findViewById(R.id.tv_name);
        TextView tvCount = (TextView) view.findViewById(R.id.tv_count);
        TextView tvTime = (TextView) view.findViewById(R.id.tv_time);
        GlideUtil.displayImage(info.getUserInfo().getAvatar(), ivAvatar);
        tvName.setText(info.getUserInfo().getRelationName());
        tvCount.setText("来过" + info.getCount() + "次");
        tvTime.setText("最近" + DateUtil.formatDate("MM-dd HH:mm", info.getTime()));
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentBridgeActivity.openFamilyMemberInfoFragment(getActivity(), info.getUserInfo());
            }
        });
        return view;
    }

    private View initView2(String s) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.item_family_member, null);
        ImageView ivAvatar = (ImageView) view.findViewById(R.id.iv_avatar);
        TextView tvName = (TextView) view.findViewById(R.id.tv_name);
        TextView tvCount = (TextView) view.findViewById(R.id.tv_count);
        TextView tvTime = (TextView) view.findViewById(R.id.tv_time);
        ivAvatar.setImageResource(R.drawable.familyavatar);
        tvName.setText(s);
        tvCount.setVisibility(View.GONE);
        tvTime.setText("未加入");
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentBridgeActivity.openInviteFragment(getActivity(), s);
            }
        });
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_invite, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.invite){
            FragmentBridgeActivity.openInviteFragment(getActivity(), "");
        }
        return super.onOptionsItemSelected(item);
    }
}
