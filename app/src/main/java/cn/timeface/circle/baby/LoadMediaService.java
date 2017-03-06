package cn.timeface.circle.baby;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cn.timeface.circle.baby.events.MediaLoadComplete;
import cn.timeface.circle.baby.support.api.models.VideoInfo;
import cn.timeface.circle.baby.support.api.models.VideoInfo_Table;
import cn.timeface.circle.baby.support.utils.ImageFactory;
import cn.timeface.circle.baby.ui.timelines.Utils.JSONUtils;
import cn.timeface.circle.baby.ui.timelines.Utils.LogUtil;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * author : wangshuai Created on 2017/2/6
 * email : wangs1992321@gmail.com
 */
public class LoadMediaService extends IntentService {
    private static final String TAG = "LoadMediaService";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public LoadMediaService() {
        super("LoadMediaService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        loadVideo();
        EventBus.getDefault().post(new MediaLoadComplete(1));
    }

    private void loadVideo() {
        ArrayList<VideoInfo> list = new ArrayList<>();
        String progress[] = {

                MediaStore.Video.Media.DISPLAY_NAME,//视频的名字
                MediaStore.Video.Media.SIZE,//大小
                MediaStore.Video.Media.DURATION,//长度
                MediaStore.Video.Media.DATA,//播放地址
                MediaStore.Video.Media.DATE_ADDED,//时间
                MediaStore.Video.Media.DATE_MODIFIED,
                MediaStore.Video.Media.DATE_TAKEN
        };
        //获取数据提供者,this是上下文
        ContentResolver cr = this.getContentResolver();

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            LogUtil.showLog(TAG, "有sd卡的情况");
            //有sd卡的情况
            Cursor cursor = cr.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, progress, null, null, null);
            while (cursor.moveToNext()) {
                // 到视频文件的信息
                String name = cursor.getString(0);//得到视频的名字
                long size = cursor.getLong(1);//得到视频的大小
                long durantion = cursor.getLong(2);//得到视频的时间长度
                String data = cursor.getString(3);//得到视频的路径，可以转化为uri进行视频播放
                long date = cursor.getLong(4);
                long modified_date = cursor.getLong(5);
                long taken_date = cursor.getLong(6);
                File file = new File(data);
                date = file.lastModified();
                //使用静态方法获取视频的缩略图
                VideoInfo videoInfo = new VideoInfo();
                //创建视频信息对象
                videoInfo.setVedioName(name);
                videoInfo.setPath(data);
                videoInfo.setDuration(durantion);
                videoInfo.setSize(size);
                Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(data, MediaStore.Video.Thumbnails.MINI_KIND);
                String thumbPath = null;
                if (thumbnail != null)
                    thumbPath = ImageFactory.saveImageCache(thumbnail);
                videoInfo.setThumbmailLocalUrl(thumbPath);
                videoInfo.setDate(date);
                videoInfo.setModifiedDate(modified_date);
                videoInfo.setTakenDate(taken_date);
                list.add(videoInfo);
            }
            cursor.close();
        }
        LogUtil.showLog(TAG, "不论是否有sd卡都要查询手机内存");
        //不论是否有sd卡都要查询手机内存
        Cursor cursor = cr.query(MediaStore.Video.Media.INTERNAL_CONTENT_URI, progress, null, null, null);
        while (cursor.moveToNext()) {
            // 到视频文件的信息
            String name = cursor.getString(0);//得到视频的名字
            long size = cursor.getLong(1);//得到视频的大小
            long durantion = cursor.getLong(2);//得到视频的时间长度
            String data = cursor.getString(3);//得到视频的路径，可以转化为uri进行视频播放
            long date = cursor.getLong(4);
            long modified_date = cursor.getLong(5);
            long taken_date = cursor.getLong(6);


            File file = new File(data);
            date = file.lastModified();
            //使用静态方法获取视频的缩略图
            VideoInfo videoInfo = new VideoInfo();
            //创建视频信息对象
            videoInfo.setPath(data);
            videoInfo.setVedioName(name);
            videoInfo.setDuration(durantion);
            videoInfo.setSize(size);
            Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(data, MediaStore.Video.Thumbnails.MINI_KIND);
            String thumbPath = null;
            if (thumbnail != null)
                thumbPath = ImageFactory.saveImageCache(thumbnail);
            videoInfo.setThumbnail(thumbnail);
            videoInfo.setThumbmailLocalUrl(thumbPath);
            videoInfo.setDate(date);
            videoInfo.setModifiedDate(modified_date);
            videoInfo.setTakenDate(taken_date);
            list.add(videoInfo);
        }
        cursor.close();
        Observable.defer(() -> Observable.from(list))
                .map(videoInfo -> {
                    VideoInfo info = SQLite.select().from(VideoInfo.class).where(VideoInfo_Table.path.eq(videoInfo.getPath())).querySingle();
                    LogUtil.showLog(TAG, "video info json===" + JSONUtils.parse2JSONString(info));
                    if (info != null) {
                        if (!TextUtils.isEmpty(info.getThumbmailLocalUrl()) && !info.getThumbmailLocalUrl().equals(videoInfo.getThumbmailLocalUrl())) {
                            File file = new File(info.getThumbmailLocalUrl());
                            if (file.exists()) file.delete();
                        }
                        info.setDate(videoInfo.getDate());
                        info.setType(0);
                        info.setThumbmailLocalUrl(videoInfo.getThumbmailLocalUrl());
                        info.setSize(videoInfo.getSize());
                        info.setDuration(videoInfo.getDuration());
                        return info;
                    } else return videoInfo;
                }).filter(videoInfo -> videoInfo != null).subscribe(videoInfo -> videoInfo.save());
    }
}
