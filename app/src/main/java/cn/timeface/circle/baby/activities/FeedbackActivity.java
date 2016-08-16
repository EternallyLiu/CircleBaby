package cn.timeface.circle.baby.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.utils.FastData;
import cn.timeface.circle.baby.utils.ToastUtil;
import cn.timeface.circle.baby.utils.rxutils.SchedulersCompat;
import cn.timeface.common.utils.CommonUtil;

/**
 * @author rayboot
 * @from 14-4-10 16:06
 * @TODO 意见反馈
 */
public class FeedbackActivity extends BaseAppCompatActivity implements View.OnClickListener {
    @Bind(R.id.etFeedback)
    EditText mEtFeedback;
    @Bind(R.id.btnAccept)
    Button mBtnAccept;
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    public static void open(Context context) {
        context.startActivity(new Intent(context, FeedbackActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mBtnAccept.setOnClickListener(this);
    }

    /**
     * 点击 提交
     */
    public void clickSubmit() {
        CommonUtil.hideSoftInput(this);
        String feedbackString = mEtFeedback.getText().toString();
        if (TextUtils.isEmpty(feedbackString)) {
            Toast.makeText(this, "反馈内容不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        mBtnAccept.setEnabled(false);
        apiService.feedback(Uri.encode(feedbackString), FastData.getUserId())
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(response -> {
                            ToastUtil.showToast(response.getInfo());
                            if (response.success()) {
                                mEtFeedback.setText("");
                            }
                            mBtnAccept.setEnabled(true);
                        }
                );
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        UmsAgent.onResume(this, UmsConstants.MODULE_PERSONAL_CENTER + this.getClass().getSimpleName());
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        UmsAgent.onPause(this);
//    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnAccept){
            clickSubmit();
        }
    }
}
