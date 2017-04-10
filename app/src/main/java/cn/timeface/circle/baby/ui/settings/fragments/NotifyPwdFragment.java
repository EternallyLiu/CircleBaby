package cn.timeface.circle.baby.ui.settings.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ta.utdid2.android.utils.AESUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.TabMainActivity;
import cn.timeface.circle.baby.fragments.base.BaseFragment;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.support.utils.encode.AES;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.timelines.Utils.LogUtil;
import cn.timeface.circle.baby.views.dialog.TFProgressDialog;

/**
 * author : wangshuai Created on 2017/1/13
 * email : wangs1992321@gmail.com
 */
public class NotifyPwdFragment extends BaseFragment implements View.OnFocusChangeListener, View.OnClickListener {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.et_old_password)
    EditText etOldPassword;
    @Bind(R.id.et_new_password)
    EditText etNewPassword;
    @Bind(R.id.pwd_tip_1)
    TextView pwdTip1;
    @Bind(R.id.et_new_password_again)
    EditText etNewPasswordAgain;
    @Bind(R.id.pwd_tip_2)
    TextView pwdTip2;
    @Bind(R.id.btn_submit)
    Button btnSubmit;
    private ForegroundColorSpan colorSpan;
    private TFProgressDialog tfProgressDialog=null;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notify_password, container, false);

        ButterKnife.bind(this, view);
        setActionBar(toolbar);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }
        etNewPassword.setOnFocusChangeListener(this);
        etNewPasswordAgain.setOnFocusChangeListener(this);
        toolbar.setTitle(R.string.notify_password);
        btnSubmit.setOnClickListener(this);
        tfProgressDialog=TFProgressDialog.getInstance("");
        return view;
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private boolean checkInputNull(EditText editText) {
        if (editText.getText() == null || TextUtils.isEmpty(editText.getText().toString()))
            return true;
        else return false;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        switch (v.getId()) {
            case R.id.et_new_password:
                if (hasFocus) {
                    if (checkInputNull(etOldPassword)) {
                        if (colorSpan == null)
                            colorSpan = new ForegroundColorSpan(Color.RED);
                        builder.append(getString(R.string.pwd_input_old_null)).append("\n");
                        builder.setSpan(colorSpan, 0, builder.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                    }
                    builder.append(getString(R.string.pwd_input_tip));
                    pwdTip1.setText(builder);
                }
                if (pwdTip1 != null)
                    pwdTip1.setVisibility(hasFocus ? View.VISIBLE : View.GONE);
                break;
            case R.id.et_new_password_again:
                if (hasFocus) {
                    if (checkInputNull(etOldPassword)) {
                        if (colorSpan == null)
                            colorSpan = new ForegroundColorSpan(Color.RED);
                        builder.append(getString(R.string.pwd_input_old_null)).append("\n");
                        builder.setSpan(colorSpan, 0, builder.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                    }
                    if (checkInputNull(etNewPassword)) {
                        if (colorSpan == null)
                            colorSpan = new ForegroundColorSpan(Color.RED);
                        builder.append(getString(R.string.pwd_input_new_no)).append("\n");
                        builder.setSpan(colorSpan, 0, builder.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                    }
                    builder.append(getString(R.string.pwd_input_tip));
                    pwdTip2.setText(builder);
                }
                if (pwdTip2 != null)
                    pwdTip2.setVisibility(hasFocus ? View.VISIBLE : View.GONE);
                break;
        }
    }

    private void notifyPassword(String old, String pwd) {
        tfProgressDialog.setTvMessage(getString(R.string.request_operation));
        tfProgressDialog.show(getFragmentManager(),"");
        addSubscription(apiService.modifyPassword(Uri.encode(pwd), Uri.encode(old))
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(response -> {
                    tfProgressDialog.dismiss();
                    if (response.success()) {
                        getActivity().finish();
                        return;
                    }
                    Toast.makeText(getActivity(), response.getInfo(), Toast.LENGTH_SHORT).show();
                }, error -> {
                    tfProgressDialog.dismiss();
                    Toast.makeText(getActivity(), "验证码校验失败", Toast.LENGTH_SHORT).show();
                }));
    }

    private boolean checkIsNull(String str, String toast) {
        if (TextUtils.isEmpty(str) || TextUtils.isEmpty(str.trim())) {
            LogUtil.showLog("toast:" + toast);
            Toast.makeText(getActivity(), toast, Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit:
                LogUtil.showLog("submit is clicked");
                String old = etOldPassword.getText().toString();
                String pwd = etNewPassword.getText().toString();
                String pwdAgain = etNewPasswordAgain.getText().toString();
                if (checkIsNull(old, getString(R.string.notify_pwd_old))
                        || checkIsNull(pwd, getString(R.string.notify_pwd_new))
                        || checkIsNull(pwdAgain, getString(R.string.notify_pwd_new_again)))
                    return;
                if (pwd.equals(pwdAgain)) {
                    AES aes = new AES();
                    old = aes.encrypt(old.trim().getBytes());
                    pwd = aes.encrypt(pwd.trim().getBytes());
                    notifyPassword(old, pwd);
                } else
                    Toast.makeText(getActivity(), R.string.check_new_password_error, Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
