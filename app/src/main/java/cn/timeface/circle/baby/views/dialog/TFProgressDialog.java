package cn.timeface.circle.baby.views.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.TextView;

import cn.timeface.circle.baby.R;


/**
 * Created by yusen on 2014/11/20.
 */
public class TFProgressDialog extends BaseDialog {
    private TextView message;

    public TFProgressDialog(Context context) {
        super(context, R.style.TFProgressDialogStyle);
        init(context);
    }

    public TFProgressDialog(Context context, int theme) {
        super(context, theme);
        init(context);
    }

    public TFProgressDialog(Context context, boolean cancelable,
                            DialogInterface.OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init(context);
    }

    private void init(Context context) {
        View view = View.inflate(context, R.layout.layout_progress, null);
        setContentView(view);
        message = (TextView) view.findViewById(R.id.dialog_progress_message);
        message.setText("正在加载…");
        this.setCanceledOnTouchOutside(false);
    }

    public void setMessage(String str) {
        this.message.setText(str);
    }

    public void setMessage(int resId) {
        this.message.setText(resId);
    }
}
