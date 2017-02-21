package cn.timeface.circle.baby.fragments;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.xiaomi.mipush.sdk.MiPushClient;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.AboutActivity;
import cn.timeface.circle.baby.activities.FragmentBridgeActivity;
import cn.timeface.circle.baby.activities.LoginActivity;
import cn.timeface.circle.baby.constants.TypeConstant;
import cn.timeface.circle.baby.events.LogoutEvent;
import cn.timeface.circle.baby.fragments.base.BaseFragment;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.MiPushUtil;
import cn.timeface.circle.baby.support.utils.Remember;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.settings.beans.UpdatePhone;
import cn.timeface.circle.baby.ui.settings.fragments.BindPhoneFragment;
import cn.timeface.circle.baby.ui.settings.fragments.NotifyPwdFragment;
import cn.timeface.circle.baby.ui.timelines.Utils.LogUtil;
import cn.timeface.circle.baby.views.ShareDialog;
import cn.timeface.common.utils.DeviceUuidFactory;
import cn.timeface.common.utils.ShareSdkUtil;
import cn.timeface.common.utils.StorageUtil;
import cn.timeface.common.utils.TimeFaceUtilInit;

public class SettingFragment extends BaseFragment implements View.OnClickListener {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.rl_setting_msg)
    RelativeLayout rlSettingMsg;
    @Bind(R.id.rl_setting_share)
    RelativeLayout rlSettingShare;
    @Bind(R.id.rl_setting_about)
    RelativeLayout rlSettingAbout;
    @Bind(R.id.rl_setting_score)
    RelativeLayout rlSettingScore;
    @Bind(R.id.tv_cache)
    TextView tvCache;
    @Bind(R.id.rl_setting_clear)
    RelativeLayout rlSettingClear;
    @Bind(R.id.btn_sign_out)
    Button btnSignOut;
    @Bind(R.id.iv_swich_wifi)
    ImageView ivSwichWifi;
    @Bind(R.id.rl_wifi)
    RelativeLayout rlWifi;
    @Bind(R.id.iv_swich_msg)
    ImageView ivSwichMsg;
    @Bind(R.id.appbar_layout)
    AppBarLayout appbarLayout;
    @Bind(R.id.rl_setting_pwd)
    RelativeLayout rlSettingPwd;
    @Bind(R.id.tv_phone_number)
    TextView tvPhoneNumber;
    @Bind(R.id.rl_setting_phone)
    RelativeLayout rlSettingPhone;
    @Bind(R.id.iv_swich_sms)
    ImageView ivSwichSms;
    @Bind(R.id.rl_sms)
    RelativeLayout rlSms;
    private String phoneNumber;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        ButterKnife.bind(this, view);
        setActionBar(toolbar);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setTitle("设置");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        initData();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Subscribe
    public void onEvent(UpdatePhone event) {
        showPhone();

    }

    public SettingFragment() {
    }

    private void showPhone() {
        phoneNumber = FastData.getUserInfo().getPhoneNumber();
        String replace = phoneNumber.substring(3, 7);

        if (!TextUtils.isEmpty(phoneNumber)) {
            tvPhoneNumber.setText(phoneNumber.replace(replace, "****"));
        }
    }

    private void initData() {

        if (Remember.getInt("wifidownload", 1) == 0) {
            ivSwichWifi.setImageResource(R.drawable.swichoff);
        }
        if (Remember.getInt("msg", 1) == 0) {
            ivSwichMsg.setImageResource(R.drawable.swichoff);
        }
        new CacheSizeTask().execute();

        showPhone();
        rlWifi.setOnClickListener(this);
        rlSettingMsg.setOnClickListener(this);
        rlSettingShare.setOnClickListener(this);
        rlSettingAbout.setOnClickListener(this);
        rlSettingScore.setVisibility(View.GONE);
        rlSettingClear.setOnClickListener(this);
        btnSignOut.setOnClickListener(this);
        rlSettingPwd.setOnClickListener(this);
        rlSettingPhone.setOnClickListener(this);
        rlSms.setOnClickListener(this);
        ivSwichSms.setImageResource(FastData.getSendSms()== 1 ? R.drawable.swichon : R.drawable.swichoff);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_sms:
                apiService.smsRemind(FastData.getSendSms() == 1 ? 0 : 1)
                        .compose(SchedulersCompat.applyIoSchedulers())
                        .subscribe(response -> {
                            if (response.success()) {
                                FastData.setSendSms(1 - FastData.getSendSms());
                                ivSwichSms.setImageResource(FastData.getSendSms()== 1 ? R.drawable.swichon : R.drawable.swichoff);
                            }
                        }, throwable -> {
                            LogUtil.showError(throwable);
                        });
                break;
            case R.id.rl_setting_phone:

                FragmentBridgeActivity.open(getActivity(), BindPhoneFragment.class.getSimpleName());
                break;
            case R.id.rl_setting_pwd:
                FragmentBridgeActivity.open(getActivity(), NotifyPwdFragment.class.getSimpleName());
                break;
            case R.id.rl_wifi:
                if (Remember.getInt("wifidownload", 1) == 0) {
                    ivSwichWifi.setImageResource(R.drawable.swichon);
                    Remember.putInt("wifidownload", 1);
                } else {
                    ivSwichWifi.setImageResource(R.drawable.swichoff);
                    Remember.putInt("wifidownload", 0);
                }
                break;

            case R.id.rl_setting_msg:
//                FragmentBridgeActivity.open(getContext(), "SettingMsgFragment");
                if (Remember.getInt("msg", 1) == 0) {
                    ivSwichMsg.setImageResource(R.drawable.swichon);
                    Remember.putInt("msg", 1);
                    MiPushClient.registerPush(getContext(), MiPushUtil.APP_ID, MiPushUtil.APP_KEY);
                    MiPushClient.setAlias(getContext(), new DeviceUuidFactory(TimeFaceUtilInit.getContext()).getDeviceId(), null);
                    pushSetting(getContext());
                } else {
                    ivSwichMsg.setImageResource(R.drawable.swichoff);
                    Remember.putInt("msg", 0);
                    MiPushClient.unregisterPush(getContext());
                }
                break;

            case R.id.rl_setting_share:
//                String url = "http://fir.im/timebabyandroid";
                String url = "http://www.timeface.cn/baby/babyShare/app.html";
                new ShareDialog(getActivity()).share("成长印记，印下美好成长时光", "一键汇聚宝宝的成长点滴，轻松愉快地为宝宝定制专属印刷品，和家人一起见证宝宝成长的每一步。",
                        ShareSdkUtil.getImgStrByResource(getActivity(), R.drawable.ic_launcher),
                        url);

                break;

            case R.id.rl_setting_about:
                AboutActivity.open(getContext());
                break;

            case R.id.rl_setting_clear:
                Glide glide = Glide.get(getActivity());
                glide.clearMemory();
                new DeleteFileTask().execute();
                Toast.makeText(getActivity(), "已清除", Toast.LENGTH_SHORT).show();
                break;

            case R.id.btn_sign_out:
                FastData.setUserFrom(-1);
                LoginActivity.open(getActivity());
                getActivity().finish();
                EventBus.getDefault().post(new LogoutEvent());
                apiService.logout()
                        .compose(SchedulersCompat.applyIoSchedulers())
                        .subscribe(response -> {

                        }, throwable -> {
                            Log.e(TAG, "logout:");
                        });
                break;


        }
    }


    // 计算缓存目录大小
    class CacheSizeTask extends AsyncTask<Void, Void, Void> {
        private long cacheSize;

        @Override
        protected Void doInBackground(Void... params) {
            // 计算缓存目录大小
            cacheSize = 0;
            try {
                if (StorageUtil.getExternalCacheDir().exists()) {
                    cacheSize += StorageUtil.getFolderSize(StorageUtil.getExternalCacheDir());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (tvCache != null)
                tvCache.setText(StorageUtil.FormatFileSize(cacheSize));
        }
    }

    class DeleteFileTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                StorageUtil.deleteFolderFile(StorageUtil.getExternalCacheDir());
                Glide glide = Glide.get(getActivity());
                glide.clearDiskCache();
            } catch (IOException | NullPointerException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (tvCache != null)
                tvCache.setText("0");
        }
    }


    public static void pushSetting(Context context) {
        if (!FastData.getBoolean(TypeConstant.PUSH_SETTING_ALL_TAG, true)) {
            MiPushClient.unregisterPush(context);
        } else {
            boolean voice = FastData.getBoolean(TypeConstant.PUSH_SETTING_VOICE_TAG, true);
            boolean vibrate = FastData.getBoolean(TypeConstant.PUSH_SETTING_VIBRATE_TAG, true);
            if (voice && vibrate) {
                MiPushClient.setLocalNotificationType(context, MiPushUtil.VOICE_SHAKE);
            } else if (voice) {
                MiPushClient.setLocalNotificationType(context, MiPushUtil.VOICE);
            } else if (vibrate) {
                MiPushClient.setLocalNotificationType(context, MiPushUtil.SHAKE);
            } else {
                MiPushClient.setLocalNotificationType(context, MiPushUtil.NULL);
            }

            // 密函 订阅或取消订阅
            if (FastData.getBoolean(TypeConstant.PUSH_SETTING_PRIVATE_MSG_TAG, true)) {
                MiPushClient.subscribe(context, MiPushUtil.SUBSCRIBE_TAG_PRIVATE_MSG, null);
            } else {
                MiPushClient.unsubscribe(context, MiPushUtil.SUBSCRIBE_TAG_PRIVATE_MSG, null);
            }

            // 系统通知 订阅或取消订阅
            if (FastData.getBoolean(TypeConstant.PUSH_SETTING_NOTIFICATION_TAG, true)) {
                MiPushClient.subscribe(context, MiPushUtil.SUBSCRIBE_TAG_SYSTEM_NOTICE, null);
            } else {
                MiPushClient.unsubscribe(context, MiPushUtil.SUBSCRIBE_TAG_SYSTEM_NOTICE, null);
            }
        }
    }
}
