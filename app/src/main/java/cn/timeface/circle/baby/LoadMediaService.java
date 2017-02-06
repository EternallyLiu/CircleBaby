package cn.timeface.circle.baby;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.provider.MediaStore;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import cn.timeface.circle.baby.events.MediaLoadComplete;
import cn.timeface.circle.baby.support.api.models.VideoInfo;
import cn.timeface.circle.baby.support.utils.ImageFactory;
import cn.timeface.circle.baby.ui.timelines.Utils.LogUtil;

/**
 * author : wangshuai Created on 2017/2/6
 * email : wangs1992321@gmail.com
 */
public class LoadMediaService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public LoadMediaService() {
        super("LoadMediaService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        loadVideo();
        EventBus.getDefault().post(new MediaLoadComplete(1));
    }

    private void loadVideo(){
        LogUtil.showLog("getData====>" + Thread.currentThread().getName());
        ArrayList<VideoInfo> list = new ArrayList<>();
        String progress[] = {

                MediaStore.Video.Media.DISPLAY_NAME,//视频的名字
                MediaStore.Video.Media.SIZE,//大小
                MediaStore.Video.Media.DURATION,//长度
                MediaStore.Video.Media.DATA,//播放地址
                MediaStore.Video.Media.DATE_ADDED//时间
        };
        //获取数据提供者,this是上下文
        ContentResolver cr = this.getContentResolver();

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            LogUtil.showLog("有sd卡的情况");
            //有sd卡的情况
            Cursor cursor = cr.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, progress, null, null, null);
            while (cursor.moveToNext()) {
                // 到视频文件的信息
                String name = cursor.getString(0);//得到视频的名字
                long size = cursor.getLong(1);//得到视频的大小
                long durantion = cursor.getLong(2);//得到视频的时间长度
                String data = cursor.getString(3);//得到视频的路径，可以转化为uri进行视频播放
                long date = cursor.getLong(4);
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
                LogUtil.showLog("thumbPath=====>" + thumbPath);
                videoInfo.setThumbnail(thumbnail);
                videoInfo.setThumbmailLocalUrl(thumbPath);
                videoInfo.setDate(date);
                videoInfo.save();
                list.add(videoInfo);
            }
            cursor.close();
        }
        LogUtil.showLog("不论是否有sd卡都要查询手机内存");
        //不论是否有sd卡都要查询手机内存
        Cursor cursor = cr.query(MediaStore.Video.Media.INTERNAL_CONTENT_URI, progress, null, null, null);
        while (cursor.moveToNext()) {
            // 到视频文件的信息
            String name = cursor.getString(0);//得到视频的名字
            long size = cursor.getLong(1);//得到视频的大小
            long durantion = cursor.getLong(2);//得到视频的时间长度
            String data = cursor.getString(3);//得到视频的路径，可以转化为uri进行视频播放
            long date = cursor.getLong(4);
            //使用静态方法获取视频的缩略图
            Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(data, MediaStore.Video.Thumbnails.MINI_KIND);
            String thumbPath = ImageFactory.saveImageCache(thumbnail);
            LogUtil.showLog("thumbPath===" + thumbPath);
            VideoInfo videoInfo = new VideoInfo();
            //创建视频信息对象
            videoInfo.setPath(data);
            videoInfo.setVedioName(name);
            videoInfo.setDuration(durantion);
            videoInfo.setSize(size);
            videoInfo.setThumbnail(thumbnail);
            videoInfo.setThumbmailLocalUrl(thumbPath);
            videoInfo.setDate(date);
            videoInfo.save();
            list.add(videoInfo);
        }
        cursor.close();
        VideoInfo.saveAll(list);
    }
}
