package cn.timeface.circle.baby.activities;

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
import android.widget.Toast;

import java.net.URLEncoder;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.utils.ToastUtil;
import cn.timeface.circle.baby.utils.rxutils.SchedulersCompat;

public class MilestoneDiyActivity extends BaseAppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.appbar_layout)
    AppBarLayout appbarLayout;
    @Bind(R.id.et_milestone)
    EditText etMilestone;
    @Bind(R.id.btn_creat)
    Button btnCreat;

    public static void open(Context context) {
        context.startActivity(new Intent(context, MilestoneDiyActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_milestonediy);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnCreat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String milestoneName = etMilestone.getText().toString().trim();
                if (TextUtils.isEmpty(milestoneName)) {
                    ToastUtil.showToast( "请输入里程碑");
                    return;
                }
                apiService.addMilestone(URLEncoder.encode(milestoneName))
                        .compose(SchedulersCompat.applyIoSchedulers())
                        .subscribe(milestoneResponse -> {
                            if (milestoneResponse.success()) {
                                Intent intent = new Intent();
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                        }, throwable -> {
                            Log.e(TAG, "addMilestone:", throwable);
                        });
            }
        });
    }
}
