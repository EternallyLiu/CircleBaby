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
 * 选择圈时光类型dialog
 * author : sunyanwei Created on 17-3-20
 * email : sunyanwei@timeface.cn
 */
public class CircleSelectTimeTypeDialog extends DialogFragment implements View.OnClickListener {

    public static int TIME_TYPE_ALL = 1;//全部
    public static int TIME_TYPE_ME = 2;//按我发布的
    public static int TIME_TYPE_ABOUT_BABY = 3;//按@我宝宝的

    SelectTypeListener selectTypeListener;
    @Bind(R.id.tv_all_times)
    TextView tvAllTimes;
    @Bind(R.id.tv_mine_time)
    TextView tvAllMineTime;
    @Bind(R.id.tv_about_baby_times)
    TextView tvAboutBabyTimes;

    @Override
    public void onClick(View view) {
        dismissAllowingStateLoss();
        switch (view.getId()){
            case R.id.tv_all_times:
                selectTypeListener.selectTypeAll();
                break;

            case R.id.tv_mine_time:
                selectTypeListener.selectTypeMe();
                break;

            case R.id.tv_about_baby_times:
                selectTypeListener.selectTypeAboutMyBaby();
                break;
        }
    }

    public interface SelectTypeListener {
        void selectTypeAll();
        void selectTypeMe();
        void selectTypeAboutMyBaby();
    }

    public static CircleSelectTimeTypeDialog newInstance(SelectTypeListener selectTypeListener) {
        CircleSelectTimeTypeDialog selectTimeTypeDialog = new CircleSelectTimeTypeDialog();
        selectTimeTypeDialog.setSelectTypeListener(selectTypeListener);
        return selectTimeTypeDialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_circle_select_time_type, container, false);
        ButterKnife.bind(this, view);
        tvAboutBabyTimes.setOnClickListener(this);
        tvAllMineTime.setOnClickListener(this);
        tvAllTimes.setOnClickListener(this);
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

    public void setSelectTypeListener(SelectTypeListener selectTypeListener) {
        this.selectTypeListener = selectTypeListener;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
