package cn.timeface.circle.baby.ui.growthcircle.mainpage.dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;


public class JoinCircleDialog extends DialogFragment {

    @Bind(R.id.et_message)
    EditText etMessage;
    @Bind(R.id.et_children_name)
    EditText etChildrenName;
    @Bind(R.id.tv_submit)
    TextView tvSubmit;

    private String joinMessage;
    private String childrenName;
    private View.OnClickListener positiveListener;

    public static JoinCircleDialog getInstance() {
        return new JoinCircleDialog();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View loadingView = inflater.inflate(R.layout.dialog_join_circle, container, false);
        ButterKnife.bind(this, loadingView);

        if (!TextUtils.isEmpty(joinMessage)) {
            etMessage.setHint(joinMessage);
            etMessage.setText(joinMessage);
        }
        if (!TextUtils.isEmpty(childrenName)) {
            etChildrenName.setHint(childrenName);
            etChildrenName.setText(childrenName);
        }
        if (positiveListener != null) {
            tvSubmit.setOnClickListener(positiveListener);
        }

        return loadingView;
    }

    public void setJoinMessage(String joinMessage) {
        this.joinMessage = joinMessage;
        etMessage.setHint(joinMessage);
        etMessage.setText(joinMessage);
        etMessage.setEnabled(false);
    }

    public void setChildrenName(String childrenName) {
        this.childrenName = childrenName;
        etChildrenName.setHint(childrenName);
        etChildrenName.setText(childrenName);
        etChildrenName.setEnabled(false);
    }

    public void setPositiveListener(View.OnClickListener positiveListener) {
        this.positiveListener = positiveListener;
    }

    public String getJoinMessage() {
        return etMessage.getText().toString();
    }

    public String getChildrenName() {
        return etChildrenName.getText().toString();
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

}
