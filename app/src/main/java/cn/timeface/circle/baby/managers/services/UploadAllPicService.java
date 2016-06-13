package cn.timeface.circle.baby.managers.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ITransaction;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import cn.timeface.circle.baby.App;
import cn.timeface.circle.baby.api.models.db.AppDatabase;
import cn.timeface.circle.baby.api.models.db.PhotoModel;
import cn.timeface.circle.baby.oss.OSSManager;
import cn.timeface.common.utils.NetworkUtil;
import rx.Observable;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by zhsheng on 2016/4/19.
 */
public class UploadAllPicService extends Service {

    private static final String TAG = "UploadAllPicService";
    private CompositeSubscription subscription;
    private LinkedBlockingQueue<PhotoModel> photoModelQueue = new LinkedBlockingQueue<>();
    ;
    /**
     * 关闭当前的全部上传的任务的开关
     */
    private boolean onOff = true;
    private boolean hasInvokeUrgent;
    private boolean removeUrgent;
    private boolean doingUploadAll;

    public static void start(Context context) {
        context.startService(new Intent(context, UploadAllPicService.class));
    }

    /**
     * 有急着需要上传的照片
     *
     * @param context
     */
    public static void addUrgent(Context context, @NonNull PhotoModel photoModel) {
        List<PhotoModel> photoModels = new ArrayList<>(1);
        photoModels.add(photoModel);
        addUrgent(context, (ArrayList<PhotoModel>) photoModels);
    }

    public static void addUrgent(Context context, ArrayList<PhotoModel> photoModel) {
        Intent intent = new Intent(context, UploadAllPicService.class);
        intent.putParcelableArrayListExtra("upload_photo_models", photoModel);
        context.startService(intent);
    }

    /**
     * 从紧急上传队列里移除
     *
     * @param context
     */
    public static void removeUrgent(Context context, @NonNull PhotoModel photoModel) {
        List<PhotoModel> photoModels = new ArrayList<>(1);
        photoModels.add(photoModel);
        removeUrgent(context, (ArrayList<PhotoModel>) photoModels);
    }

    public static void removeUrgent(Context context, @NonNull ArrayList<PhotoModel> photoModels) {
        Intent intent = new Intent(context, UploadAllPicService.class);
        intent.putParcelableArrayListExtra("upload_photo_models", photoModels);
        intent.putExtra("remove_urgent", true);
        context.startService(intent);
    }

    public static void stop(Context context) {
        context.stopService(new Intent(context, UploadAllPicService.class));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        hasInvokeUrgent = false;
        subscription = new CompositeSubscription();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) return START_STICKY;
        if (hasInvokeUrgent && doingUploadAll) return START_STICKY;//已经在Working
        ArrayList<PhotoModel> photoModels = intent.getParcelableArrayListExtra("upload_photo_models");
        removeUrgent = intent.getBooleanExtra("remove_urgent", false);
        if (removeUrgent) {
            if (!hasInvokeUrgent) {
                removeUrgent = false;
                return super.onStartCommand(intent, flags, startId);//没有唤起上传队列
            }
            for (PhotoModel pm : photoModels) {
                if (photoModelQueue.contains(pm)) {
                    photoModelQueue.remove(pm);
                }
            }
            removeUrgent = false;
        } else {
            if (photoModels != null && photoModels.size() > 0) {
                onOff = false;//关闭当前的全部上传
                //加入队列
                photoModelQueue.addAll(photoModels);
                if (!hasInvokeUrgent) {
                    invokeUrgentUpload();
                }
            } else {
                if (!doingUploadAll) onHandleIntent();//上传所有的图片
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 唤起自动上传队列
     */
    private void invokeUrgentUpload() {
        hasInvokeUrgent = true;
        Subscription subscribe = Observable.interval(1500, TimeUnit.MILLISECONDS)//1.5s取一次
                .filter(aLong -> !removeUrgent)//有需要移除的PhotoModel，停止取
                .map(aLong -> photoModelQueue.poll())
                .filter(photoModel -> {
                    if (photoModel == null) {
                        stopSelf();
                    }
                    return photoModel != null;
                })
                .flatMap(
                        photoModel -> {
                            return OSSManager.getOSSManager(App.getInstance()).uploadPicToBabys(photoModel.getLocalPath());
                        }, (photoModel, objectKey) -> {
                            photoModel.setObjectKey(objectKey);
                            return photoModel;
                        })
                .toList()
                .doOnTerminate(() -> hasInvokeUrgent = false)
                .subscribe(photoModels -> {
                    if (photoModels != null && photoModels.size() != 0) {
                        FlowManager.getDatabase(AppDatabase.class).executeTransaction(new ITransaction() {
                            @Override
                            public void execute(DatabaseWrapper databaseWrapper) {
                                // something here
                                for (PhotoModel pm : photoModels) {
                                    pm.save(databaseWrapper);
                                }
                            }
                        });
                    }
                }, throwable -> {
                });
        subscription.add(subscribe);
    }


    /**
     * 查询所有图片进行上传
     */
    protected void onHandleIntent() {
        if (!NetworkUtil.isWifiConnected(this)) {
            stopSelf();
            return;
        }
        List<PhotoModel> photoModels = new Select().from(PhotoModel.class).queryList();
        Log.d(TAG, "onHandleIntent: " + photoModels);
        Subscription subscribe = Observable.from(photoModels)
                .doOnSubscribe(() -> doingUploadAll = true)
                .filter(photoModel -> onOff)
                .flatMap(
                        photoModel -> {
                            return OSSManager.getOSSManager(App.getInstance()).uploadPicToBabys(photoModel.getLocalPath());
                        }, (photoModel, objectKey) -> {
                            photoModel.setObjectKey(objectKey);
                            return photoModel;
                        })
                .toList()
                .doOnTerminate(this::stopSelf)
                .subscribe(photoModels1 -> {
                    Log.d(TAG, "onHandleIntent uploaded: " + photoModels1);
                    if (photoModels1 != null && photoModels1.size() != 0) {
                        FlowManager.getDatabase(AppDatabase.class).executeTransaction(new ITransaction() {
                            @Override
                            public void execute(DatabaseWrapper databaseWrapper) {
                                // something here
                                for (PhotoModel pm : photoModels1) {
                                    pm.save(databaseWrapper);
                                }
                            }
                        });
                    }
                }, throwable -> {
                    Log.d(TAG, "throwable: " + throwable.getMessage());
                });
        subscription.add(subscribe);
    }

    @Override
    public void onDestroy() {
        doingUploadAll = false;
        hasInvokeUrgent = false;
        if (subscription != null) {
            subscription.unsubscribe();
        }
        photoModelQueue.clear();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
