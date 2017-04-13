package cn.timeface.circle.baby.ui.circle.groupmembers.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.github.rayboot.widget.ratioview.RatioImageView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.timeface.circle.baby.BuildConfig;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.GlideUtil;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.circle.bean.GrowthCircleObj;
import cn.timeface.circle.baby.ui.circle.response.QrcodeResponse;
import cn.timeface.circle.baby.views.ShareDialog;
import cn.timeface.common.utils.ShareSdkUtil;
import rx.Subscription;
import rx.functions.Action1;

public class InviteActivity extends BaseAppCompatActivity {


    @Bind(R.id.iv_img)
    RatioImageView ivImg;
    @Bind(R.id.tv_circle)
    TextView tvCircle;
    @Bind(R.id.tv_share_circle)
    TextView tvShareCircle;

    GrowthCircleObj circleObj;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.appbar_layout)
    AppBarLayout appbarLayout;

    ProgressDialog progressDialog;


    public static void open(Context context, GrowthCircleObj circleObj) {
        Intent intent = new Intent(context, InviteActivity.class);
        intent.putExtra("circleObj", circleObj);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("邀请");
        circleObj = getIntent().getParcelableExtra("circleObj");
        tvCircle.setText(circleObj.getCircleNumber() + "");
        reqQrCode();
    }

    private void reqQrCode() {
        showProgress();
        Subscription subscribe = apiService.qrcode(circleObj.getCircleId())
                .compose(SchedulersCompat.applyIoSchedulers())
                .doOnTerminate(() -> dismissProgress())
                .subscribe(new Action1<QrcodeResponse>() {
                    @Override
                    public void call(QrcodeResponse qrcodeResponse) {
                        if (qrcodeResponse.success()) {
                            GlideUtil.displayImage(qrcodeResponse.getQRcodeUrl(), ivImg);
//                            Glide.with(InviteActivity.this)
//                                    .load(qrcodeResponse.getQRcodeUrl())
//                                    .into(ivImg);
                        } else {
                            ToastUtil.showToast(InviteActivity.this,qrcodeResponse.getInfo());
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        ToastUtil.showToast(InviteActivity.this,throwable.getMessage());
                    }
                });
        addSubscription(subscribe);
    }
    public void showProgress() {
        showProgress(R.string.loading);
    }

    public void showProgress(int resId) {
        showProgress(getString(resId));
    }

    public void showProgress(String msg) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(msg);
        } else {
            progressDialog.setMessage(msg);
        }
        progressDialog.show();
    }

    public void dismissProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
    @OnClick(R.id.tv_share_circle)
    public void clickShare(View view) {
        new ShareDialog(this).share(FastData.getUserName()+"请你加圈", GrowthCircleObj.getInstance().getCircleName()+FastData.getUserName()+"邀请你加入，在这里一起记录宝宝的学习成长时光！"
                , ShareSdkUtil.getImgStrByResource(this, R.drawable.ic_laucher_quadrate),
                BuildConfig.API_URL+"growthCircleShare/index.html?circleId="+circleObj.getCircleId()+"&circleMemberId="+ FastData.getCircleUserInfo().getCircleUserId());
    }
}
