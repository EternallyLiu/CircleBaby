package cn.timeface.circle.baby.ui.growthcircle.mainpage.dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.support.utils.FastData;


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

        if (!TextUtils.isEmpty(childrenName)) {
            setChildrenName(childrenName);
        }
        if (positiveListener != null) {
            tvSubmit.setOnClickListener(positiveListener);
        }

        etChildrenName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setJoinMessage(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        return loadingView;
    }

    public void setChildrenName(String childrenName) {
        this.childrenName = childrenName;

        if (etChildrenName != null) {
            etChildrenName.setHint(childrenName);
            etChildrenName.setText(childrenName);
            etChildrenName.setEnabled(false);
        }

        setJoinMessage(childrenName);
    }

    private void setJoinMessage(String childrenName) {
        if (TextUtils.isEmpty(childrenName)) {
            this.joinMessage = "";
            if (etMessage != null) {
                etMessage.setHint(joinMessage);
                etMessage.setText(joinMessage);
            }
        } else {
            this.joinMessage = "我是" + childrenName + "的" + FastData.getRelationName();
            if (etMessage != null) {
                etMessage.setHint(joinMessage);
                etMessage.setText(joinMessage);
            }
        }
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
