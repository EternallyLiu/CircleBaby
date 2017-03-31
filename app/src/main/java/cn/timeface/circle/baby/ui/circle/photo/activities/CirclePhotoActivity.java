package cn.timeface.circle.baby.ui.circle.photo.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wechat.photopicker.fragment.BigImageFragment;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.FragmentBridgeActivity;
import cn.timeface.circle.baby.constants.TypeConstants;
import cn.timeface.circle.baby.support.managers.listeners.IEventBus;
import cn.timeface.circle.baby.support.mvp.bases.BasePresenterAppCompatActivity;
import cn.timeface.circle.baby.ui.circle.bean.CircleActivityAlbumObj;
import cn.timeface.circle.baby.ui.circle.bean.CircleMediaObj;
import cn.timeface.circle.baby.ui.circle.bean.CirclePhotoMonthObj;
import cn.timeface.circle.baby.ui.circle.bean.CircleUserInfo;
import cn.timeface.circle.baby.ui.circle.bean.QueryByCircleBabyObj;
import cn.timeface.circle.baby.ui.circle.bean.QueryByCircleUserObj;
import cn.timeface.circle.baby.ui.circle.photo.dialogs.CircleByTimeMenuDialog;
import cn.timeface.circle.baby.ui.circle.photo.dialogs.SelectCirclePhotoTypeDialog;
import cn.timeface.circle.baby.ui.circle.photo.fragments.CirclePhotoFragment;
import cn.timeface.circle.baby.ui.circle.photo.fragments.SelectCircleActivityFragment;
import cn.timeface.circle.baby.ui.circle.photo.fragments.SelectCircleBabyFragment;
import cn.timeface.circle.baby.ui.circle.photo.fragments.SelectCircleTimeFragment;
import cn.timeface.circle.baby.ui.circle.photo.fragments.SelectCircleUserFragment;
import cn.timeface.circle.baby.ui.circle.timelines.activity.PublishActivity;
import cn.timeface.circle.baby.ui.circle.timelines.events.CircleMediaEvent;
import cn.timeface.circle.baby.ui.timelines.Utils.LogUtil;
import cn.timeface.circle.baby.views.TFStateView;

/**
 * 圈照片
 * Created by lidonglin on 2017/3/15.
 */

public class CirclePhotoActivity extends BasePresenterAppCompatActivity implements IEventBus, View.OnClickListener, SelectCirclePhotoTypeDialog.CirclePhotoTypeListener, CircleByTimeMenuDialog.CircleByTimeMenuListener {
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
    private MenuItem itemTime;
    private MenuItem itemActivity;
    private long circleId;
    private boolean user;
    private boolean atBaby;
    private CircleUserInfo circleUserInfo;
    private ArrayList<CircleMediaObj> mediaList;
    private int babyId;
    private String babyName;


    public static void open(Context context, long circleId) {
        Intent intent = new Intent(context, CirclePhotoActivity.class);
        intent.putExtra("circle_id", circleId);
        context.startActivity(intent);
    }

    public static void open(Context context, CircleUserInfo circleUserInfo, boolean user) {
        Intent intent = new Intent(context, CirclePhotoActivity.class);
        intent.putExtra("circleUserInfo", circleUserInfo);
        intent.putExtra("user", user);
        context.startActivity(intent);
    }

    public static void open(Context context, long circleId, String babyName, int babyId, boolean atBaby) {
        Intent intent = new Intent(context, CirclePhotoActivity.class);
        intent.putExtra("circle_id", circleId);
        intent.putExtra("baby_name", babyName);
        intent.putExtra("baby_id", babyId);
        intent.putExtra("at_baby", atBaby);
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
        circleId = getIntent().getLongExtra("circle_id", 0);
        circleUserInfo = getIntent().getParcelableExtra("circleUserInfo");
        if (circleUserInfo != null) {
            circleId = circleUserInfo.getCircleId();
        }
        user = getIntent().getBooleanExtra("user", false);
        atBaby = getIntent().getBooleanExtra("at_baby", false);
        babyId = getIntent().getIntExtra("baby_id", 0);
        babyName = getIntent().getStringExtra("baby_name");
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
                tvContentType.setVisibility(View.GONE);
                tvContent.setVisibility(View.VISIBLE);
                tvContent.setText(albumObj.getAlbumName());

                showContentEx(CirclePhotoFragment.newInstance(TypeConstants.PHOTO_TYPE_ACTIVITY, albumObj.getAlbumId()));
                break;
            case R.id.ll_root:
                Object obj = view.getTag(R.string.tag_obj);
                if (obj instanceof QueryByCircleUserObj) {
                    //按发布人条目
                    tvContentType.setVisibility(View.GONE);
                    tvContent.setVisibility(View.VISIBLE);
                    tvContent.setText(((QueryByCircleUserObj) obj).getUserInfo().getCircleNickName());

                    showContentEx(CirclePhotoFragment.newInstance(TypeConstants.PHOTO_TYPE_USER, circleId, ((QueryByCircleUserObj) obj).getUserInfo().getCircleUserId()));

                } else if (obj instanceof QueryByCircleBabyObj) {
                    //按@圈的宝宝条目
                    tvContentType.setVisibility(View.GONE);
                    tvContent.setVisibility(View.VISIBLE);
                    tvContent.setText(((QueryByCircleBabyObj) obj).getBabyInfo().getNickName());

                    showContentEx(CirclePhotoFragment.newInstance(TypeConstants.PHOTO_TYPE_ATBABY, circleId, ((QueryByCircleBabyObj) obj).getBabyInfo().getBabyId()));
                }
                break;
            case R.id.rl_month:
                CirclePhotoMonthObj monthObj = (CirclePhotoMonthObj) view.getTag(R.string.tag_obj);
                tvContentType.setVisibility(View.GONE);
                tvContent.setVisibility(View.VISIBLE);
                tvContent.setText(monthObj.getYear() + "年" + monthObj.getMonth() + "月");

                showContentEx(CirclePhotoFragment.newInstance(TypeConstants.PHOTO_TYPE_TIME, circleId, monthObj.getYear(), monthObj.getMonth()));
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
            selectCircleTimeFragment = SelectCircleTimeFragment.newInstance(this, circleId);
        }
        showContent(selectCircleTimeFragment);
        if (fragmentShow)
            onClick(tvContentType);
    }

    @Override
    public void selectTypeUser() {
        tvContentType.setText("按发布人");
        if (selectCircleUserFragment == null) {
            selectCircleUserFragment = SelectCircleUserFragment.newInstance(this, circleId);
        }
        showContent(selectCircleUserFragment);
        if (fragmentShow)
            onClick(tvContentType);
    }

    @Override
    public void selectTypeActivity() {
        tvContentType.setText("按活动");
        if (selectCircleActivityFragment == null) {
            selectCircleActivityFragment = SelectCircleActivityFragment.newInstance(this, circleId);
        }
        showContent(selectCircleActivityFragment);
        if (fragmentShow)
            onClick(tvContentType);
    }

    @Override
    public void selectTypeAt() {
        tvContentType.setText("按@圈的宝宝");
        if (selectCircleBabyFragment == null) {
            selectCircleBabyFragment = SelectCircleBabyFragment.newInstance(this, circleId);
        }
        showContent(selectCircleBabyFragment);
        if (fragmentShow)
            onClick(tvContentType);
    }

    @Override
    public void setTypeText(String title) {

    }

    public void clickCirclePhotoView(View view) {
        ArrayList<String> paths = new ArrayList<>();
        CircleMediaObj mediaObj = (CircleMediaObj) view.getTag(R.string.tag_obj);
        mediaList = (ArrayList<CircleMediaObj>) view.getTag(R.string.tag_ex);
        for (int i = 0; i < mediaList.size(); i++) {
            paths.add(mediaList.get(i).getImgUrl());
        }
        int index = mediaList.indexOf(mediaObj);
        FragmentBridgeActivity.openBigimageFragment(this, 0, mediaList, paths, index, BigImageFragment.CIRCLE_MEDIA_IMAGE_EDITOR, true, false);

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

        initMenu();
    }

    private void initMenu() {
        if (currentFragment instanceof SelectCircleTimeFragment) {
            showMenu(false, true);
        } else if (currentFragment instanceof SelectCircleActivityFragment) {
            showMenu(true, false);
        } else {
            showMenu(false, false);
        }
    }

    Fragment currentFragmentEx = null;

    public void showContentEx(Fragment fragment) {
        flContainerEx.setVisibility(View.VISIBLE);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if (currentFragmentEx != null) {
            ft.hide(currentFragmentEx);
        }
        if (fragment.isAdded()) {
            ft.show(fragment);
        } else {
            ft.add(R.id.fl_container_ex, fragment);
        }
        currentFragmentEx = fragment;
        ft.commitAllowingStateLoss();

        canBack = true;
        showMenu(false, false);
    }

    public void showMenu(boolean activity, boolean time) {
        if (activity) {
            itemActivity.setVisible(true);
            itemTime.setVisible(false);
        } else if (time) {
            itemActivity.setVisible(false);
            itemTime.setVisible(true);
        } else {
            itemActivity.setVisible(false);
            itemTime.setVisible(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_circle_photo, menu);
        itemActivity = menu.findItem(R.id.action_by_actvity);
        itemTime = menu.findItem(R.id.action_by_time);
        selectTypeActivity();
        if (user) {
            tvContentType.setVisibility(View.GONE);
            tvContent.setVisibility(View.VISIBLE);
            tvContent.setText(circleUserInfo.getCircleNickName());
            showContentEx(CirclePhotoFragment.newInstance(TypeConstants.PHOTO_TYPE_USER, circleUserInfo.getCircleId(), Long.valueOf(circleUserInfo.getCircleUserId())));
            canBack = false;
        } else if (atBaby) {
            tvContentType.setVisibility(View.GONE);
            tvContent.setVisibility(View.VISIBLE);
            tvContent.setText(babyName);
            showContentEx(CirclePhotoFragment.newInstance(TypeConstants.PHOTO_TYPE_ATBABY, circleId, babyId));
            canBack = false;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_by_actvity:
                ActiveAddActivity.open(this, circleId);
                return true;
            case R.id.action_by_time:
                new CircleByTimeMenuDialog(this, this).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (canBack) {
            canBack = false;
            tvContent.setVisibility(View.GONE);
            tvContentType.setVisibility(View.VISIBLE);
            flContainerEx.setVisibility(View.GONE);

            initMenu();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void inputMobile() {
        PublishActivity.open(this);
    }

    @Override
    public void inputPc() {
        InputPcActivity.open(this, 0);
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onEvent(CircleMediaEvent event) {
        LogUtil.showLog("event===" + event.getType());
        if (event.getType() == 0 && event.getMediaObj() != null) {
            int index = mediaList.indexOf(event.getMediaObj());
            mediaList.get(index).setTips(event.getMediaObj().getTips());
            mediaList.get(index).setIsFavorite(event.getMediaObj().getIsFavorite());
            mediaList.get(index).setFavoritecount(event.getMediaObj().getFavoritecount());
            mediaList.get(index).setRelateBabys(event.getMediaObj().getRelateBabys());
        }
    }
}
