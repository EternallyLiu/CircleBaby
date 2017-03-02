package cn.timeface.circle.baby.ui.settings.fragments;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.FragmentBridgeActivity;
import cn.timeface.circle.baby.activities.TabMainActivity;
import cn.timeface.circle.baby.fragments.base.BaseFragment;
import cn.timeface.circle.baby.support.managers.receivers.SmsReceiver;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.Remember;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.babyInfo.fragments.CreateBabyFragment;
import cn.timeface.circle.baby.ui.settings.beans.UpdatePhone;
import cn.timeface.circle.baby.ui.timelines.Utils.LogUtil;
import cn.timeface.common.utils.CheckedUtil;
import rx.Observable;
import rx.Subscription;

public class BindPhoneFragment extends BaseFragment implements View.OnClickListener {

    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.current_phone)
    TextView currentPhone;
    @Bind(R.id.et_phone)
    EditText etPhone;
    @Bind(R.id.tv_get_code)
    TextView tvGetCode;
    @Bind(R.id.et_code)
    EditText etCode;
    @Bind(R.id.btn_submit)
    Button btnSubmit;
    @Bind(R.id.code_tip)
    TextView codeTip;
    @Bind(R.id.code_tip_red)
    TextView codeTipRed;

    private String phoneNumber = null;
    private SmsReceiver smsReceiver;
    private Timer timer;
    private TimerTask task;
    private Subscription s;

    private int type = 1;//标记跳转来源 0、登录页面；1、设置页面点击进入

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("type"))
            type = bundle.getInt("type", 1);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bind_phone, container, false);

        ButterKnife.bind(this, view);
        setActionBar(toolbar);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        if (type == 0)
            phoneNumber = null;
        phoneNumber = FastData.getUserInfo().getPhoneNumber();
        currentPhone.setVisibility(TextUtils.isEmpty(phoneNumber) ? View.GONE : View.VISIBLE);
        codeTip.setVisibility(TextUtils.isEmpty(phoneNumber) ? View.GONE : View.VISIBLE);
        codeTipRed.setVisibility(View.GONE);
        title.setText(TextUtils.isEmpty(phoneNumber) ? "验证手机号" : "修改手机号");
        if (!TextUtils.isEmpty(phoneNumber)) {
            String replace = phoneNumber.substring(3, 7);
            currentPhone.setText(getString(R.string.bind_current_phone_tip) + phoneNumber.replace(replace, "****"));
            tvGetCode.setText(R.string.bind_phone_get_code);
        } else {
            tvGetCode.setText(R.string.bind_phone_get_code);
            btnSubmit.setText(R.string.next);
        }
        codeTip.setVisibility(TextUtils.isEmpty(phoneNumber) ? View.VISIBLE : View.GONE);
        codeTipRed.setVisibility(TextUtils.isEmpty(phoneNumber) ? View.VISIBLE : View.GONE);

        tvGetCode.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (type == 0) {
            inflater.inflate(R.menu.menu_fragment_bigimage, menu);
            menu.findItem(R.id.save).setTitle("暂不绑定");
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                next();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void next() {
        if (FastData.getBabyCount() == 0) {
            FragmentBridgeActivity.open(getActivity(), CreateBabyFragment.class.getSimpleName());
        } else if (type == 0) {
            startActivity(new Intent(getActivity(), TabMainActivity.class));
        }
        EventBus.getDefault().post(new UpdatePhone());
        getActivity().finish();
    }

    private void bindPhone() {
        String mobile = etPhone.getText().toString().trim();
        String code = etCode.getText().toString().trim();
        if (TextUtils.isEmpty(mobile) || !CheckedUtil.isMobileNum(mobile)) {
            Toast.makeText(getActivity(), "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(code)) {
            Toast.makeText(getActivity(), "请输入验证码", Toast.LENGTH_SHORT).show();
            return;
        }
        s = apiService.bindPhone(mobile, code)
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(response -> {
                    if (response.success()) {
                        Remember.putString("phoneNumber", mobile);
                        next();
                        return;
                    }
                    Toast.makeText(getActivity(), response.getInfo(), Toast.LENGTH_SHORT).show();
                }, error -> {
                    Toast.makeText(getActivity(), "验证码校验失败", Toast.LENGTH_SHORT).show();
                });
    }

    public void getCode() {
        String phone = etPhone.getText().toString().trim();
        if (TextUtils.isEmpty(phone) || !CheckedUtil.isMobileNum(phone)) {
            Toast.makeText(getActivity(), "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
            return;
        }
        timeRun();
        if (!TextUtils.isEmpty(phone) && CheckedUtil.isMobileNum(phone)) {
            registerSMSReceiver();// 注册接收短信，获取的手机验证码并自动填充

            s = apiService.getVeriCode(phone, 3)
                    .compose(SchedulersCompat.applyIoSchedulers())
                    .subscribe(response -> {
                        Toast.makeText(getActivity(), response.getInfo(), Toast.LENGTH_SHORT).show();
                        if (response.success()) {
                            etCode.requestFocus();
                        } else {
                            if (tvGetCode != null) {
                                tvGetCode.setText("发送验证码");
                                tvGetCode.setEnabled(true);
                            }
                        }
                    }, error -> {
                        Toast.makeText(getActivity(), "获取验证码失败", Toast.LENGTH_SHORT).show();
                        if (tvGetCode != null) {
                            tvGetCode.setText("发送验证码");
                            tvGetCode.setEnabled(true);
                        }
                    });
        }
    }

    private void registerSMSReceiver() {
        smsReceiver = new SmsReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(1000);
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        getActivity().registerReceiver(smsReceiver, filter);
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
                        tvGetCode.setText("发送验证码");
                        tvGetCode.setEnabled(true);
                    }
                }, throwable -> {
                    if (tvGetCode != null) {
                        tvGetCode.setText("发送验证码");
                        tvGetCode.setEnabled(true);
                    }
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
            case R.id.back:
                getActivity().finish();
                break;
            case R.id.right:
                next();
                break;
            case R.id.tv_get_code:
                getCode();
                break;
            case R.id.btn_submit:
                bindPhone();
                break;
        }
    }
}
