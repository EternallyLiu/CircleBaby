package cn.timeface.circle.baby.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.WindowCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tbruyelle.rxpermissions.RxPermissions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.api.models.DistrictModel;
import cn.timeface.circle.baby.api.models.responses.DistrictListResponse;
import cn.timeface.circle.baby.api.services.OpenUploadServices;
import cn.timeface.circle.baby.constants.TypeConstant;
import cn.timeface.circle.baby.dialogs.PublishDialog;
import cn.timeface.circle.baby.events.ConfirmRelationEvent;
import cn.timeface.circle.baby.events.EventTabMainWake;
import cn.timeface.circle.baby.events.LogoutEvent;
import cn.timeface.circle.baby.fragments.HomeFragment;
import cn.timeface.circle.baby.fragments.MineFragment;
import cn.timeface.circle.baby.fragments.base.BaseFragment;
import cn.timeface.circle.baby.managers.listeners.IEventBus;
import cn.timeface.circle.baby.managers.services.SavePicInfoService;
import cn.timeface.circle.baby.utils.FastData;
import cn.timeface.circle.baby.utils.Remember;
import cn.timeface.circle.baby.utils.rxutils.SchedulersCompat;
import cn.timeface.common.utils.CommonUtil;
import cn.timeface.open.GlobalSetting;
import cn.timeface.open.api.models.objs.TFOUserObj;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;

public class TabMainActivity extends BaseAppCompatActivity implements View.OnClickListener ,IEventBus{
    @Bind(R.id.menu_home_tv)
    TextView menuHomeTv;
    @Bind(R.id.menu_mime_tv)
    TextView menuMimeTv;
    @Bind(R.id.iv_publish)
    ImageView ivPublish;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.rl_toensurerelation)
    RelativeLayout rlToensurerelation;
    @Bind(R.id.tv_toensurerelation)
    TextView tvToensurerelation;
    @Bind(R.id.foot_menu_ll)
    View footMenu;
    private long lastPressedTime = 0;
    private static final int TAB1 = 0;
    private static final int TAB2 = 1;
    @Bind(R.id.container)
    FrameLayout container;
    private BaseFragment currentFragment = null;
    private PopupWindow popupWindow;

    public static void open(Context context) {
        context.startActivity(new Intent(context, TabMainActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        supportRequestWindowFeature(WindowCompat.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.activity_tab_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        updateRegionDB();
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth() / 3;
        Remember.putInt("width", width);

        RxPermissions.getInstance(this).request(Manifest.permission.MEDIA_CONTENT_CONTROL)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        if (aBoolean) {
                            SavePicInfoService.open(getApplicationContext());
                        }
                    }
                });

        clickTab(menuHomeTv);
        ivPublish.setOnClickListener(this);
        tvToensurerelation.setOnClickListener(this);
        rlToensurerelation.setOnClickListener(this);

        if (TextUtils.isEmpty(FastData.getRelationName())) {
            rlToensurerelation.setVisibility(View.VISIBLE);
        }else{
            rlToensurerelation.setVisibility(View.GONE);
        }

        EventBus.getDefault().post(new EventTabMainWake());

        RxPermissions.getInstance(this)
                .request(Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        if (aBoolean) {
                            //初始化开放平台
                            TFOUserObj tfoUserObj = new TFOUserObj();
                            tfoUserObj.setAvatar(FastData.getAvatar());
                            tfoUserObj.setGender(FastData.getBabyGender());
                            tfoUserObj.setNick_name(FastData.getUserName());
                            tfoUserObj.setPhone(FastData.getAccount());

                            GlobalSetting.getInstance().init(TypeConstant.APP_ID, TypeConstant.APP_SECRET, tfoUserObj, new OpenUploadServices());
                        }
                    }
                });}

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
        switch (v.getId()) {
            case R.id.iv_publish:
                new PublishDialog(this).show();
                break;
            case R.id.tv_toensurerelation:
            case R.id.rl_toensurerelation:
                Intent intent = new Intent(this, ConfirmRelationActivity.class);
                String code = Remember.getString("code", "");
                intent.putExtra("code", code);
                startActivity(intent);
                break;

        }
    }

    /**
     * 更新选择地区数据库
     */
    private void updateRegionDB() {
        long preDate = FastData.getRegionDBUpdateTime(0) == 0
                ? System.currentTimeMillis()
                : FastData.getRegionDBUpdateTime(0);
        if (FastData.getRegionDBUpdateTime(0) == 0 || CommonUtil.concurrent(preDate, System.currentTimeMillis()) > 6) {
            reqData();
        }
    }

    private void reqData() {
        Subscription s = apiService.getLocationList()
                .doOnNext(new Action1<DistrictListResponse>() {
                    @Override
                    public void call(DistrictListResponse response) {
                        if (response != null && response.success()) {
                            DistrictModel.deleteAll();
                        }
                    }
                })
                .flatMap(new Func1<DistrictListResponse, Observable<DistrictModel>>() {
                    @Override
                    public Observable<DistrictModel> call(DistrictListResponse response) {
                        List<DistrictModel> list = response.getDataList();
                        return Observable.from(list);
                    }
                })
                .map(new Func1<DistrictModel, Byte>() {
                    @Override
                    public Byte call(DistrictModel districtModel) {
                        byte error;
                        try {
                            districtModel.save();
                        } finally {
                            error = 1;
                        }
                        return error;
                    }
                })
                .last()
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(new Action1<Byte>() {
                    @Override
                    public void call(Byte error) {
                        if (error == 1) {
                            FastData.setRegionDBUpdateTime(System.currentTimeMillis());
                        }
                    }
                }, throwable -> {
                    Log.e(TAG, "error", throwable);
                });
        addSubscription(s);
    }

    public View getFootMenuView() {
        return footMenu;
    }

//    @SuppressWarnings("unused")
    @Subscribe
    public void onEvent(LogoutEvent event) {
        finish();
    }

    @Subscribe
    public void onEvent(ConfirmRelationEvent event) {
        if(TextUtils.isEmpty(FastData.getRelationName())){
            rlToensurerelation.setVisibility(View.VISIBLE);
        }else{
            rlToensurerelation.setVisibility(View.GONE);
        }
    }
}
