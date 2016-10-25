package cn.timeface.circle.baby.dialogs;

import android.content.Context;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.CardPublishActivity;
import cn.timeface.circle.baby.activities.DiaryPublishActivity;
import cn.timeface.circle.baby.activities.PublishActivity;
import cn.timeface.circle.baby.views.dialog.BaseDialog;

/**
 * Created by lidonglin on 2016/6/25.
 */
public class PublishDialog extends BaseDialog {
    private Context context;
    private ImageView ivPublishSelect;
    private TextView tvVideo;
    private TextView tvPhoto;
    private TextView tvDiary;
    private TextView tvCard;
    private RelativeLayout rlPublish;

    public PublishDialog(Context context) {
        super(context, R.style.TFDialogStyle);
        this.context = context;
        init();
    }

    public PublishDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;
        init();
    }


    private void init() {
        View mView = LayoutInflater.from(getContext()).inflate(R.layout.view_publish, null);
        this.setContentView(mView);
        rlPublish = ButterKnife.findById(mView, R.id.rl_publish);
        ivPublishSelect = ButterKnife.findById(mView, R.id.iv_publish_select);
        tvVideo = ButterKnife.findById(mView, R.id.tv_video);
        tvPhoto = ButterKnife.findById(mView, R.id.tv_photo);
        tvDiary = ButterKnife.findById(mView, R.id.tv_diary);
        tvCard = ButterKnife.findById(mView, R.id.tv_card);

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
        rlPublish.setOnClickListener(v1 -> {
            dismiss();
        });
        ivPublishSelect.setOnClickListener(v -> {
            dismiss();
        });
        tvPhoto.setOnClickListener(v -> {
            dismiss();
            PublishActivity.open(context, PublishActivity.PHOTO);
        });
        tvVideo.setOnClickListener(v -> {
            dismiss();
            PublishActivity.open(context, PublishActivity.VIDEO);
        });
        tvDiary.setOnClickListener(v -> {
            dismiss();
//            PublishActivity.open(context, PublishActivity.DIALY);
            DiaryPublishActivity.open(context);
        });
        tvCard.setOnClickListener(v -> {
            dismiss();
//            PublishActivity.open(context, PublishActivity.CARD);
            CardPublishActivity.open(context);
        });
    }

}
