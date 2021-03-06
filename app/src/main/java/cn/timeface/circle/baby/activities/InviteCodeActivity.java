package cn.timeface.circle.baby.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.App;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.events.ConfirmRelationEvent;
import cn.timeface.circle.baby.support.api.models.objs.BabyObj;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.Remember;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.babyInfo.beans.BabyChanged;
import cn.timeface.circle.baby.ui.images.views.DeleteDialog;
import cn.timeface.circle.baby.ui.timelines.Utils.SpannableUtils;
import cn.timeface.circle.baby.views.dialog.TFProgressDialog;

public class InviteCodeActivity extends BaseAppCompatActivity implements View.OnClickListener, DeleteDialog.SubmitListener, DeleteDialog.CloseListener {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.et_code)
    EditText etCode;
    @Bind(R.id.btn_test)
    Button btnTest;

    private DeleteDialog dialog = null;
    private TFProgressDialog tfprogress;

    public static void open(Context context) {
        Intent intent = new Intent(context, InviteCodeActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitecode);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("请输入邀请码");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InviteCodeActivity.this.finish();
            }
        });
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnTest.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_test:
                String code = etCode.getText().toString().trim();
                if (TextUtils.isEmpty(code)) {
                    Toast.makeText(this, "请填写邀请码", Toast.LENGTH_SHORT).show();
                    return;
                }
                showProgress();
                apiService.verifiedInviteCode(code)
                        .compose(SchedulersCompat.applyIoSchedulers())
                        .subscribe(userLoginResponse -> {
                            if (userLoginResponse.success()) {
                                Remember.putString("code", code);
                                FastData.setUserInfo(userLoginResponse.getUserInfo());
                                if (TextUtils.isEmpty(userLoginResponse.getUserInfo().getRelationName())) {
                                    //跳转到确认关系界面
                                    Intent intent = new Intent(this, ConfirmRelationActivity.class);
                                    intent.putExtra("code", code);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    babyChanged = new BabyChanged(userLoginResponse.getUserInfo());
                                    queryFamily(userLoginResponse.getUserInfo().getRelationName(), userLoginResponse.getUserInfo().getBabyObj());
                                }
                            } else {
                                Toast.makeText(this, userLoginResponse.getInfo(), Toast.LENGTH_SHORT).show();
                            }
                        }, error -> {
                            Log.e(TAG, "verifiedInviteCode:");
                        });

                break;
        }
    }

    private BabyChanged babyChanged = null;

    private void queryFamily(String relativeName, BabyObj babyObj) {
        apiService.queryBabyFamilyLoginInfoList().compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(familyListResponse -> {
                    if (familyListResponse.success()) {
                        ForegroundColorSpan colorSpan = SpannableUtils.getTextColor(this, R.color.sea_buckthorn);
                        ForegroundColorSpan babyColorSpan = SpannableUtils.getTextColor(this, R.color.sea_buckthorn);
                        StyleSpan styleSpan = new StyleSpan(Typeface.BOLD);
                        SpannableStringBuilder builder = new SpannableStringBuilder();
                        builder.append(String.format("欢迎 %s 加入！", relativeName)).append("\n");
                        builder.append("你将和");
                        int beginIndex = builder.length();
                        for (int i = 0; i < familyListResponse.getDataList().size(); i++) {
                            if (i != 0 && i < familyListResponse.getDataList().size() - 1)
                                builder.append("、");
                            if (!FastData.getUserId().equals(familyListResponse.getDataList().get(i).getUserInfo().getUserId())) {
                                builder.append(familyListResponse.getDataList().get(i).getUserInfo().getRelationName());
                            }
                        }
                        int endIndex = builder.length();
                        builder.append("一起来记录").append("\n");
                        builder.append(String.format("见证 %s 的成长~", babyObj.getNickName()));
                        String content = builder.toString();
                        builder.setSpan(colorSpan, content.indexOf(relativeName), content.indexOf(relativeName) + relativeName.length() + 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                        builder.setSpan(styleSpan, content.indexOf(relativeName), content.indexOf(relativeName) + relativeName.length() + 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                        builder.setSpan(babyColorSpan, content.indexOf(babyObj.getNickName()), content.indexOf(babyObj.getNickName()) + babyObj.getNickName().length() + 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                        builder.setSpan(styleSpan, content.indexOf(babyObj.getNickName()), content.indexOf(babyObj.getNickName()) + babyObj.getNickName().length() + 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                        builder.setSpan(colorSpan, beginIndex, endIndex, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                        babyChanged.setBuilder(builder);
                        hideProgress();
                        submit();
                    }
                }, error -> {
                    hideProgress();
                });
    }


    private void hideProgress() {
        if (tfprogress != null && !tfprogress.isHidden())
            tfprogress.dismiss();
    }

    private void showProgress() {
        if (tfprogress == null) {
            tfprogress = TFProgressDialog.getInstance("正在加载……");
        }
        if (tfprogress.isHidden())
            tfprogress.show(getSupportFragmentManager(), "");
    }

    @Override
    public void submit() {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
        EventBus.getDefault().post(babyChanged);
        EventBus.getDefault().post(new ConfirmRelationEvent());
        finish();
    }

    @Override
    public void close() {
        EventBus.getDefault().post(new ConfirmRelationEvent());
        finish();
    }
}
