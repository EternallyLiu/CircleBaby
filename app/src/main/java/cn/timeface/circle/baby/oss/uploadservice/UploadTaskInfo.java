package cn.timeface.circle.baby.oss.uploadservice;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * author: rayboot  Created on 15/10/10.
 * email : sy0725work@gmail.com
 */
public class UploadTaskInfo {

    Context context;
    String infoId;
    String infoName;

    private Gson gson = new Gson();

    public ArrayList<UploadFileObj> fileObjs = new ArrayList<>();
    private UploadNotificationConfig notificationConfig;

    public UploadTaskInfo(String infoId, String infoName, ArrayList<UploadFileObj> fileObjs, UploadNotificationConfig uploadNotificationConfig) {
        this.infoId = infoId;
        this.infoName = infoName;
        this.fileObjs = fileObjs;
        this.notificationConfig = uploadNotificationConfig;
    }

    public UploadTaskInfo(Context context, String infoId, String infoName, ArrayList<UploadFileObj> fileObjs) {
        this.infoId = infoId;
        this.infoName = infoName;
        this.fileObjs = fileObjs;
        this.context = context;
    }

    public String getUploadedFileJson() {

        String imagesString = gson.toJson(fileObjs);
        Log.e("getUploadFIleJson", "postRecord: " + imagesString);
        return imagesString;
    }

    public String getInfoId() {
        return infoId;
    }

    public String getInfoName() {
        return infoName;
    }

    public void setInfoId(String infoId) {
        this.infoId = infoId;
    }

    public ArrayList<UploadFileObj> getFileObjs() {
        return fileObjs;
    }

    public void setFileObjs(ArrayList<UploadFileObj> fileObjs) {
        this.fileObjs = fileObjs;
    }


    /**
     * Sets custom notification configuration.
     *
     * @param iconResourceID     ID of the notification icon. You can use your own app's R.drawable.your_resource
     * @param title              Notification title
     * @param message            Text displayed in the notification when the uploadWithProgress is in progress
     * @param completed          Text displayed in the notification when the uploadWithProgress is completed successfully
     * @param error              Text displayed in the notification when an error occurs
     * @param autoClearOnSuccess true if you want to automatically clear the notification when the uploadWithProgress gets completed
     *                           successfully
     */
    public void setNotificationConfig(final int iconResourceID, final String title, final String message,
                                      final String completed, final String error, final boolean autoClearOnSuccess) {
        notificationConfig = new UploadNotificationConfig(iconResourceID, title, message, completed, error,
                autoClearOnSuccess);
    }

    /**
     * Sets the intent to be executed when the user taps on the uploadWithProgress progress notification.
     *
     * @param intent
     */
    public final void setNotificationClickIntent(Intent intent) {
        notificationConfig.setClickIntent(intent);
    }

    /**
     * Gets the uploadWithProgress notification configuration.
     *
     * @return
     */
    public UploadNotificationConfig getNotificationConfig() {
        return notificationConfig;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
