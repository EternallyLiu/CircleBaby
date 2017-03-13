package cn.timeface.circle.baby.activities;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.App;
import cn.timeface.circle.baby.BuildConfig;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.dialogs.TFDialog;
import cn.timeface.circle.baby.support.api.models.PushItem;
import cn.timeface.circle.baby.support.api.models.responses.PushResponse;
import cn.timeface.circle.baby.support.api.models.responses.UpdateResponse;
import cn.timeface.circle.baby.support.managers.receivers.DownloadCompleteReceiver;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.NotificationUtil;
import cn.timeface.circle.baby.support.utils.Once;
import cn.timeface.circle.baby.support.utils.Utils;
import cn.timeface.circle.baby.ui.settings.fragments.BindPhoneFragment;
import cn.timeface.circle.baby.views.dialog.TFProgressDialog;
import cn.timeface.common.utils.DeviceUtil;
import cn.timeface.common.utils.NetworkUtil;
import cn.timeface.common.utils.PackageUtils;
import cn.timeface.common.utils.Remember;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author rayboot
 * @from 14-8-18 14:08
 * @TODO
 */
public class SplashActivity extends BaseAppCompatActivity {
    private static final int REQUEST_CODE_WEBVIEW = 101;
    private static final int REQUEST_CODE_GUIDE = 102;
    public static TFProgressDialog progressDialog;
    UpdateResponse updateModule;
    @Bind(R.id.image)
    ImageView image;
    private String adUrl = "";
    private String scheme, data;
    private boolean is_Scheme = false;
    private final MyHandler handler = new MyHandler(this);
    private String adImgUrl;

    static class MyHandler extends Handler {
        // WeakReference to the outer class's instance.
        private WeakReference<SplashActivity> mOuter;

        public MyHandler(SplashActivity activity) {
            mOuter = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            SplashActivity outer = mOuter.get();
            if (outer != null) {
                if(TextUtils.isEmpty(FastData.getUserId())){
                    LoginActivity.open(outer);
                } else {
                    //手机号空，跳转绑定
                    if(TextUtils.isEmpty(FastData.getUserInfo().getPhoneNumber())){
                        Bundle bundle = new Bundle();
                        bundle.putInt("type", 0);
                        FragmentBridgeActivity.open(outer, BindPhoneFragment.class.getSimpleName(), bundle);
                    } else {
                        if(FastData.getBabyCount() > 0){
                            TabMainActivity.open(outer);
                        } else {
                            CreateBabyActivity.open(outer, true);
                        }
                    }
                    outer.finish();
                }
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }

        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        Remember.init(this, BuildConfig.APPLICATION_ID + "_remember");
        firstRun();
        requestCheckUpdate();
    }

    @Override
    protected void onDestroy() {
        handler.removeMessages(1);
        super.onDestroy();
    }

    private void firstRun() {
        Subscription s = apiService.firstRun(Build.MODEL, Build.VERSION.RELEASE, BuildConfig.VERSION_NAME,
                DeviceUtil.getScreenHeight(this) + "X" + DeviceUtil.getScreenWidth(this))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        response -> {
                        }
                        , error -> {
                            Toast.makeText(SplashActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        });
        addSubscription(s);

    }

    private void requestCheckUpdate() {
        FastData.setIsEnforceUpgrade(-1);

        Subscription s = apiService.latestVersion(BuildConfig.VERSION_CODE, 2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        response -> {
                            if (response.success()) {
                                if (!checkUpdate(response)) {
                                    showGuide();
                                }
                            } else {
                                if (!response.getInfo().equals("已是最新版本")) {
                                    Toast.makeText(SplashActivity.this, response.getInfo(), Toast.LENGTH_SHORT).show();
                                }
                                showGuide();
                            }
                        }
                        , throwable -> {
                            Toast.makeText(SplashActivity.this, "检测更新失败", Toast.LENGTH_SHORT).show();
                            showGuide();
                        });
        addSubscription(s);
    }

    private void showGuide() {
        new Once().show(
                "show_guide_" + BuildConfig.VERSION_CODE
                , new Once.OnceCallback() {
                    @Override
                    public void onOnce() {
                        startActivity(new Intent(SplashActivity.this, GuideActivity.class));
                        finish();
                    }

                    @Override
                    public void onMore() {
                        showAD();
                    }
                });
    }

    /**
     * 检测是否需要更新
     * 如果需要更新则弹出提示框
     *
     * @param data
     * @return 是否需要更新
     */
    private boolean checkUpdate(UpdateResponse data) {
        updateModule = data;
        updateModule.info = updateModule.info.replace("\\n", "\n");

        if (BuildConfig.VERSION_CODE >= updateModule.getVersion()) {
            return false;
        }

        FastData.setIsEnforceUpgrade(updateModule.getEnforce());
        if (updateModule.getEnforce() == 1) {
            final TFDialog dialog = TFDialog.getInstance();
            dialog.setTitle(R.string.check_update);
            dialog.setMessage(updateModule.info);
            dialog.setPositiveButton(R.string.dialog_submit,
                    v -> {
                        dialog.dismiss();
                        if (needDownloadFile()) {
                            fullScreen(false);
                            doDownload();
                        } else {
                            sendBroadcast(new Intent(DownloadCompleteReceiver.ACTION_NOTIFICATION_CLICKED));
                        }
                    });
            dialog.setNegativeButton(R.string.dialog_cancle,
                    v -> {
                        dialog.dismiss();
                        SplashActivity.this.finish();
                    });
            dialog.show(getSupportFragmentManager(), TAG);
        } else if (updateModule.getEnforce() == 0) {
            if (needDownloadFile()) {
                if (NetworkUtil.isWifiConnected(SplashActivity.this)) {
                    doDownload();
                }
            } else {
                showUpgradeNotification();
            }
            showGuide();
        }

        return true;
    }

    /**
     * 下载逻辑
     */
    private void doDownload() {
//        fullScreen(false);
        final DownloadManager.Request req = getDownloadRequest();
        final DownloadManager downloadManager =
                (DownloadManager) SplashActivity.this.getSystemService(
                        Activity.DOWNLOAD_SERVICE);
        if (NetworkUtil.getConnectedType(this) != ConnectivityManager.TYPE_WIFI) {
            final TFDialog smileDialog = TFDialog.getInstance();
            smileDialog.setTitle(R.string.check_update);
            smileDialog.setMessage("温馨提示：当前处于非WiFi环境下，下载会耗费手机流量，是否继续下载？");
            smileDialog.setPositiveButton("下载更新", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    smileDialog.dismiss();
                    progressDialog = TFProgressDialog.getInstance("");
                    progressDialog.setTvMessage("正在下载...");
                    progressDialog.show(getSupportFragmentManager(), "");
                    try {
                        downloadManager.enqueue(req);
                    } catch (Exception e) {
                        showDownloadManagerStateDialog();
                    }
                }
            });
            smileDialog.setNegativeButton("取消", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                    smileDialog.dismiss();
                    //强制更新
                    if (updateModule.getEnforce() == 1) {
                        SplashActivity.this.finish();
                    } else {
                        showGuide();
                    }
                    return;
                }
            });
            smileDialog.show(getSupportFragmentManager(), "");
        } else {
            progressDialog = TFProgressDialog.getInstance("");
            progressDialog.setTvMessage("正在下载...");
            progressDialog.show(getSupportFragmentManager(), "");
            try {
                downloadManager.enqueue(req);
            } catch (Exception e) {
                showDownloadManagerStateDialog();
            }
        }


    }

    /**
     * 打开通知栏，通知升级
     */
    private void showUpgradeNotification() {
        List<PushItem> notifys = new ArrayList<PushItem>();
        PushResponse notify = new PushResponse();
        PushItem pushItem = new PushItem();
        pushItem.setAlert("新版本已下载成功，点击安装更新！");
        pushItem.setDataType(pushItem.DownloadSuccess);
        pushItem.setPushId(0x33);
        notifys.add(pushItem);
        notify.setDatas(notifys);
        NotificationUtil.showPushMsg(App.getInstance(), notify, true);
    }

    /**
     * 如果已下载apk文件，且apk文件的versionCode不低于线上版本，则返回fale
     *
     * @return false 表示已下载最新apk文件，不需要重新下载
     */
    private boolean needDownloadFile() {
        // 文件不存在  需要下载文件
        if (TextUtils.isEmpty(FastData.getApkDownloadPath())
                || !(new File(FastData.getApkDownloadPath()).exists())) {
            return true;
        }

        // 文件存在且版本号不低于线上版本
        return PackageUtils.getApkInfo(this, FastData.getApkDownloadPath()).versionCode < updateModule.getVersion();
    }

    private void showDownloadManagerStateDialog() {
        final TFDialog dialog = TFDialog.getInstance();
        dialog.setTitle(R.string.check_update);
        dialog.setMessage("请手动 “开启” 下载管理程序才能完成新版本下载。");
        dialog.setPositiveButton(R.string.dialog_submit, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Utils.openDownloadManagerState(SplashActivity.this)) {
                    Toast.makeText(SplashActivity.this, "未发现下载管理器组件,已从浏览器直接下载。", Toast.LENGTH_LONG).show();
                    Uri uri = Uri.parse(updateModule.getDownUrl());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                    dialog.dismiss();
                }
            }
        });
        dialog.setNegativeButton(R.string.dialog_cancle, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (updateModule.getEnforce() == 1)// 强制更新
                {
                    SplashActivity.this.finish();
                }
                dialog.dismiss();
            }
        });
        dialog.show(getSupportFragmentManager(), "");
    }

    private DownloadManager.Request getDownloadRequest() {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(this.updateModule.getDownUrl()));
        request.setDestinationInExternalFilesDir(this,
                Environment.DIRECTORY_DOWNLOADS,
                "TimeFace" + this.updateModule.getVersion() + ".apk");
        request.setTitle(getString(R.string.app_name));
        request.setDescription(getString(R.string.app_name));
        // 设置允许使用的网络类型，这里是移动网络和wifi都可以
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        request.setAllowedOverRoaming(true);
        return request;
    }

    private void showAD() {
        handler.sendEmptyMessageDelayed(1, 3000);
        Subscription s = apiService.getAD(DeviceUtil.getScreenWidth(this) + "X" + DeviceUtil.getScreenHeight(this))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if (response.success() && response.getAdInfo() != null && !TextUtils.isEmpty(response.getAdInfo().getAdImgUrl())) {
                        adUrl = response.getAdInfo().getAdUri();
                        adImgUrl = response.getAdInfo().getAdImgUrl();
                        if (!adImgUrl.startsWith("http")) {
                            adImgUrl = adImgUrl.substring(adImgUrl.indexOf("http"));
                        }
                        Glide.with(SplashActivity.this)
                                .load(adImgUrl)
                                .into(image);
                    }
                }, throwable -> {
                    Toast.makeText(SplashActivity.this, "服务器返回失败", Toast.LENGTH_SHORT).show();
                });
        addSubscription(s);
    }

    /**
     * 广告点击
     */
    public void toAd(View view) {
        if (!TextUtils.isEmpty(adUrl)) {
            handler.removeMessages(1);
            String isShare = Uri.parse(adUrl).getQueryParameter("share");
//            WebViewActivity.open4Result(this, adUrl, "", REQUEST_CODE_WEBVIEW, !TextUtils.isEmpty(isShare) && isShare.equals("0"));
            FragmentBridgeActivity.openWebViewFragment(this, adUrl, "广告");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_WEBVIEW:
                if (resultCode == RESULT_OK) {
                    handler.sendEmptyMessage(1);
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void fullScreen(boolean enable) {
        if (enable) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            getWindow().setAttributes(lp);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } else {
            WindowManager.LayoutParams attr = getWindow().getAttributes();
            attr.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setAttributes(attr);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    private void getScheme() {
        scheme = getIntent().getScheme();//获得Scheme名称
        data = getIntent().getDataString();//获得Uri全部路径
        if (scheme != null && !scheme.equals("") && data != null && !data.equals("")) {
            is_Scheme = true;
        }
    }

}