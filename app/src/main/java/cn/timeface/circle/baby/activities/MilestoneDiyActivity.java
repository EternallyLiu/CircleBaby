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

import java.net.URLEncoder;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.support.utils.Utils;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.timelines.Utils.LogUtil;

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

    /**
     * 里程碑输入正则
     */
    private static final String INPUT_REGEX = "^[\\u4e00-\\u9fa5_a-zA-Z0-9]+$";

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
                    ToastUtil.showToast("请输入里程碑");
                    return;
                }
                if (!Utils.isMatch(INPUT_REGEX, milestoneName)) {
                    ToastUtil.showToast("您输入的内容含有非法字符");
                    return;
                }
                if (Utils.getByteSize(milestoneName) > 10) {
                    ToastUtil.showToast("输入的内容长度限制在10个字符（1个汉字按2个字符计算）");
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
                            LogUtil.showError(throwable);
                        });
            }
        });
    }
}
