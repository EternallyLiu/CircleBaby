package cn.timeface.circle.baby.ui.images.views;

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
import cn.timeface.circle.baby.support.api.models.objs.TimeLineObj;
import cn.timeface.circle.baby.views.dialog.BaseDialog;

/**
 * author : wangshuai Created on 2017/1/17
 * email : wangs1992321@gmail.com
 */
public class ImageActionDialog extends BaseDialog implements View.OnClickListener {
    private Context context;
    private RelativeLayout tvEdit;
    private RelativeLayout tvDlete;
    private TextView tvDownload;
    private RelativeLayout tvShare;
    private RelativeLayout tvCancel;

    private ClickCallBack clickListener;

    public ImageActionDialog(Context context) {
        super(context);
        init();
    }

    public ImageActionDialog(Context context, int theme) {
        super(context, theme);
        init();
    }

    public ImageActionDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init();
    }

    private void init() {
        View mView = LayoutInflater.from(getContext()).inflate(R.layout.view_timedetail_menu, null);
        this.setContentView(mView);
        tvEdit = ButterKnife.findById(mView, R.id.rl_add_content);
        tvDlete = ButterKnife.findById(mView, R.id.rl_delete);
        tvDownload = ButterKnife.findById(mView, R.id.tv_download);
        tvShare = ButterKnife.findById(mView, R.id.rl_share);
        tvCancel = ButterKnife.findById(mView, R.id.rl_cancel);

        initListener();

        Window window = getWindow();
        WindowManager m = window.getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = window.getAttributes();
        p.width = d.getWidth();
        window.setAttributes(p);
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.bottom_dialog_animation);
    }

    /**
     * 是否有编辑功能
     * @param flag
     */
    public void isEdit(boolean flag) {
        tvEdit.setVisibility(flag ? View.VISIBLE : View.GONE);
    }
    /**
     * 是否有删除功能
     * @param flag
     */
    public void isDelete(boolean flag) {
        tvDlete.setVisibility(flag ? View.VISIBLE : View.GONE);
    }

    public void isShared(boolean flag) {
        tvShare.setVisibility(flag ? View.VISIBLE : View.GONE);
    }
    public void isDownload(boolean flag) {
        tvDownload.setVisibility(flag ? View.VISIBLE : View.GONE);
    }

    private void initListener() {
        tvEdit.setOnClickListener(this);
        tvDlete.setOnClickListener(this);
        tvDownload.setOnClickListener(this);
        tvShare.setOnClickListener(this);
        tvCancel.setOnClickListener(this);
    }

    public ClickCallBack getClickListener() {
        return clickListener;
    }

    public void setClickListener(ClickCallBack clickListener) {
        this.clickListener = clickListener;
    }

    private void callback(View view, int type) {
        dismiss();
        if (getClickListener() != null)
            getClickListener().click(view, type);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_add_content:
                //编辑
                callback(v, 1);
                break;
            case R.id.rl_delete:
                //删除
                callback(v, 2);
                break;
            case R.id.tv_download:
                //下载
                callback(v, 3);
                break;
            case R.id.rl_share:
                //分享
                callback(v, 1);
                break;
            case R.id.rl_cancel:
                //取消
                callback(v, 0);
                break;
        }
    }

    public interface ClickCallBack {
        /**
         * 对话框点击回调页面
         *
         * @param view
         * @param type
         */
        public void click(View view, int type);
    }
}
