package cn.timeface.circle.baby.ui.growthcircle.mainpage.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.views.dialog.BaseDialog;

public class CircleAlertDialog extends BaseDialog {

    private TextView tvTitle;
    private TextView tvMessage;
    private TextView tvSubmit;

    private CharSequence message;

    public CircleAlertDialog(Context context) {
        this(context, R.style.TFDialogStyle);
    }

    public CircleAlertDialog(Context context, int themeResId) {
        super(context, themeResId);
        init();
    }

    protected CircleAlertDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init();
    }

    private void init() {
        View mView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_circle_alert, null);
        this.setContentView(mView);

        tvTitle = ButterKnife.findById(mView, R.id.tv_title);
        tvMessage = ButterKnife.findById(mView, R.id.tv_message);
        tvSubmit = ButterKnife.findById(mView, R.id.tv_submit);

        this.setCancelable(false);
        this.setCanceledOnTouchOutside(false);
    }

    public void setMessage(CharSequence message) {
        this.message = message;
        tvMessage.setText(message);
    }

    public void setPositiveClickListener(View.OnClickListener listener) {
        tvSubmit.setOnClickListener(listener);
    }
}
