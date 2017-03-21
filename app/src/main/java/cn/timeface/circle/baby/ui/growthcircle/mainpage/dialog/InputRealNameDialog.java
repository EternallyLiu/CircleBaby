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

/**
 * 创建圈子宝宝真实姓名为空时弹出输入框
 */
public class InputRealNameDialog extends DialogFragment {

    @Bind(R.id.et_children_name)
    EditText etChildrenName;
    @Bind(R.id.tv_submit)
    TextView tvSubmit;

    private String childrenName;
    private View.OnClickListener positiveListener;

    public static InputRealNameDialog getInstance() {
        return new InputRealNameDialog();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View loadingView = inflater.inflate(R.layout.dialog_input_real_name, container, false);
        ButterKnife.bind(this, loadingView);

        if (!TextUtils.isEmpty(childrenName)) {
            etChildrenName.setHint(childrenName);
            etChildrenName.setText(childrenName);
        }
        if (positiveListener != null) {
            tvSubmit.setOnClickListener(positiveListener);
        }

        return loadingView;
    }

    public void setChildrenName(String childrenName) {
        this.childrenName = childrenName;
    }

    public void setPositiveListener(View.OnClickListener positiveListener) {
        this.positiveListener = positiveListener;
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
