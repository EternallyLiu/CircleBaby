package cn.timeface.circle.baby.api.services;

import android.net.Uri;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;

import java.io.File;

import cn.timeface.circle.baby.App;
import cn.timeface.circle.baby.api.models.objs.MyUploadFileObj;
import cn.timeface.circle.baby.oss.OSSManager;
import cn.timeface.open.managers.interfaces.IUploadServices;
import cn.timeface.open.ucrop.util.FileUtils;

/**
 * author: rayboot  Created on 16/8/2.
 * email : sy0725work@gmail.com
 */
public class OpenUploadServices implements IUploadServices {
    @Override
    public String doUpload(Uri uri) {
        File file = FileUtils.getFile(App.getInstance(), uri);
        MyUploadFileObj uploadFileObj = new MyUploadFileObj(file.getAbsolutePath());

        try {
            if (!OSSManager.getOSSManager(App.getInstance()).checkFileExist(uploadFileObj.getObjectKey())) {
                OSSManager.getOSSManager(App.getInstance())
                        .upload(uploadFileObj.getObjectKey(), file.getAbsolutePath());
            }
            return ApiService.IMAGE_BASE_URL + uploadFileObj.getObjectKey();
        } catch (ClientException e) {
            e.printStackTrace();
        } catch (ServiceException e) {
            e.printStackTrace();
        }
        return null;
    }
}
