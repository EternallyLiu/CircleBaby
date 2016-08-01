package cn.timeface.circle.baby.dialogs;

import android.content.Context;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.views.dialog.BaseDialog;

public class TimeLineActivityCommentDialog extends BaseDialog{

    RelativeLayout rl_action;
    RelativeLayout rl_cancel;

    public TimeLineActivityCommentDialog(Context context) {
        super(context, R.style.TFDialogStyle);
        init();
    }

    private void init() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_comment_menu, null);
        rl_action = (RelativeLayout) view.findViewById(R.id.rl_action);
        rl_cancel = (RelativeLayout) view.findViewById(R.id.rl_cancel);

        Window window = getWindow();
        WindowManager m = window.getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = window.getAttributes();
        p.width = d.getWidth();
        window.setAttributes(p);
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.bottom_dialog_animation);
    }
}
