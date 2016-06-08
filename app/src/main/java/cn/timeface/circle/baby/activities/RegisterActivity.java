package cn.timeface.circle.baby.activities;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import de.greenrobot.event.Subscribe;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.events.EventTabMainWake;
import cn.timeface.circle.baby.managers.listeners.IEventBus;
import cn.timeface.circle.baby.managers.receivers.SmsReceiver;
import cn.timeface.circle.baby.utils.rxutils.SchedulersCompat;
import cn.timeface.common.utils.CheckedUtil;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RegisterActivity extends BaseAppCompatActivity implements IEventBus, View.OnClickListener {

    @Bind(R.id.tv_cancle)
    TextView tvCancle;
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
        context.startActivity(new Intent(context, RegisterActivity.class));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnNext.setOnClickListener(this);
        tvGetCode.setOnClickListener(this);
        tvCancle.setOnClickListener(this);
    }

    private void timeRun() {
        tvGetCode.setEnabled(false);
        final int[] sec = {60};

        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (sec[0] > 0) {
                    sec[0] = sec[0] - 1;
                    tvGetCode.setText(sec[0] + "秒后再次获取");
                } else {
                    tvGetCode.setText("获取验证码");
                    tvGetCode.setEnabled(true);
                    timer.cancel();
                    task.cancel();
                }

                super.handleMessage(msg);
            }
        };
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.what = 1;
                handler.sendMessage(msg);
            }
        };

        timer.schedule(task, 0, 1000);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onEvent(EventTabMainWake event) {
        finish();
    }

    @Override
    public void onEvent(Object event) {

    }

    private void registerSMSReceiver() {
        smsReceiver = new SmsReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(1000);
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(smsReceiver, filter);
    }

    @Override
    public void onClick(View v) {
        Subscription s = null;
        switch (v.getId()) {
            case R.id.tv_get_code:
                String phone = etPhone.getText().toString().trim();
                if (TextUtils.isEmpty(phone) || !CheckedUtil.isMobileNum(phone)) {
                    Toast.makeText(this, "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!TextUtils.isEmpty(phone) && CheckedUtil.isMobileNum(phone)) {
                    registerSMSReceiver();// 注册接收短信，获取的手机验证码并自动填充

                    s = apiService.getVeriCode(phone)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(response -> {
                                Toast.makeText(this, response.getInfo(), Toast.LENGTH_SHORT).show();
                                if (response.success()) {
                                    timeRun();
                                }
                            }, error -> {
                                Log.e(TAG, "getVeriCode:");
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
                s = apiService.verifyCode(mobile, code)
                        .compose(SchedulersCompat.applyIoSchedulers())
                        .subscribe(response -> {
                            if (response.success()) {
                                ConfirmPasswordActivity.open(RegisterActivity.this, mobile);
//                                finish();
                            }else{
                                Toast.makeText(this, response.getInfo(), Toast.LENGTH_SHORT).show();
                            }
                            return;
                        }, error -> {
                            Log.e(TAG,"verifyCode:");
                        });
                break;
            case R.id.tv_cancle:
                this.finish();
                break;
        }
        addSubscription(s);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        if (smsReceiver != null)
            unregisterReceiver(smsReceiver);
    }
}
