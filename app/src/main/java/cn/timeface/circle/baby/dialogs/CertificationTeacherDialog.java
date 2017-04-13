package cn.timeface.circle.baby.dialogs;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;

/**
 * Created by wangwei on 2017/4/13.
 */

public class CertificationTeacherDialog extends DialogFragment {

    private static final int MAX_LENGTH = 6;

    @Bind(R.id.dialog_title)
    TextView dialogTitle;
    @Bind(R.id.dialog_message)
    EditText editText;
    @Bind(R.id.tvCount)
    TextView tvCount;
    @Bind(R.id.dialog_negative_button)
    TextView dialogNegativeButton;
    @Bind(R.id.dialog_positive_button)
    TextView dialogPositiveButton;
    @Bind(R.id.dialog_main)
    LinearLayout dialogMain;
    @Bind(R.id.dialog_root)
    RelativeLayout dialogRoot;
    @Bind(R.id.iv_search_clear)
    ImageView ivSearchClear;

    private CharSequence title;
    private String positiveName;
    private String negativeName;
    private View.OnClickListener positiveListener;
    private View.OnClickListener negativeListener;

    public static CertificationTeacherDialog getInstance() {
        return new CertificationTeacherDialog();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_certification_teacher, container, false);
        ButterKnife.bind(this, view);

        dialogTitle.setText(title);
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

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setupTextCount(editText.getText().length());
                if (!TextUtils.isEmpty(editText.getText().toString())) {
                    ivSearchClear.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        ivSearchClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("");
                ivSearchClear.setVisibility(View.GONE);
            }
        });
        return view;
    }

    private void setupTextCount(int length) {
        tvCount.setText(length + "/" + MAX_LENGTH);
    }

    public String getEditContent() {
        String teacherName = editText.getText().toString();
        return teacherName;
    }

    public CertificationTeacherDialog setTitle(CharSequence title) {
        this.title = title;
        if (title != null && title.length() > 0) {
            this.title = title;
        }
        return this;
    }

    public CertificationTeacherDialog setPositiveButton(String text, View.OnClickListener listener) {
        if (text != null && text.length() > 0 && listener != null) {
            this.positiveName = text;
            this.positiveListener = listener;
        }
        return this;
    }

    public CertificationTeacherDialog setNegativeButton(String text, View.OnClickListener listener) {
        if (text != null && text.length() > 0 && listener != null) {
            this.negativeName = text;
            this.negativeListener = listener;
        }
        return this;
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
