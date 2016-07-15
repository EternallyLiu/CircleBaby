package cn.timeface.circle.baby.dialogs;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.App;
import cn.timeface.circle.baby.R;

/**
 * Created by tao on 2015/11/11.
 */
public class TFDialog extends DialogFragment {

    @Bind(R.id.dialog_title)
    TextView dialogTitle;
    @Bind(R.id.dialog_title_line)
    ImageView dialogTitleLine;
    @Bind(R.id.dialog_message)
    TextView dialogMessage;
    @Bind(R.id.dialog_negative_button)
    TextView dialogNegativeButton;
    @Bind(R.id.dialog_positive_button)
    TextView dialogPositiveButton;
    @Bind(R.id.dialog_main)
    LinearLayout dialogMain;
    private String message;
    private String title;
    private String positiveName;
    private String negativeName;
    private View.OnClickListener positiveListener;
    private View.OnClickListener negativeListener;
    private int pBg;
    private int nBg;

    public static TFDialog getInstance() {
        return new TFDialog();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View loadingView = inflater.inflate(R.layout.dialog_standard, container, false);
        dialogMessage = (TextView) loadingView.findViewById(R.id.dialog_message);
        ButterKnife.bind(this, loadingView);
        if (!TextUtils.isEmpty(message)) {
            dialogMessage.setText(message);
            dialogMessage.setVisibility(View.VISIBLE);
        }
        if (TextUtils.isEmpty(title)) {
            title = "提示";
        }
        dialogTitle.setText(title);
        dialogTitle.setVisibility(View.VISIBLE);
        dialogTitleLine.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(positiveName) && positiveListener != null) {
            dialogPositiveButton.setVisibility(View.VISIBLE);
            dialogPositiveButton.setText(positiveName);
            dialogPositiveButton.setOnClickListener(positiveListener);
        }
        if (!TextUtils.isEmpty(negativeName) && negativeListener != null) {
            dialogNegativeButton.setVisibility(View.VISIBLE);
            dialogNegativeButton.setText(negativeName);
            dialogNegativeButton.setOnClickListener(negativeListener);
        }
        if (pBg != 0) dialogPositiveButton.setBackgroundResource(pBg);
        if (nBg != 0) dialogNegativeButton.setBackgroundResource(nBg);
        return loadingView;
    }

    public TFDialog setTitle(String title) {
        this.title = title;
        if (title != null && title.length() > 0) {
            this.title = title;
        }
        return this;
    }

    public TFDialog setTitle(@StringRes int resId) {
        return setTitle(App.getInstance().getString(resId));
    }

    public TFDialog setMessage(String message) {
        if (message != null && message.length() > 0) {
            this.message = message;
        }
        return this;
    }

    public TFDialog setMessage(@StringRes int resId) {
        return setMessage(App.getInstance().getString(resId));
    }

    public TFDialog setPositiveButton(String text, View.OnClickListener listener) {
        if (text != null && text.length() > 0 && listener != null) {
            this.positiveName = text;
            this.positiveListener = listener;
        }
        return this;
    }

    public TFDialog setPositiveButton(@StringRes int resId, View.OnClickListener listener) {
        setPositiveButton(App.getInstance().getString(resId), listener);
        return this;
    }

    public TFDialog setNegativeButton(String text, View.OnClickListener listener) {
        if (text != null && text.length() > 0 && listener != null) {
            this.negativeName = text;
            this.negativeListener = listener;
        }
        return this;
    }

    public TFDialog setNegativeButton(@StringRes int resId, View.OnClickListener listener) {
        return setNegativeButton(App.getInstance().getString(resId), listener);
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        window.setBackgroundDrawable(new
                ColorDrawable(Color.TRANSPARENT));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public void setpBg(@DrawableRes int pBg) {
        this.pBg = pBg;
    }

    public void setnBg(@DrawableRes int nBg) {
        this.nBg = nBg;
    }
}
