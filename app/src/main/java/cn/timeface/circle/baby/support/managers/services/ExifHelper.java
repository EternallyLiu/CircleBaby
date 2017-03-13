package cn.timeface.circle.baby.support.managers.services;

import android.media.ExifInterface;

import java.io.IOException;

import cn.timeface.circle.baby.support.api.models.db.PhotoModel;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func0;

/**
 * author: rayboot  Created on 16/4/14.
 * email : sy0725work@gmail.com
 */
public class ExifHelper {

    public static Observable<PhotoModel> setExifObservable(PhotoModel photoModel) {
        return Observable.defer(new Func0<Observable<PhotoModel>>() {
            @Override
            public Observable<PhotoModel> call() {
                return Observable.create(new Observable.OnSubscribe<PhotoModel>() {
                    @Override
                    public void call(Subscriber<? super PhotoModel> subscriber) {
                        try {
                            ExifInterface exifInterface = new ExifInterface(photoModel.getLocalPath());
                            photoModel.setWidth(exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, photoModel.getWidth()));
                            photoModel.setHeight(exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, photoModel.getWidth()));
                            photoModel.setOrientation(exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL));
                            photoModel.setLatitude(exifInterface.getAttributeDouble(ExifInterface.TAG_GPS_LATITUDE, photoModel.getLatitude()));
                            photoModel.setLongitude(exifInterface.getAttributeDouble(ExifInterface.TAG_GPS_LONGITUDE, photoModel.getLongitude()));
                            photoModel.setFlash(exifInterface.getAttributeInt(ExifInterface.TAG_FLASH, photoModel.getFlash()));
                            photoModel.setMaker(exifInterface.getAttribute(ExifInterface.TAG_MAKE));
                            photoModel.setModel(exifInterface.getAttribute(ExifInterface.TAG_MODEL));
                            photoModel.setWhiteBalance(exifInterface.getAttributeInt(ExifInterface.TAG_WHITE_BALANCE, photoModel.getWhiteBalance()));
                            subscriber.onNext(photoModel);
                        } catch (IOException e) {
                            e.printStackTrace();
                            subscriber.onError(e);
                        }
                        subscriber.onCompleted();
                    }
                });
            }
        });
    }
}
