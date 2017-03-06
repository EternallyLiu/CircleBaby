package cn.timeface.circle.baby.activities.base;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;

import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;

import cn.timeface.circle.baby.BuildConfig;
import cn.timeface.circle.baby.constants.TypeConstant;
import cn.timeface.circle.baby.support.managers.listeners.IEventBus;
import cn.timeface.circle.baby.support.api.ApiFactory;
import cn.timeface.circle.baby.support.api.services.ApiService;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.ui.timelines.Utils.LogUtil;
import cn.timeface.open.TFOpen;
import cn.timeface.open.TFOpenConfig;
import cn.timeface.open.api.bean.obj.TFOUserObj;
import ly.count.android.sdk.Countly;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * @author rayboot
 * @from 14/11/3 14:04
 * @TODO
 */
public class BaseAppCompatActivity extends AppCompatActivity {

    public static final ApiService apiService = ApiFactory.getApi().getApiService();
    protected final String TAG = this.getClass().getSimpleName();

    private CompositeSubscription mCompositeSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Countly.sharedInstance().setViewTracking(true);
        Countly.sharedInstance().enableCrashReporting();
        if (this instanceof IEventBus) {
            EventBus.getDefault().register(this);
        }

        if(!TextUtils.isEmpty(FastData.getUserId()) && FastData.getBabyObj() != null){
            //初始化开放平台
            TFOUserObj tfoUserObj = new TFOUserObj();
            tfoUserObj.setAvatar(FastData.getAvatar());
            tfoUserObj.setGender(FastData.getBabyGender());
            tfoUserObj.setNick_name(FastData.getBabyName());
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
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        LogUtil.showLog(getClass().getName());
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        if (this instanceof IEventBus) {
            EventBus.getDefault().unregister(this);
        }
        if (this.mCompositeSubscription != null) {
            this.mCompositeSubscription.unsubscribe();
        }
        super.onDestroy();
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

    public CompositeSubscription getCompositeSubscription() {
        if (this.mCompositeSubscription == null) {
            this.mCompositeSubscription = new CompositeSubscription();
        }

        return this.mCompositeSubscription;
    }

    public void addSubscription(Subscription s) {
        if (s == null) {
            return;
        }
        if (this.mCompositeSubscription == null) {
            this.mCompositeSubscription = new CompositeSubscription();
        }

        this.mCompositeSubscription.add(s);
    }
}
