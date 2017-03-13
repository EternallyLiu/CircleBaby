package cn.timeface.circle.baby.support.managers.services;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

/**
 * author: rayboot  Created on 16/5/3.
 * email : sy0725work@gmail.com
 */
public class ThumbHelper {
    public static String getImgThumbnailPath(Context context, String imgId) {
        ContentResolver cr = context.getContentResolver();
        String[] projection = {
                MediaStore.Images.Thumbnails.DATA,
        };
        String path = null;
        Cursor cursor = null;
        try {
            cursor = cr.query(
                    MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
                    projection,
                    MediaStore.Images.Thumbnails.IMAGE_ID + "=?",
                    new String[]{imgId},
                    null);
            if (cursor != null) {
                cursor.moveToFirst();
                path = cursor.getString(0);
                cursor.close();
            }
            return path;
        } catch (Exception e) {

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return null;
    }

    public static String queryImgThumb(Context context, String imgId) {
        String path = null;
        ContentResolver cr = context.getContentResolver();
        String[] projection = {
                MediaStore.Images.Thumbnails.DATA,
        };
        Cursor cursor = null;
        try {
            cursor = MediaStore.Images.Thumbnails.queryMiniThumbnail(cr, Long.parseLong(imgId), MediaStore.Images.Thumbnails.MINI_KIND, projection);
            if (cursor != null) {
                boolean first = cursor.moveToFirst();
                if (first) {
                    path = cursor.getString(0);
                }
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return path;
    }
}
