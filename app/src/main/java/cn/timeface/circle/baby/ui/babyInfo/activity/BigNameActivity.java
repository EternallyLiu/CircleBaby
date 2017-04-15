package cn.timeface.circle.baby.ui.babyInfo.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.net.URLEncoder;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.events.HomeRefreshEvent;
import cn.timeface.circle.baby.support.api.models.objs.BabyObj;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.timelines.views.SelectImageView;

/**
 * Created by wangshuai on 2017/1/12.
 */

public class BigNameActivity extends BaseAppCompatActivity implements View.OnClickListener {
    @Bind(R.id.back)
    ImageView back;
    @Bind(R.id.tv_mine)
    TextView tvMine;
    @Bind(R.id.tv_count)
    TextView tvCount;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.et_big_name)
    EditText etBigName;
    @Bind(R.id.iv_swich_big_name)
    SelectImageView ivSwichBigName;
    @Bind(R.id.rl_setting_big_name)
    RelativeLayout rlSettingBigName;
    @Bind(R.id.btn_submit)
    Button btnSubmit;

    private BabyObj mBabyObj = null;

    public static void open(Context context, BabyObj babyObj) {
        if (babyObj == null)
            return;
        context.startActivity(new Intent(context, BigNameActivity.class).putExtra("data", babyObj));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baby_info);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        mBabyObj = getIntent().getParcelableExtra("data");
        if (mBabyObj == null) {
            finish();
            return;
        }
        etBigName.setText(mBabyObj.getRealName());
        ivSwichBigName.setChecked(mBabyObj.getShowRealName() == 0);
        tvMine.setText(R.string.baby_real_name_title);
        tvCount.setText(R.string.baby_real_name_save);
        tvCount.setVisibility(View.GONE);
        rlSettingBigName.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        tvCount.setOnClickListener(this);
        back.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_swich_big_name:
            case R.id.rl_setting_big_name:
                ivSwichBigName.setChecked(!ivSwichBigName.isChecked());
                mBabyObj.setShowRealName(ivSwichBigName.isChecked() ? 0 : 1);
                break;
            case R.id.btn_submit:
            case R.id.tv_count:
                btnSubmit.setEnabled(false);
                tvCount.setEnabled(false);
                String realName = etBigName.getText().toString();
                if (TextUtils.isEmpty(realName)) {
                    ToastUtil.showToast(getString(R.string.baby_real_name_submit_null));
                    return;
                }
                mBabyObj.setRealName(realName);
                EventBus.getDefault().post(mBabyObj);
                finish();
//                String n = mBabyObj.getName();
//                long time = mBabyObj.getBithday();
//                String b = mBabyObj.getBlood();
//                String s = mBabyObj.getAvatar();
//                int baby = s.indexOf("baby");
//                String objectKey = "";
//                if (baby > 0)
//                    objectKey = s.substring(baby);
//                apiService.editBabyInfo(time, URLEncoder.encode(b), mBabyObj.getGender(), URLEncoder.encode(n),
//                        URLEncoder.encode(realName), mBabyObj.getShowRealName() + "", objectKey)
//                        .compose(SchedulersCompat.applyIoSchedulers())
//                        .subscribe(response -> {
//                            if (response.success()) {
//                                apiService.queryBabyInfoDetail(mBabyObj.getBabyId())
//                                        .compose(SchedulersCompat.applyIoSchedulers())
//                                        .subscribe(babyInfoResponse -> {
//                                            if (babyInfoResponse.success()) {
//                                                mBabyObj = babyInfoResponse.getBabyInfo();
//                                                FastData.setBabyObj(mBabyObj);
//                                                EventBus.getDefault().post(new HomeRefreshEvent());
//                                                EventBus.getDefault().post(mBabyObj);
//                                                finish();
//                                            }
//                                        }, throwable -> {
//                                            Log.e(TAG, "queryBabyInfoDetail:", throwable);
//                                        });
//                            }
//                            btnSubmit.setEnabled(true);
//                            tvCount.setEnabled(true);
//                        }, throwable -> {
//                            Log.e(TAG, "editBabyInfo:", throwable);
//                        });
                break;
            case R.id.back:
                finish();
                break;
        }
    }
}
