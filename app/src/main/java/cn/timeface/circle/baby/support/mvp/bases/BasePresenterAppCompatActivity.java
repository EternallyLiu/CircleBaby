package cn.timeface.circle.baby.support.mvp.bases;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;

import cn.timeface.circle.baby.BuildConfig;
import cn.timeface.circle.baby.constants.TypeConstant;
import cn.timeface.circle.baby.support.api.ApiFactory;
import cn.timeface.circle.baby.support.api.services.ApiService;
import cn.timeface.circle.baby.support.managers.listeners.IEventBus;
import cn.timeface.circle.baby.support.mvp.presentations.BasePresenterView;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.open.TFOpen;
import cn.timeface.open.TFOpenConfig;
import cn.timeface.open.api.bean.obj.TFOUserObj;
import ly.count.android.sdk.Countly;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * base presenter activity
 * author : YW.SUN Created on 2017/1/11
 * email : sunyw10@gmail.com
 */
public abstract class BasePresenterAppCompatActivity extends RxAppCompatActivity implements BasePresenterView {

    public ApiService apiService;

    protected final String TAG = this.getClass().getSimpleName();
    private CompositeSubscription mCompositeSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Log.d("Base", getClass().getSimpleName());
        Countly.sharedInstance().setViewTracking(true);
        Countly.sharedInstance().enableCrashReporting();
        apiService = ApiFactory.getApi().getApiService();
        if (this instanceof IEventBus) {
            EventBus.getDefault().register(this);
        }
        if(!TextUtils.isEmpty(FastData.getUserId()) && FastData.getBabyObj() != null){
            //初始化开放平台
            TFOUserObj tfoUserObj = new TFOUserObj();
            tfoUserObj.setAvatar(FastData.getAvatar());
            tfoUserObj.setGender(FastData.getBabyGender());
            tfoUserObj.setNick_name(FastData.getBabyNickName());
            tfoUserObj.setPhone(FastData.getAccount());
            tfoUserObj.setUserId(FastData.getUserId());
            TFOpen.init(this, new TFOpenConfig.Builder(TypeConstant.APP_ID, TypeConstant.APP_SECRET, tfoUserObj)
                    .debug(BuildConfig.DEBUG).build()
            );
        }
    }

    @Override
    public void onStart()
    {
        super.onStart();
        Countly.sharedInstance().onStart(this);
    }

    @Override
    public void onStop()
    {
        Countly.sharedInstance().onStop();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCompositeSubscription != null) {
            mCompositeSubscription.unsubscribe();
        }

        if(this instanceof IEventBus){
            EventBus.getDefault().unregister(this);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public AppCompatActivity getCurrentActivity() {
        return this;
    }

    public CompositeSubscription getCompositeSubscription() {
        if (this.mCompositeSubscription == null) {
            this.mCompositeSubscription = new CompositeSubscription();
        }

        return this.mCompositeSubscription;
    }

    public void addSubscription(Subscription s) {
        if (this.mCompositeSubscription == null) {
            this.mCompositeSubscription = new CompositeSubscription();
        }

        this.mCompositeSubscription.add(s);
    }

    protected void showToast(String msg) {
        ToastUtil.showToast(msg);
    }

    protected void showToast(@StringRes int msg) {
        ToastUtil.showToast(getString(msg));
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);//友盟页面统计，谁注释的请自觉请大家撸串^_^!
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);//友盟页面统计，谁注释的请自觉请大家撸串^_^!
    }
}
