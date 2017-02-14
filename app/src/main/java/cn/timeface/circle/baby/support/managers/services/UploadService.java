package cn.timeface.circle.baby.support.managers.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

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

public class UploadService extends Service {

    private static final String TAG = "UploadAllPicService";
    private ArrayList<String> localUrls;
    private int count = 0;
    private double oldProgress = 0;

    public static void start(Context context, List<String> localUrls) {
        Intent intent = new Intent(context, UploadService.class);
        intent.putStringArrayListExtra("localurls", (ArrayList<String>) localUrls);
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
        localUrls = intent.getStringArrayListExtra("localurls");
        count = 0;
        uploadImage(localUrls.get(count));

        return super.onStartCommand(intent, flags, startId);
    }


    private void uploadImage(String path) {
        if (TextUtils.isEmpty(path)) {
            return;
        }
        Log.v(TAG, "count ============ " + count);
        Log.v(TAG, "img.getUrl ============ " + path);
        OSSManager ossManager = OSSManager.getOSSManager(this);
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
                                        EventBus.getDefault().post(new UploadEvent((int) progress));
                                    }
                                }
                            }, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
                                @Override
                                public void onSuccess(PutObjectRequest putObjectRequest, PutObjectResult putObjectResult) {
                                    count++;
                                    int i = count * 100 / localUrls.size();
                                    if (i > oldProgress) {
                                        oldProgress = i;
                                        EventBus.getDefault().post(new UploadEvent(i));
                                    }
                                    if (model!=null){
                                        model.setNeedUpload(false);
                                        model.update();
                                    }
                                    if (count < localUrls.size()) {
                                        uploadImage(localUrls.get(count));
                                    }
                                }

                                @Override
                                public void onFailure(PutObjectRequest putObjectRequest, ClientException e, ServiceException e1) {
                                    if (count < localUrls.size()) {
                                        uploadImage(localUrls.get(count));
                                    }
                                }
                            });
                        } else {
                            count++;
                            int i = count * 100 / localUrls.size();
                            if (i > oldProgress) {
                                oldProgress = i;
                                EventBus.getDefault().post(new UploadEvent(i));
                            }
                            if (count < localUrls.size()) {
                                uploadImage(localUrls.get(count));
                            }
                        }
                        String objectKey = uploadFileObj.getObjectKey();
                        if (model!=null){
                            model.setNeedUpload(false);
                            model.update();
                        }
                        Log.v(TAG, "uploadImage  objectKey============ " + objectKey);
                    } catch (Exception e) {
                        Log.e(TAG, "uploadImage", e);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "uploadImage", e);
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
