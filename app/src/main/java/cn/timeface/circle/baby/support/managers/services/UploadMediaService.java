package cn.timeface.circle.baby.support.managers.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import cn.timeface.circle.baby.support.api.models.db.PhotoModel;
import cn.timeface.circle.baby.support.api.models.objs.MediaObj;
import cn.timeface.circle.baby.support.api.models.objs.MyUploadFileObj;
import cn.timeface.circle.baby.support.api.models.objs.TimeLineObj;
import cn.timeface.circle.baby.support.oss.OSSManager;
import cn.timeface.circle.baby.support.oss.uploadservice.UploadFileObj;
import cn.timeface.circle.baby.ui.circle.bean.CircleTimeLineExObj;
import cn.timeface.circle.baby.ui.circle.bean.CircleTimelineObj;
import cn.timeface.circle.baby.ui.timelines.Utils.JSONUtils;
import cn.timeface.circle.baby.ui.timelines.Utils.LogUtil;
import cn.timeface.circle.baby.ui.timelines.beans.UploadTaskProgress;

/**
 * author : wangshuai Created on 2017/4/14
 * email : wangs1992321@gmail.com
 */
public class UploadMediaService extends IntentService {

    private UploadTaskProgress currentTask = null;
    private List<UploadTaskProgress> list = new ArrayList<>();

    public static void start(Context context) {
        ArrayList<UploadTaskProgress> array = new ArrayList<>(0);
        array.addAll(UploadTaskProgress.queryTaskList());
        if (array == null || array.size() <= 0)
            return;
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(UploadTaskProgress.class.getSimpleName() + "list", array);
        Intent intent = new Intent(context, UploadMediaService.class);
        intent.putExtras(bundle);
        context.startService(intent);
    }

    public static void start(Context context, TimeLineObj timeLineObj) {
        UploadTaskProgress taskProgress = new UploadTaskProgress(String.valueOf(timeLineObj.getTimeId()) + String.valueOf(UploadTaskProgress.TYPE_TIMELINE)
                , timeLineObj.getTimeId()
                , UploadTaskProgress.TYPE_TIMELINE
                , JSONUtils.parse2JSONString(timeLineObj)
                , System.currentTimeMillis(), System.currentTimeMillis(), 0
                , timeLineObj.getMediaList().size());
        taskProgress.save();
        Bundle bundle = new Bundle();
        bundle.putParcelable(UploadTaskProgress.class.getSimpleName(), taskProgress);
        Intent intent = new Intent(context, UploadMediaService.class);
        intent.putExtras(bundle);
        context.startService(intent);
    }

    public static void start(Context context, CircleTimelineObj timeLineObj) {
        Bundle bundle = new Bundle();
        UploadTaskProgress taskProgress = new UploadTaskProgress(String.valueOf(timeLineObj.getCircleTimelineId()) + String.valueOf(UploadTaskProgress.TYPE_CIRCLETIMELINE)
                , timeLineObj.getCircleTimelineId()
                , UploadTaskProgress.TYPE_CIRCLETIMELINE
                , JSONUtils.parse2JSONString(timeLineObj)
                , System.currentTimeMillis(), System.currentTimeMillis(), 0
                , timeLineObj.getMediaList().size());
        taskProgress.save();
        bundle.putParcelable(UploadTaskProgress.class.getSimpleName(), taskProgress);
        Intent intent = new Intent(context, UploadMediaService.class);
        intent.putExtras(bundle);
        context.startService(intent);
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public UploadMediaService() {
        super("UploadMediaService");
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Bundle bundle = intent.getExtras();
        ArrayList<UploadTaskProgress> arrayList = bundle.getParcelableArrayList(UploadTaskProgress.class.getSimpleName() + "list");
        if (arrayList != null && arrayList.size() > 0)
            list.addAll(0, arrayList);
        UploadTaskProgress taskProgress = bundle.getParcelable(UploadTaskProgress.class.getSimpleName());
        if (taskProgress != null) {
            list.add(0, taskProgress);
        }
        upload();
    }

    private void upload() {
        if (currentTask != null && currentTask.getProgress() >= currentTask.getCount())
            currentTask = null;
        if (currentTask == null && list != null && list.size() > 0)
            currentTask = list.remove(0);
        if (currentTask == null) return;
        switch (currentTask.getType()) {
            case UploadTaskProgress.TYPE_TIMELINE:
                TimeLineObj timeLineObj = new Gson().fromJson(currentTask.getContentString(), TimeLineObj.class);
                if (timeLineObj != null) {
                    uploadMedias(timeLineObj.getMediaList());
                }
                break;
            case UploadTaskProgress.TYPE_CIRCLETIMELINE:
                CircleTimelineObj circleTimelineObj = new Gson().fromJson(currentTask.getContentString(), CircleTimelineObj.class);
                if (circleTimelineObj != null) {
                    uploadMedias(circleTimelineObj.getMediaList());
                }
                break;
            case UploadTaskProgress.TYPE_LOCAL_URL:
                try {
                    JSONArray array = new JSONArray(currentTask.getContentString());
                    ArrayList<MediaObj> mediaObjs = new ArrayList<>(0);
                    for (int i = 0; i < array.length(); i++) {
                        mediaObjs.add(new MediaObj(array.getString(i)));
                    }
                    if (mediaObjs.size() > 0) {
                        uploadMedias(mediaObjs);
                    }
                } catch (Exception e) {
                    LogUtil.showError(e);
                }

                break;
        }

    }

    private void updatePhotoModel(PhotoModel model) {
        if (model != null) {
            model.setNeedUpload(false);
            model.update();
        }
    }

    public void uploadMedias(List<? extends MediaObj> list) {
        int progress = currentTask.getCount() - list.size();
        currentTask.setProgress(progress);
        LogUtil.showLog("progress", progress + "--" + currentTask.getCount());
        currentTask.save();
        if (progress >= currentTask.getCount()) {
            currentTask.delete();
            upload();
            return;
        }

        MediaObj mediaObj = list.remove(0);
        if (mediaObj.getId() > 0) {
            uploadMedias(list);
            mediaObj = null;
            return;
        }
        UploadFileObj uploadFileObj = new MyUploadFileObj(mediaObj.getLocalPath());
        PhotoModel model = PhotoModel.getPhotoModel(mediaObj.getLocalPath());
        mediaObj = null;
        if (!OSSManager.getOSSManager(this).checkFileExist(uploadFileObj.getObjectKey())) {
            OSSManager.getOSSManager(this).upload(uploadFileObj.getObjectKey(), uploadFileObj.getFinalUploadFile().getAbsolutePath(), new OSSProgressCallback<PutObjectRequest>() {
                @Override
                public void onProgress(PutObjectRequest putObjectRequest, long l, long l1) {
                }
            }, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
                @Override
                public void onSuccess(PutObjectRequest putObjectRequest, PutObjectResult putObjectResult) {
                    updatePhotoModel(model);
                    uploadMedias(list);
                }

                @Override
                public void onFailure(PutObjectRequest putObjectRequest, ClientException e, ServiceException e1) {
                }
            });
        } else {
            updatePhotoModel(model);
            uploadMedias(list);
        }
    }

}
