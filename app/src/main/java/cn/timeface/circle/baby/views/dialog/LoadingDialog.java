package cn.timeface.circle.baby.views.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import cn.timeface.circle.baby.R;


/**
 * Created by zhsheng on 2016/5/5.
 */
public class LoadingDialog extends DialogFragment {
    private TextView tv_loading_msg;
    private ImageView imageView;
    private CharSequence tvLoadingMsg;

    public static LoadingDialog getInstance() {
        return new LoadingDialog();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View loadingView = inflater.inflate(R.layout.view_loading, container, false);
        imageView = (ImageView) loadingView.findViewById(R.id.pb_loading);
        tv_loading_msg = (TextView) loadingView.findViewById(R.id.tv_loading_msg);
        if (!TextUtils.isEmpty(tvLoadingMsg)) tv_loading_msg.setText(tvLoadingMsg);
        return loadingView;
    }

    @Override
    public void onResume() {
        super.onResume();
      //  ((Animatable) imageView.getDrawable()).start();
    }

    public void setLoadingMsg(CharSequence loading_msg) {
        if (!TextUtils.isEmpty(loading_msg)) {
            tvLoadingMsg = loading_msg;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        window.setBackgroundDrawable(new
                ColorDrawable(Color.TRANSPARENT));
    }
}
