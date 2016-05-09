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
package cn.timeface.circle.baby.utils.mediastore;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Images.ImageColumns;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import cn.timeface.circle.baby.api.models.objs.ImgObj;
import cn.timeface.common.utils.DateUtil;


public class MediaStoreCursorHelper {

    public static final String[] PHOTOS_PROJECTION = {Images.Media._ID,
            Images.Media.MINI_THUMB_MAGIC,
            Images.Media.DATA, Images.Media.BUCKET_DISPLAY_NAME, Images.Media.BUCKET_ID,
            Images.Media.DATE_TAKEN};
    public static final String PHOTOS_ORDER_BY = Images.Media.DATE_TAKEN + " DESC";

    public static final Uri MEDIA_STORE_CONTENT_URI = Images.Media.EXTERNAL_CONTENT_URI;

    public static ArrayList<ImgObj> photosCursorToSelectionList(Uri contentUri,
                                                                      Cursor cursor) {
        ArrayList<ImgObj> items = new ArrayList<ImgObj>(cursor.getCount());
        ImgObj item;

        if (cursor.moveToFirst()) {
            do {
                try {
                    item = photosCursorToSelection(contentUri, cursor);
                    if (null != item) {
                        items.add(item);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
        }

        // Need to reset the List so that oldest is first
        Collections.reverse(items);

        return items;
    }

    public static TreeMap<String, List<ImgObj>> photoCursorSelectionMap(Uri contentUri, Cursor cursor){
        final int dateTakenColumn = cursor.getColumnIndex(ImageColumns.DATE_TAKEN);
        TreeMap<String, List<ImgObj>> takenDateMap = new TreeMap<String, List<ImgObj>>(
                new Comparator<String>() {
                    @Override
                    public int compare(String lhs, String rhs) {
                        return rhs.compareTo(lhs);
                    }
                }
        );
        ImgObj item;

        if (cursor.moveToFirst()) {
            do {
                try {
                    String takenDate = DateUtil.formatDate("yyyy.MM.dd", cursor.getLong(dateTakenColumn));
                    item = photosCursorToSelection(contentUri, cursor);

                    if(item != null){
                        if (takenDateMap.containsKey(takenDate)) {
                            takenDateMap.get(takenDate).add(item);
                        } else {
                            List<ImgObj> items = new ArrayList<>();
                            items.add(item);
                            takenDateMap.put(takenDate, items);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
        }
        return takenDateMap;
    }

    public static ImgObj photosCursorToSelection(Uri contentUri, Cursor cursor) {
        ImgObj item = null;

        try {
            File file = new File(cursor.getString(cursor.getColumnIndexOrThrow(ImageColumns.DATA)));
            if (file.exists()) {
                item = ImgObj.getSelection(contentUri,
                        cursor.getInt(cursor.getColumnIndexOrThrow(ImageColumns._ID)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return item;
    }

    public static void photosCursorToBucketList(Cursor cursor, ArrayList<MediaStoreBucket> items) {
        final HashSet<String> bucketIds = new HashSet<String>();
        final Map<String, Integer> bucketItemCount = new HashMap<String, Integer>(10);

        final int idColumn = cursor.getColumnIndex(ImageColumns.BUCKET_ID);
        final int nameColumn = cursor.getColumnIndex(ImageColumns.BUCKET_DISPLAY_NAME);
        if (cursor.moveToFirst()) {
            do {
                try {
                    final String path = cursor.getString(cursor.getColumnIndexOrThrow(ImageColumns.DATA));
                    final String bucketId = cursor.getString(idColumn);
                    if (bucketIds.add(bucketId)) {
                        items.add(new MediaStoreBucket(bucketId, cursor.getString(nameColumn), Uri.fromFile(new File(path))));
                        bucketItemCount.put(bucketId, 0);
                    }
                    bucketItemCount.put(bucketId, bucketItemCount.get(bucketId) + 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
        }

        for (MediaStoreBucket bucket : items) {
            if (bucketItemCount.get(bucket.getId()) != null) {
                bucket.setTotalCount(bucketItemCount.get(bucket.getId()));
            }
        }
    }

    public static Cursor openPhotosCursor(Context context, Uri contentUri) {
        return context.getContentResolver()
                .query(contentUri, PHOTOS_PROJECTION, null, null, PHOTOS_ORDER_BY);
    }

}
