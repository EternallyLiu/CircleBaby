package cn.timeface.circle.baby.support.oss;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;
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
import com.alibaba.sdk.android.oss.common.auth.OSSFederationCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSFederationToken;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.ObjectMetadata;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;

import java.io.IOException;
import java.util.Locale;

import cn.timeface.circle.baby.BuildConfig;
import cn.timeface.circle.baby.support.api.models.TFUploadFile;
import cn.timeface.circle.baby.support.oss.token.FederationTokenGetter;
import okhttp3.OkHttpClient;
import rx.Observable;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;


/**
 * author: rayboot  Created on 15/12/29.
 * email : sy0725work@gmail.com
 */
public class OSSManager {
    private static volatile OSSManager ossManager;
    private final OSS oss;
    private final String bucket;
    private final String endPoint;
    private final OkHttpClient httpClient;


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
        final String sts = info.metaData.getString("ALI_STS");
        bucket = info.metaData.getString("ALI_BUCKET");

        if (TextUtils.isEmpty(endPoint) || TextUtils.isEmpty(sts) || TextUtils.isEmpty(bucket)) {
            throw new IllegalArgumentException("you must set END_POINT,ALI_STS, ALI_BUCKET in manifests ");
        }

        //sts服务
        OSSCredentialProvider credentialProvider = new OSSFederationCredentialProvider() {

            @Override
            public OSSFederationToken getFederationToken() {

                // 为指定的用户拿取服务其授权需求的FederationToken
                OSSFederationToken token = FederationTokenGetter.getToken(sts);
                if (token == null) {
                    Log.e("aliyun", "获取FederationToken失败!!!");
                }
                return token;
            }
        };

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
        httpClient = new OkHttpClient();
    }

    public static OSSManager getOSSManager(Context context) {
        if (ossManager == null) {
            synchronized (OSSManager.class) {
                if (ossManager == null) {
                    ossManager = new OSSManager(context.getApplicationContext());
                }
            }
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
        String url = String.format("http://%s.%s/%s", this.bucket, this.endPoint.replace("http://", ""), objectKey);
        okhttp3.Request request = new okhttp3.Request.Builder().head()
                .url(url)
                .build();
        okhttp3.Response response = null;
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
        if (oss == null) throw new NullPointerException("oss is a null object");
        return oss.putObject(put);
    }

    public OSSAsyncTask upload(String objectKey, String uploadFilePath, OSSProgressCallback<PutObjectRequest> progressCallback, OSSCompletedCallback<PutObjectRequest, PutObjectResult> completedCallback) {
        PutObjectRequest put = getPutRequest(bucket, objectKey, uploadFilePath);
        // 异步上传时可以设置进度回调
        put.setProgressCallback(progressCallback);
        return oss.asyncPutObject(put, completedCallback);
    }


//    public Observable<PutObjectResult> upload(final String objectKey, final String uploadFilePath) {
//        return Observable.create(new Observable.OnSubscribe<PutObjectResult>() {
//            @Override
//            public void call(Subscriber<? super PutObjectResult> subscriber) {
//
//                PutObjectRequest put = getPutRequest(bucket, objectKey, uploadFilePath);
//                try {
//                    PutObjectResult putResult = oss.putObject(put);
//                    subscriber.onNext(putResult);
//                    subscriber.onCompleted();
//                    Log.d("PutObject", "UploadSuccess");
//                    Log.d("ETag", putResult.getETag());
//                    Log.d("RequestId", putResult.getRequestId());
//                } catch (ClientException e) {
//                    // 本地异常如网络异常等
//                    subscriber.onError(e);
//                    e.printStackTrace();
//                } catch (ServiceException e) {
//                    // 服务异常
//                    Log.e("RequestId", e.getRequestId());
//                    Log.e("ErrorCode", e.getErrorCode());
//                    Log.e("HostId", e.getHostId());
//                    Log.e("RawMessage", e.getRawMessage());
//                    subscriber.onError(e);
//                }
//            }
//        });
//    }
//
//    public Observable<Integer> uploadWithProgress(final String objectKey, final String uploadFilePath) {
//        return Observable.create(new Observable.OnSubscribe<Integer>() {
//            @Override
//            public void call(final Subscriber<? super Integer> subscriber) {
//                PutObjectRequest put = getPutRequest(bucket, objectKey, uploadFilePath);
//                // 异步上传时可以设置进度回调
//                put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
//                    @Override
//                    public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
//                        subscriber.onNext((int) (currentSize * 100 / totalSize));
//                        Log.d("PutObject", "currentSize: " + currentSize + " totalSize: " + totalSize);
//                    }
//                });
//
//                OSSAsyncTask task = oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
//                    @Override
//                    public void onSuccess(PutObjectRequest request, PutObjectResult result) {
//                        Log.d("PutObject", "UploadSuccess");
//                        Log.d("ETag", result.getETag());
//                        Log.d("RequestId", result.getRequestId());
//                        subscriber.onCompleted();
//                    }
//
//                    @Override
//                    public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
//                        // 请求异常
//                        subscriber.onError(clientExcepion != null ? clientExcepion : serviceException);
//                        if (clientExcepion != null) {
//                            // 本地异常如网络异常等
//                            clientExcepion.printStackTrace();
//                        }
//                        if (serviceException != null) {
//                            // 服务异常
//                            Log.e("ErrorCode", serviceException.getErrorCode());
//                            Log.e("RequestId", serviceException.getRequestId());
//                            Log.e("HostId", serviceException.getHostId());
//                            Log.e("RawMessage", serviceException.getRawMessage());
//                        }
//                    }
//                });
//                task.waitUntilFinished();
//            }
//        });
//    }

    public PutObjectRequest getPutRequest(String bucket, String objectKey, String uploadFilePath) {
        // 构造上传请求
        PutObjectRequest put = new PutObjectRequest(bucket, objectKey, uploadFilePath);
        ObjectMetadata metaData = new ObjectMetadata();
        metaData.setContentType(getContentType(uploadFilePath));
        put.setMetadata(metaData);
        return put;
    }

    /**
     * 同步上传文件到阿里云某个文件夹下
     *
     * @param folder "times","avatar",....
     * @param path   文件的绝对路径
     * @return 上传成功返回ObjectKey, 上传失败返回的ObjectKey为空字符串；
     */
    public Observable<String> asyncUploadFile(String folder, String path) {
        return Observable.just(path)
                .observeOn(Schedulers.io())
                .map(filePath -> new TFUploadFile(path, folder))
                .compose(uploadPicTransformer);
    }

    private final Observable.Transformer<TFUploadFile, String> uploadPicTransformer = new Observable.Transformer<TFUploadFile, String>() {
        @Override
        public Observable<String> call(Observable<TFUploadFile> pathObservable) {
            return pathObservable
                    .map(tfUploadFile -> {
                        String objectKey = tfUploadFile.getObjectKey();
                        //云端不存在，才会上传
                        if (!checkFileExist(objectKey)) {
                            try {
                                upload(objectKey, tfUploadFile.getFilePath());
                            } catch (ClientException | ServiceException e) {
                                e.printStackTrace();
                                tfUploadFile.setObjectKey("");
                            }
                        }
                        return tfUploadFile.getObjectKey();
                    });
        }
    };

    /**
     * 上传一个文件到阿里云，监听它的上传进度
     *
     * @param folder "times","avatar",....
     * @param path   文件的绝对路径
     * @return 可观察的上传进度
     */
    public PublishSubject<Integer> uploadPicObserveProgress(String folder, String path) {
        PublishSubject<Integer> publishSubject = PublishSubject.create();
        TFUploadFile uploadFile = new TFUploadFile(path, folder);//这个可以不用封装了
        PutObjectRequest put = getPutRequest(bucket, uploadFile.getObjectKey(), uploadFile.getFilePath());
        put.setProgressCallback((request, currentSize, totalSize)
                -> publishSubject.onNext((int) (currentSize * 100 / totalSize)));
        OSSAsyncTask task = oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                Log.d("PutObject", "UploadSuccess");
                Log.d("ETag", result.getETag());
                Log.d("RequestId", result.getRequestId());
                publishSubject.onCompleted();
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                // 请求异常
                if (clientExcepion != null) {
                    // 本地异常如网络异常等
                    clientExcepion.printStackTrace();
                    publishSubject.onError(clientExcepion);
                }
                if (serviceException != null) {
                    // 服务异常
                    publishSubject.onError(serviceException);
                    Log.e("ErrorCode", serviceException.getErrorCode());
                    Log.e("RequestId", serviceException.getRequestId());
                    Log.e("HostId", serviceException.getHostId());
                    Log.e("RawMessage", serviceException.getRawMessage());
                }
            }
        });
        if (publishSubject.hasThrowable()) {
            if (task != null) task.cancel();
        }
        return publishSubject;
    }

    public Observable<String> uploadPicToTimes(String filePath) {
        return asyncUploadFile("times", filePath);
    }

    public Observable<String> uploadPicToBabys(String filePath) {
        return asyncUploadFile("baby", filePath);
    }

    public Observable<String> uploadPicToAvatar(String filePath) {
        return asyncUploadFile("avatar", filePath);
    }
}
