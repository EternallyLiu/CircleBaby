package cn.timeface.circle.baby.ui.images.views;

import android.content.Context;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.views.dialog.BaseDialog;

/**
 * author : wangshuai Created on 2017/1/18
 * email : wangs1992321@gmail.com
 */
public class DeleteDialog extends BaseDialog implements View.OnClickListener {

    private TextView title, message;
    private Button cancel, submit;
    private ImageView close;

    private View titleLine;

    private SubmitListener submitListener;
    private CloseListener closeListener;

    public CloseListener getCloseListener() {
        return closeListener;
    }

    public void setCloseListener(CloseListener closeListener) {
        this.closeListener = closeListener;
    }

    public DeleteDialog(Context context) {
        super(context);
        init();
    }

    public DeleteDialog(Context context, int theme) {
        super(context, theme);
        init();
    }

    public void hideCacelButton() {
        if (this.cancel != null)
            this.cancel.setVisibility(View.GONE);
    }

    public void setCancelTip(CharSequence tip){
        if (this.cancel!=null)this.cancel.setText(tip);
    }
    public void setSubmitTip(CharSequence tip){
        if (this.submit!=null)submit.setText(tip);
    }

    public Button getSubmit() {
        return submit;
    }

    public DeleteDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init();
    }

    public void setMessage(CharSequence message) {
        if (this.message != null && !TextUtils.isEmpty(message))
            this.message.setText(message);
    }

    public void setMessageGravity(int gravity) {
        if (this.message != null)
            this.message.setGravity(gravity);
    }

    public void showClose(boolean flag) {
        if (this.close != null)
            this.close.setVisibility(flag ? View.VISIBLE : View.GONE);
    }

    public void setTitle(CharSequence title) {
        if (this.title != null && !TextUtils.isEmpty(title)) {
            this.title.setVisibility(View.VISIBLE);
            this.titleLine.setVisibility(View.VISIBLE);
            this.title.setText(title);
        }else if (this.title!=null){
            this.title.setVisibility(View.GONE);
            this.titleLine.setVisibility(View.GONE);

        }
    }

    private void init() {

        View mView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_delete_layout, null);
        this.setContentView(mView);
        title = (TextView) mView.findViewById(R.id.title);
        message = (TextView) mView.findViewById(R.id.message);
        cancel = (Button) mView.findViewById(R.id.cancel);
        submit = (Button) mView.findViewById(R.id.submit);
        close = (ImageView) mView.findViewById(R.id.close);
        close.setOnClickListener(this);
        title.setVisibility(View.GONE);
        titleLine=mView.findViewById(R.id.title_line);
        titleLine.setVisibility(View.GONE);

        cancel.setOnClickListener(this);
        submit.setOnClickListener(this);

        Window window = getWindow();
//        WindowManager m = window.getWindowManager();
//        Display d = m.getDefaultDisplay();
//        WindowManager.LayoutParams p = window.getAttributes();
//        p.width = d.getWidth();
//        window.setAttributes(p);
        window.setGravity(Gravity.CENTER);
        window.setWindowAnimations(R.style.bottom_dialog_animation);
    }

    public SubmitListener getSubmitListener() {
        return submitListener;
    }

    public void setSubmitListener(SubmitListener submitListener) {
        this.submitListener = submitListener;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel:
                dismiss();
                break;
            case R.id.submit:
                if (getSubmitListener() != null) {
                    getSubmitListener().submit();
                    dismiss();
                }
                break;
            case R.id.close:
                if (getCloseListener() != null)
                    getCloseListener().close();
                dismiss();
                break;
        }
    }


    public interface SubmitListener {
        /**
         * 对话框点击回调页面
         */
        public void submit();
    }

    public interface CloseListener {
        public void close();
    }
}
