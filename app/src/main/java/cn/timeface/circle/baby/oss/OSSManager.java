package cn.timeface.circle.baby.oss;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.ObjectMetadata;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.Locale;

import cn.timeface.circle.baby.BuildConfig;
import cn.timeface.circle.baby.utils.FastData;
import cn.timeface.circle.baby.utils.encode.AES;


/**
 * author: rayboot  Created on 15/12/29.
 * email : sy0725work@gmail.com
 */
public class OSSManager {
    static OSSManager ossManager;
    private final OSS oss;
    private final String bucket;
    private final String endPoint;


    OSSManager(Context context) {
        //从manifests中获取endpoint sts 和 bucket
        ApplicationInfo info = null;
        try {
            info = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        endPoint = info.metaData.getString("END_POINT");
        bucket = info.metaData.getString("ALI_BUCKET");

        OSSCredentialProvider credentialProvider = new OSSPlainTextAKSKCredentialProvider(new AES().decrypt(FastData.getUploadak()), new AES().decrypt(FastData.getUploadsk()));

        //配置
        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
        conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
        conf.setMaxConcurrentRequest(5); // 最大并发请求数，默认5个
        conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次
        if (BuildConfig.DEBUG) {
            OSSLog.enableLog();
        }
        oss = new OSSClient(context, endPoint, credentialProvider, conf);
    }

    public static OSSManager getOSSManager(Context context) {
        if (ossManager == null) {
            ossManager = new OSSManager(context);
        }
        return ossManager;
    }

    public OSS getOss() {
        return oss;
    }

    public String getBucket() {
        return bucket;
    }


    /**
     * 同步检测该文件是否在阿里云上存在
     *
     * @param objectKey object key
     * @return true 已存在
     */
    public boolean checkFileExist(String objectKey) {
        OkHttpClient httpClient = new OkHttpClient();
        String url = String.format("http://%s.%s/%s", this.bucket, this.endPoint.replace("http://", ""), objectKey);
        Request request = new Request.Builder().head()
                .url(url)
                .build();
        Response response = null;
        try {
            response = httpClient.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response != null && (response.code() == 200);
    }

    /**
     * 查找Content Type
     *
     * @param filePath 文件路径
     * @return
     */
    public String getContentType(String filePath) {
        if (TextUtils.isEmpty(filePath) || !filePath.contains(".")) {
            return "application/octet-stream";
        }
        int index = filePath.lastIndexOf(".");
        if (index != -1) {
            return MimeTypeMap.getSingleton().getMimeTypeFromExtension(filePath.substring(index + 1).toLowerCase(Locale.US));
        } else {
            return "application/octet-stream";
        }
    }

    public PutObjectResult upload(String objectKey, String uploadFilePath) throws ClientException, ServiceException {
        PutObjectRequest put = getPutRequest(bucket, objectKey, uploadFilePath);
        return oss.putObject(put);
    }

    public OSSAsyncTask upload(String objectKey, String uploadFilePath, OSSProgressCallback<PutObjectRequest> progressCallback, OSSCompletedCallback<PutObjectRequest, PutObjectResult> completedCallback) {
        PutObjectRequest put = getPutRequest(bucket, objectKey, uploadFilePath);
        // 异步上传时可以设置进度回调
        put.setProgressCallback(progressCallback);
        return oss.asyncPutObject(put, completedCallback);
    }

    public PutObjectRequest getPutRequest(String bucket, String objectKey, String uploadFilePath) {
        // 构造上传请求
        PutObjectRequest put = new PutObjectRequest(bucket, objectKey, uploadFilePath);
        ObjectMetadata metaData = new ObjectMetadata();
        metaData.setContentType(getContentType(uploadFilePath));
        put.setMetadata(metaData);
        return put;
    }
}
