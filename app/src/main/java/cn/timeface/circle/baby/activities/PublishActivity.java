package cn.timeface.circle.baby.activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.google.gson.Gson;
import com.wechat.photopicker.PickerVideoActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.adapters.PhotoGridAdapter;
import cn.timeface.circle.baby.adapters.PublishPhotoAdapter;
import cn.timeface.circle.baby.api.models.PhotoRecode;
import cn.timeface.circle.baby.api.models.VideoInfo;
import cn.timeface.circle.baby.api.models.objs.ImgObj;
import cn.timeface.circle.baby.api.models.objs.MediaObj;
import cn.timeface.circle.baby.api.models.objs.Milestone;
import cn.timeface.circle.baby.api.models.objs.MyUploadFileObj;
import cn.timeface.circle.baby.api.models.objs.PublishObj;
import cn.timeface.circle.baby.events.CardEvent;
import cn.timeface.circle.baby.events.HomeRefreshEvent;
import cn.timeface.circle.baby.events.MediaObjEvent;
import cn.timeface.circle.baby.events.PickerPhototAddEvent;
import cn.timeface.circle.baby.events.PublishRefreshEvent;
import cn.timeface.circle.baby.events.StartUploadEvent;
import cn.timeface.circle.baby.events.TimeEditPhotoDeleteEvent;
import cn.timeface.circle.baby.events.UploadEvent;
import cn.timeface.circle.baby.managers.listeners.IEventBus;
import cn.timeface.circle.baby.managers.services.UploadService;
import cn.timeface.circle.baby.oss.OSSManager;
import cn.timeface.circle.baby.oss.uploadservice.UploadFileObj;
import cn.timeface.circle.baby.utils.DateUtil;
import cn.timeface.circle.baby.utils.GlideUtil;
import cn.timeface.circle.baby.utils.MD5;
import cn.timeface.circle.baby.utils.Remember;
import cn.timeface.circle.baby.utils.ToastUtil;
import cn.timeface.circle.baby.utils.Utils;
import cn.timeface.circle.baby.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.views.dialog.TFProgressDialog;

public class PublishActivity extends BaseAppCompatActivity implements View.OnClickListener, IEventBus {

    public static final int NOMAL = 0;
    public static final int PHOTO = 1;
    public static final int VIDEO = 2;
    public static final int DIALY = 3;
    public static final int CARD = 4;

    protected final int PHOTO_COUNT_MAX = 99;

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
    @Bind(R.id.ll_single_date)
    LinearLayout llSingleDate;
    @Bind(R.id.content_recycler_view)
    RecyclerView contentRecyclerView;
    @Bind(R.id.iv_card)
    ImageView ivCard;
    @Bind(R.id.iv_video)
    ImageView ivVideo;
    @Bind(R.id.iv_cover)
    ImageView ivCover;
    @Bind(R.id.rl_video)
    RelativeLayout rlVideo;
    @Bind(R.id.tv_videotime)
    TextView tvVideotime;

    private PhotoGridAdapter adapter;
    private List<String> imageUrls = new ArrayList<>();
    public final int PICTURE = 0;
    public final int MILESTONE = 1;
    public final int TIME = 2;
    public final int PHOTO_RECORD_DETAIL = 3;
    public final int PICTURE_DIALY = 4;
    public final int PICTURE_CARD = 5;
    public final int VIDEO_SELECT = 6;

    private Milestone milestone;
    private int publishType;
    private List<ImgObj> selImages = new ArrayList<>();
    private List<PhotoRecode> photoRecodes = new ArrayList<>();
    private List<String> titles = new ArrayList<>();
    private List<ImgObj>[] imagelLists;
    private PublishPhotoAdapter publishPhotoAdapter;
    private int type;
    private MediaObj mediaObj;
    private List<MediaObj> mediaObjs;
    private VideoInfo videoInfo;
    private TFProgressDialog tfProgressDialog;
    private String time_shot;
    private List<String> localUrls;
    private int count = 0;
    private boolean isPublish;
    private double oldProgress = 0;

    public static void open(Context context, int type) {
        Intent intent = new Intent(context, PublishActivity.class);
        intent.putExtra("publish_type", type);
        context.startActivity(intent);
    }

    public static void open(Context context, MediaObj mediaObj) {
        Intent intent = new Intent(context, PublishActivity.class);
        intent.putExtra("mediaObj", mediaObj);
        context.startActivity(intent);
    }

    public static void open(Context context, List<MediaObj> mediaObjs) {
        Intent intent = new Intent(context, PublishActivity.class);
        intent.putParcelableArrayListExtra("mediaObjs", (ArrayList<? extends Parcelable>) mediaObjs);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tfProgressDialog = new TFProgressDialog(this);

        publishType = getIntent().getIntExtra("publish_type", NOMAL);
        mediaObj = getIntent().getParcelableExtra("mediaObj");
        mediaObjs = getIntent().getParcelableArrayListExtra("mediaObjs");

        rlMileStone.setOnClickListener(this);
        rlTime.setOnClickListener(this);

        adapter = new PhotoGridAdapter(this);
        gvGridView.setAdapter(adapter);
        switch (publishType) {
            case PHOTO:
                type = 0;
                selectImages();
                break;
            case VIDEO:
                type = 1;
                selectVideos();
                break;
            case DIALY:
                type = 2;
                DiaryPublishActivity.open(this);
                break;
            case CARD:
                type = 3;
                CardPublishActivity.open(this);
                break;
        }

        if (mediaObj != null) {
            type = 2;
            gvGridView.setVisibility(View.GONE);
            ivCard.setVisibility(View.VISIBLE);
            GlideUtil.displayImage(mediaObj.getImgUrl(), ivCard);
            time_shot = DateUtil.formatDate("yyyy.MM.dd", mediaObj.getPhotographTime());
            tvTime.setText(time_shot);
        }
        if (mediaObjs != null) {
            type = 3;
            gvGridView.setVisibility(View.VISIBLE);
            ivCard.setVisibility(View.GONE);
            List<String> list = new ArrayList<>();
            for (MediaObj media : mediaObjs) {
                list.add(media.getImgUrl());
            }
            adapter.setData(list);
            adapter.notifyDataSetChanged();
            if (mediaObjs.get(0).getPhotographTime() == 0) {
                time_shot = DateUtil.formatDate("yyyy.MM.dd", System.currentTimeMillis());
            } else {
                time_shot = DateUtil.formatDate("yyyy.MM.dd", mediaObjs.get(0).getPhotographTime());
            }
            tvTime.setText(time_shot);
        }

        adapter.setOnAddClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (type) {
                    case 0:
                        selectImages();
                        break;
                    case 1:
                        selectVideos();
                        break;
                    case 2:
                        DiaryPublishActivity.open(PublishActivity.this);
                        break;
                    case 3:
                        CardPublishActivity.open(PublishActivity.this);
                        break;
                }
            }
        });
        gvGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FragmentBridgeActivity.openBigimageFragment(PublishActivity.this, (ArrayList<String>) adapter.getData(), position, false, true);
            }
        });

    }

    private void selectImages() {
        SelectPhotoActivity.openForResult(this, selImages, PHOTO_COUNT_MAX, PICTURE);

    }

    private void selectVideos() {
        Intent intent = new Intent(this, PickerVideoActivity.class);
        startActivityForResult(intent, VIDEO_SELECT);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case PICTURE:
                    selImages = data.getParcelableArrayListExtra("result_select_image_list");
                    photoRecodes.clear();
                    imageUrls.clear();
                    titles.clear();
                    for (ImgObj item : selImages) {
                        imageUrls.add(item.getLocalPath());
                        String title = item.getDate();

                        if (!titles.contains(title)) {
                            titles.add(title);

                        }
                    }

                    imagelLists = new List[titles.size()];

                    for (int i = 0; i < titles.size(); i++) {
                        imagelLists[i] = new ArrayList<>();
                        for (ImgObj item : selImages) {
                            if (titles.get(i).equals(item.getDate())) {
                                imagelLists[i].add(item);
                            }
                        }
                        photoRecodes.add(new PhotoRecode(titles.get(i), imagelLists[i]));
                    }
                    if (photoRecodes.size() > 1) {
                        llSingleDate.setVisibility(View.GONE);
                        contentRecyclerView.setVisibility(View.VISIBLE);
                        publishPhotoAdapter = new PublishPhotoAdapter(this, photoRecodes);
                        contentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                        contentRecyclerView.setAdapter(publishPhotoAdapter);
                    } else {
                        llSingleDate.setVisibility(View.VISIBLE);
                        contentRecyclerView.setVisibility(View.GONE);

                        if (imageUrls.size() > 0) {
                            adapter.getData().clear();
                            adapter.getData().addAll(imageUrls);
                            adapter.notifyDataSetChanged();

                            tvTime.setText(titles.get(0));
                        }
                    }
                    time_shot = titles.get(0);
                    break;
                case MILESTONE:
                    milestone = (Milestone) data.getParcelableExtra("milestone");
                    tvMileStone.setText(milestone.getMilestone());
                    if (photoRecodes.size() > 0) {
                        photoRecodes.get(0).setMileStone(milestone);
                    }
                    break;
                case TIME:
                    String time = data.getStringExtra("time");
                    tvTime.setText(time);
                    break;
                case PHOTO_RECORD_DETAIL:
                    PhotoRecode photoRecode = (PhotoRecode) data.getParcelableExtra("photoRecode");
                    int position = data.getIntExtra("position", 0);
                    photoRecodes.set(position, photoRecode);
                    if (publishPhotoAdapter == null) {
                        publishPhotoAdapter = new PublishPhotoAdapter(this, photoRecodes);
                    }
                    publishPhotoAdapter.setListData(photoRecodes);
                    publishPhotoAdapter.notifyDataSetChanged();
                    break;
                case VIDEO_SELECT:
                    String imgObjectKey = data.getStringExtra("imgObjectKey");
                    String path = data.getStringExtra("path");
                    long duration = data.getLongExtra("duration", 0);
                    long date = data.getLongExtra("date", System.currentTimeMillis());
                    String s = date + "";
                    s = s.length() == 13 ? s : s + "000";
                    date = Long.valueOf(s);
                    videoInfo = new VideoInfo(duration, imgObjectKey, path, date);
                    String videoObjectKey = "baby/" + MD5.encode(new File(path)) + path.substring(path.lastIndexOf("."));
                    videoInfo.setVideoObjectKey(videoObjectKey);
                    GlideUtil.displayImage("http://img1.timeface.cn/" + imgObjectKey, ivVideo);
                    rlVideo.setVisibility(View.VISIBLE);
                    gvGridView.setVisibility(View.GONE);
                    ViewGroup.LayoutParams layoutParams = ivVideo.getLayoutParams();
                    int width = Remember.getInt("width", 0);
                    layoutParams.width = width;
                    layoutParams.height = width;
                    ivVideo.setLayoutParams(layoutParams);
                    time_shot = DateUtil.formatDate("yyyy.MM.dd", videoInfo.getDate());
                    tvTime.setText(time_shot);
                    tvVideotime.setText("时长：" + DateUtil.getTime4(videoInfo.getDuration() * 1000));
                    mediaObj = new MediaObj(videoInfo.getImgObjectKey(), videoInfo.getDuration(), videoObjectKey, videoInfo.getDate());
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
                if (TextUtils.isEmpty(time_shot)) {
                    time_shot = tvTime.getText().toString();
                }
                intent1.putExtra("time_shot", time_shot);
                intent1.putExtra("time_now", tvTime.getText().toString());
                startActivityForResult(intent1, TIME);
                break;
        }

    }

    //发布识图卡片
    private void publishCard() {
        String content = etContent.getText().toString();
        if (mediaObjs.size() == 0) {
            Toast.makeText(this, "发张照片吧~", Toast.LENGTH_SHORT).show();
            isPublish = false;
            return;
        }
        tfProgressDialog.setMessage("发布中");
        tfProgressDialog.show();
        String t = tvTime.getText().toString() + DateUtil.formatDate(" kk:mm", System.currentTimeMillis());
        long time = DateUtil.getTime(t, "yyyy.MM.dd kk:mm");

        List<PublishObj> datalist = new ArrayList<>();

        PublishObj publishObj = new PublishObj(content, mediaObjs, milestone == null ? 0 : milestone.getId(), time);
        datalist.add(publishObj);

        Gson gson = new Gson();
        String s = gson.toJson(datalist);

        apiService.publish(URLEncoder.encode(s), type)
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(response -> {
                    tfProgressDialog.dismiss();
                    if (response.success()) {
                        EventBus.getDefault().post(new HomeRefreshEvent());
                        EventBus.getDefault().post(new PickerPhototAddEvent());
                        finish();
                    }else{
                        ToastUtil.showToast(response.getInfo());
                    }
                    isPublish = false;
                }, throwable -> {
                    tfProgressDialog.dismiss();
                    Log.e(TAG, "publish:", throwable);
                    isPublish = false;
                    ToastUtil.showToast("服务器异常，请稍后重试");
                });

    }

    //发布日记
    private void publishDiary() {
        String content = etContent.getText().toString();
        if (mediaObj == null) {
            if (type == 1) {
                Toast.makeText(this, "请选择视频~", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "发张照片吧~", Toast.LENGTH_SHORT).show();
            }

            isPublish = false;
            return;
        }
        tfProgressDialog.setMessage("发布中");
        tfProgressDialog.show();
        String t = tvTime.getText().toString() + DateUtil.formatDate(" kk:mm", System.currentTimeMillis());
        long time = DateUtil.getTime(t, "yyyy.MM.dd kk:mm");
        List<PublishObj> datalist = new ArrayList<>();
        List<MediaObj> mediaObjs = new ArrayList<>();
        mediaObjs.add(mediaObj);

        PublishObj publishObj = new PublishObj(content, mediaObjs, milestone == null ? 0 : milestone.getId(), time);
        datalist.add(publishObj);

        Gson gson = new Gson();
        String s = gson.toJson(datalist);

        Log.v(TAG, "s============" + s);
        apiService.publish(URLEncoder.encode(s), type)
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(response -> {
                    tfProgressDialog.dismiss();
                    if (response.success()) {
                        if (type == 1) {
                            EventBus.getDefault().post(new StartUploadEvent());
                            uploadVideo(videoInfo.getPath());
                        } else {
                            EventBus.getDefault().post(new PickerPhototAddEvent());
                            EventBus.getDefault().post(new HomeRefreshEvent());
                        }
                        finish();
                    }else {
                        ToastUtil.showToast(response.getInfo());
                    }
                    isPublish = false;
                }, throwable -> {
                    tfProgressDialog.dismiss();
                    Log.e(TAG, "publish:", throwable);
                    isPublish = false;
                    ToastUtil.showToast("服务器异常，请稍后重试");
                });

    }


    //发布照片
    private void postRecord() {
        String value = etContent.getText().toString();
        long time = 0;
        if (imageUrls.size() < 1) {
            Toast.makeText(this, "发张照片吧~", Toast.LENGTH_SHORT).show();
            isPublish = false;
            return;
        }

        if (photoRecodes.size() == 1) {
            String t = tvTime.getText().toString() + DateUtil.formatDate(" kk:mm", System.currentTimeMillis());
            time = DateUtil.getTime(t, "yyyy.MM.dd kk:mm");
        }
        tfProgressDialog.setMessage("发布中");
        tfProgressDialog.show();
        //发布
        localUrls = new ArrayList<>();
        List<PublishObj> datalist = new ArrayList<>();
        if (photoRecodes.size() == 1) {
            photoRecodes.get(0).setContent(value);
        }
        for (PhotoRecode photoRecode : photoRecodes) {
            if (photoRecodes.size() > 1) {
                String title = photoRecode.getTitle();
                String t = title + DateUtil.formatDate(" kk:mm", System.currentTimeMillis());
                time = DateUtil.getTime(t, "yyyy.MM.dd kk:mm");
            }
            String content = photoRecode.getContent();
            Milestone mileStone = photoRecode.getMileStone();
            int mileStoneId = mileStone == null ? 0 : mileStone.getId();
            List<ImgObj> imgObjList = photoRecode.getImgObjList();
            List<MediaObj> mediaObjs = new ArrayList<>();
            for (ImgObj img : imgObjList) {
//                Bitmap bitmap = BitmapFactory.decodeFile(img.getLocalPath());
                int height = img.getHeight();
                int width = img.getWidth();
                Log.v(TAG, "img.getUrl ============ " + img.getUrl());
                localUrls.add(img.getLocalPath());
                MediaObj mediaObj = new MediaObj(img.getContent(), img.getUrl(), width, height, img.getDateMills());
                mediaObjs.add(mediaObj);
            }
            PublishObj publishObj = new PublishObj(content, mediaObjs, mileStoneId, time);
            datalist.add(publishObj);
        }
        Gson gson = new Gson();
        String s = gson.toJson(datalist);

        Log.v(TAG, s);

        apiService.publish(URLEncoder.encode(s), type)
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(response -> {
                    tfProgressDialog.dismiss();
                    if (response.success()) {
                        finish();
                        isPublish = false;
                        count = 0;
                        EventBus.getDefault().post(new StartUploadEvent());
                        UploadService.start(PublishActivity.this,localUrls);
                    }else{
                        ToastUtil.showToast(response.getInfo());
                    }
                }, throwable -> {
                    tfProgressDialog.dismiss();
                    isPublish = false;
                    Log.e(TAG, "publish:", throwable);
                    ToastUtil.showToast("服务器异常，请稍后重试");
                });


    }

    @Subscribe
    public void onEvent(Object event) {
        if (event instanceof MediaObjEvent) {
            gvGridView.setVisibility(View.GONE);
            mediaObj = ((MediaObjEvent) event).getMediaObj();
            GlideUtil.displayImage(mediaObj.getImgUrl(), ivCard);
            tvTime.setText(DateUtil.formatDate("yyyy.MM.dd", System.currentTimeMillis()));
        } else if (event instanceof CardEvent) {
            mediaObjs = ((CardEvent) event).getMediaObjs();
            List<String> list = new ArrayList<>();
            for (MediaObj media : mediaObjs) {
                list.add(media.getImgUrl());
            }
            adapter.setData(list);
            adapter.notifyDataSetChanged();
            tvTime.setText(DateUtil.formatDate("yyyy.MM.dd", System.currentTimeMillis()));
        } else if (event instanceof PublishRefreshEvent) {
            mediaObjs = ((PublishRefreshEvent) event).getDataList();
            List<String> list = new ArrayList<>();
            for (MediaObj media : mediaObjs) {
                list.add(media.getImgUrl());
            }
            adapter.setData(list);
            adapter.notifyDataSetChanged();
        } else if (event instanceof TimeEditPhotoDeleteEvent) {
            int position = ((TimeEditPhotoDeleteEvent) event).getPosition();
            List<String> data = adapter.getData();
            if (data.size() > position) {
                data.remove(position);
                selImages.remove(position);
                adapter.setData(data);
                adapter.notifyDataSetChanged();
                if (photoRecodes.size() == 1) {
                    List<ImgObj> imgObjList = photoRecodes.get(0).getImgObjList();
                    if (imgObjList.size() > position) {
                        imgObjList.remove(position);
                    }
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    private void uploadVideo(String path) {
        if (TextUtils.isEmpty(path)) {
            ToastUtil.showToast("视频文件异常");
            return;
        }
//        tfProgressDialog.setMessage("上传视频中");
//        tfProgressDialog.show();
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
                            ossManager.upload(uploadFileObj.getObjectKey(), uploadFileObj.getFinalUploadFile().getAbsolutePath(), new OSSProgressCallback<PutObjectRequest>() {
                                @Override
                                public void onProgress(PutObjectRequest putObjectRequest, long l, long l1) {
                                    int progress = (int) (l * 100 / l1);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            EventBus.getDefault().post(new UploadEvent(progress));
                                        }
                                    });
                                }
                            }, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
                                @Override
                                public void onSuccess(PutObjectRequest putObjectRequest, PutObjectResult putObjectResult) {

                                }

                                @Override
                                public void onFailure(PutObjectRequest putObjectRequest, ClientException e, ServiceException e1) {

                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    EventBus.getDefault().post(new UploadEvent(100));
                                }
                            });
                        }
                        String videoObjectKey = uploadFileObj.getObjectKey();
                    } catch (Exception e) {
                        Log.e(TAG, "uploadVideo", e);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "uploadVideo", e);
                }
            }
        }.start();
    }

    @Override
    public void onBackPressed() {
        delMileStone();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.complete) {
            //检测网络环境
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
                            publish();
                        }
                    }).show();
                } else {
                    publish();
                }
            } else {
                ToastUtil.showToast("请检查是否联网");
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void publish() {
        if (!isPublish) {
            isPublish = true;
            switch (type) {
                case 0:
                    postRecord();
                    break;
                case 1:
                case 2:
                    publishDiary();
                    break;
                case 3:
                    publishCard();
                    break;
            }
        }
    }

    public void delMileStone() {
        new AlertDialog.Builder(this).setTitle("确认退出发布流程？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_complete, menu);
        return super.onCreateOptionsMenu(menu);
    }

}
