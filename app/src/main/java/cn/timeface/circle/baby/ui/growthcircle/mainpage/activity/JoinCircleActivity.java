package cn.timeface.circle.baby.ui.growthcircle.mainpage.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.dialogs.TFDialog;
import cn.timeface.circle.baby.support.managers.listeners.IEventBus;
import cn.timeface.circle.baby.support.utils.network.NetworkError;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.circle.bean.GrowthCircleObj;
import cn.timeface.circle.baby.ui.growthcircle.mainpage.event.JoinCircleCommitEvent;
import cn.timeface.circle.baby.views.dialog.TFProgressDialog;
import rx.Subscription;

/**
 * 加入圈子
 */
public class JoinCircleActivity extends BaseAppCompatActivity implements IEventBus {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.et_circle_name)
    EditText etCircleName;

    private TFProgressDialog progressDialog;

    public static void open(Context context) {
        context.startActivity(new Intent(context, JoinCircleActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_join);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = TFProgressDialog.getInstance();
    }

    @OnClick(R.id.tv_submit)
    public void onClick(View view) {
        String circleName = etCircleName.getText().toString();
        if (TextUtils.isEmpty(circleName)) {
            Toast.makeText(this, "请输入圈号或者圈名称", Toast.LENGTH_SHORT).show();
        } else {
            progressDialog.show(getSupportFragmentManager(), "ProgressDialog");
            reqData(circleName);
        }
    }

    private void reqData(String circleName) {
        Subscription s = apiService.queryByCircleNumOrName(Uri.encode(circleName))
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(
                        response -> {
                            progressDialog.dismiss();
                            if (response.success() && response.getErrorCode() == 0) {
                                if (response.getDataList() != null
                                        && response.getDataList().size() > 0) {
                                    if (response.getDataList().size() == 1) {
                                        CircleInfoActivity.open(this, response.getDataList().get(0));
                                    } else {
                                        JoinCircleMatchListActivity.open(this,
                                                (ArrayList<GrowthCircleObj>) response.getDataList());
                                    }
                                }
                            } else {
                                showErrorDialog(response.info);
                            }
                        },
                        throwable -> {
                            progressDialog.dismiss();
                            NetworkError.showException(this, throwable);
                        }
                );
        addSubscription(s);
    }

    private void showErrorDialog(String errorMsg) {
        TFDialog dialog = TFDialog.getInstance();
        dialog.setMessage(errorMsg);
        dialog.setPositiveButton("确定", v -> dialog.dismiss());
        dialog.show(getSupportFragmentManager(), "ErrorDialog");
    }

    @Subscribe
    public void onEvent(JoinCircleCommitEvent event) {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
