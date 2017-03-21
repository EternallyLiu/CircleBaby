package cn.timeface.circle.baby.ui.circle.groupmembers.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.rayboot.widget.ratioview.RatioImageView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.circle.bean.GrowthCircleObj;
import cn.timeface.circle.baby.ui.circle.response.QrcodeResponse;
import cn.timeface.circle.baby.views.ShareDialog;
import rx.Subscription;
import rx.functions.Action1;

public class InviteActivity extends BaseAppCompatActivity {

    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.iv_img)
    RatioImageView ivImg;
    @Bind(R.id.tv_circle)
    TextView tvCircle;
    @Bind(R.id.tv_share_circle)
    TextView tvShareCircle;

    GrowthCircleObj circleObj;

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
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        title.setText("邀请");
        circleObj = getIntent().getParcelableExtra("circleObj");
        tvCircle.setText(circleObj.getCircleNumber() + "");
        reqQrCode();
    }

    private void reqQrCode() {
        Subscription subscribe = apiService.qrcode(circleObj.getCircleId())
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(new Action1<QrcodeResponse>() {
                    @Override
                    public void call(QrcodeResponse qrcodeResponse) {
                        if (qrcodeResponse.success()) {
                            Glide.with(InviteActivity.this)
                                    .load(qrcodeResponse.getQRcodeUrl())
                                    .into(ivImg);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });
        addSubscription(subscribe);
    }

    @OnClick(R.id.tv_share_circle)
    public void clickShare(View view) {
        new ShareDialog(this).share("成长圈", "快把这个成长圈分给你的朋友吧！"
                ,"http://img1.timeface.cn/uploads/avator/default.png","http://img1.timeface.cn/uploads/avator/default.png");
    }
}
