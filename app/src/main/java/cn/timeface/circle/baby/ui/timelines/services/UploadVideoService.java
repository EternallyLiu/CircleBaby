package cn.timeface.circle.baby.ui.timelines.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
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

import cn.timeface.circle.baby.events.UploadEvent;
import cn.timeface.circle.baby.support.api.models.objs.MyUploadFileObj;
import cn.timeface.circle.baby.support.oss.OSSManager;
import cn.timeface.circle.baby.support.oss.uploadservice.UploadFileObj;
import cn.timeface.circle.baby.ui.timelines.Utils.LogUtil;

/**
 * author : wangshuai Created on 2017/2/22
 * email : wangs1992321@gmail.com
 */
public class UploadVideoService extends IntentService {

    private static SparseArray<String> sparseArray = new SparseArray<>(0);
    private static int currentNegex = -1;

    private boolean isComplete = true;
    private OSSManager ossManager;

    public static void start(Context context, int timeId, String path) {
        if (TextUtils.isEmpty(path))
            return;
        if (timeId <= 0) {
            timeId = currentNegex;
            currentNegex--;
        }
        sparseArray.put(timeId, path);
        Intent intent = new Intent(context, UploadVideoService.class);
        context.startService(intent);
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public UploadVideoService() {
        super("upload_video_service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (isComplete)
            uploadVideo();
    }

    private void init() {
        if (ossManager == null)
            ossManager = OSSManager.getOSSManager(this);
    }

    private void uploadVideo() {
        init();
        while (sparseArray.size() > 0) {
            isComplete = false;
            try {
                int timeId = sparseArray.keyAt(0);
                String path = sparseArray.get(timeId);
                sparseArray.remove(timeId);
                LogUtil.showLog("timeId==" + timeId + "---path==" + path);
                //获取上传文件
                UploadFileObj uploadFileObj = new MyUploadFileObj(path);
                //上传操作
                try {
                    //判断服务器是否已存在该文件
                    if (!ossManager.checkFileExist(uploadFileObj.getObjectKey())) {
                        //如果不存在则上传
                        ossManager.upload(uploadFileObj.getObjectKey(), uploadFileObj.getFinalUploadFile().getAbsolutePath(), new OSSProgressCallback<PutObjectRequest>() {
                            @Override
                            public void onProgress(PutObjectRequest putObjectRequest, long l, long l1) {
                                int sinpro = (int) (l * 100 / l1);
                                EventBus.getDefault().post(new UploadEvent(sinpro, timeId, false));
                            }
                        }, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
                            @Override
                            public void onSuccess(PutObjectRequest putObjectRequest, PutObjectResult putObjectResult) {

                                LogUtil.showLog("onSuccess");
                                EventBus.getDefault().post(new UploadEvent(100, timeId, true));
                            }

                            @Override
                            public void onFailure(PutObjectRequest putObjectRequest, ClientException e, ServiceException e1) {

                            }
                        });
                    } else {
                        LogUtil.showLog("sucess");
                        EventBus.getDefault().post(new UploadEvent(100, timeId, true));
                    }
                } catch (Exception e) {
                    LogUtil.showError(e);
                }
            } catch (Exception e) {
                LogUtil.showError(e);
            }
        }
        isComplete = true;
    }
}
