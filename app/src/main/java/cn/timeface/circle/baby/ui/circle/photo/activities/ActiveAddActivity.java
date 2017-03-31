package cn.timeface.circle.baby.ui.circle.photo.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.support.mvp.bases.BasePresenterAppCompatActivity;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;

/**
 * 添加圈活动相册
 * Created by lidonglin on 2017/3/20.
 */

public class ActiveAddActivity extends BasePresenterAppCompatActivity implements View.OnClickListener {


    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.appbar_layout)
    AppBarLayout appbarLayout;
    @Bind(R.id.tv)
    TextView tv;
    @Bind(R.id.et_active_name)
    EditText etActiveName;
    @Bind(R.id.btn_ok)
    Button btnOk;
    private long circleId;

    public static void open(Context context, long circleId) {
        Intent intent = new Intent(context, ActiveAddActivity.class);
        intent.putExtra("circle_id", circleId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_active);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        circleId = getIntent().getLongExtra("circle_id",0);
        btnOk.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
        String name = etActiveName.getText().toString().trim();
        if(TextUtils.isEmpty(name)){
            showToast("请输入活动名称");
            return;
        }
        if (name.length() > 8) {
            showToast("活动相册为1~8个汉字");
            return;
        }
        apiService.createActive(name, circleId)
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(
                        response -> {
                            ToastUtil.showToast(response.getInfo());
                            if (response.success()) {
                                finish();
                            }
                        },
                        throwable -> {
                            Log.e(TAG, throwable.getLocalizedMessage());
                        }
                );
    }
}
