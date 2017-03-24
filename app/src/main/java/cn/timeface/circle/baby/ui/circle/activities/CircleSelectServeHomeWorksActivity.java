package cn.timeface.circle.baby.ui.circle.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.support.utils.FragmentUtils;
import cn.timeface.circle.baby.ui.circle.dialogs.CircleSelectSchoolTaskTypeDialog;
import cn.timeface.circle.baby.ui.circle.fragments.CircleSelectBabyFragment;
import cn.timeface.circle.baby.ui.circle.fragments.CircleSelectSchoolTaskFragment;
import cn.timeface.circle.baby.ui.circle.photo.fragments.SelectCircleBabyFragment;
import cn.timeface.circle.baby.ui.growth.fragments.ServerTimeFragment;

/**
 * 圈作品家校纪念册选择作业
 * author : sunyanwei Created on 17-3-22
 * email : sunyanwei@timeface.cn
 */
public class CircleSelectServeHomeWorksActivity extends BaseAppCompatActivity
        implements View.OnClickListener, CircleSelectSchoolTaskTypeDialog.SelectTypeListener {

    @Bind(R.id.tv_content_type)
    TextView tvContentType;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.fl_container)
    FrameLayout flContainer;

    CircleSelectBabyFragment selectBabyFragment;
    CircleSelectSchoolTaskFragment selectSchoolTaskFragment;
    CircleSelectSchoolTaskTypeDialog selectSchoolTaskTypeDialog;
    String circleId;

    public static void open(Context context, String circleId){
        Intent intent = new Intent(context, CircleSelectServeHomeWorksActivity.class);
        intent.putExtra("circle_id", circleId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_select_serve_home_works);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        circleId = getIntent().getStringExtra("circle_id");
        tvContentType.setOnClickListener(this);

        if(selectBabyFragment == null) {
            selectBabyFragment = CircleSelectBabyFragment.newInstance(circleId);
        }
        showContent(selectBabyFragment);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_content_type:
                if(selectSchoolTaskTypeDialog == null){
                    selectSchoolTaskTypeDialog = CircleSelectSchoolTaskTypeDialog.newInstance(this);
                }
                selectSchoolTaskTypeDialog.show(getSupportFragmentManager(), "dialog");
                break;
        }
    }

    @Override
    public void selectTypeBaby() {
        if(selectBabyFragment == null) {
            selectBabyFragment = CircleSelectBabyFragment.newInstance(circleId);
        }
        showContent(selectBabyFragment);
    }

    Fragment currentFragment = null;
    public void showContent(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if (currentFragment != null) {
            ft.hide(currentFragment);
        }
        if (fragment.isAdded()) {
            ft.show(fragment);
        } else {
            ft.add(R.id.fl_container, fragment);
        }
        currentFragment = fragment;
        ft.commitAllowingStateLoss();
    }

    @Override
    public void selectTypeTask() {
        if(selectSchoolTaskFragment == null) {
            selectSchoolTaskFragment = CircleSelectSchoolTaskFragment.newInstance(circleId);
        }
        showContent(selectSchoolTaskFragment);
    }
}
