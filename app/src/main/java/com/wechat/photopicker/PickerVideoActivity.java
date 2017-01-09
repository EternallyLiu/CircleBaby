package com.wechat.photopicker;


import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;

import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;

import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.VideoEditActivity;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.events.ClipVideoSuccessEvent;
import cn.timeface.circle.baby.support.managers.listeners.IEventBus;
import cn.timeface.circle.baby.support.api.models.VideoInfo;
import cn.timeface.circle.baby.support.api.models.objs.MyUploadFileObj;
import cn.timeface.circle.baby.support.oss.OSSManager;
import cn.timeface.circle.baby.support.oss.uploadservice.UploadFileObj;
import cn.timeface.circle.baby.support.utils.DateUtil;
import cn.timeface.circle.baby.support.utils.ImageFactory;
import cn.timeface.circle.baby.views.dialog.TFProgressDialog;

/**
 * 选择视频界面
 */
public class PickerVideoActivity extends BaseAppCompatActivity implements IEventBus, MediaScannerConnection.OnScanCompletedListener {

    public final static String EXTRA_SHOW_CAMERA = "SHOW_CAMERA";
    public final static String EXTRA_SHOW_GIF = "SHOW_GIF";
    public final static String KEY_SELECTED_PHOTOS = "SELECTED_PHOTOS";
    public final static String KEY_OPTIONAL_PICTURE_SIZE = "OPTIONAL_PICTURE_SIZE";
    private static final int RECORD_CAMERA_REQUEST_CODE = 50;
    private static final int CAMERA_REQUEST_CODE = 51;
    public static final String TAG = "PickerPhotoActivity";
    //可选图片大小
    private int optionalPhotoSize;
    private GridView gv;
    private ArrayList<VideoInfo> videos = new ArrayList<>();
    private File mVideoFile;
    private VideoInfo videoInfo;
    private VideoAdapter adapter;
    private TFProgressDialog tfProgressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        checkPermission();
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        gv = (GridView) findViewById(R.id.gv_video);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle(R.string.picker_video);
        actionBar.setDisplayHomeAsUpEnabled(true);
        tfProgressDialog = TFProgressDialog.getInstance("");
//        if (savedInstanceState != null) {
//            optionalPhotoSize = (int) savedInstanceState.get(KEY_OPTIONAL_PICTURE_SIZE);
//        }
        initData();
//        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, OrientationHelper.VERTICAL);
//        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
//        mRecyclerView.setLayoutManager(layoutManager);
//        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new VideoAdapter(videos);
        gv.setAdapter(adapter);
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                position = position - 1;
                if (position == -1) {
                    if (ContextCompat.checkSelfPermission(PickerVideoActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(PickerVideoActivity.this, new String[]{Manifest.permission.CAMERA},
                                CAMERA_REQUEST_CODE);
                    }else{
                        //跳转到录像
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
//                    takePictureIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 5);
//                    mVideoFile = StorageUtil.genSystemVideoFile();
//                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mVideoFile));
                        startActivityForResult(takePictureIntent, 1);
                    }
                } else {
                    //跳转到裁剪视频界面
                    videoInfo = videos.get(position);
                    String s = ImageFactory.saveImage(videoInfo.getThumbnail());
                    videoInfo.setImgLocalUrl(s);
                    Intent intent = new Intent(PickerVideoActivity.this, VideoEditActivity.class);
                    intent.putExtra("path", videoInfo.getPath());
                    intent.putExtra("duration", videoInfo.getDuration());
                    startActivity(intent);
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        initData();
        adapter.notifyDataSetChanged();
    }

    //获取手机中的视频
    private void initData() {
        videos.clear();
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
                Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(data, MediaStore.Video.Thumbnails.MINI_KIND);
                VideoInfo videoInfo = new VideoInfo();
//创建视频信息对象
                videoInfo.setVedioName(name);
                videoInfo.setPath(data);
                videoInfo.setDuration(durantion);
                videoInfo.setSize(size);
                videoInfo.setThumbnail(thumbnail);
                videoInfo.setDate(date);

                videos.add(videoInfo);
            }
            cursor.close();
        }
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
            VideoInfo videoInfo = new VideoInfo();
//创建视频信息对象
            videoInfo.setPath(data);
            videoInfo.setVedioName(name);
            videoInfo.setDuration(durantion);
            videoInfo.setSize(size);
            videoInfo.setThumbnail(thumbnail);
            videoInfo.setDate(date);

            videos.add(videoInfo);
        }
        cursor.close();
    }

    private void checkPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA},
                        RECORD_CAMERA_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        doNext(requestCode, grantResults);
    }

    private void doNext(int requestCode, int[] grantResults) {
        if (requestCode == RECORD_CAMERA_REQUEST_CODE) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "您拒绝了选择视频的权限", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onScanCompleted(String path, Uri uri) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                initData();
                adapter = new VideoAdapter(videos);
                gv.setAdapter(adapter);
            }
        });
    }

    class VideoAdapter extends BaseAdapter {

        ArrayList<VideoInfo> videos;
        private ImageView ivPhoto;
        private TextView tvLong;
        private File mPhotoFile;

        public VideoAdapter(ArrayList<VideoInfo> videos) {
            this.videos = videos;
        }

        @Override
        public int getCount() {
            return videos.size() + 1;
        }

        @Override
        public Object getItem(int position) {
            return videos.get(position - 1);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(PickerVideoActivity.this, R.layout.item_video, null);
            ivPhoto = (ImageView) view.findViewById(R.id.iv_photo);
            tvLong = (TextView) view.findViewById(R.id.tv_long);
            if (position == 0) {
                ivPhoto.setImageResource(R.drawable.camera);
                ivPhoto.setScaleType(ImageView.ScaleType.CENTER);
            } else {
                ivPhoto.setImageBitmap(videos.get(position - 1).getThumbnail());
                tvLong.setText(DateUtil.getTime4(videos.get(position - 1).getDuration()));
            }


            return view;
        }
    }


    @Subscribe
    public void onEvent(ClipVideoSuccessEvent event) {
        tfProgressDialog.setTvMessage("剪裁视频中，请稍候…");
        tfProgressDialog.show(getSupportFragmentManager(), "");
        String clipVideoPath = event.getClipVideoPath();
        int duration = event.getDuration();
        videoInfo.setPath(clipVideoPath);
        videoInfo.setDuration(duration);
//        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//        retriever.setDataSource(clipVideoPath);
//        Bitmap bitmap = retriever.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
//        String s = ImageFactory.saveImage(bitmap);
//        videoInfo.setImgLocalUrl(s);
        uploadImage(videoInfo.getImgLocalUrl());
    }

    private void uploadImage(String imgLocalUrl) {
        if (TextUtils.isEmpty(imgLocalUrl)) {
            return;
        }
        OSSManager ossManager = OSSManager.getOSSManager(this);
        new Thread() {
            @Override
            public void run() {
                try {
                    //获取上传文件
                    UploadFileObj uploadFileObj = new MyUploadFileObj(imgLocalUrl);
                    //上传操作
                    try {
                        //判断服务器是否已存在该文件
                        if (!ossManager.checkFileExist(uploadFileObj.getObjectKey())) {
                            //如果不存在则上传
                            ossManager.upload(uploadFileObj.getObjectKey(), uploadFileObj.getFinalUploadFile().getAbsolutePath());
                        }
                        String imgObjectKey = uploadFileObj.getObjectKey();
//                        "http://img1.timeface.cn/"
                        videoInfo.setImgObjectKey(imgObjectKey);
                        new File(imgLocalUrl).delete();

                        Intent intent = new Intent();
                        intent.putExtra("imgObjectKey", imgObjectKey);
                        intent.putExtra("path", videoInfo.getPath());
                        intent.putExtra("duration", videoInfo.getDuration());
                        intent.putExtra("date", videoInfo.getDate());
                        setResult(RESULT_OK, intent);
                        finish();
//                recorder.oneFileCompleted(uploadTaskInfo.getInfoId(), uploadFileObj.getObjectKey());
                    } catch (ServiceException | ClientException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {

                }
            }
        }.start();
    }


}
