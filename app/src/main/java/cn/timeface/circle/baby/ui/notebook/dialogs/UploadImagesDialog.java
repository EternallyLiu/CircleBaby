package cn.timeface.circle.baby.ui.notebook.dialogs;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.common.utils.DeviceUtil;

/**
 * 台历界面里  选择创建方式
 * <p>
 * Created by JieGuo on 16/10/24.
 */

public class UploadImagesDialog extends DialogFragment implements View.OnClickListener {

    private static final String TAG = "UploadImagesDialog";
    @Bind(R.id.tv_close)
    ImageView tvClose;
    @Bind(R.id.btn_upload)
    AppCompatButton btnUpload;
    @Bind(R.id.btn_nope)
    AppCompatButton btnNope;

    private Action action = null;

    public static UploadImagesDialog newInstance() {
        UploadImagesDialog dialog = new UploadImagesDialog();
        // dialog.setArguments(new Bundle());
        return dialog;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        int screenW = DeviceUtil.getScreenWidth(getActivity());
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout((int) (screenW * 0.85), WindowManager.LayoutParams.WRAP_CONTENT);
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.dialog_create_calendar_upload, container, false);
        ButterKnife.bind(this, contentView);
        bindEvent();
        return contentView;
    }

    private void bindEvent() {
        tvClose.setOnClickListener(this);
        btnNope.setOnClickListener(this);
        btnUpload.setOnClickListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onClick(View v) {

        if (action == null) return;

        try {
            switch (v.getId()) {
                case R.id.tv_close:
                    action.onFinish();
                    break;

                case R.id.btn_upload:
                    action.onUpload();
                    break;

                case R.id.btn_nope:
                    dismiss();
                    action.onDoLatter();
                    break;

                default:
                    break;
            }
        } catch (Exception e) {
            Log.e(TAG, "error", e);
        }
    }

    public interface Action {

        /**
         * 点击X 了
         */
        void onFinish();

        /**
         * 批量上传
         */
        void onUpload();

        /**
         * 过会再传
         */
        void onDoLatter();
    }
}
