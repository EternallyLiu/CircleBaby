package cn.timeface.circle.baby.ui.circle.dialogs;

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
import cn.timeface.circle.baby.R;

/**
 * 圈作品选择作业类型筛选条件
 * author : sunyanwei Created on 17-3-22
 * email : sunyanwei@timeface.cn
 */
public class CircleSelectSchoolTaskTypeDialog extends DialogFragment implements View.OnClickListener {

    public static int TASK_TYPE_MEMBER = 1;//按成员
    public static int TASK_TYPE_HOMEWORK = 2;//按作业

    SelectTypeListener selectTypeListener;
    @Bind(R.id.tv_baby)
    TextView tvBaby;
    @Bind(R.id.tv_task)
    TextView tvTask;

    @Override
    public void onClick(View view) {
        dismissAllowingStateLoss();
        switch (view.getId()) {
            case R.id.tv_baby:
                selectTypeListener.selectTypeBaby();
                break;

            case R.id.tv_task:
                selectTypeListener.selectTypeTask();
                break;

        }
    }

    public interface SelectTypeListener {
        void selectTypeBaby();
        void selectTypeTask();
    }

    public static CircleSelectSchoolTaskTypeDialog newInstance(CircleSelectSchoolTaskTypeDialog.SelectTypeListener selectTypeListener) {
        CircleSelectSchoolTaskTypeDialog selectTimeTypeDialog = new CircleSelectSchoolTaskTypeDialog();
        selectTimeTypeDialog.setSelectTypeListener(selectTypeListener);
        return selectTimeTypeDialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_circle_select_school_task_type, container, false);
        ButterKnife.bind(this, view);
        tvBaby.setOnClickListener(this);
        tvTask.setOnClickListener(this);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        getDialog().getWindow().setBackgroundDrawableResource(R.color.trans);
        getDialog().getWindow().setWindowAnimations(R.style.DialogAnimation);
        getDialog().getWindow().setGravity(Gravity.BOTTOM);
    }

    public void setSelectTypeListener(CircleSelectSchoolTaskTypeDialog.SelectTypeListener selectTypeListener) {
        this.selectTypeListener = selectTypeListener;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
