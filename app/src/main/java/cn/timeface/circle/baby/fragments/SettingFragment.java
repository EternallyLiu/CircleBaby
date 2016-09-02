package cn.timeface.circle.baby.fragments;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
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

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.AboutActivity;
import cn.timeface.circle.baby.activities.FragmentBridgeActivity;
import cn.timeface.circle.baby.activities.LoginActivity;
import cn.timeface.circle.baby.constants.TypeConstant;
import cn.timeface.circle.baby.constants.TypeConstants;
import cn.timeface.circle.baby.events.LogoutEvent;
import cn.timeface.circle.baby.fragments.base.BaseFragment;
import cn.timeface.circle.baby.utils.FastData;
import cn.timeface.circle.baby.utils.MiPushUtil;
import cn.timeface.circle.baby.utils.Remember;
import cn.timeface.circle.baby.utils.rxutils.SchedulersCompat;
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

    public SettingFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

    private void initData() {
        if (Remember.getInt("wifidownload", 1) == 0) {
            ivSwichWifi.setImageResource(R.drawable.swichoff);
        }
        if (Remember.getInt("msg", 1) == 0) {
            ivSwichMsg.setImageResource(R.drawable.swichoff);
        }
        new CacheSizeTask().execute();


        rlWifi.setOnClickListener(this);
        rlSettingMsg.setOnClickListener(this);
        rlSettingShare.setOnClickListener(this);
        rlSettingAbout.setOnClickListener(this);
        rlSettingScore.setVisibility(View.GONE);
        rlSettingClear.setOnClickListener(this);
        btnSignOut.setOnClickListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
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
                String url = "http://fir.im/timebabyandroid";
                new ShareDialog(getActivity()).share("成长印记，印下美好成长时光", "一键汇聚宝宝的成长点滴，轻松愉快地为宝宝定制专属印刷品，和家人一起见证宝宝成长的每一步。",
                        ShareSdkUtil.getImgStrByResource(getActivity(), R.mipmap.ic_launcher),
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
                apiService.logout()
                        .compose(SchedulersCompat.applyIoSchedulers())
                        .subscribe(response -> {
                            FastData.setUserFrom(-1);
                            LoginActivity.open(getActivity());
                            getActivity().finish();
                            EventBus.getDefault().post(new LogoutEvent());
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
                System.out.println("cacheSize:" + cacheSize);
                System.out.println(StorageUtil.FormatFileSize(cacheSize));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
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
