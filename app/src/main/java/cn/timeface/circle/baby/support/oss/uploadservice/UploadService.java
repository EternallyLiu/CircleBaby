package cn.timeface.circle.baby.support.oss.uploadservice;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.PowerManager;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;

import java.util.ArrayList;

import cn.timeface.circle.baby.support.oss.OSSManager;
import cn.timeface.circle.baby.support.oss.Recorder;


/**
 * author: rayboot  Created on 15/10/10.
 * email : sy0725work@gmail.com
 */
public class UploadService extends IntentService {

    public static String NAMESPACE = "cn.timeface.circle.baby";
    private static final String ACTION_UPLOAD_SUFFIX = ".habit.upload.action.upload";
    private static final String BROADCAST_ACTION_SUFFIX = ".habit.upload.broadcast.status";
    protected static final String PARAM_NOTIFICATION_CONFIG = "param_notificationConfig";
    protected static final String PARAM_ID = "param_id";
    protected static final String PARAM_NAME = "param_name";
    protected static final String PARAM_FILES = "param_files";
    protected static final String PARAM_UPLOAD_TYPE = "param_upload_type";

    private static final int UPLOAD_NOTIFICATION_ID = 1234; // Something unique
    private static final int UPLOAD_NOTIFICATION_ID_DONE = 1235; // Something unique

    /**
     * The minimum interval between progress reports in milliseconds.
     * If the upload Tasks report more frequently, we will throttle notifications.
     * We aim for 6 updates per second.
     */
    protected static final long PROGRESS_REPORT_INTERVAL = 166;

    private static final String TAG = UploadService.class.getName();
    private NotificationManager notificationManager;
    private NotificationCompat.Builder notification;
    private UploadNotificationConfig notificationConfig;
    private PowerManager.WakeLock wakeLock;
    private long lastProgressNotificationTime;
    private static Recorder recorder;
    private int uploadType;

    public static final String UPLOAD_ID = "id";
    public static final String STATUS = "status";
    public static final int STATUS_IN_PROGRESS = 1;
    public static final int STATUS_COMPLETED = 2;
    public static final int STATUS_ERROR = 3;
    public static final String PROGRESS = "progress";
    public static final String PROGRESS_UPLOADED_BYTES = "progressUploadedBytes";
    public static final String PROGRESS_TOTAL_BYTES = "progressTotalBytes";
    public static final String ERROR_EXCEPTION = "errorException";
    public static final String WAKE_LOCK = "WAKE_LOCK";
    public static final String UPLOADED_IMAGES_JSON = "habit:record:upload";
    public static final String UPLOAD_MESSAGE_TYPE = "habit:record:type";

    public static final int TYPE_PUBLISH = 0;
    public static final int TYPE_EDIT = 1;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public UploadService() {
        super(TAG);
    }

    public static String getActionUpload() {
        return NAMESPACE + ACTION_UPLOAD_SUFFIX;
    }

    public static String getActionBroadcast() {
        return NAMESPACE + BROADCAST_ACTION_SUFFIX;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notification = new NotificationCompat.Builder(getApplicationContext());
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
    }

    public static void setRecorder(Recorder myRecord) {
        recorder = myRecord;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent == null) {
            return;
        }

        if (getActionUpload().equals(intent.getAction())) {
            uploadType = intent.getIntExtra(PARAM_UPLOAD_TYPE, TYPE_PUBLISH);
            notificationConfig = intent.getParcelableExtra(PARAM_NOTIFICATION_CONFIG);

            lastProgressNotificationTime = 0;
            wakeLock.acquire();

            Log.e("111111", "111  createNotification");
            createNotification();
            Log.e("111111", "222  createNotification");
            UploadTaskInfo upInfo = new UploadTaskInfo(intent.getStringExtra(PARAM_ID),
                    intent.getStringExtra(PARAM_NAME),
                    (ArrayList) intent.getParcelableArrayListExtra(PARAM_FILES),
                    notificationConfig);

            doUpload(upInfo);
            Log.e("111111", "333  doUpload");
        }
    }

    public static void startUploadService(UploadTaskInfo uploadTask, int uploadType) {
        Intent intent = new Intent(uploadTask.getContext(), UploadService.class);
        intent.setAction(UploadService.getActionUpload());
        intent.putExtra(UploadService.PARAM_NOTIFICATION_CONFIG, uploadTask.getNotificationConfig());
        intent.putExtra(UploadService.PARAM_ID, uploadTask.getInfoId());
        intent.putExtra(UploadService.PARAM_NAME, uploadTask.getInfoName());
        intent.putParcelableArrayListExtra(UploadService.PARAM_FILES, uploadTask.getFileObjs() == null ? new ArrayList<UploadFileObj>() : uploadTask.getFileObjs());
        intent.putExtra(UploadService.PARAM_UPLOAD_TYPE, uploadType);
        uploadTask.getContext().startService(intent);
    }

    public static void startUploadService(UploadTaskInfo uploadTask) {
        startUploadService(uploadTask, TYPE_PUBLISH);
    }

    public void doUpload(UploadTaskInfo uploadTaskInfo) {
        recorder.addTask(uploadTaskInfo);
        //添加任务列表
        int errorCount = 0;


        OSSManager ossManager = OSSManager.getOSSManager(this.getApplicationContext());
        int totalCount = uploadTaskInfo.fileObjs.size();
//        if(totalCount == 0){
//            broadcastProgress(uploadTaskInfo.getInfoId(), 100, 100);
//        }
        for (int i = 0; i < totalCount; i++) {
            //更新进度条
            broadcastProgress(uploadTaskInfo.getInfoId(), i, totalCount);

            //获取上传文件
            UploadFileObj uploadFileObj = uploadTaskInfo.fileObjs.get(i);

            //上传操作
            try {
                //判断服务器是否已存在该文件
                if (!ossManager.checkFileExist(uploadFileObj.getObjectKey())) {
                    //如果不存在则上传
                    ossManager.upload(uploadFileObj.getObjectKey(), uploadFileObj.getFinalUploadFile().getAbsolutePath());
                }

                recorder.oneFileCompleted(uploadTaskInfo.getInfoId(), uploadFileObj.getObjectKey());
            } catch (ServiceException | ClientException e) {
                e.printStackTrace();
                errorCount += 1;
            }
        }

        //上传完成，清空任务列表
        if (errorCount == 0) {
            recorder.deleteTask(uploadTaskInfo.getInfoId());
            broadcastCompleted(uploadTaskInfo.getInfoId(), uploadTaskInfo.getUploadedFileJson());
        } else {
            broadcastError(uploadTaskInfo.getInfoId(), new Exception("上传异常"));
        }
    }

    void broadcastProgress(final String taskId, final long uploaded, final long total) {
        long currentTime = System.currentTimeMillis();
        if (currentTime < lastProgressNotificationTime + PROGRESS_REPORT_INTERVAL) {
            return;
        }

        lastProgressNotificationTime = currentTime;

        updateNotificationProgress((int) uploaded, (int) total);

        final Intent intent = new Intent(getActionBroadcast());
        intent.putExtra(UPLOAD_ID, taskId);
        intent.putExtra(STATUS, STATUS_IN_PROGRESS);

        final int percentsProgress = (int) (uploaded * 100 / total);
        intent.putExtra(PROGRESS, percentsProgress);

        intent.putExtra(PROGRESS_UPLOADED_BYTES, uploaded);
        intent.putExtra(PROGRESS_TOTAL_BYTES, total);
        intent.putExtra(PARAM_UPLOAD_TYPE, uploadType);
        sendBroadcast(intent);
    }

    void broadcastCompleted(final String taskId, final String uploadedImageJson) {
        updateNotificationCompleted();
        final Intent intent = new Intent(getActionBroadcast());
        intent.putExtra(UPLOAD_ID, taskId);
        intent.putExtra(STATUS, STATUS_COMPLETED);
        intent.putExtra(PARAM_UPLOAD_TYPE, uploadType);
        intent.putExtra(UPLOADED_IMAGES_JSON, uploadedImageJson);
        intent.putExtra(UPLOAD_MESSAGE_TYPE, 0);
        sendBroadcast(intent);
        synchronized (WAKE_LOCK) {
            // sanity check for null as this is a public method
            if (wakeLock != null) {
                Log.v(TAG, "Releasing wakelock");
                try {
                    wakeLock.release();
                } catch (Throwable th) {
                    // ignoring this exception, probably wakeLock was already released
                }
            } else {
                // should never happen during normal workflow
                Log.e(TAG, "Wakelock reference is null");
            }
        }
    }

    void broadcastError(final String taskId, final Exception exception) {
        updateNotificationError(taskId);

        final Intent intent = new Intent(getActionBroadcast());
        intent.setAction(getActionBroadcast());
        intent.putExtra(UPLOAD_ID, taskId);
        intent.putExtra(STATUS, STATUS_ERROR);
        intent.putExtra(ERROR_EXCEPTION, exception.getMessage());
        intent.putExtra(PARAM_UPLOAD_TYPE, uploadType);
        sendBroadcast(intent);
        synchronized (WAKE_LOCK) {
            // sanity check for null as this is a public method
            if (wakeLock != null) {
                Log.v(TAG, "Releasing wakelock");
                try {
                    wakeLock.release();
                } catch (Throwable th) {
                    // ignoring this exception, probably wakeLock was already released
                }
            } else {
                // should never happen during normal workflow
                Log.e(TAG, "Wakelock reference is null");
            }
        }
    }


    private void createNotification() {
        Log.d(TAG, "createNotification() called with: " + "");
        notification.setContentTitle(notificationConfig.getTitle())
                .setContentText(notificationConfig.getMessage())
                .setContentIntent(notificationConfig.getPendingIntent(this))
                .setSmallIcon(android.R.drawable.stat_sys_upload)
                .setWhen(System.currentTimeMillis())  // the time stamp
                .setTicker("")
                .setProgress(100, 0, true)
                .setOngoing(true);

        startForeground(UPLOAD_NOTIFICATION_ID, notification.build());
    }

    private void updateNotificationProgress(int uploaded, int total) {
        Log.e(TAG, "updateNotificationProgress() called with: " + "uploadedBytes = [" + uploaded + "], totalBytes = [" + total + "]");
        notification.setContentTitle(notificationConfig.getTitle())
                .setContentText(notificationConfig.getMessage())
                .setContentIntent(notificationConfig.getPendingIntent(this))
                .setSmallIcon(notificationConfig.getIconResourceID())
                .setWhen(System.currentTimeMillis())  // the time stamp
                .setTicker("")
                .setProgress(total, uploaded, false)
                .setOngoing(true);

        startForeground(UPLOAD_NOTIFICATION_ID, notification.build());
    }

    private void updateNotificationCompleted() {
        Log.e(TAG, "updateNotificationCompleted() called with: " + "");
        stopForeground(notificationConfig.isAutoClearOnSuccess());

        if (!notificationConfig.isAutoClearOnSuccess()) {
            notification.setContentTitle(notificationConfig.getTitle())
                    .setContentText(notificationConfig.getCompleted())
                    .setContentIntent(notificationConfig.getPendingIntent(this))
                    .setSmallIcon(android.R.drawable.stat_sys_upload_done).setProgress(0, 0, false).setOngoing(false);
            setRingtone();
            notificationManager.notify(UPLOAD_NOTIFICATION_ID_DONE, notification.build());
        }
    }

    private void updateNotificationError(String taskId) {
        Log.e(TAG, "updateNotificationError() called with: " + "");
        stopForeground(false);
        notification.setContentTitle(notificationConfig.getTitle())
                .setContentText(notificationConfig.getError())
                .setContentIntent(notificationConfig.getPendingIntent(this))
                .setSmallIcon(android.R.drawable.stat_notify_error)
                .setProgress(0, 0, false)
                .setOngoing(false)
                .setAutoCancel(true);

        if (!TextUtils.isEmpty(recorder.getTaskName(taskId))) {

            Intent intent = new Intent(getApplicationContext(), UploadService.class);
            intent.setAction(UploadService.getActionUpload());
            intent.putExtra(UploadService.PARAM_NOTIFICATION_CONFIG, notificationConfig);
            intent.putExtra(UploadService.PARAM_ID, taskId);
            intent.putExtra(UploadService.PARAM_NAME, recorder.getTaskName(taskId));
            intent.putParcelableArrayListExtra(UploadService.PARAM_FILES, recorder.getUploadFileObjs(taskId));

            notification.setContentIntent(PendingIntent.getService(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT));

        }
        setRingtone();
        notificationManager.notify(UPLOAD_NOTIFICATION_ID_DONE, notification.build());
    }

    private void setRingtone() {
        Log.e(TAG, "setRingtone() called with: " + "");
        if (notificationConfig.isRingTone()) {
            notification.setSound(RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_NOTIFICATION));
            notification.setOnlyAlertOnce(false);
        }
    }
}
