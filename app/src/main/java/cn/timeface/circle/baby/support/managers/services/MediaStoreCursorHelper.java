/*
 * Copyright 2013 Chris Banes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.timeface.circle.baby.support.managers.services;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore.Images;

import cn.timeface.circle.baby.support.api.models.db.PhotoModel;
import rx.Observable;
import rx.Subscriber;


public class MediaStoreCursorHelper {

    public static final String[] PHOTOS_PROJECTION =
            {
                    Images.ImageColumns._ID,
                    Images.ImageColumns.DATA,
                    Images.ImageColumns.SIZE,
                    Images.ImageColumns.DISPLAY_NAME,
                    Images.ImageColumns.TITLE,
                    Images.ImageColumns.DATE_ADDED,
                    Images.ImageColumns.MIME_TYPE,
//                    Images.Media.WIDTH,
//                    Images.Media.HEIGHT,
                    Images.ImageColumns.DESCRIPTION,
                    Images.ImageColumns.LATITUDE,
                    Images.ImageColumns.LONGITUDE,
                    Images.ImageColumns.DATE_TAKEN,
                    Images.ImageColumns.ORIENTATION,
                    Images.ImageColumns.MINI_THUMB_MAGIC,
                    Images.ImageColumns.BUCKET_ID,
                    Images.ImageColumns.BUCKET_DISPLAY_NAME
            };
    public static final String PHOTOS_ORDER_BY = Images.Media.DATE_TAKEN + " DESC";

    public static Cursor openPhotosCursor(Context context, long timeStamp) {
        return Images.Media.query(context.getContentResolver(), Images.Media.EXTERNAL_CONTENT_URI, PHOTOS_PROJECTION, Images.Media.DATE_TAKEN + " > " + timeStamp, PHOTOS_ORDER_BY);
    }

    public static Observable<PhotoModel> getAllPhotoFrom(Cursor cursor) {
        return Observable.defer(() ->
                Observable.create(new Observable.OnSubscribe<PhotoModel>() {
                    @Override
                    public void call(Subscriber<? super PhotoModel> subscriber) {
                        if (cursor.moveToFirst()) {
                            do {
                                try {
                                    subscriber.onNext(new PhotoModel(cursor));
                                } catch (Exception e) {
                                    subscriber.onError(e);
                                }
                            } while (cursor.moveToNext());
                        }
                        subscriber.onCompleted();
                    }
                }));
    }
}
