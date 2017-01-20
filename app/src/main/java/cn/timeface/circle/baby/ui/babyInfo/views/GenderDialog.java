package cn.timeface.circle.baby.ui.babyInfo.views;

import android.content.Context;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.views.dialog.BaseDialog;

/**
 * author : wangshuai Created on 2017/1/20
 * email : wangs1992321@gmail.com
 */
public class GenderDialog extends BaseDialog implements View.OnClickListener {

    private Context context;
    private RelativeLayout tvEdit;
    private RelativeLayout tvDlete;
    private TextView tvDownload;
    private RelativeLayout tvShare;
    private RelativeLayout tvCancel;

    private GenderSelectedListener genderSelectedListener;

    public GenderDialog(Context context) {
        super(context);
        init();
    }

    public GenderDialog(Context context, int theme) {
        super(context, theme);
        init();
    }

    public GenderDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init();
    }

    private void init() {
        View mView = LayoutInflater.from(getContext()).inflate(R.layout.gender_select_dialog, null);
        this.setContentView(mView);
        tvEdit = ButterKnife.findById(mView, R.id.rl_add_content);
        tvDlete = ButterKnife.findById(mView, R.id.rl_delete);
        tvDownload = ButterKnife.findById(mView, R.id.tv_download);
        tvShare = ButterKnife.findById(mView, R.id.rl_share);
        tvCancel = ButterKnife.findById(mView, R.id.rl_cancel);

        initListener();
        tvEdit.setVisibility(View.VISIBLE);
        tvDlete.setVisibility(View.VISIBLE);
        tvDownload.setVisibility(View.VISIBLE);
        tvShare.setVisibility(View.GONE);
        tvCancel.setVisibility(View.GONE);

        Window window = getWindow();
        WindowManager m = window.getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = window.getAttributes();
        p.width = d.getWidth();
        window.setAttributes(p);
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.bottom_dialog_animation);
    }

    private void initListener() {
        tvEdit.setOnClickListener(this);
        tvDlete.setOnClickListener(this);
        tvDownload.setOnClickListener(this);
        tvShare.setOnClickListener(this);
        tvCancel.setOnClickListener(this);
    }

    public GenderSelectedListener getgenderSelectedListener() {
        return genderSelectedListener;
    }

    public void setgenderSelectedListener(GenderSelectedListener genderSelectedListener) {
        this.genderSelectedListener = genderSelectedListener;
    }

    public interface GenderSelectedListener {
        /**
         * 返回性别
         *
         * @param view
         * @param type
         */
        public void genderSelected(int gender);
    }

    private void callback(int type) {
        dismiss();
        if (getgenderSelectedListener() != null)
            getgenderSelectedListener().genderSelected(type);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.rl_add_content:
                //女
                callback(0);
                break;
            case R.id.rl_delete:
                //男
                callback(1);
                break;
            case R.id.tv_download:
                //龙凤胎
                callback(2);
                break;
            default:
                dismiss();
                break;
        }
    }
}
