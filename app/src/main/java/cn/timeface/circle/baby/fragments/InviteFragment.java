package cn.timeface.circle.baby.fragments;


import android.content.Intent;
import android.net.Uri;
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

import java.net.URLEncoder;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.BuildConfig;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.fragments.base.BaseFragment;
import cn.timeface.circle.baby.support.api.models.responses.InviteResponse;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.GlideUtil;
import cn.timeface.circle.baby.support.utils.Remember;
import cn.timeface.circle.baby.support.utils.ShareSdkUtil;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.views.ShareDialog;

public class InviteFragment extends BaseFragment implements View.OnClickListener {


    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.tv_code)
    TextView tvCode;
    @Bind(R.id.iv)
    ImageView iv;
    @Bind(R.id.btn_wx)
    Button btnWx;
    @Bind(R.id.btn_qq)
    Button btnQq;
    @Bind(R.id.btn_sms)
    Button btnSms;
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
        initActionBar();
        title.setText("邀请");
        reqData();
        btnWx.setOnClickListener(this);
        btnQq.setOnClickListener(this);
        btnSms.setOnClickListener(this);
        return view;
    }


    private void reqData() {
        apiService.inviteFamily(URLEncoder.encode(relationName))
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(inviteResponse -> {
                    this.inviteResponse = inviteResponse;
                    tvCode.setText(inviteResponse.getInviteCode());
                    int width = Remember.getInt("width", 0);
                    ViewGroup.LayoutParams layoutParams = iv.getLayoutParams();
                    layoutParams.width = (int) (width * 1.8);
                    layoutParams.height = (int) (width * 1.8);
                    iv.setLayoutParams(layoutParams);
                    GlideUtil.displayImage(inviteResponse.getCodeUrl(), iv);
                }, throwable -> {
                    Log.e(TAG, "queryBabyFamilyList:");
                    throwable.printStackTrace();
                });

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onClick(View v) {
        String content = "我在成长印记记录了" + FastData.getBabyNickName() + "的成长，快来一起关注" + FastData.getBabyNickName() + "的成长瞬间！";
        String url = "";
        if (inviteResponse != null) {
            String inviteCode = inviteResponse.getInviteCode();
            url = BuildConfig.API_URL + getActivity().getString(R.string.share_url_invite, FastData.getBabyId()) + "&inviteCode=" + inviteCode;
        }
        switch (v.getId()) {
            case R.id.btn_wx:
                new ShareDialog(getActivity()).shareToWx("成长印记", content,
                        ShareSdkUtil.getImgStrByResource(getActivity(),R.drawable.ic_laucher_quadrate), "", url);
                break;
            case R.id.btn_qq:
                new ShareDialog(getActivity()).shareToQQ("成长印记", content,
                        ShareSdkUtil.getImgStrByResource(getActivity(),R.drawable.ic_laucher_quadrate), "", url);
                break;
            case R.id.btn_sms:
                String sms_body = content + url;
                Uri smsToUri = Uri.parse("smsto:");
                Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
                intent.putExtra("sms_body", sms_body);
                startActivity(intent);
                break;
        }
    }
}
