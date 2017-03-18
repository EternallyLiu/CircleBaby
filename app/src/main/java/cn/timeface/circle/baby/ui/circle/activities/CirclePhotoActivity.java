package cn.timeface.circle.baby.ui.circle.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.support.mvp.bases.BasePresenterAppCompatActivity;
import cn.timeface.circle.baby.ui.circle.bean.CircleActivityAlbumObj;
import cn.timeface.circle.baby.ui.circle.bean.CirclePhotoMonthObj;
import cn.timeface.circle.baby.ui.circle.bean.QueryByCircleBabyObj;
import cn.timeface.circle.baby.ui.circle.bean.QueryByCircleUserObj;
import cn.timeface.circle.baby.ui.circle.dialogs.SelectCirclePhotoTypeDialog;
import cn.timeface.circle.baby.ui.circle.fragments.SelectCircleActivityFragment;
import cn.timeface.circle.baby.ui.circle.fragments.SelectCircleBabyFragment;
import cn.timeface.circle.baby.ui.circle.fragments.SelectCircleTimeFragment;
import cn.timeface.circle.baby.ui.circle.fragments.SelectCircleUserFragment;
import cn.timeface.circle.baby.views.TFStateView;

/**
 * 圈照片
 * Created by lidonglin on 2017/3/15.
 */

public class CirclePhotoActivity extends BasePresenterAppCompatActivity implements View.OnClickListener, SelectCirclePhotoTypeDialog.CirclePhotoTypeListener {
    boolean fragmentShow = false;
    boolean canBack = false;
    @Bind(R.id.tv_content_type)
    TextView tvContentType;
    @Bind(R.id.tv_content)
    TextView tvContent;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.fl_container)
    FrameLayout flContainer;
    @Bind(R.id.fl_container_ex)
    FrameLayout flContainerEx;
    @Bind(R.id.fl_container_type)
    FrameLayout flContainerType;
    @Bind(R.id.tf_stateView)
    TFStateView tfStateView;
    @Bind(R.id.content_circle_photo)
    RelativeLayout contentCirclePhoto;
    private SelectCirclePhotoTypeDialog selectCirclePhotoTypeDialog;
    SelectCircleUserFragment selectCircleUserFragment;
    private SelectCircleActivityFragment selectCircleActivityFragment;
    private SelectCircleBabyFragment selectCircleBabyFragment;
    private SelectCircleTimeFragment selectCircleTimeFragment;


    public static void open(Context context, long circleId, long babyId) {
        Intent intent = new Intent(context, CirclePhotoActivity.class);
        intent.putExtra("circle_id", circleId);
        intent.putExtra("baby_id", babyId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_photo);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        tfStateView.finish();
        tvContentType.setOnClickListener(this);
        selectTypeActivity();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_content_type:
                showSelectContentType(!fragmentShow);
                break;
            case R.id.ll_circle_activity:
                //按活动条目
                CircleActivityAlbumObj albumObj = (CircleActivityAlbumObj) view.getTag(R.string.tag_obj);
                showToast(albumObj.getAlbumName());
                break;
            case R.id.ll_root:
                Object obj = view.getTag(R.string.tag_obj);
                if (obj instanceof QueryByCircleUserObj) {
                    //按发布人条目
                    showToast(((QueryByCircleUserObj) obj).getUserInfo().getCircleNickName());
                    tvContentType.setVisibility(View.GONE);
                    tvContent.setVisibility(View.VISIBLE);
                    tvContent.setText(((QueryByCircleUserObj) obj).getUserInfo().getCircleNickName());


                } else if (obj instanceof QueryByCircleBabyObj) {
                    //按@圈的宝宝条目
                    showToast(((QueryByCircleBabyObj) obj).getBabyInfo().getNickName());
                }
                break;
            case R.id.rl_month:
                CirclePhotoMonthObj monthObj = (CirclePhotoMonthObj) view.getTag(R.string.tag_obj);
                showToast(monthObj.getYear() + monthObj.getMonth() + monthObj.getMediaCount() + "张");
                break;
        }
    }

    private void showSelectContentType(boolean show) {
        if (fragmentShow == show) return;
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        if (!show) {
            transaction.hide(selectCirclePhotoTypeDialog);
            fragmentShow = false;
        } else {
            if (selectCirclePhotoTypeDialog == null) {
                selectCirclePhotoTypeDialog = SelectCirclePhotoTypeDialog.newInstance(this);
                transaction.add(R.id.fl_container_type, selectCirclePhotoTypeDialog);
            } else {
                transaction.show(selectCirclePhotoTypeDialog);
            }
            fragmentShow = true;
        }
        transaction.commit();
    }

    @Override
    public void selectTypeTime() {
        tvContentType.setText("按时间");
        if (selectCircleTimeFragment == null) {
            selectCircleTimeFragment = SelectCircleTimeFragment.newInstance(this);
        }
        showContent(selectCircleTimeFragment);
        if (fragmentShow)
            onClick(tvContentType);
    }

    @Override
    public void selectTypeUser() {
        tvContentType.setText("按发布人");
        if (selectCircleUserFragment == null) {
            selectCircleUserFragment = SelectCircleUserFragment.newInstance(this);
        }
        showContent(selectCircleUserFragment);
        if (fragmentShow)
            onClick(tvContentType);
    }

    @Override
    public void selectTypeActivity() {
        tvContentType.setText("按活动");
        if (selectCircleActivityFragment == null) {
            selectCircleActivityFragment = SelectCircleActivityFragment.newInstance(this);
        }
        showContent(selectCircleActivityFragment);
        if (fragmentShow)
            onClick(tvContentType);
    }

    @Override
    public void selectTypeAt() {
        tvContentType.setText("按@圈的宝宝");
        if (selectCircleBabyFragment == null) {
            selectCircleBabyFragment = SelectCircleBabyFragment.newInstance(this);
        }
        showContent(selectCircleBabyFragment);
        if (fragmentShow)
            onClick(tvContentType);
    }

    @Override
    public void setTypeText(String title) {

    }

    @Override
    public void dismiss() {
        if (selectCirclePhotoTypeDialog != null) showSelectContentType(false);
    }

    Fragment currentFragment = null;

    public void showContent(Fragment fragment) {
        canBack = false;
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
}
