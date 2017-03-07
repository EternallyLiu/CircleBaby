package cn.timeface.circle.baby.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tbruyelle.rxpermissions.RxPermissions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.App;
import cn.timeface.circle.baby.BuildConfig;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.constants.CountlyEventHelper;
import cn.timeface.circle.baby.constants.TypeConstant;
import cn.timeface.circle.baby.dialogs.PublishDialog;
import cn.timeface.circle.baby.events.ConfirmRelationEvent;
import cn.timeface.circle.baby.events.EventTabMainWake;
import cn.timeface.circle.baby.events.LogoutEvent;
import cn.timeface.circle.baby.fragments.HomeFragment;
import cn.timeface.circle.baby.fragments.MineFragment;
import cn.timeface.circle.baby.fragments.base.BaseFragment;
import cn.timeface.circle.baby.support.api.models.DistrictModel;
import cn.timeface.circle.baby.support.api.models.objs.BabyObj;
import cn.timeface.circle.baby.support.api.models.responses.DistrictListResponse;
import cn.timeface.circle.baby.support.managers.listeners.IEventBus;
import cn.timeface.circle.baby.support.managers.services.SavePicInfoService;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.Remember;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.babyInfo.beans.BabyAttentionEvent;
import cn.timeface.circle.baby.ui.growth.fragments.PrintGrowthHomeFragment;
import cn.timeface.circle.baby.ui.images.views.DeleteDialog;
import cn.timeface.circle.baby.ui.kiths.KithFragment;
import cn.timeface.circle.baby.ui.timelines.Utils.LogUtil;
import cn.timeface.circle.baby.ui.timelines.Utils.SpannableUtils;
import cn.timeface.circle.baby.views.dialog.TFProgressDialog;
import cn.timeface.common.utils.CommonUtil;
import cn.timeface.open.TFOpen;
import cn.timeface.open.TFOpenConfig;
import cn.timeface.open.api.bean.obj.TFOUserObj;
import rx.Observable;
import rx.Subscription;
import rx.functions.Func1;

/**
 * 首页activity
 * author : YW.SUN Created on 2017/1/11
 * email : sunyw10@gmail.com
 */
public class TabMainActivity extends BaseAppCompatActivity implements View.OnClickListener, IEventBus, DeleteDialog.SubmitListener, DeleteDialog.CloseListener {
    @Bind(R.id.menu_home_tv)
    TextView menuHomeTv;
    @Bind(R.id.menu_mime_tv)
    TextView menuMimeTv;
    @Bind(R.id.menu_growth_up_tv)
    TextView menuGrowthTv;
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
    @Bind(R.id.send_timeface)
    ImageView sendTimeface;
    private long lastPressedTime = 0;
    private static final int TAB1 = 0;//时光轴
    private static final int TAB2 = 1;//我的
    private static final int TAB3 = 2;//印成长
    @Bind(R.id.container)
    FrameLayout container;
    private BaseFragment currentFragment = null;
    private DeleteDialog dialog;
    private TFProgressDialog tfprogress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        updateRegionDB();
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth() / 3;
        Remember.putInt("width", width);

        new RxPermissions(this).request(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .subscribe(aBoolean -> {
                    if (aBoolean) {
                        SavePicInfoService.open(getApplicationContext());
                    }
                });

        clickTab(menuHomeTv);
        ivPublish.setOnClickListener(this);
        tvToensurerelation.setOnClickListener(this);
        rlToensurerelation.setOnClickListener(this);
        sendTimeface.setOnClickListener(this);

        if (TextUtils.isEmpty(FastData.getRelationName
                ())) {
            rlToensurerelation.setVisibility(View.VISIBLE);
        } else {
            rlToensurerelation.setVisibility(View.GONE);
        }
        EventBus.getDefault().post(new EventTabMainWake());

        //初始化开放平台
//        TFOUserObj tfoUserObj = new TFOUserObj();
//        tfoUserObj.setAvatar(FastData.getAvatar());
//        tfoUserObj.setGender(FastData.getBabyGender());
//        tfoUserObj.setNick_name(FastData.getBabyName());
//        tfoUserObj.setPhone(FastData.getAccount());
//        tfoUserObj.setUserId(FastData.getUserId());
//        TFOpen.init(this, new TFOpenConfig.Builder(TypeConstant.APP_ID, TypeConstant.APP_SECRET, tfoUserObj)
//                .debug(BuildConfig.DEBUG).build()
//        );
        int type = getIntent().getIntExtra("type", 0);
        if (type == 1)
            onEvent(new BabyAttentionEvent(-1));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    public static void open(Context context) {
        context.startActivity(new Intent(context, TabMainActivity.class));
    }

    /**
     * 为了扩展跳转模式，例如1、创建宝宝，默认为0无任何操作
     *
     * @param context
     * @param type
     */
    public static void open(Context context, int type) {
        context.startActivity(new Intent(context, TabMainActivity.class).putExtra("type", type));
    }

    public void clickTab(View view) {

        if(currentFragment instanceof HomeFragment && !((HomeFragment) currentFragment).isBottomMenuShow()){
            return;
        }

        switch (view.getId()) {
            //时光轴
            case R.id.menu_home_tv:
                sendTimeface.setVisibility(View.VISIBLE);
                menuHomeTv.setSelected(true);
                menuMimeTv.setSelected(false);
                menuGrowthTv.setSelected(false);
                showContent(TAB1);
                break;
            //我的
            case R.id.menu_mime_tv:
                sendTimeface.setVisibility(View.GONE);
                menuHomeTv.setSelected(false);
                menuMimeTv.setSelected(true);
                menuGrowthTv.setSelected(false);
                showContent(TAB2);
                break;
            //印成长
            case R.id.menu_growth_up_tv:
                sendTimeface.setVisibility(View.GONE);
                menuHomeTv.setSelected(false);
                menuMimeTv.setSelected(false);
                menuGrowthTv.setSelected(true);
                showContent(TAB3);
                break;
        }
    }

    public BaseFragment getFragment(int navType) {
        switch (navType) {
            case TAB1:
                return HomeFragment.newInstance("首页");
            case TAB2:
                return MineFragment.newInstance("我的");
            case TAB3:
                return PrintGrowthHomeFragment.newInstance("印成长");

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

    public View getSendTimeface() {
        return sendTimeface;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_timeface:
                new PublishDialog(this).show();
                CountlyEventHelper.getInstance().publishEvent(FastData.getUserId());
                break;
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
                .doOnNext(response -> {
                    if (response != null && response.success()) {
                        DistrictModel.deleteAll();
                    }
                })
                .flatMap(new Func1<DistrictListResponse, Observable<DistrictModel>>() {
                    @Override
                    public Observable<DistrictModel> call(DistrictListResponse response) {
                        List<DistrictModel> list = response.getDataList();
                        return Observable.from(list);
                    }
                })
                .map(districtModel -> {
                    byte error;
                    try {
                        districtModel.save();
                    } finally {
                        error = 1;
                    }
                    return error;
                })
                .last()
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(error -> {
                    if (error == 1) {
                        FastData.setRegionDBUpdateTime(System.currentTimeMillis());
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
        if (TextUtils.isEmpty(FastData.getRelationName())) {
            rlToensurerelation.setVisibility(View.VISIBLE);
        } else {
            rlToensurerelation.setVisibility(View.GONE);
        }
    }

    /**
     * 显示关注宝宝之后的对话框
     *
     * @param tiitle
     */
    private void showDialog(int type, CharSequence tiitle) {
        if (dialog == null) {
            dialog = new DeleteDialog(this);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) dialog.getSubmit().getLayoutParams();
            if (params == null) {
                params = new LinearLayout.LayoutParams(App.mScreenWidth / 2, ViewGroup.LayoutParams.WRAP_CONTENT);
            } else params.width = App.mScreenWidth / 2;
            params.weight = 0;
            dialog.getSubmit().setLayoutParams(params);
            dialog.hideCacelButton();
            dialog.setMessageGravity(Gravity.CENTER_HORIZONTAL);
            dialog.setCloseListener(this);
            dialog.setCanceledOnTouchOutside(false);
            dialog.showClose(true);
            dialog.setSubmitListener(this);
        }
        if (type == -1) {
            dialog.setTitle(new SpannableStringBuilder("创建成功！ "));
            dialog.getTitle().setVisibility(View.VISIBLE);
            dialog.getTitle().setGravity(Gravity.CENTER);
            dialog.getTitle().setTextColor(Color.BLACK);
        } else dialog.getTitle().setVisibility(View.GONE);
        dialog.getSubmit().setText(type == 1 ? "立即查看" : "立即导入");
        dialog.setType(type);
        dialog.setMessage(tiitle);
        if (!dialog.isShowing())
            dialog.show();
    }


    @Subscribe
    public void onEvent(BabyAttentionEvent attentionEvent) {
        if (attentionEvent.getType() == 1 && attentionEvent.getBuilder() != null)
            showDialog(attentionEvent.getType(), attentionEvent.getBuilder());
        else if (attentionEvent.getType() == -1) {
            String relativeName = FastData.getRelationName();
            BabyObj babyObj = FastData.getBabyObj();
            StringBuilder sb = new StringBuilder();
            SpannableStringBuilder builder = new SpannableStringBuilder();
            sb.append(String.format("%s 创建成功", babyObj.getNickName()));
            builder.append(String.format("%s 创建成功", babyObj.getNickName()));
            builder.setSpan(SpannableUtils.getTextColor(this, R.color.sea_buckthorn), sb.lastIndexOf(babyObj.getNickName()), sb.lastIndexOf(babyObj.getNickName()) + babyObj.getNickName().length() + 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            builder.setSpan(SpannableUtils.getTextStyle(Typeface.BOLD), sb.lastIndexOf(babyObj.getNickName()), sb.lastIndexOf(babyObj.getNickName()) + babyObj.getNickName().length() + 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

            sb.append(String.format("\n %s 可以导入 %s 的照片了", relativeName, babyObj.getNickName())).append("\n");
            builder.append(String.format("\n %s 可以导入 %s 的照片了", relativeName, babyObj.getNickName())).append("\n");
            builder.setSpan(SpannableUtils.getTextColor(this, R.color.sea_buckthorn), sb.lastIndexOf(babyObj.getNickName()), sb.lastIndexOf(babyObj.getNickName()) + babyObj.getNickName().length() + 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            builder.setSpan(SpannableUtils.getTextStyle(Typeface.BOLD), sb.lastIndexOf(babyObj.getNickName()), sb.lastIndexOf(babyObj.getNickName()) + babyObj.getNickName().length() + 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            sb.append(String.format(" 回顾 %s 的成长吧！", babyObj.getNickName())).append("\n");
            builder.append(String.format(" 回顾 %s 的成长吧！", babyObj.getNickName()));
            builder.setSpan(SpannableUtils.getTextColor(this, R.color.sea_buckthorn), sb.lastIndexOf(babyObj.getNickName()), sb.lastIndexOf(babyObj.getNickName()) + babyObj.getNickName().length() + 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            builder.setSpan(SpannableUtils.getTextStyle(Typeface.BOLD), sb.lastIndexOf(babyObj.getNickName()), sb.lastIndexOf(babyObj.getNickName()) + babyObj.getNickName().length() + 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

            builder.setSpan(SpannableUtils.getTextSize(this, R.dimen.text_medium), sb.indexOf(relativeName), sb.indexOf(relativeName) + relativeName.length() + 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            builder.setSpan(SpannableUtils.getTextColor(this, R.color.sea_buckthorn), sb.indexOf(relativeName), sb.indexOf(relativeName) + relativeName.length() + 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            builder.setSpan(SpannableUtils.getTextStyle(Typeface.BOLD), sb.indexOf(relativeName), sb.indexOf(relativeName) + relativeName.length() + 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            showDialog(attentionEvent.getType(), builder);
        }
    }

    @Override
    public void close() {

    }

    @Override
    public void submit() {
        if (dialog != null && dialog.getType() == -1) {
            PublishActivity.open(this, PublishActivity.PHOTO);
        } else FragmentBridgeActivity.open(this, KithFragment.class.getSimpleName());
    }
}
