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
import cn.timeface.circle.baby.api.models.base.BaseResponse;
import cn.timeface.common.utils.CommonUtil;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * @author rayboot
 * @from 14-4-10 16:06
 * @TODO 意见反馈
 */
public class FeedbackActivity extends BaseAppCompatActivity {
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
    }

    /**
     * 点击 提交
     */
    public void clickSubmit(View v) {
        CommonUtil.hideSoftInput(this);
        String feedbackString = mEtFeedback.getText().toString();
        if (TextUtils.isEmpty(feedbackString)) {
            Toast.makeText(this, "反馈内容不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        mBtnAccept.setEnabled(false);

//        Subscription s = apiService.feedback(Uri.encode(feedbackString), "timeface")
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(
//                        new Action1<BaseResponse>() {
//                            @Override
//                            public void call(BaseResponse response) {
//                                Toast.makeText(FeedbackActivity.this, response.info, Toast.LENGTH_SHORT)
//                                        .show();
//                                mBtnAccept.setEnabled(true);
//                                finish();
//                            }
//                        }
//                        , new Action1<Throwable>() {
//                            @Override
//                            public void call(Throwable throwable) {
//                                mBtnAccept.setEnabled(true);
//                            }
//                        }
//                );
//        addSubscription(s);
    }
}
