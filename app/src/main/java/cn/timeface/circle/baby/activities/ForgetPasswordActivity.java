package cn.timeface.circle.baby.activities;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.Subscribe;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.events.EventTabMainWake;
import cn.timeface.circle.baby.events.SmsEvent;
import cn.timeface.circle.baby.support.managers.listeners.IEventBus;
import cn.timeface.circle.baby.support.managers.receivers.SmsReceiver;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.common.utils.CheckedUtil;
import rx.Observable;
import rx.Subscription;

public class ForgetPasswordActivity extends BaseAppCompatActivity implements IEventBus {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.et_phone)
    EditText etPhone;
    @Bind(R.id.et_code)
    EditText etCode;
    @Bind(R.id.tv_get_code)
    TextView tvGetCode;
    @Bind(R.id.btn_next)
    Button btnNext;
    private Timer timer;
    private TimerTask task;
    private SmsReceiver smsReceiver;

    public static void open(Context context) {
        context.startActivity(new Intent(context, ForgetPasswordActivity.class));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ForgetPasswordActivity.this.finish();
            }
        });
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @OnClick({R.id.btn_next, R.id.tv_get_code, R.id.tv_cancle})
    public void clickBtn(View view) {
        Subscription s = null;
        switch (view.getId()) {
            case R.id.tv_get_code:
                String phone = etPhone.getText().toString().trim();
                if (TextUtils.isEmpty(phone) || !CheckedUtil.isMobileNum(phone)) {
                    Toast.makeText(this, "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
                    return;
                }
                timeRun();
                if (!TextUtils.isEmpty(phone) && CheckedUtil.isMobileNum(phone)) {
                    registerSMSReceiver();// 注册接收短信，获取的手机验证码并自动填充

                    s = apiService.getVeriCode(phone, 2)
                            .compose(SchedulersCompat.applyIoSchedulers())
                            .subscribe(response -> {
                                Toast.makeText(this, response.getInfo(), Toast.LENGTH_SHORT).show();
                                if (response.success()) {
                                    etCode.requestFocus();
                                } else {
                                    if (tvGetCode != null) {
                                        tvGetCode.setText("获取验证码");
                                        tvGetCode.setEnabled(true);
                                    }
                                }
                            }, error -> {
                                Toast.makeText(this, "获取验证码失败", Toast.LENGTH_SHORT).show();
                                if (tvGetCode != null) {
                                    tvGetCode.setText("获取验证码");
                                    tvGetCode.setEnabled(true);
                                }
                            });
                }
                break;
            case R.id.btn_next:
                String mobile = etPhone.getText().toString().trim();
                String code = etCode.getText().toString().trim();
                if (TextUtils.isEmpty(mobile) || !CheckedUtil.isMobileNum(mobile)) {
                    Toast.makeText(this, "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(code)) {
                    Toast.makeText(this, "请输入验证码", Toast.LENGTH_SHORT).show();
                    return;
                }
                s = apiService.verifyCode(mobile, code, 0)
                        .compose(SchedulersCompat.applyIoSchedulers())
                        .subscribe(response -> {
                            if (response.success()) {
                                ForgetPasswordSetActivity.open(ForgetPasswordActivity.this, mobile);
                                finish();
                                return;
                            }
                            Toast.makeText(this, response.getInfo(), Toast.LENGTH_SHORT).show();
                        }, error -> {
                            Toast.makeText(this, "验证码校验失败", Toast.LENGTH_SHORT).show();
                        });
                break;
            case R.id.tv_cancle:
                this.finish();
                break;
        }
        addSubscription(s);
    }

    private void timeRun() {

        tvGetCode.setEnabled(false);
        tvGetCode.setText(60 + "秒后再次获取");
        Observable.interval(1, TimeUnit.SECONDS)
                .takeWhile(aLong -> aLong <= 59 && !tvGetCode.isEnabled())
                .map(aLong -> 59 - aLong)
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(aLong -> {
                    if (aLong > 0 && tvGetCode != null)
                        tvGetCode.setText(aLong + "秒后再次获取");
                    else if (aLong <= 0 && tvGetCode != null) {
                        tvGetCode.setText("获取验证码");
                        tvGetCode.setEnabled(true);
                    }
                }, throwable -> {
                    if (tvGetCode != null) {
                        tvGetCode.setText("获取验证码");
                        tvGetCode.setEnabled(true);
                    }
                });
    }


    private void registerSMSReceiver() {
        smsReceiver = new SmsReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(1000);
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(smsReceiver, filter);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onEvent(EventTabMainWake event) {
        finish();
    }

    @Subscribe
    public void onEvent(SmsEvent event) {
        etCode.setText(event.content);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        if (smsReceiver != null)
            unregisterReceiver(smsReceiver);
    }
}
