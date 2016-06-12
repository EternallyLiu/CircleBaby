package cn.timeface.circle.baby.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
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
import cn.timeface.circle.baby.managers.services.SavePicInfoService;
import cn.timeface.circle.baby.utils.Remember;
import de.greenrobot.event.EventBus;

public class TabMainActivity extends BaseAppCompatActivity implements View.OnClickListener {
    @Bind(R.id.menu_home_tv)
    TextView menuHomeTv;
    @Bind(R.id.menu_mime_tv)
    TextView menuMimeTv;
    @Bind(R.id.iv_publish)
    ImageView ivPublish;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.foot_menu_ll)
    View footMenu;
    private long lastPressedTime = 0;
    private static final int TAB1 = 0;
    private static final int TAB2 = 1;
    @Bind(R.id.container)
    FrameLayout container;
    private BaseFragment currentFragment = null;

    public static void open(Context context) {
        context.startActivity(new Intent(context, TabMainActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth() / 3;
        Remember.putInt("width", width);

        SavePicInfoService.open(getApplicationContext());
        clickTab(menuHomeTv);
        ivPublish.setOnClickListener(this);

        EventBus.getDefault().post(new EventTabMainWake());
    }

    public void clickTab(View view) {

        switch (view.getId()) {
            case R.id.menu_home_tv:
                menuHomeTv.setSelected(true);
                menuMimeTv.setSelected(false);
                showContent(TAB1);
                break;
            case R.id.menu_mime_tv:
                menuHomeTv.setSelected(false);
                menuMimeTv.setSelected(true);
                showContent(TAB2);
                break;
        }
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
        currentFragment = fragment;
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
    public void onClick(View v) {
        Intent intent = new Intent(this, SelectPublishActivity.class);
        startActivity(intent);
    }
}
