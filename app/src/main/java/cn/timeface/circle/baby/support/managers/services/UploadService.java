package cn.timeface.circle.baby.support.managers.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import cn.timeface.circle.baby.events.UploadEvent;
import cn.timeface.circle.baby.support.api.models.db.PhotoModel;
import cn.timeface.circle.baby.support.api.models.objs.MyUploadFileObj;
import cn.timeface.circle.baby.support.oss.OSSManager;
import cn.timeface.circle.baby.support.oss.uploadservice.UploadFileObj;
import cn.timeface.circle.baby.ui.timelines.Utils.LogUtil;

public class UploadService extends Service {

    private static final String TAG = "UploadAllPicService";
    private ArrayList<String> localUrls;
    private int count = 0;
    private double oldProgress = 0;
    private SparseArray<ArrayList<String>> urlArray = new SparseArray<>(0);

    /**
     * 在没有timeid绑定value的情况下使用负数作为urlArray的key
     */
    private int currentNegative = -1;

    /**
     * 当前上传处理的timeId
     */
    private int currentTimeId = 0;

    private boolean isCompeleteUpload = true;

    public static void start(Context context, List<String> localUrls) {
        Intent intent = new Intent(context, UploadService.class);
        intent.putStringArrayListExtra("localurls", (ArrayList<String>) localUrls);
        context.startService(intent);
    }

    public static void start(Context context, int timeId, List<String> localUrls) {
        Intent intent = new Intent(context, UploadService.class);
        intent.putStringArrayListExtra("localurls", (ArrayList<String>) localUrls);
        intent.putExtra("timeId", timeId);
        context.startService(intent);
    }

    public static void stop(Context context) {
        context.stopService(new Intent(context, UploadService.class));
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) return START_STICKY;
        int id = intent.getIntExtra("timeId", currentNegative);
        if (id <= 0) {
            id = currentNegative;
            currentNegative--;
        }
        ArrayList<String> urls = intent.getStringArrayListExtra("localurls");
        if (urls != null && urls.size() > 0)
            urlArray.put(id, urls);
//        localUrls = ;
//        count = 0;
        uploadNext();
        return super.onStartCommand(intent, flags, startId);
    }

    private void uploadNext() {
        LogUtil.showLog("urlArray size:"+urlArray.size());
        if (isCompeleteUpload && (localUrls == null || localUrls.size() <= 0)) {
            if (urlArray.size() > 0) {
                currentTimeId = urlArray.keyAt(0);
                localUrls = urlArray.get(currentTimeId);
                count = 0;
                uploadImage(localUrls.get(count));
                urlArray.remove(currentTimeId);
            }else stopSelf();
            LogUtil.showLog("currentTimeid=="+currentTimeId+"----");
        }
    }

    private void uploadImage(String path) {
        if (TextUtils.isEmpty(path)) {
            return;
        }
        LogUtil.showLog("count ============ " + count+"------size:"+localUrls.size());
        OSSManager ossManager = OSSManager.getOSSManager(this);
        isCompeleteUpload = false;
        new Thread() {
            @Override
            public void run() {
                try {
                    //获取上传文件
                    UploadFileObj uploadFileObj = new MyUploadFileObj(path);
                    //上传操作
                    try {
                        PhotoModel model = PhotoModel.getPhotoModel(path);
                        //判断服务器是否已存在该文件
                        if (!ossManager.checkFileExist(uploadFileObj.getObjectKey())) {
                            //如果不存在则上传
                            ossManager.upload(uploadFileObj.getObjectKey(), uploadFileObj.getFinalUploadFile().getAbsolutePath(), new OSSProgressCallback<PutObjectRequest>() {
                                @Override
                                public void onProgress(PutObjectRequest putObjectRequest, long l, long l1) {
                                    int sinpro = (int) (l * 100 / l1);
                                    int i = count * 100 / localUrls.size();
                                    double progress = i + sinpro / localUrls.size();
                                    if (progress > oldProgress) {
                                        oldProgress = progress;
                                        EventBus.getDefault().post(new UploadEvent((int) progress, currentTimeId, false));
                                    }
                                }
                            }, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
                                @Override
                                public void onSuccess(PutObjectRequest putObjectRequest, PutObjectResult putObjectResult) {
                                    count++;
                                    int i = count * 100 / localUrls.size();
                                    if (i > oldProgress) {
                                        oldProgress = i;
//                                        EventBus.getDefault().post(new UploadEvent(i, currentTimeId, false));
                                    }
                                    if (model != null) {
                                        model.setNeedUpload(false);
                                        model.update();
                                    }
                                    if (count < localUrls.size()) {
                                        uploadImage(localUrls.get(count));
                                    } else {
                                        isCompeleteUpload = true;
                                        localUrls.clear();
                                        EventBus.getDefault().post(new UploadEvent(i, currentTimeId, true));
                                        uploadNext();
                                    }
                                }

                                @Override
                                public void onFailure(PutObjectRequest putObjectRequest, ClientException e, ServiceException e1) {
                                    if (count < localUrls.size()) {
                                        uploadImage(localUrls.get(count));
                                    } else {
                                        isCompeleteUpload = true;
                                        localUrls.clear();
                                        EventBus.getDefault().post(new UploadEvent(currentTimeId, true));
                                        localUrls = null;
                                        uploadNext();
                                    }
                                }
                            });
                        } else {
                            count++;
                            int i = count * 100 / localUrls.size();
                            if (i > oldProgress) {
                                oldProgress = i;
//                                EventBus.getDefault().post(new UploadEvent(i));
                            }
                            if (count < localUrls.size()) {
                                uploadImage(localUrls.get(count));
                            } else {
                                isCompeleteUpload = true;
                                localUrls.clear();
                                EventBus.getDefault().post(new UploadEvent(currentTimeId, true));
                                localUrls = null;
                                uploadNext();
                            }
                        }
                        String objectKey = uploadFileObj.getObjectKey();
                        if (model != null) {
                            model.setNeedUpload(false);
                            model.update();
                        }
                        LogUtil.showLog("uploadImage  objectKey============ " + objectKey);
                    } catch (Exception e) {
                        LogUtil.showError(e);
                    }
                } catch (Exception e) {
                    LogUtil.showError(e);
                }
            }
        }.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
