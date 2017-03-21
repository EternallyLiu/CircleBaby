package cn.timeface.circle.baby.ui.growthcircle.mainpage.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.ui.growthcircle.mainpage.activity.CreateCircleActivity;
import cn.timeface.circle.baby.ui.growthcircle.mainpage.activity.JoinCircleActivity;

public class CreateCircleDialog extends DialogFragment {

    @Bind(R.id.tv_create)
    TextView tvCreate;
    @Bind(R.id.tv_join)
    TextView tvJoin;

    public static CreateCircleDialog newInstance() {
        return new CreateCircleDialog();
    }

    public CreateCircleDialog() {
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        getDialog().getWindow().setBackgroundDrawableResource(R.color.trans);
        getDialog().getWindow().setWindowAnimations(R.style.DialogAnimation);
        getDialog().getWindow().setGravity(Gravity.BOTTOM);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_create_circle, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.tv_create, R.id.tv_join})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_create:
                dismiss();
                CreateCircleActivity.open(getContext());
                break;
            case R.id.tv_join:
                dismiss();
                JoinCircleActivity.open(getContext());
                break;
        }
    }
}
