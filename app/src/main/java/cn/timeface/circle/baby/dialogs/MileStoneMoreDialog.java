package cn.timeface.circle.baby.dialogs;

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

public class MileStoneMoreDialog extends BaseDialog implements View.OnClickListener {

    private Context context;
    private TextView tvMilestoneCreate;
    private TextView tvMilestoneShare;
    private TextView tvMilestoneBook;
    private View.OnClickListener onClickListener;

    public MileStoneMoreDialog(Context context) {
        super(context, R.style.TFDialogStyle);
        this.context = context;
        init();
    }

    public void show(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
        this.show();
    }

    private void init() {
        View mView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_milestone_more, null);
        this.setContentView(mView);
        tvMilestoneCreate = (TextView) mView.findViewById(R.id.tv_milestone_create);
        tvMilestoneShare = (TextView) mView.findViewById(R.id.tv_milestone_share);
        tvMilestoneBook = (TextView) mView.findViewById(R.id.tv_milestone_book);
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
        tvMilestoneCreate.setOnClickListener(this);
        tvMilestoneShare.setOnClickListener(this);
        tvMilestoneBook.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        dismiss();
        onClickListener.onClick(view);

    }
}
