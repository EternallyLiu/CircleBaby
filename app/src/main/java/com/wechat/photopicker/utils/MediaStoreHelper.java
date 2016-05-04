package com.wechat.photopicker.utils;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;

import com.wechat.photopicker.endity.PhotoDirectory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import cn.timeface.circle.baby.R;

import static android.provider.BaseColumns._ID;
import static android.provider.MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME;
import static android.provider.MediaStore.Images.ImageColumns.BUCKET_ID;
import static android.provider.MediaStore.MediaColumns.DATA;
import static android.provider.MediaStore.MediaColumns.DATE_ADDED;

/**
 * 类描述：获取手机图片帮助类
 */
public class MediaStoreHelper {

    public final static int INDEX_ALL_PHOTOS = 0;

    public static final void getPhotoDirs(FragmentActivity activity,Bundle args, DirectoryResultCallback resultCallback){
        activity.getSupportLoaderManager()
                .initLoader(0,args,new PhotoDirLoaderCallbacks(activity,resultCallback));
    }
    //有图片增加或者减少时，该类下除onLoaderReset()方法会执行一遍
    public static class PhotoDirLoaderCallbacks implements LoaderManager.LoaderCallbacks<Cursor>{

        private Context context;
        private DirectoryResultCallback resultCallback;

        public PhotoDirLoaderCallbacks(Context context,DirectoryResultCallback resultCallback){
            this.context = context;
            this.resultCallback = resultCallback;
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            //根据args判断是否加载GIF图
            return new PhotoDirectoryLoader(context,false);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

            if(data==null){
                return;
            }else{
                LinkedHashMap<String,PhotoDirectory> directories = new LinkedHashMap<>();
                List<PhotoDirectory> directoriesList = new ArrayList<>();
                PhotoDirectory allPhotoDirectory = new PhotoDirectory();
                allPhotoDirectory.setName("全部图片");
                allPhotoDirectory.setId("ALL");
                int i = 1;
                while (data.moveToNext()) {
                    if (i % 10 == 0) {
                        Log.w("while次数", "" + i);
                    }
                    //获取图片ID，图片创建时间，图片路径,图片文件夹ID，图片所属文件夹名称，
                    int imageId = data.getInt(data.getColumnIndexOrThrow(_ID));
                    long addDate = data.getLong(data.getColumnIndexOrThrow(DATE_ADDED));
                    String path = data.getString(data.getColumnIndexOrThrow(DATA));
                    String bucketId = data.getString(data.getColumnIndexOrThrow(BUCKET_ID));
                    String name = data.getString(data.getColumnIndexOrThrow(BUCKET_DISPLAY_NAME));
                    //判断图片所属文件夹是否已添加，是则添加图片，否则创建photoDirectory
                    if (!directories.containsKey(bucketId)) {
                        PhotoDirectory photoDirectory = new PhotoDirectory();
                        photoDirectory.setId(bucketId);
                        photoDirectory.setName(name);
                        photoDirectory.setCoverPath(path);
                        photoDirectory.addPhoto(imageId, path);
                        photoDirectory.setDateAdded(addDate);
                        directories.put(bucketId, photoDirectory);
                    } else {
                        directories.get(bucketId).addPhoto(imageId,path);
                        //directories.get(directories.indexOf(photoDirectory)).addPhoto(imageId, path);
                    }
                    allPhotoDirectory.addPhoto(imageId, path);
                }
                //全部图片添加封面
                List<String> photosPath = allPhotoDirectory.getPhotoPaths();
                if(photosPath.size()>0 ){
                    allPhotoDirectory.setCoverPath(photosPath.get(0));
                }
                directoriesList.addAll(directories.values());
                directoriesList.add(INDEX_ALL_PHOTOS,allPhotoDirectory);
                if (resultCallback!=null){
                    resultCallback.onResultCallback(directoriesList);
                }
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    }
    public interface DirectoryResultCallback {
        void onResultCallback(List<PhotoDirectory> directories);
    }
}
