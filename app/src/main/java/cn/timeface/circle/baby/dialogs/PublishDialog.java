package cn.timeface.circle.baby.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.CardPublishActivity;
import cn.timeface.circle.baby.activities.DiaryPublishActivity;
import cn.timeface.circle.baby.activities.PublishActivity;
import cn.timeface.circle.baby.activities.TimeLineEditActivity;
import cn.timeface.circle.baby.api.ApiFactory;
import cn.timeface.circle.baby.api.models.objs.TimeLineObj;
import cn.timeface.circle.baby.api.services.ApiService;
import cn.timeface.circle.baby.events.DeleteTimeLineEvent;
import cn.timeface.circle.baby.utils.FastData;
import cn.timeface.circle.baby.utils.ImageFactory;
import cn.timeface.circle.baby.utils.ToastUtil;
import cn.timeface.circle.baby.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.views.ShareDialog;
import cn.timeface.circle.baby.views.dialog.BaseDialog;
import cn.timeface.common.utils.DeviceUuidFactory;
import cn.timeface.common.utils.ShareSdkUtil;
import cn.timeface.common.utils.TimeFaceUtilInit;

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
