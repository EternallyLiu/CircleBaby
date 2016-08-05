package cn.timeface.circle.baby.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.api.models.objs.ImgObj;
import cn.timeface.circle.baby.api.models.objs.MediaObj;
import cn.timeface.circle.baby.api.models.objs.Milestone;
import cn.timeface.circle.baby.api.models.objs.MyUploadFileObj;
import cn.timeface.circle.baby.api.models.objs.TimeLineObj;
import cn.timeface.circle.baby.events.TimelineEditEvent;
import cn.timeface.circle.baby.oss.OSSManager;
import cn.timeface.circle.baby.oss.uploadservice.UploadFileObj;
import cn.timeface.circle.baby.utils.DateUtil;
import cn.timeface.circle.baby.utils.GlideUtil;
import cn.timeface.circle.baby.utils.Remember;
import cn.timeface.circle.baby.utils.ToastUtil;
import cn.timeface.circle.baby.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.views.NoScrollGridView;

public class TimeLineEditActivity extends BaseAppCompatActivity implements View.OnClickListener {

    protected final int PHOTO_COUNT_MAX = 100;

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.gv_grid_view)
    GridView gvGridView;
    @Bind(R.id.tv_mile_stone)
    TextView tvMileStone;
    @Bind(R.id.rl_mile_stone)
    RelativeLayout rlMileStone;
    @Bind(R.id.tv_time)
    TextView tvTime;
    @Bind(R.id.rl_time)
    RelativeLayout rlTime;
    @Bind(R.id.et_content)
    EditText etContent;
    @Bind(R.id.appbar_layout)
    AppBarLayout appbarLayout;
    @Bind(R.id.iv_video)
    ImageView ivVideo;
    @Bind(R.id.iv_cover)
    ImageView ivCover;
    @Bind(R.id.tv_videotime)
    TextView tvVideotime;
    @Bind(R.id.ll_video)
    LinearLayout llVideo;
    private PhotoGridAdapter adapter;
    private HashSet<String> imageUrls = new HashSet<>();
    private final int PICTURE = 0;
    private final int MILESTONE = 1;
    private final int TIME = 2;
    private Milestone milestone;
    private List<ImgObj> selImages = new ArrayList<>();
    private TimeLineObj timelimeobj;
    private List<MediaObj> mediaList;
    private int milestoneId;
    private String time;
    private List<String> urls;
    private ArrayList<ImgObj> imgObjs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline_edit);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initView();
    }

    private void initView() {
        timelimeobj = getIntent().getParcelableExtra("timelimeobj");
        mediaList = timelimeobj.getMediaList();
        milestoneId = timelimeobj.getMilestoneId();
        time = DateUtil.getYear2(timelimeobj.getDate());

        rlMileStone.setOnClickListener(this);
        rlTime.setOnClickListener(this);

        etContent.setText(timelimeobj.getContent());
        tvMileStone.setText(timelimeobj.getMilestone());
        tvTime.setText(time);

        if (timelimeobj.getType() == 1 || !TextUtils.isEmpty(timelimeobj.getMediaList().get(0).getVideoUrl())) {
            llVideo.setVisibility(View.VISIBLE);
            gvGridView.setVisibility(View.GONE);

            GlideUtil.displayImage(timelimeobj.getMediaList().get(0).getImgUrl(), ivVideo);
            long length = timelimeobj.getMediaList().get(0).getLength();
            tvVideotime.setText("时长：" + DateUtil.getTime4(length * 1000));
            int width = Remember.getInt("width", 0);
            ViewGroup.LayoutParams layoutParams = ivVideo.getLayoutParams();
            layoutParams.width = width;
            layoutParams.height = width;
            ivVideo.setLayoutParams(layoutParams);

        } else {
            llVideo.setVisibility(View.GONE);
            gvGridView.setVisibility(View.VISIBLE);

            for (MediaObj media : mediaList) {
                ImgObj imgObj = media.getImgObj();
                selImages.add(imgObj);
                imageUrls.add(media.getImgUrl());
            }
            if (timelimeobj.getType() == 0) {
                adapter = new PhotoGridAdapter(this);
                adapter.getData().addAll(imageUrls);
                gvGridView.setAdapter(adapter);
                adapter.setOnAddClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectImages();
                    }
                });
                /*gvGridView.setOnItemClickListener((parent, v, position, id) -> {
                    if (position == 0) {
                        selectImages();
                    } else {
//                int relPosition = position - 1;
//                imageUrls.remove(adapter.getData().get(relPosition));
//                adapter.getData().remove(relPosition);
//                adapter.notifyDataSetChanged();
                    }
                });*/
            } else {
                PhotoGridAdapter2 photoGridAdapter2 = new PhotoGridAdapter2(this);
                photoGridAdapter2.getData().addAll(imageUrls);
                gvGridView.setAdapter(photoGridAdapter2);
            }
        }
    }

    private void selectImages() {
        SelectPhotoActivity.openForResult(this, PHOTO_COUNT_MAX, PICTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case PICTURE:
                    imgObjs = data.getParcelableArrayListExtra("result_select_image_list");
                    urls = new ArrayList<>();
                    for (ImgObj item : imgObjs) {
                        urls.add(item.getLocalPath());
                    }
                    if (urls.size() > 0) {
                        adapter.getData().addAll(urls);
                        adapter.notifyDataSetChanged();
                    }
                    break;
                case MILESTONE:
                    milestone = (Milestone) data.getParcelableExtra("milestone");
                    milestoneId = milestone.getId();
                    tvMileStone.setText(milestone.getMilestone());
                    break;
                case TIME:
                    time = data.getStringExtra("time");
                    tvTime.setText(time);
                    break;
            }

        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_mile_stone:
                Intent intent = new Intent(this, SelectMileStoneActivity.class);
                startActivityForResult(intent, MILESTONE);
                break;
            case R.id.rl_time:
                Intent intent1 = new Intent(this, SelectTimeActivity.class);
                intent1.putExtra("time", tvTime.getText().toString());
                startActivityForResult(intent1, TIME);
                break;
        }
    }

    private void editTime() {
        String value = etContent.getText().toString();
        if (value.length() < 1 && imageUrls.size() < 1) {
            Toast.makeText(this, "发点文字或图片吧", Toast.LENGTH_SHORT).show();
            return;
        }
        if (imgObjs.size() > 0) {
            for (ImgObj img : imgObjs) {
                MediaObj mediaObj = img.getMediaObj();
                mediaList.add(mediaObj);
            }
        }
        String s = new Gson().toJson(mediaList);
//        long t = DateUtil.getTime(time, "yyyy.MM.dd");
        long t = System.currentTimeMillis();
        apiService.editTime(URLEncoder.encode(value), URLEncoder.encode(s), milestoneId, t, timelimeobj.getTimeId())
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(response -> {
                    ToastUtil.showToast(response.getInfo());
                    if (response.success()) {
                        finish();
                        for (String url : urls) {
                            uploadImage(url);
                        }
                        EventBus.getDefault().post(new TimelineEditEvent());
                    }
                }, throwable -> {
                    Log.e(TAG, "editTime:");
                });
        finish();
    }

    private static class PhotoGridAdapter extends BaseAdapter {
        private static final int TYPE_HEADER = 1;
        private static final int TYPE_BODY = 2;
        private Context context;
        private View.OnClickListener listener;
        List<String> data = new ArrayList<>();

        public PhotoGridAdapter(Context context) {
            this.context = context;
        }

        public List<String> getData() {
            return data;
        }

        @Override
        public int getCount() {
            return data.size() + 1;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public int getItemViewType(int position) {
            return position == data.size() ? TYPE_HEADER : TYPE_BODY;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            int width = (int) (Remember.getInt("width", 0) * 0.75);
            if (getItemViewType(position) == TYPE_HEADER) {
                view = View.inflate(context, R.layout.item_timeline_add, null);
                ImageView ivAdd = (ImageView) view.findViewById(R.id.iv_add);
                ivAdd.setLayoutParams(new FrameLayout.LayoutParams(width,width));
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(listener!=null){
                            listener.onClick(v);
                        }
                    }
                });
            } else {
                view = View.inflate(context, R.layout.item_image, null);
                ImageView imageView = (ImageView) view.findViewById(R.id.iv_image);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, width);
                imageView.setLayoutParams(params);
                GlideUtil.displayImage(data.get(position), imageView);
            }
            return view;
        }

        public void setOnAddClickListener(View.OnClickListener listener){
            this.listener = listener;
        }
    }

    private static class PhotoGridAdapter2 extends BaseAdapter {

        private Context context;

        List<String> data = new ArrayList<>();

        public PhotoGridAdapter2(Context context) {
            this.context = context;
        }

        public List<String> getData() {
            return data;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int width = (int) (Remember.getInt("width", 0) * 0.75);
            View view = View.inflate(context, R.layout.item_image, null);
            ImageView imageView = (ImageView) view.findViewById(R.id.iv_image);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, width);
            imageView.setLayoutParams(params);
            GlideUtil.displayImage(data.get(position), imageView);
            return view;
        }
    }

    private void uploadImage(String path) {
        if (TextUtils.isEmpty(path)) {
            return;
        }
        OSSManager ossManager = OSSManager.getOSSManager(this);
        new Thread() {
            @Override
            public void run() {
                try {
                    //获取上传文件
                    UploadFileObj uploadFileObj = new MyUploadFileObj(path);
                    //上传操作
                    try {
                        //判断服务器是否已存在该文件
                        if (!ossManager.checkFileExist(uploadFileObj.getObjectKey())) {
                            //如果不存在则上传
                            ossManager.upload(uploadFileObj.getObjectKey(), uploadFileObj.getFinalUploadFile().getAbsolutePath());
                        }
//                recorder.oneFileCompleted(uploadTaskInfo.getInfoId(), uploadFileObj.getObjectKey());
                    } catch (ServiceException | ClientException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {

                }
            }
        }.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_complete, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        editTime();
        return super.onOptionsItemSelected(item);
    }
}
