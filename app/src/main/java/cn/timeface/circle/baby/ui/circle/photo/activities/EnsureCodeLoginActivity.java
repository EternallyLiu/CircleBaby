package cn.timeface.circle.baby.ui.circle.photo.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.support.mvp.bases.BasePresenterAppCompatActivity;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.timelines.Utils.LogUtil;

/**
 * 扫码登录确认
 * Created by lidonglin on 2017/4/7.
 */

public class EnsureCodeLoginActivity extends BasePresenterAppCompatActivity implements View.OnClickListener {


    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.appbar_layout)
    AppBarLayout appbarLayout;
    @Bind(R.id.btn_login)
    Button btnLogin;
    @Bind(R.id.tv_cancel)
    TextView tvCancel;
    private String code;

    public static void open(Context context, String code) {
        Intent intent = new Intent(context, EnsureCodeLoginActivity.class);
        intent.putExtra("code", code);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ensure_code_login);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        code = getIntent().getStringExtra("code");
        btnLogin.setOnClickListener(this);
        tvCancel.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                apiService.scanLogin(code)
                        .compose(SchedulersCompat.applyIoSchedulers())
                        .subscribe(
                                response -> {
                                    if (response.success()) {
                                        showToast("登录成功");
                                    } else {
                                        showToast(response.getInfo());
                                    }
                                    finish();
                                },
                                throwable -> {
                                    LogUtil.showError(TAG,throwable);
                                    finish();
                                }
                        );
                break;
            case R.id.tv_cancel:
                finish();
                break;
        }
    }
}
