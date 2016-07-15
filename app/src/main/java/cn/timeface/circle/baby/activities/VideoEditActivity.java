package cn.timeface.circle.baby.activities;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.events.ClipVideoSuccessEvent;
import cn.timeface.circle.baby.utils.ClipUtil;
import cn.timeface.circle.baby.utils.Remember;
import cn.timeface.circle.baby.utils.ToastUtil;
import cn.timeface.circle.baby.views.RangeSeekBar;

/**
 * Created by lidonglin on 2016/5/10.
 */
public class VideoEditActivity extends BaseAppCompatActivity implements View.OnClickListener {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.videoview)
    VideoView videoview;
    @Bind(R.id.ll_image)
    LinearLayout llImage;
    @Bind(R.id.hs)
    HorizontalScrollView hs;
    @Bind(R.id.tv_long)
    TextView tvLong;
    @Bind(R.id.rl_clip)
    RelativeLayout rlClip;
    @Bind(R.id.tv_next)
    TextView tvNext;
    @Bind(R.id.tv_tag)
    TextView tvTag;
    private int max;
    private int min = 0;
    private String path;
    private int seconds;
    private int j;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_edit);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        path = getIntent().getStringExtra("path");
        MediaController mc = new MediaController(this);
        videoview.setMediaController(mc);
        videoview.setVideoPath(path);
        videoview.start();
        videoview.requestFocus();

        List<Bitmap> bitmaps = getBitmaps();

        RangeSeekBar<Integer> seekBar = new RangeSeekBar<Integer>(0, seconds, this);
        max = seconds;
        tvLong.setText(seconds + "秒");
        seekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
                max = maxValue;
                min = minValue;
                int i = maxValue - minValue;
                tvLong.setText(i + "秒");
            }
        });
        rlClip.addView(seekBar);
        tvNext.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_next:
                int i = max - min;
                if(i>60){
                    ToastUtil.showToast("视频不能超过60秒");
                    return;
                }
                tvTag.setText("剪裁视频中…");
                try {
                    String s = ClipUtil.clipVideo(path, min, max);
                    EventBus.getDefault().post(new ClipVideoSuccessEvent(s,i));
                    finish();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }


    public List<Bitmap> getBitmaps() {
        ArrayList<Bitmap> bitmaps = new ArrayList<>();
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(path);
        String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        seconds = Integer.valueOf(duration) / 1000;
        if(seconds<15){
            j = 1;
        }else if(j<300){
            j = 5 ;
        }else{
            j=10;
        }
        for (int i = 1; i < seconds; i= i+j) {
            Bitmap bitmap = retriever.getFrameAtTime(i * 1000 * 1000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
            bitmaps.add(bitmap);
            ImageView imageView = initImageView(bitmap);
            llImage.addView(imageView);
        }
        return bitmaps;
    }

    public ImageView initImageView(Bitmap bitmap){
        int w = Remember.getInt("width", 0)/5;
        int h = (int) (w * 1.5);
        ImageView iv = new ImageView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(w,h);
        iv.setLayoutParams(params);
        iv.setImageBitmap(bitmap);
        return iv;
    }

}
