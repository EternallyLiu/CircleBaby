package cn.timeface.circle.baby.support.managers.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;
import com.raizlabs.android.dbflow.structure.database.transaction.ITransaction;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.timeface.circle.baby.App;
import cn.timeface.circle.baby.events.PicSaveCompleteEvent;
import cn.timeface.circle.baby.support.api.models.db.AppDatabase;
import cn.timeface.circle.baby.support.api.models.db.PhotoModel;
import cn.timeface.circle.baby.support.utils.rxutils.ExecutorManager;
import cn.timeface.common.utils.DateUtil;
import cn.timeface.common.utils.MD5;
import rx.Observable;
import rx.Subscription;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by zhsheng on 2016/4/14.
 */
public class SavePicInfoService extends Service {

    private static final String TAG = "==SavePicInfoService==";
    private CompositeSubscription subscription;
    public static boolean saveComplete = false;

    public static void open(Context context) {
        context.startService(new Intent(context, SavePicInfoService.class));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        subscription = new CompositeSubscription();
        onHandleIntent();
    }

    protected void onHandleIntent() {
        Cursor cursor = MediaStoreCursorHelper.openPhotosCursor(this, 0);
        long startMillis = System.currentTimeMillis();

        Subscription subscribe = MediaStoreCursorHelper.getAllPhotoFrom(cursor)
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(cameraPhotoModels -> {
                    List<PhotoModel> dbPhotoModels = SQLite.select().from(PhotoModel.class).queryList();
                    //camera里有，db里没有的，添加到db里；camera里没有，db里有的，从db里删除；
                    if (dbPhotoModels.size() == 0) {
                        return cameraPhotoModels;
                    }
                    Map<PhotoModel, Integer> map = new HashMap<>(cameraPhotoModels.size());
                    for (PhotoModel model : cameraPhotoModels) {
                        map.put(model, 1);
                    }
                    for (PhotoModel model : dbPhotoModels) {
                        if (map.get(model) != null) {
                            map.put(model, 2);
                            continue;
                        }
                        model.delete();//camera里没有，db里有的，从db里删除；
                    }
                    ArrayList<PhotoModel> needDealPhotoModels = new ArrayList<>();
                    for (Map.Entry<PhotoModel, Integer> entry : map.entrySet()) {
                        if (entry.getValue() == 1) {
                            needDealPhotoModels.add(entry.getKey());//camera里有，db里没有的，添加到db里；
                        }
                    }
                    return needDealPhotoModels;
                })
                .concatMap(new Func1<List<PhotoModel>, Observable<PhotoModel>>() {
                    @Override
                    public Observable<PhotoModel> call(List<PhotoModel> photoModels) {
                        return Observable.from(photoModels);
                    }
                })
                .window(100)
                .flatMap(new Func1<Observable<PhotoModel>, Observable<List<PhotoModel>>>() {
                    @Override
                    public Observable<List<PhotoModel>> call(Observable<PhotoModel> photoModelObservable) {
                        return photoModelObservable.compose(modelListTransformer);
                    }
                })
                .doOnTerminate(() -> {
                    cursor.close();
                    stopSelf();
                    saveComplete = true;
                    EventBus.getDefault().post(new PicSaveCompleteEvent());
                    Log.d(TAG, "onHandleIntent: start==" + (System.currentTimeMillis() - startMillis));
                })
                .filter(photoModels -> photoModels.size() != 0)
                .subscribe(photoModels -> {
                    Log.d(TAG, "本次存储的图片个数call: " + photoModels.size());
                    FlowManager.getDatabase(AppDatabase.class).executeTransaction(new ITransaction() {
                        @Override
                        public void execute(DatabaseWrapper databaseWrapper) {
                            for (PhotoModel pm : photoModels) {
                                pm.save(databaseWrapper);
                            }
                        }
                    });
                }, throwable -> {
                    Log.d(TAG, "queryAndSaveCurrentPics: " + throwable.getMessage());
                }, () -> {
                    // UploadAllPicService.start(App.getInstance());
                });//保存完成后上传
        subscription.add(subscribe);
    }

    private Observable.Transformer<PhotoModel, List<PhotoModel>> modelListTransformer = new Observable.Transformer<PhotoModel, List<PhotoModel>>() {
        @Override
        public Observable<List<PhotoModel>> call(Observable<PhotoModel> photoModelObservable) {
            return photoModelObservable
                    .observeOn(Schedulers.from(ExecutorManager.eventExecutor))
                    .map(photoModel -> {
                        photoModel.setMd5(MD5.encode(new File(photoModel.getLocalPath())));
                        return photoModel;
                    })
                    .map(photoModel -> {
                        //获取系统的缩略图
                        String path = ThumbHelper.queryImgThumb(App.getInstance(), photoModel.getPhotoId());
                        if (!TextUtils.isEmpty(path)) {
                            photoModel.setThumbPath(path);
                        }
                        return photoModel;
                    })
                    //解析照片中的经纬度和宽高
                    .concatMap(ExifHelper::setExifObservable)
                    .map(photoModel -> {
                        if (photoModel.getWidth() == 0 || photoModel.getHeight() == 0) {
                            return getImgSize(photoModel);
                        }
                        return photoModel;
                    })
                    // .map(photoModel -> AlbumUtil.compressPhotoModelLocalPathTemp(photoModel, 16384))
                    .map(photoModel -> {
                        photoModel.setStringDate(DateUtil.formatDate("yyyy.MM.dd", photoModel.getDateTaken()));
                        return photoModel;
                    })
                    .toList();
        }
    };

    public static PhotoModel getImgSize(PhotoModel photoModel) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoModel.getLocalPath(), options); // 此时返回的bitmap为null
        photoModel.setWidth(options.outWidth);
        photoModel.setHeight(options.outHeight);
        return photoModel;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (subscription != null) subscription.unsubscribe();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
