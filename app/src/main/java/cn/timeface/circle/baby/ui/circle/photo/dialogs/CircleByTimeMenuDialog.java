package cn.timeface.circle.baby.ui.circle.photo.dialogs;

import android.content.Context;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.views.dialog.BaseDialog;

/**
 * 圈照片按时间fragment菜单
 * Created by lidonglin on 2017/3/20.
 */
public class CircleByTimeMenuDialog extends BaseDialog {
    private TextView tvInputMobile;
    private TextView tvInputPc;
    CircleByTimeMenuListener circleByTimeMenuListener;

    public interface CircleByTimeMenuListener {
        void inputMobile();
        void inputPc();
    }

    public CircleByTimeMenuDialog(Context context, CircleByTimeMenuListener circleByTimeMenuListener) {
        super(context);
        this.circleByTimeMenuListener = circleByTimeMenuListener;
        init();
    }

    private void init() {
        View mView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_circle_bytime, null);
        this.setContentView(mView);
        tvInputMobile = (TextView) mView.findViewById(R.id.tv_input_mobile);
        tvInputPc = (TextView) mView.findViewById(R.id.tv_input_pc);
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

    private void initListener() {
        tvInputMobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (circleByTimeMenuListener!=null)
                circleByTimeMenuListener.inputMobile();
                dismiss();
            }
        });
        tvInputPc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (circleByTimeMenuListener!=null)
                circleByTimeMenuListener.inputPc();
                dismiss();
            }
        });
    }

}
