package cn.timeface.circle.baby.views.dialog;

import android.app.Dialog;
import android.graphics.drawable.Animatable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import cn.timeface.circle.baby.R;

/**
 * Created by yusen on 2014/11/20.
 */
public class TFProgressDialog extends DialogFragment {
    private TextView tvMessage;
    private ImageView ivProgress;
    private String message;

    public static TFProgressDialog getInstance(String message) {
        TFProgressDialog progressDialog = new TFProgressDialog();
        Bundle bundle = new Bundle();
        bundle.putString("message", message);
        progressDialog.setArguments(bundle);
        return progressDialog;
    }

    public void setTvMessage(String str) {
        this.message = str;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return initDialog();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return initView();
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    protected Dialog initDialog() {
        final Dialog dialog = new Dialog(getActivity(), R.style.TFProgressDialogStyle);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    protected View initView() {
        View view = View.inflate(getActivity(), R.layout.layout_progress, null);
        tvMessage = (TextView) view.findViewById(R.id.dialog_progress_message);
        ivProgress = (ImageView) view.findViewById(R.id.dialog_progress);
        this.message = getArguments() == null ? message : getArguments().getString("message");
        tvMessage.setText(TextUtils.isEmpty(message) ? getResources().getString(R.string.loading) : message);
        ((Animatable) ivProgress.getDrawable()).start();
        return view;
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        if(!this.isAdded()){
            FragmentTransaction ft = manager.beginTransaction();
            ft.add(this, tag);
            ft.commitAllowingStateLoss();
        }
    }

    public void setLoadingMsg(String tipMsg) {
        if (tvMessage != null) tvMessage.setText(tipMsg);
    }

    @Override
    public void dismiss() {
        try{
            super.dismiss();;
            super.dismissAllowingStateLoss();
        }catch (Exception e){
        }
    }
}
