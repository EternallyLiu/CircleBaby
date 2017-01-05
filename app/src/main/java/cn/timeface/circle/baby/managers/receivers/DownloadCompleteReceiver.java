package cn.timeface.circle.baby.managers.receivers;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.timeface.circle.baby.App;
import cn.timeface.circle.baby.activities.SplashActivity;
import cn.timeface.circle.baby.api.models.PushItem;
import cn.timeface.circle.baby.api.models.responses.PushResponse;
import cn.timeface.circle.baby.utils.FastData;
import cn.timeface.circle.baby.utils.NotificationUtil;
import cn.timeface.common.utils.PackageUtils;

/**
 * @author rayboot
 * @from 14-4-29 9:31
 * @TODO 下载完成状态监听
 */
public class DownloadCompleteReceiver extends BroadcastReceiver {
    public static final String ACTION_NOTIFICATION_CLICKED = "cn.timeface.intent.action.upgrade";
    File downloadFile = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        // get complete download id
        String action = intent.getAction();

        if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
            // 处理下载后的文件
            DownloadManager downloadManager = (DownloadManager) context.getSystemService(
                    Context.DOWNLOAD_SERVICE);
            long completeDownloadId =
                    intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            DownloadManager.Query myDownloadQuery = new DownloadManager.Query();
            myDownloadQuery.setFilterById(completeDownloadId);
            Cursor myDownload = downloadManager.query(myDownloadQuery);
            if (myDownload.moveToFirst()) {
                int fileNameIdx = myDownload.getColumnIndex(
                        DownloadManager.COLUMN_LOCAL_FILENAME);
                downloadFile = new File(myDownload.getString(fileNameIdx));
            }
            myDownload.close();
            FastData.setApkDownloadPath(downloadFile.getAbsolutePath());

            //强制更新
            if (FastData.getIsEnforceUpgrade() == 1) {
                if (SplashActivity.progressDialog != null && SplashActivity.progressDialog.isShowing()) {
                    SplashActivity.progressDialog.dismiss();
                }
                PackageUtils.install(context, downloadFile.getAbsolutePath());
            } else if (FastData.getIsEnforceUpgrade() == 0) {
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
        }

        // 点击通知栏 安装应用程序
        if (ACTION_NOTIFICATION_CLICKED.equals(action)) {
            if (downloadFile == null && !TextUtils.isEmpty(FastData.getApkDownloadPath())) {
                downloadFile = new File(FastData.getApkDownloadPath());
            }

            if (downloadFile.exists() && downloadFile.getAbsolutePath().endsWith("apk")) {
                PackageUtils.install(context, downloadFile.getAbsolutePath());
            }
        }
    }
}
