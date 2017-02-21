package cn.timeface.circle.baby.support.oss;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;

import java.io.File;

import cn.timeface.circle.baby.support.api.models.TFUploadFile;
import cn.timeface.circle.baby.support.utils.FileUtils;
import cn.timeface.open.managers.interfaces.IUploadServices;

/**
 * Created by JieGuo on 16/9/29.
 */

public class OpenSDKUploader implements IUploadServices {

    private static final String TAG = "OpenSDKUploader";
    public static final String UPLOAD_CALENDAR_FOLDER = "times";

    private Context context;

    public OpenSDKUploader(Context context) {
        this.context = context;
    }

    @Override
    public String doUpload(Uri uri) {

        File file = FileUtils.getFile(context, uri);
        if (file == null) {
            return null;
        }
        TFUploadFile uploadFile = new TFUploadFile(file.getAbsolutePath(), UPLOAD_CALENDAR_FOLDER);
        String objectKey = uploadFile.getObjectKey();
        Log.e(TAG, "objectKey : " + objectKey);
        try {

            OSSManager ossManager = OSSManager.getOSSManager(context);
            if (!ossManager.checkFileExist(objectKey)) {
                ossManager.upload(objectKey, uploadFile.getFilePath());
            }
            // @NOTICE 这里需要确认一下这个域名,是不是一直可用的。
            return "http://img1.timeface.cn/" + objectKey;
        } catch (ClientException e) {
            Log.e(TAG, "error", e);
        } catch (ServiceException e) {
            Log.e(TAG, "error", e);
        }
        return null;
    }
}
