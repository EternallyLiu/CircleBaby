package cn.timeface.circle.baby.activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
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
import cn.timeface.circle.baby.events.HomeRefreshEvent;
import cn.timeface.circle.baby.events.PickerPhototAddEvent;
import cn.timeface.circle.baby.events.PublishRefreshEvent;
import cn.timeface.circle.baby.events.StartUploadEvent;
import cn.timeface.circle.baby.events.TimeEditPhotoDeleteEvent;
import cn.timeface.circle.baby.events.UploadEvent;
import cn.timeface.circle.baby.support.api.models.PhotoRecode;
import cn.timeface.circle.baby.support.api.models.VideoInfo;
import cn.timeface.circle.baby.support.api.models.objs.CardObj;
import cn.timeface.circle.baby.support.api.models.objs.ImgObj;
import cn.timeface.circle.baby.support.api.models.objs.LocationObj;
import cn.timeface.circle.baby.support.api.models.objs.MediaObj;
import cn.timeface.circle.baby.support.api.models.objs.Milestone;
import cn.timeface.circle.baby.support.api.models.objs.MyUploadFileObj;
import cn.timeface.circle.baby.support.api.models.objs.PublishObj;
import cn.timeface.circle.baby.support.managers.listeners.IEventBus;
import cn.timeface.circle.baby.support.managers.services.UploadService;
import cn.timeface.circle.baby.support.oss.OSSManager;
import cn.timeface.circle.baby.support.oss.uploadservice.UploadFileObj;
import cn.timeface.circle.baby.support.utils.DateUtil;
import cn.timeface.circle.baby.support.utils.GlideUtil;
import cn.timeface.circle.baby.support.utils.MD5;
import cn.timeface.circle.baby.support.utils.Remember;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.support.utils.Utils;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.timelines.Utils.LogUtil;
import cn.timeface.circle.baby.ui.timelines.beans.MediaUpdateEvent;
import cn.timeface.circle.baby.ui.timelines.beans.NearLocationObj;
import cn.timeface.circle.baby.ui.timelines.beans.SendTimeFace;
import cn.timeface.circle.baby.ui.timelines.beans.TimeConttent;
import cn.timeface.circle.baby.ui.timelines.fragments.LocationListFragment;
import cn.timeface.circle.baby.ui.timelines.services.UploadVideoService;
import cn.timeface.circle.baby.views.dialog.TFProgressDialog;

public class PublishActivity extends BaseAppCompatActivity implements View.OnClickListener, IEventBus {

    public static final int NOMAL = 0;
    public static final int PHOTO = 1;
    public static final int VIDEO = 2;
    public static final int DIALY = 3;
    public static final int CARD = 4;
    public static final int VOICE = 5;

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
    @Bind(R.id.tv_location)
    TextView tvLocation;
    @Bind(R.id.rl_location)
    RelativeLayout rlLocation;
    @Bind(R.id.rlvideo)
    RelativeLayout rlvideo;

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
    private CardObj cardObj;
    private List<CardObj> cardObjs;
    private VideoInfo videoInfo;
    private TFProgressDialog tfProgressDialog;
    private String time_shot;
    private List<String> localUrls;
    private int count = 0;
    private boolean isPublish;
    private double oldProgress = 0;
    private NearLocationObj currentLocation = null;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private ArrayList<MediaObj> mediaObjs;

    public static void open(Context context, int type) {
        Intent intent = new Intent(context, PublishActivity.class);
        intent.putExtra("publish_type", type);
        context.startActivity(intent);
    }

    public static void open(Context context, CardObj cardObj) {
        Intent intent = new Intent(context, PublishActivity.class);
        intent.putExtra("mediaObj", cardObj);
        context.startActivity(intent);
    }

    public static void open(Context context, List<CardObj> mediaObjs) {
        Intent intent = new Intent(context, PublishActivity.class);
        intent.putParcelableArrayListExtra("mediaObjs", (ArrayList<? extends Parcelable>) mediaObjs);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);
        ButterKnife.bind(this);
        rlLocation.setOnClickListener(this);
        initLocation();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tfProgressDialog = TFProgressDialog.getInstance("");

        publishType = getIntent().getIntExtra("publish_type", NOMAL);
        cardObj = getIntent().getParcelableExtra("mediaObj");
        cardObjs = getIntent().getParcelableArrayListExtra("mediaObjs");

        rlMileStone.setOnClickListener(this);
        rlTime.setOnClickListener(this);

        adapter = new PhotoGridAdapter(this);
        gvGridView.setAdapter(adapter);
        LogUtil.showLog(publishType + "");
        switch (publishType) {
            case PHOTO:
                type = 0;
                selectImages();
                etContent.setHint("为这天记录说点什么吧~");
                break;
            case VIDEO:
                type = 1;
                selectVideos();
                etContent.setHint("为这天记录说点什么吧~");
                break;
            case DIALY:
                type = 2;
                DiaryPublishActivity.open(this);
                break;
            case CARD:
                type = 3;
                CardPublishActivity.open(this);
                break;
            case VOICE:
                type = 4;
                tvTime.setText(DateUtil.getYear2(System.currentTimeMillis()));
                etContent.setHint("记录下宝宝的童言趣语吧~");
                break;
        }

        if (cardObj != null) {
            type = 2;
            gvGridView.setVisibility(View.GONE);
            ivCard.setVisibility(View.VISIBLE);
            GlideUtil.displayImage(cardObj.getMedia().getImgUrl(), ivCard);
            time_shot = DateUtil.formatDate("yyyy.MM.dd", cardObj.getMedia().getPhotographTime());
            tvTime.setText(time_shot);
        }
        if (cardObjs != null) {
            type = 3;
            gvGridView.setVisibility(View.VISIBLE);
            ivCard.setVisibility(View.GONE);
            List<String> list = new ArrayList<>();
            mediaObjs = new ArrayList<>();
            for (CardObj cardObj : cardObjs) {
                list.add(cardObj.getMedia().getImgUrl());
                mediaObjs.add(cardObj.getMedia());
            }
            adapter.setMediaObjs(mediaObjs);
            adapter.setData(list);
            adapter.notifyDataSetChanged();
            if (cardObjs.get(0).getMedia().getPhotographTime() == 0) {
                time_shot = DateUtil.formatDate("yyyy.MM.dd", System.currentTimeMillis());
            } else {
                time_shot = DateUtil.formatDate("yyyy.MM.dd", cardObjs.get(0).getMedia().getPhotographTime());
            }
            tvTime.setText(time_shot);
        }

        adapter.setOnAddClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (type) {
                    case 0:
                    case 4:
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
                LogUtil.showLog("adapter medias:" + (adapter.getMediaObjs() == null ? "null" : adapter.getMediaObjs().size()));
                FragmentBridgeActivity.openBigimageFragment(PublishActivity.this, -1, adapter.getMediaObjs(), (ArrayList<String>) adapter.getData(), position, false, true);
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void img2Medias() {
        if (selImages != null && selImages.size() > 0) {
            if (mediaObjs == null)
                mediaObjs = new ArrayList();
            ArrayList<MediaObj> list = new ArrayList<>();
            if (mediaObjs.size() > 0) {
                list.addAll(mediaObjs);
                mediaObjs.clear();
            }
            MediaObj mediaObj = null;
            boolean requestLocation = false;
            for (int i = 0; i < selImages.size(); i++) {
                mediaObj = selImages.get(i).getMediaObj();
                if (list.contains(mediaObj))
                    mediaObjs.add(list.get(list.indexOf(mediaObj)));
                else mediaObjs.add(mediaObj);
                if (!requestLocation)
                    if (mediaObj.getLocation() != null) {
                        gettLocationAddress(mediaObj.getLocation());
                        requestLocation = true;
                    }

            }
        }
    }

    private void initLocation() {
        if (currentLocation == null) {
            tvLocation.setText("不显示位置");
        } else tvLocation.setText(currentLocation.getArea());
    }

    @Subscribe
    public void onEvent(MediaUpdateEvent mediaUpdateEvent) {
        MediaObj mediaObj = null;
        if (mediaObjs.contains(mediaUpdateEvent.getMediaObj())) {
            mediaObj = mediaObjs.get(mediaObjs.indexOf(mediaUpdateEvent.getMediaObj()));
            if (mediaObj != null) {
                mediaObj.setTips(mediaUpdateEvent.getMediaObj().getTips());
                mediaObj.setFavoritecount(mediaUpdateEvent.getMediaObj().getFavoritecount());
                mediaObj.setIsFavorite(mediaUpdateEvent.getMediaObj().getIsFavorite());
            }
        }
        for (int i = 0; i < photoRecodes.size(); i++) {
            ArrayList<MediaObj> list = photoRecodes.get(i).getMediaObjList();
            if (list.contains(mediaUpdateEvent.getMediaObj())) {
                MediaObj obj = list.get(list.indexOf(mediaUpdateEvent.getMediaObj()));

                obj.setTips(mediaUpdateEvent.getMediaObj().getTips());
                obj.setFavoritecount(mediaUpdateEvent.getMediaObj().getFavoritecount());
                obj.setIsFavorite(mediaUpdateEvent.getMediaObj().getIsFavorite());
                publishPhotoAdapter.setListData(photoRecodes);
                publishPhotoAdapter.notifyDataSetChanged();
            }
        }

    }

    private void selectImages() {
        SelectPhotoActivity.openForResult(this, selImages, PHOTO_COUNT_MAX, PICTURE);

    }

    private void selectVideos() {
        Intent intent = new Intent(this, PickerVideoActivity.class);
        startActivityForResult(intent, VIDEO_SELECT);

    }


    private void resultPictrue() {
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
        img2Medias();

        ArrayList<MediaObj> list = null;
        for (int i = 0; i < titles.size(); i++) {
            list = new ArrayList<>(0);
            imagelLists[i] = new ArrayList<>();
            for (int index = 0; index < selImages.size(); index++) {
                if (titles.get(i).equals(selImages.get(index).getDate())) {
                    imagelLists[i].add(selImages.get(index));
                    list.add(mediaObjs.get(index));
                }
            }
            photoRecodes.add(new PhotoRecode(titles.get(i), imagelLists[i], list));
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
                adapter.setMediaObjs(mediaObjs);

                tvTime.setText(titles.get(0));
            }
        }
        time_shot = titles.get(0);
    }

    private void resultSpeak() {
        photoRecodes.clear();
        imageUrls.clear();
        titles.clear();
        titles.add(TextUtils.isEmpty(tvTime.getText().toString()) ? DateUtil.getYear2(System.currentTimeMillis()) : tvTime.getText().toString());
        for (ImgObj item : selImages) {
            imageUrls.add(item.getLocalPath());
        }

        imagelLists = new List[titles.size()];
        img2Medias();

        ArrayList<MediaObj> list = null;
        for (int i = 0; i < titles.size(); i++) {
            list = new ArrayList<>(0);
            imagelLists[i] = new ArrayList<>();
            for (int index = 0; index < selImages.size(); index++) {
                imagelLists[i].add(selImages.get(index));
                list.add(mediaObjs.get(index));
            }
            photoRecodes.add(new PhotoRecode(titles.get(i), imagelLists[i], list));
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
                adapter.setMediaObjs(mediaObjs);

                tvTime.setText(titles.get(0));
            }
        }
        time_shot = titles.get(0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case PICTURE:
                    selImages = data.getParcelableArrayListExtra("result_select_image_list");
                    if (type == 0 && (selImages == null || selImages.size() <= 0)) {
                        finish();
                        return;
                    }
                    if (type == 4) {
                        resultSpeak();
                    } else
                        resultPictrue();
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
                    LogUtil.showLog("time=="+videoInfo.getDate());
                    tvTime.setText(time_shot);
                    tvVideotime.setText("时长：" + DateUtil.getTime4(videoInfo.getDuration() * 1000));
                    MediaObj mediaObj = new MediaObj(videoInfo.getImgObjectKey(), videoInfo.getDuration(), videoObjectKey, videoInfo.getDate());
                    if (cardObj == null)
                        cardObj = new CardObj();
                    cardObj.setMedia(mediaObj);
                    break;
            }

        } else if (data == null) {
            if (type == 0 && (selImages == null || selImages.size() <= 0)) {
                finish();
                return;
            } else if (type == 1 && (cardObj == null || cardObj.getMedia() == null)) {
                finish();
                return;
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
            case R.id.rl_location:
                Bundle bundle = new Bundle();
                if (currentLocation == null)
                    bundle.putBoolean("isShowLocation", true);
                else bundle.putBoolean("isShowLocation", false);
                FragmentBridgeActivity.open(this, LocationListFragment.class.getSimpleName(), bundle);
                break;
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
        if (cardObjs.size() == 0) {
            Toast.makeText(this, "发张照片吧~", Toast.LENGTH_SHORT).show();
            isPublish = false;
            return;
        }
        tfProgressDialog.setTvMessage("发布中");
        tfProgressDialog.show(getSupportFragmentManager(), "");
        String t = tvTime.getText().toString() + DateUtil.formatDate(" kk:mm", System.currentTimeMillis());
        long time = DateUtil.getTime(t, "yyyy.MM.dd kk:mm");

        SendTimeFace data = new SendTimeFace(0);
        data.getDataList().add(new TimeConttent(null, content, mediaObjs, milestone == null ? 0 : milestone.getId(), time));

        Gson gson = new Gson();
        String s = gson.toJson(data.getDataList());

        apiService.publish(URLEncoder.encode(s), data.getType())
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(response -> {
                    tfProgressDialog.dismiss();
                    if (response.success()) {
                        EventBus.getDefault().post(new HomeRefreshEvent());
                        EventBus.getDefault().post(new PickerPhototAddEvent());
                        finish();
                    } else {
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
        if (cardObj == null) {
            if (type == 1) {
                Toast.makeText(this, "请选择视频~", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "发张照片吧~", Toast.LENGTH_SHORT).show();
            }

            isPublish = false;
            return;
        }
        tfProgressDialog.setTvMessage("发布中");
        tfProgressDialog.show(getSupportFragmentManager(), "");
        String t = tvTime.getText().toString() + DateUtil.formatDate(" kk:mm", System.currentTimeMillis());
        long time = DateUtil.getTime(t, "yyyy.MM.dd kk:mm");
        List<TimeConttent> datalist = new ArrayList<>();
        ArrayList<MediaObj> cardObjs = new ArrayList<>();
        cardObjs.add(cardObj.getMedia());

        TimeConttent timeConttent = new TimeConttent(currentLocation, content, cardObjs, milestone == null ? 0 : milestone.getId(), time);
        datalist.add(timeConttent);

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
                            uploadVideo(response.getTimeInfo().getTimeId(), videoInfo.getPath());
                        } else {
                            EventBus.getDefault().post(new PickerPhototAddEvent());
                            EventBus.getDefault().post(new HomeRefreshEvent());
                        }
                        finish();
                    } else {
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
        if (imageUrls.size() < 1 && type != 4) {
            Toast.makeText(this, "发张照片吧~", Toast.LENGTH_SHORT).show();
            isPublish = false;
            return;
        }
        if (type == 4 && TextUtils.isEmpty(value)) {
            ToastUtil.showToast("在输入框写点内容吧？");
            isPublish = false;
            return;
        }

        if (photoRecodes.size() == 1) {
            String t = tvTime.getText().toString() + DateUtil.formatDate(" kk:mm", System.currentTimeMillis());
            time = DateUtil.getTime(t, "yyyy.MM.dd kk:mm");
        }
        tfProgressDialog.setTvMessage("发布中");
        tfProgressDialog.show(getSupportFragmentManager(), "");
        //发布
        localUrls = new ArrayList<>();
        List<TimeConttent> datalist = new ArrayList<>();
        if (photoRecodes.size() == 1) {
            photoRecodes.get(0).setContent(value);
            photoRecodes.get(0).setLocationObj(currentLocation);
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
            TimeConttent timeContent = new TimeConttent();
            for (ImgObj img : imgObjList) {
//                Bitmap bitmap = BitmapFactory.decodeFile(img.getLocalPath());
                localUrls.add(img.getLocalPath());
            }
            timeContent.setMilestone(mileStoneId);
            timeContent.setContent(content);
            timeContent.setTime(time);
            timeContent.setMediaList(photoRecode.getMediaObjList());
            timeContent.setLocationInfo(photoRecode.getLocationObj());
            datalist.add(timeContent);
        }
        if (datalist.size() <= 0 && photoRecodes.size() <= 0) {
            TimeConttent timeContent = new TimeConttent();
            if (milestone != null)
                timeContent.setMilestone(milestone.getId());
            timeContent.setContent(value);
            timeContent.setTime(time);
            timeContent.setLocationInfo(currentLocation);
            datalist.add(timeContent);
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
                        if (localUrls != null && localUrls.size() > 0) {
                            EventBus.getDefault().post(new StartUploadEvent(response.getTimeInfo().getTimeId()));
                            UploadService.start(PublishActivity.this, response.getTimeInfo().getTimeId(), localUrls);
                        } else
                            EventBus.getDefault().post(new HomeRefreshEvent(response.getTimeInfo().getTimeId()));
                    } else {
                        ToastUtil.showToast(response.getInfo());
                    }
                }, throwable -> {
                    tfProgressDialog.dismiss();
                    isPublish = false;
                    LogUtil.showError(throwable);
                    ToastUtil.showToast("服务器异常，请稍后重试");
                });


    }

    @Subscribe
    public void onEvent(Object event) {
        if (event instanceof PublishRefreshEvent) {
//            cardObjs = ((PublishRefreshEvent) event).getDataList();
            List<String> list = new ArrayList<>();
            for (CardObj cardObj : cardObjs) {
                list.add(cardObj.getMedia().getImgUrl());
            }
            adapter.setData(list);
            adapter.notifyDataSetChanged();
        } else if (event instanceof TimeEditPhotoDeleteEvent) {
            int position = ((TimeEditPhotoDeleteEvent) event).getPosition();
            if (photoRecodes.size() <= 1) {
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
            } else {
                MediaObj obj = ((TimeEditPhotoDeleteEvent) event).getMediaObj();
                for (int i = 0; i < photoRecodes.size(); i++) {
                    if (photoRecodes.get(i).getMediaObjList().contains(obj)) {
                        photoRecodes.get(i).getMediaObjList().remove(obj);
                        photoRecodes.get(i).getImgObjList().remove(obj.getImgObj());
                        publishPhotoAdapter.notifyDataSetChanged();
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

    private void uploadVideo(int timeId, String path) {
        if (TextUtils.isEmpty(path)) {
            ToastUtil.showToast("视频文件异常");
            return;
        }
        UploadVideoService.start(this, timeId, path);
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
                case 4:
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

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Publish Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Subscribe
    public void onEvent(NearLocationObj location) {
        if (location.getLocation() == null)
            currentLocation = null;
        else
            currentLocation = location;
        initLocation();
    }

    private void gettLocationAddress(LocationObj locationObj) {
        apiService.queryLocationInfo(locationObj.getLat(), locationObj.getLog())
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(response -> {
                    if (response.success()) {
                        currentLocation = response.getLocationInfo();
                        initLocation();
                    }
                }, throwable -> {
                    LogUtil.showError(throwable);
                });
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
