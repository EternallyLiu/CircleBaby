package cn.timeface.circle.baby.support.utils;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.IOException;

import cn.timeface.circle.baby.App;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;
import rx.Observable;
import rx.Subscriber;

/**
 * @author wxw
 * @from 2016/2/3
 * @TODO
 */
public class RxDownloadUtil {

    private static final String TAG = "RxDownloadUtil";

    private static final String CARD_IMAGE_DIR = "baby/";

    private static RxDownloadUtil mInstance;
    private OkHttpClient mOkHttpClient;

    private RxDownloadUtil() {
        mOkHttpClient = new OkHttpClient();
    }

    public static RxDownloadUtil getInstance() {
        if (mInstance == null) {
            synchronized (RxDownloadUtil.class) {
                if (mInstance == null) {
                    mInstance = new RxDownloadUtil();
                }
            }
        }
        return mInstance;
    }

    // 保存识图卡片
    public Observable<File> saveCardImage(String imageUrl) {
        File cacheDir = getCardImageDir();
        return downloadFile(imageUrl, cacheDir.getAbsolutePath(),
                getDownloadFileName(imageUrl, MD5.encode(imageUrl) + ".jpg"));
    }

    // 识图卡片保存目录
    public static File getCardImageDir() {
        File dir = new File(Environment.getExternalStorageDirectory(), CARD_IMAGE_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    public String getDownloadFileName(String downloadUrl, String defaultName) {
        int start = downloadUrl.lastIndexOf("/");
        if (start > 0 && start < downloadUrl.length() - 1) {
            return downloadUrl.substring(start + 1, downloadUrl.length());
        }
        return defaultName;
    }

    public Observable<File> downloadFile(String downloadUrl, String destFileDir, String destFileName) {
        return Observable.create(new Observable.OnSubscribe<File>() {
            @Override
            public void call(Subscriber<? super File> subscriber) {
                Request request = new Request.Builder()
                        .url(downloadUrl)
                        .tag(TAG)
                        .build();
                mOkHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        subscriber.onError(e);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        BufferedSink bufferedSink = null;
                        try {
                            File dir = new File(destFileDir);
                            if (!dir.exists()) {
                                dir.mkdirs();
                            }
                            File file = new File(dir, destFileName);

                            bufferedSink = Okio.buffer(Okio.sink(file));
                            bufferedSink.writeAll(response.body().source());

                            // notify system
                            App.getInstance().sendBroadcast(
                                    new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));

                            subscriber.onNext(file);
                            subscriber.onCompleted();
                        } catch (IOException e) {
                            subscriber.onError(e);
                        } finally {
                            try {
                                if (bufferedSink != null)
                                    bufferedSink.close();
                            } catch (IOException e) {
                                subscriber.onError(e);
                            }
                        }
                    }
                });
            }
        });
    }

}
