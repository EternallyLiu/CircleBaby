package cn.timeface.circle.baby.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.Subscribe;
import org.w3c.dom.Text;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.events.ConfirmRelationEvent;
import cn.timeface.circle.baby.utils.FastData;
import cn.timeface.circle.baby.utils.Remember;
import cn.timeface.circle.baby.utils.rxutils.SchedulersCompat;

public class InviteCodeActivity extends BaseAppCompatActivity implements View.OnClickListener {


    @Bind(R.id.tv_back)
    TextView tvBack;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.et_code)
    EditText etCode;
    @Bind(R.id.btn_test)
    Button btnTest;

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
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tvBack.setOnClickListener(this);
        btnTest.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.tv_back:
                this.finish();
                break;
            case R.id.btn_test:
                String code = etCode.getText().toString().trim();
                if (TextUtils.isEmpty(code)) {
                    Toast.makeText(this, "请填写邀请码", Toast.LENGTH_SHORT).show();
                    return;
                }
                apiService.verifiedInviteCode(code)
                        .compose(SchedulersCompat.applyIoSchedulers())
                        .subscribe(userLoginResponse -> {
                            if (userLoginResponse.success()) {
                                Remember.putString("code",code);
                                FastData.setUserInfo(userLoginResponse.getUserInfo());
                                if(TextUtils.isEmpty(userLoginResponse.getUserInfo().getRelationName())){
                                    //跳转到确认关系界面
                                    Intent intent = new Intent(this,ConfirmRelationActivity.class);
                                    intent.putExtra("code",code);
                                    startActivity(intent);
                                }
                                finish();
                            }else{
                                Toast.makeText(this, userLoginResponse.getInfo(), Toast.LENGTH_SHORT).show();
                            }
                        }, error -> {
                            Log.e(TAG, "verifiedInviteCode:");
                        });

                break;
        }
    }

}
