package cn.timeface.circle.baby.fragments;


import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.api.models.responses.InviteResponse;
import cn.timeface.circle.baby.fragments.base.BaseFragment;
import cn.timeface.circle.baby.utils.FastData;
import cn.timeface.circle.baby.utils.GlideUtil;
import cn.timeface.circle.baby.utils.Remember;
import cn.timeface.circle.baby.utils.ShareSdkUtil;
import cn.timeface.circle.baby.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.views.ShareDialog;

public class InviteFragment extends BaseFragment implements View.OnClickListener {


    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.tv_code)
    TextView tvCode;
    @Bind(R.id.btn_wx)
    Button btnWx;
    @Bind(R.id.btn_qq)
    Button btnQq;
    @Bind(R.id.btn_sms)
    Button btnSms;
    @Bind(R.id.iv)
    ImageView iv;
    private String relationName;
    public InviteResponse inviteResponse;

    public InviteFragment() {
    }

    public static InviteFragment newInstance() {
        InviteFragment fragment = new InviteFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        relationName = getArguments().getString("relationName");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_invite, container, false);
        ButterKnife.bind(this, view);
        setActionBar(toolbar);
        ActionBar actionBar = getActionBar();
        if(actionBar!=null){
            actionBar.setTitle("邀请");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        reqData();
        btnWx.setOnClickListener(this);
        btnQq.setOnClickListener(this);
        btnSms.setOnClickListener(this);
        return view;
    }


    private void reqData() {
        apiService.inviteFamily(relationName)
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(inviteResponse -> {
                    this.inviteResponse = inviteResponse;
                    tvCode.setText(inviteResponse.getInviteCode() + "");
                    GlideUtil.displayImage(inviteResponse.getCodeUrl(), iv);
                    int width = Remember.getInt("width", 0);
                    ViewGroup.LayoutParams layoutParams = iv.getLayoutParams();
                    layoutParams.width = (int) (width * 1.8);
                    layoutParams.height = (int) (width * 1.8);
                    iv.setLayoutParams(layoutParams);
                }, throwable -> {
                    Log.e(TAG, "queryBabyFamilyList:");
                });

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_wx:
//                new ShareDialog(getActivity()).shareToWx("亲子好习惯，让家庭充满和谐，让教育充满温馨。", "邀请码为:" + inviteResponse.getInviteCode(),
//                        ShareSdkUtil.getImgStrByResource(getActivity(), R.drawable.ic_launcher),
//                        ShareSdkUtil.getImgStrByResource(getActivity(), R.drawable.ic_login_wechat),
//                        inviteResponse.getInviteUrl());
                new ShareDialog(getActivity()).shareToWx("成长印记", "我在成长印记记录了"+ FastData.getBabyName() +"的成长，快来一起关注涵涵的成长瞬间。",
                        ShareSdkUtil.getImgStrByResource(getActivity(), R.mipmap.ic_launcher),
                        ShareSdkUtil.getImgStrByResource(getActivity(), R.mipmap.ic_launcher),
                        inviteResponse.getInviteUrl());
                break;
            case R.id.btn_qq:
                new ShareDialog(getActivity()).shareToQQ("成长印记", "我在成长印记记录了"+ FastData.getBabyName() +"的成长，快来一起关注涵涵的成长瞬间。",
                        ShareSdkUtil.getImgStrByResource(getActivity(), R.mipmap.ic_launcher),
                        ShareSdkUtil.getImgStrByResource(getActivity(), R.mipmap.ic_launcher),
                        inviteResponse.getInviteUrl());
                break;
            case R.id.btn_sms:
                break;
        }
    }
}
