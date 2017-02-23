package cn.timeface.circle.baby.ui.calendar.dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.ui.calendar.events.PhotoUploadProgressEvent;
import cn.timeface.common.utils.DeviceUtil;

/**
 * Created by JieGuo on 16/10/24.
 */

public class UploadImageProgressDialog extends DialogFragment {

    @Bind(R.id.uploadProgressBar)
    ProgressBar uploadProgressBar;
    @Bind(R.id.tv_message)
    AppCompatTextView tvMessage;

    public static UploadImageProgressDialog newInstance() {
        return new UploadImageProgressDialog();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setCancelable(false);
        EventBus.getDefault().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.dialog_create_calendar_upload_progress, container, false);
        ButterKnife.bind(this, contentView);
        return contentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        int screenW = DeviceUtil.getScreenWidth(getActivity());
        getDialog().getWindow().setLayout((int) (screenW * 0.85f), WindowManager.LayoutParams.WRAP_CONTENT);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    @SuppressWarnings("unused")
    public void onUpdateEvent(PhotoUploadProgressEvent event) {
        if (event != null && event.getProgress() > 0 && uploadProgressBar != null) {

            uploadProgressBar.post(() -> {
                int progress = (int) (event.getProgress() * 100);
                uploadProgressBar.setProgress(progress);
            });
        }
    }
}
