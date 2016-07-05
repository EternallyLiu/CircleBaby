package cn.timeface.circle.baby.fragments;


import android.os.AsyncTask;
import android.os.Bundle;
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

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.FragmentBridgeActivity;
import cn.timeface.circle.baby.activities.LoginActivity;
import cn.timeface.circle.baby.fragments.base.BaseFragment;
import cn.timeface.circle.baby.utils.FastData;
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
        getActionBar().setDisplayHomeAsUpEnabled(true);
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
        rlSettingScore.setOnClickListener(this);
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
                } else {
                    ivSwichMsg.setImageResource(R.drawable.swichoff);
                    Remember.putInt("msg", 0);
                }
                break;

            case R.id.rl_setting_share:
                String baseUrl = "http://h5.stg1.v5time.net/hobbyDetail?";
                String url = baseUrl + "userId=" + FastData.getUserId() + "&deviceId=" + new DeviceUuidFactory(
                        TimeFaceUtilInit.getContext()).getDeviceId();
                new ShareDialog(getActivity()).share("宝宝时光，让家庭充满和谐，让教育充满温馨。", "宝宝时光，让家庭充满和谐，让教育充满温馨。",
                        ShareSdkUtil.getImgStrByResource(getActivity(), R.mipmap.ic_launcher),
                        ShareSdkUtil.getImgStrByResource(getActivity(), R.drawable.setting_sina_share_img),
                        url);

                break;

            case R.id.rl_setting_about:

                break;

            case R.id.rl_setting_score:

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
                            FastData.setAccount("");
                            LoginActivity.open(getActivity());
                            getActivity().finish();
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


}