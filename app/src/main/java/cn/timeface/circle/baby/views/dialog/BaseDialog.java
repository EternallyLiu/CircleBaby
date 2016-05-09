package cn.timeface.circle.baby.views.dialog;

import android.app.Dialog;
import android.content.Context;

import cn.timeface.circle.baby.R;


public class BaseDialog extends Dialog {

    public BaseDialog(Context context) {
        this(context, R.style.TFDialogStyle);
    }

    public BaseDialog(Context context, int theme) {
        super(context, theme);
    }

    public BaseDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }
}
