package cn.timeface.circle.baby.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.MediaController;
import android.widget.VideoView;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.support.api.models.VideoInfo;
import cn.timeface.circle.baby.support.utils.ImageFactory;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.support.utils.Utils;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.timelines.Utils.LogUtil;
import cn.timeface.circle.baby.views.dialog.TFProgressDialog;

public class VideoPlayActivity extends BaseAppCompatActivity {


    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.videoview)
    VideoView videoview;
    private MenuItem save;
    private String url;
    private TFProgressDialog tfProgressDialog;

    private String objectKey;

    public static void open(Context context, String url) {
        Intent intent = new Intent(context, VideoPlayActivity.class);
        intent.putExtra("url", url);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videoplay);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("视频播放");

        if (!Utils.isNetworkConnected(this)) {
            ToastUtil.showToast("网络异常");
        }

        if (Utils.isNetworkConnected(this)) {
            int networkType = Utils.getNetworkType(this);
            if (networkType != 1) {
                //非Wifi提示
                new AlertDialog.Builder(this).setTitle("当前为非Wifi网络环境，是否继续？")
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                return;
                            }
                        }).setPositiveButton("继续", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
            }
        } else {
            ToastUtil.showToast("请检查是否联网");
            return;
        }

        url = getIntent().getStringExtra("url");
        VideoInfo.findVideo(url).compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(videoInfo -> {
                    LogUtil.showLog("url==="+url);
                    LogUtil.showLog(videoInfo==null?"null":videoInfo.getPath());
                    if (videoInfo != null && !TextUtils.isEmpty(videoInfo.getPath())) {
                        if (TextUtils.isEmpty(videoInfo.getVideoUrl())) {
                            videoInfo.setVideoUrl(url);
                            videoInfo.save();
                        }
                        url = videoInfo.getPath();
                        init();
                    } else init();
                }, throwable -> {
                    LogUtil.showError(throwable);
                    init();
                });

    }

    private void init() {
        LogUtil.showLog("url:" + url);
        MediaController mc = new MediaController(this);
        videoview.setMediaController(mc);
        videoview.setVideoPath(url);
        videoview.requestFocus();
        videoview.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_fragment_bigimage, menu);
        save = menu.findItem(R.id.save);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.home) {
            onBackPressed();
        } else if (item.getItemId() == R.id.save) {
            save.setEnabled(false);
            saveVideo();
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveVideo() {
        String path = url;
        String fileName = path.substring(path.lastIndexOf("/"));
        String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        File file = new File(absolutePath + "/baby");
        if (!file.exists()) {
            file.mkdirs();
        }
        File file1 = new File(file, fileName);
        if (file1.exists()) {
            ToastUtil.showToast("已保存到baby文件夹下");
            save.setEnabled(true);
            return;
        }
        if (!Utils.isNetworkConnected(VideoPlayActivity.this)) {
            ToastUtil.showToast("网络异常");
            save.setEnabled(true);
            return;
        }
        ToastUtil.showToast("保存视频…");
        tfProgressDialog = TFProgressDialog.getInstance("");
        tfProgressDialog.setTvMessage("保存视频中…");
        tfProgressDialog.show(getSupportFragmentManager(), "");
        new Thread(new Runnable() {
            @Override
            public void run() {
                ImageFactory.saveVideo(path, file1);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tfProgressDialog.dismiss();
                        ToastUtil.showToast("已保存到baby文件夹下");
                        save.setEnabled(true);
                    }
                });
                return;
            }
        }).start();
    }
}
