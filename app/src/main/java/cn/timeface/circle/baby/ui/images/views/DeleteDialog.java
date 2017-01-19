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

    private SubmitListener submitListener;

    public DeleteDialog(Context context) {
        super(context);
        init();
    }

    public DeleteDialog(Context context, int theme) {
        super(context, theme);
        init();
    }

    public DeleteDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init();
    }

    public void setMessage(String message) {
        if (this.message != null && !TextUtils.isEmpty(message))
            this.message.setText(message);
    }

    public void setTitle(String title) {
        if (this.title != null && !TextUtils.isEmpty(title)) {
            this.title.setVisibility(View.VISIBLE);
            this.title.setText(title);
        }
    }

    private void init() {

        View mView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_delete_layout, null);
        this.setContentView(mView);
        title = (TextView) mView.findViewById(R.id.title);
        message = (TextView) mView.findViewById(R.id.message);
        cancel = (Button) mView.findViewById(R.id.cancel);
        submit = (Button) mView.findViewById(R.id.submit);
        title.setVisibility(View.GONE);

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
        }
    }

    public interface SubmitListener {
        /**
         * 对话框点击回调页面
         *
         * @param view
         * @param type
         */
        public void submit();
    }
}
