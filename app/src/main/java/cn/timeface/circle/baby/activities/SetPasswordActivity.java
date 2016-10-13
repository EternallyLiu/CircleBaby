package cn.timeface.circle.baby.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.utils.FastData;
import cn.timeface.circle.baby.utils.ToastUtil;
import cn.timeface.circle.baby.utils.rxutils.SchedulersCompat;
import cn.timeface.common.utils.encode.AES;
import rx.Subscription;

public class SetPasswordActivity extends BaseAppCompatActivity {


    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.et_oldpsw)
    EditText etOldpsw;
    @Bind(R.id.et_password)
    EditText etPassword;
    @Bind(R.id.et_re_password)
    EditText etRePassword;
    @Bind(R.id.btn_finish)
    Button btnFinish;

    String account;

    public static void open(Context context, String account) {
        Intent intent = new Intent(context, SetPasswordActivity.class);
        intent.putExtra("account", account);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_password);
        ButterKnife.bind(this);
        account = getIntent().getStringExtra("account");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @OnClick({R.id.btn_finish})
    public void clickBtn(View view) {
        Subscription s = null;
        switch (view.getId()) {
            case R.id.btn_finish:
                if (check()) {
                    String psw = new AES().encrypt(etPassword.getText().toString().trim().getBytes());
                    s = apiService.login(Uri.encode(account), Uri.encode(psw), 1)
                            .compose(SchedulersCompat.applyIoSchedulers())
                            .subscribe(response -> {
                                Toast.makeText(this, response.getInfo(), Toast.LENGTH_SHORT).show();
                                if (response.success()) {
                                    FastData.setPassword(psw);
                                    finish();
                                    return;
                                }
                            }, error -> {
                                Log.e(TAG, error.getMessage());
                            });
                }
                break;
        }
        addSubscription(s);
    }

    private boolean check() {
        String oldpwd = etOldpsw.getText().toString().trim();
        String pwd = etPassword.getText().toString().trim();
        String rePwd = etRePassword.getText().toString().trim();
        if (TextUtils.isEmpty(oldpwd)) {
            ToastUtil.showToast("请填写原密码");
            return false;
        } else if (TextUtils.isEmpty(pwd)) {
            Toast.makeText(this, "请填写密码", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(rePwd)) {
            Toast.makeText(this, "请确认密码", Toast.LENGTH_SHORT).show();
            return false;
        } else if (pwd.length() < 6 || pwd.length() > 16) {
            ToastUtil.showToast("请输入6—16位的密码");
            return false;
        } else if (!FastData.getPassword().equals(new AES().encrypt(oldpwd.getBytes()))) {
            ToastUtil.showToast("原密码错误");
            return false;
        }

        if (!pwd.equals(rePwd)) {
            Toast.makeText(this, "两次输入密码不一致", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

}
