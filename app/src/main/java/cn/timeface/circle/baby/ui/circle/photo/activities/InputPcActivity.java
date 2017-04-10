package cn.timeface.circle.baby.ui.circle.photo.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.ForgetPasswordActivity;
import cn.timeface.circle.baby.support.mvp.bases.BasePresenterAppCompatActivity;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.timelines.Utils.LogUtil;
import cn.timeface.common.utils.encode.AES;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 导入电脑照片
 * Created by lidonglin on 2017/3/20.
 */

public class InputPcActivity extends BasePresenterAppCompatActivity implements View.OnClickListener {


    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.appbar_layout)
    AppBarLayout appbarLayout;
    @Bind(R.id.iv_scan)
    ImageView ivScan;
    @Bind(R.id.iv_avatar)
    CircleImageView ivAvatar;
    @Bind(R.id.tv_name)
    TextView tvName;
    @Bind(R.id.btn_forget)
    Button btnForget;
    @Bind(R.id.tv1)
    TextView tv1;
    @Bind(R.id.tv_account)
    TextView tvAccount;

    public static void open(Context context, long circleId) {
        Intent intent = new Intent(context, InputPcActivity.class);
        intent.putExtra("circle_id", circleId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_pc);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tvAccount.setText("账号：" + FastData.getAccount());

        ivScan.setOnClickListener(this);
        btnForget.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_scan:
                Intent intent = new Intent(this, com.xys.libzxing.zxing.activity.CaptureActivity.class);
                startActivityForResult(intent, 1);
                break;
            case R.id.btn_forget:
                ForgetPasswordActivity.open(this);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            String result = data.getStringExtra("result");
            scanLogin(result);
        }
    }

    private void scanLogin(String qrCode) {
        String result = new AES().decrypt(qrCode);
        try {
            JSONObject obj = new JSONObject(result);
            qrCode = obj.getString("code");
            if (qrCode != null && !qrCode.isEmpty()) {
                EnsureCodeLoginActivity.open(this, qrCode);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
