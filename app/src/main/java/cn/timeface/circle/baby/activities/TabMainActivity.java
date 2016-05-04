package cn.timeface.circle.baby.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.events.EventTabMainWake;
import cn.timeface.circle.baby.fragments.HomeFragment;
import cn.timeface.circle.baby.fragments.MineFragment;
import cn.timeface.circle.baby.fragments.base.BaseFragment;
import cn.timeface.circle.baby.views.MainCircleMenuPopup;
import de.greenrobot.event.EventBus;

public class TabMainActivity extends BaseAppCompatActivity implements MainCircleMenuPopup.OnMenuClickListener {
    @Bind(R.id.menu_home_tv)
    TextView menuHomeTv;
    @Bind(R.id.menu_mime_tv)
    TextView menuMimeTv;
    @Bind(R.id.iv_publish)
    ImageView ivPublish;
    private long lastPressedTime = 0;
    private static final int TAB1 = 0;
    private static final int TAB2 = 1;
    @Bind(R.id.container)
    FrameLayout container;
    private BaseFragment currentFragment = null;
    private MainCircleMenuPopup circleMenuPopu;

    public static void open(Context context) {
        context.startActivity(new Intent(context, TabMainActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_main);
        ButterKnife.bind(this);

        clickTab(findViewById(R.id.menu_home_tv));

        EventBus.getDefault().post(new EventTabMainWake());
    }

    public void clickTab(View view) {

        switch (view.getId()) {
            case R.id.menu_home_tv:
                showContent(TAB1);
                break;
            case R.id.menu_mime_tv:
                showContent(TAB2);
                break;
        }
    }

    public void clickPublish(View view){
        if (circleMenuPopu == null) {
            circleMenuPopu = new MainCircleMenuPopup(this);
            circleMenuPopu.setOnMenuClickListener(this);
        }
        circleMenuPopu.showAtLocation(getWindow().getDecorView(), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

    }

    public BaseFragment getFragment(int navType) {
        switch (navType) {
            case TAB1:
                return HomeFragment.newInstance("首页");
            case TAB2:
                return MineFragment.newInstance("我的");
        }
        return null;
    }


    public void showContent(int navType) {

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        BaseFragment fragment = (BaseFragment) fm.findFragmentByTag(navType + "");
        if (fragment == null) {
            fragment = getFragment(navType);
        }
        if (currentFragment != null && currentFragment.equals(fragment)) {
            return;
        }

        invalidateOptionsMenu();
        if (currentFragment != null) {
            ft.hide(currentFragment);
        }
        if (fragment.isAdded()) {
            ft.show(fragment);
        } else {
            ft.add(R.id.container, fragment, navType + "");
        }
        ft.commitAllowingStateLoss();
    }

    @Override
    public void onBackPressed() {
        if (isExit()) {
            super.onBackPressed();
        }
    }

    /**
     * 是否是退出操作
     *
     * @return true 如果两次返回键点击事件在2s之内
     */
    private boolean isExit() {
        if (0 == lastPressedTime
                || System.currentTimeMillis() - lastPressedTime > 2000) {
            lastPressedTime = System.currentTimeMillis();
            Toast.makeText(this, R.string.tip_exit, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public void clickMenu(View view) {
        switch (view.getId()) {
            case R.id.tv_photo://相机
//                PublishEditActivity.open(this, PublishEditActivity.TYPE_TAKE_PHOTO, "", "");
                break;
            case R.id.tv_video://扫描仪
//                if (!Utils.getArchType(this).equals(Utils.CPU_ARCHITECTURE_TYPE_64)) {
//                    PublishEditActivity.open(this, PublishEditActivity.TYPE_SCAN, "", "");
//                } else {
//                    Toast.makeText(MainActivity.this, "很抱歉，此机型不支持使用扫描仪！", Toast.LENGTH_SHORT).show();
//                }
                break;
            case R.id.tv_diary://相册
//                PublishEditActivity.open(this, PublishEditActivity.TYPE_PHOTO, "", "");
                break;
            case R.id.tv_card://文本
//                PublishEditActivity.open(this, PublishEditActivity.TYPE_TEXT, "", "");
                break;
        }
    }
}
