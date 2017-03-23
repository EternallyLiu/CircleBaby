package cn.timeface.circle.baby.ui.circle.groupmembers.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.events.UpdateMemberEvent;
import cn.timeface.circle.baby.events.UpdateNameEvent;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.circle.groupmembers.bean.CircleUserInfo;
import rx.Subscription;

public class ChangNameActivity extends BaseAppCompatActivity {


    @Bind(R.id.et_nickname)
    EditText etNickname;
    @Bind(R.id.tv_confirm)
    TextView tvConfirm;

    CircleUserInfo circleUserInfo;
    int type;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.appbar_layout)
    AppBarLayout appbarLayout;

    public static void open(Context context, CircleUserInfo circleUserInfo, int type) {
        Intent intent = new Intent(context, ChangNameActivity.class);
        intent.putExtra("circleUserInfo", circleUserInfo);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chang_name);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("修改昵称");
        circleUserInfo = getIntent().getParcelableExtra("circleUserInfo");
        type = getIntent().getIntExtra("type", 0);
    }

    @OnClick(R.id.tv_confirm)
    public void clickConfirm(View view) {
        String s = etNickname.getText().toString();
        if (TextUtils.isEmpty(s)) {
            ToastUtil.showToast(this, "请输入你的新昵称");
            return;
        }
        switch (type) {
            case 2:
                Subscription subscribe = apiService.updateBabyRealName(FastData.getBabyId(), s)
                        .compose(SchedulersCompat.applyIoSchedulers())
                        .subscribe(baseResponse -> {
                            if (baseResponse.success()) {
                                ToastUtil.showToast(ChangNameActivity.this, "操作成功");
                                EventBus.getDefault().post(new UpdateMemberEvent());
                                EventBus.getDefault().post(new UpdateNameEvent());
                                finish();
                            }
                        }, throwable -> {

                        });
                addSubscription(subscribe);
                break;
            case 1:
                Subscription subscribe1 = apiService.updateNickname(circleUserInfo.getCircleId(), s, circleUserInfo.getCircleUserId())
                        .compose(SchedulersCompat.applyIoSchedulers())
                        .subscribe(baseResponse -> {
                            if (baseResponse.success()) {
                                ToastUtil.showToast(ChangNameActivity.this, "操作成功");
                                EventBus.getDefault().post(new UpdateMemberEvent());
                                EventBus.getDefault().post(new UpdateNameEvent());
                                finish();
                            }
                        }, throwable -> {

                        });
                addSubscription(subscribe1);
                break;
        }
    }
}
