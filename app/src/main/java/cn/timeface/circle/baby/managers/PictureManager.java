package cn.timeface.circle.baby.managers;

import java.util.ArrayList;

import cn.timeface.circle.baby.api.models.db.PhotoModel;
import rx.Observable;
import rx.functions.Func1;
import rx.subjects.PublishSubject;

/**
 * Created by zhsheng on 2016/6/13.
 */
public class PictureManager {
    private static volatile PictureManager dataManger = null;
    private ArrayList<PhotoModel> photoModels = new ArrayList<>();
    private final PublishSubject<PhotoModel> publishSubject;
    private final Observable<PhotoModel> modelObservable;

    private PictureManager() {
        publishSubject = PublishSubject.create();
        modelObservable = publishSubject.filter(new Func1<PhotoModel, Boolean>() {
            @Override
            public Boolean call(PhotoModel photoModel) {
                return !photoModels.contains(photoModel);
            }
        });
    }

    public static PictureManager getInstance() {
        PictureManager inst = dataManger;
        if (inst == null) {
            synchronized (PictureManager.class) {
                inst = dataManger;
                if (inst == null) {
                    inst = new PictureManager();
                    dataManger = inst;
                }
            }
        }
        return inst;
    }

    public void addNeedUploadPic(PhotoModel photoModel) {
        if (publishSubject != null) publishSubject.onNext(photoModel);
    }

    public void removeUrgent(PhotoModel photoModel) {
        photoModels.add(photoModel);
    }

    public Observable<PhotoModel> getModelObservable() {
        return modelObservable;
    }

}
