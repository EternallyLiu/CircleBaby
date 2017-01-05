package cn.timeface.circle.baby.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.support.utils.encode.AES;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import rx.Subscription;

public class ConfirmPasswordActivity extends BaseAppCompatActivity {


    @Bind(R.id.tv_cancle)
    TextView tvCancle;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.et_password)
    EditText etPassword;
    @Bind(R.id.et_nickname)
    EditText etNickname;
    @Bind(R.id.btn_finish)
    Button btnFinish;
    private String account;

    public static void open(Context context, String account) {
        Intent intent = new Intent(context, ConfirmPasswordActivity.class);
        intent.putExtra("account", account);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_password);
        ButterKnife.bind(this);
        account = getIntent().getStringExtra("account");
        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @OnClick({R.id.btn_finish, R.id.tv_cancle})
    public void clickBtn(View view) {
        Subscription s = null;
        switch (view.getId()) {
            case R.id.btn_finish:
                if (check()) {
                    String pwd = etPassword.getText().toString().trim();
                    String name = etNickname.getText().toString().trim();
                    s = apiService.register(Uri.encode(account), Uri.encode(name), Uri.encode(new AES().encrypt(pwd.getBytes())))
                            .compose(SchedulersCompat.applyIoSchedulers())
                            .subscribe(registerResponse -> {
                                Toast.makeText(this, registerResponse.getInfo(), Toast.LENGTH_SHORT).show();
                                if (registerResponse.success()) {
                                    FastData.setUserInfo(registerResponse.getUserInfo());
                                    CreateBabyActivity.open(ConfirmPasswordActivity.this, true);
                                    finish();
                                }
                                return;
                            }, throwable -> {
                                Log.e(TAG, "register:", throwable);
                            });
                }
                break;
            case R.id.tv_cancle:
                finish();
                break;
        }
        addSubscription(s);
    }

    private boolean check() {
        String pwd = etPassword.getText().toString().trim();
        String name = etNickname.getText().toString().trim();
        if (TextUtils.isEmpty(pwd)) {
            Toast.makeText(this, "请填写密码", Toast.LENGTH_SHORT).show();
            return false;
        } else if (pwd.length() < 6 || pwd.length() > 16) {
            ToastUtil.showToast("请输入6-16位密码");
            return false;
        } else if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "请填写昵称", Toast.LENGTH_SHORT).show();
            return false;
        } else if (name.length() > 12) {
            Toast.makeText(this, "请输入12个字符以内的昵称", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

}
