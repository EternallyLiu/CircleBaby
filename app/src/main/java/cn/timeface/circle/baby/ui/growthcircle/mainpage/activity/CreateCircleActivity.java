package cn.timeface.circle.baby.ui.growthcircle.mainpage.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.dialogs.TFDialog;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.network.NetworkError;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.growthcircle.mainpage.dialog.InputRealNameDialog;
import cn.timeface.circle.baby.ui.growthcircle.mainpage.event.CircleChangedEvent;
import cn.timeface.circle.baby.views.dialog.TFProgressDialog;
import cn.timeface.common.utils.CheckedUtil;
import rx.Subscription;

/**
 * 创建圈子
 */
public class CreateCircleActivity extends BaseAppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.et_circle_name)
    EditText etCircleName;
    @Bind(R.id.switch_public)
    SwitchCompat switchPublic;

    public static void open(Context context) {
        context.startActivity(new Intent(context, CreateCircleActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_create);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @OnClick({R.id.tv_create, R.id.tv_join})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_create:
                if (checkData()) {
                    createCircle();
                }
                break;
            case R.id.tv_join:
                JoinCircleActivity.open(this);
                break;
        }
    }

    private boolean checkData() {
        String circleName = etCircleName.getText().toString();
        if (TextUtils.isEmpty(circleName)) {
            Toast.makeText(this, "请输入圈名称", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (CheckedUtil.isNumeric(circleName)) {
            Toast.makeText(this, "圈名称不能为纯数字", Toast.LENGTH_SHORT).show();
            return false;
        }

        // 判断宝宝真实姓名
        if (TextUtils.isEmpty(FastData.getBabyRealName())) {
            showInputDialog();
            return false;
        }
        return true;
    }

    private void createCircle() {
        TFProgressDialog progressDialog = TFProgressDialog.getInstance("正在创建...");
        progressDialog.show(getSupportFragmentManager(), "TFProgressDialog");

        String circleName = etCircleName.getText().toString();
        Subscription s = apiService.createCircle(Uri.encode(circleName), switchPublic.isChecked() ? 1 : 0)
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(
                        response -> {
                            progressDialog.dismiss();
                            if (response.success()) {
                                EventBus.getDefault().post(
                                        new CircleChangedEvent(response.getCircleId(),
                                                CircleChangedEvent.TYPE_CREATED)
                                );
                                showCreatedDialog(response.getCircleId());
                            } else {
                                Toast.makeText(this, response.info, Toast.LENGTH_SHORT).show();
                            }
                        },
                        throwable -> {
                            progressDialog.dismiss();
                            NetworkError.showException(this, throwable);
                        }
                );
        addSubscription(s);
    }

    private void updateBabyRealName(String realName) {
        TFProgressDialog progressDialog = TFProgressDialog.getInstance();
        progressDialog.show(getSupportFragmentManager(), "TFProgressDialog");

        Subscription s = apiService.updateBabyRealName(FastData.getBabyId(), Uri.encode(realName))
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(
                        response -> {
                            progressDialog.dismiss();
                            if (response.success()) {
                                FastData.setBabyRealName(realName);
                                createCircle();
                            } else {
                                Toast.makeText(this, response.info, Toast.LENGTH_SHORT).show();
                            }
                        },
                        throwable -> {
                            progressDialog.dismiss();
                            NetworkError.showException(this, throwable);
                        }
                );
        addSubscription(s);
    }

    private void showInputDialog() {
        InputRealNameDialog inputRealNameDialog = InputRealNameDialog.getInstance();

        inputRealNameDialog.setPositiveListener(v -> {
            if (TextUtils.isEmpty(inputRealNameDialog.getChildrenName())) {
                Toast.makeText(this, "请输入孩子的真实姓名", Toast.LENGTH_SHORT).show();
                return;
            }

            inputRealNameDialog.dismiss();
            updateBabyRealName(inputRealNameDialog.getChildrenName());
        });
        inputRealNameDialog.show(getSupportFragmentManager(), "JoinCircleDialog");
    }

    private void showCreatedDialog(long circleId) {
        TFDialog dialog = TFDialog.getInstance();
        dialog.setMessage("你已成功创建圈子！");
        dialog.setTitle("提示");
        dialog.setPositiveButton("确定", v -> {
            dialog.dismiss();
            CircleMainActivity.open(this, circleId);
            finish();
        });
        dialog.setCancelable(false);
        dialog.show(getSupportFragmentManager(), "CreatedDialog");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
