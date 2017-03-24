package cn.timeface.circle.baby.ui.circle.timelines.dialog;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.timeface.circle.baby.App;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.support.api.ApiFactory;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.support.utils.Utils;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.circle.bean.CircleMediaObj;
import cn.timeface.circle.baby.ui.circle.bean.GetCircleAllBabyObj;
import cn.timeface.circle.baby.ui.circle.timelines.adapter.RelateBabyAdapter;
import cn.timeface.circle.baby.ui.images.views.DeleteDialog;
import cn.timeface.circle.baby.ui.images.views.FlipImageView;
import cn.timeface.circle.baby.ui.timelines.Utils.JSONUtils;
import cn.timeface.circle.baby.ui.timelines.Utils.LogUtil;
import cn.timeface.circle.baby.ui.timelines.adapters.BaseAdapter;
import cn.timeface.circle.baby.ui.timelines.adapters.ViewHolder;
import cn.timeface.circle.baby.views.ClearableEditText;
import cn.timeface.circle.baby.views.dialog.BaseDialog;
import rx.Subscription;

/**
 * author : wangshuai Created on 2017/3/20
 * email : wangs1992321@gmail.com
 */
public class CircleNewBabyDialog extends BaseDialog implements View.OnClickListener {

    private ClearableEditText etInput;
    private Button btnSubmit;
    private String babyNames;
    private Subscription currentSubscription;
    private DeleteDialog deleteDialog;


    public CircleNewBabyDialog(Context context) {
        super(context);
        init();
    }

    private CircleNewBabyDialog(Context context, int theme) {
        super(context, theme);
        init();
    }

    private CircleNewBabyDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init();
    }

    private void init() {
        View mView = LayoutInflater.from(getContext()).inflate(R.layout.circle_new_baby_dialog_layout, null);
        this.setContentView(mView);
        btnSubmit = (Button) mView.findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(this);
        etInput = (ClearableEditText) mView.findViewById(R.id.et_input);

        Window window = getWindow();
        WindowManager m = window.getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = window.getAttributes();
        p.width = d.getWidth();
        p.height = App.mScreenHeight / 3 * 2;
        p.dimAmount = 0;
        window.setAttributes(p);
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.right_dialog_enter);
    }


    @Override
    public void dismiss() {
        if (currentSubscription != null && !currentSubscription.isUnsubscribed()) {
            currentSubscription.unsubscribe();
            currentSubscription = null;
        }
        super.dismiss();
    }

    @Override
    public void show() {
        super.show();
    }

    public String getBabyNames() {
        return babyNames;
    }

    public void setBabyNames(String babyNames) {
        this.babyNames = babyNames;
    }

    private void newBaby(String babyName) {
        ApiFactory.getApi().getApiService().addBaby(FastData.getCircleId(), Uri.encode(babyName))
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(baseResponse -> {
                    if (baseResponse.success()) {
                        dismiss();
                        ;
                    } else ToastUtil.showToast(getContext(), baseResponse.getInfo());
                }, throwable -> LogUtil.showError(throwable));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit:
                String babyName = etInput.getText().toString();
                if (TextUtils.isEmpty(babyName)) {
                    ToastUtil.showToast(getContext(), "宝贝名字不能为空");
                    return;
                } else if (Utils.getByteSize(babyName) > 16) {
                    ToastUtil.showToast(getContext(), String.format("%s" + getContext().getString(R.string.input_max_tip), "宝贝名字", 16));
                    return;
                } else if (!TextUtils.isEmpty(babyNames) && babyNames.contains(babyName)) {
                    if (deleteDialog == null)
                        deleteDialog = new DeleteDialog(getContext());
                    deleteDialog.setMessage(String.format("%s 已被添加至成员列表中，\n是否重复添加", babyName));
                    deleteDialog.setTitle("提示");
                    deleteDialog.getSubmit().setText("确认添加");
                    deleteDialog.setSubmitListener(() -> newBaby(babyName));
                    if (!deleteDialog.isShowing())
                        deleteDialog.show();
                } else newBaby(babyName);

                break;
        }
    }
}
