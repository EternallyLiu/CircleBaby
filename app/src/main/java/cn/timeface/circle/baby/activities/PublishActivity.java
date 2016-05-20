package cn.timeface.circle.baby.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.wechat.photopicker.PickerVideoActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.adapters.PhotoGridAdapter;
import cn.timeface.circle.baby.adapters.PublishPhotoAdapter;
import cn.timeface.circle.baby.api.models.PhotoRecode;
import cn.timeface.circle.baby.api.models.objs.ImgObj;
import cn.timeface.circle.baby.api.models.objs.Milestone;
import cn.timeface.circle.baby.utils.GlideUtil;
import cn.timeface.circle.baby.views.NoScrollGridView;

public class PublishActivity extends BaseAppCompatActivity implements View.OnClickListener {

    public static final int NOMAL = 0;
    public static final int PHOTO = 1;
    public static final int VIDEO = 2;
    public static final int DIALY = 3;
    public static final int CARD = 4;

    protected final int PHOTO_COUNT_MAX = 100;

    @Bind(R.id.tv_next)
    TextView tvNext;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.gv_grid_view)
    NoScrollGridView gvGridView;
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
    private PhotoGridAdapter adapter;
    private HashSet<String> imageUrls = new HashSet<>();
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

    public static void open(Context context, int type) {
        Intent intent = new Intent(context, PublishActivity.class);
        intent.putExtra("publish_type", type);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        publishType = getIntent().getIntExtra("publish_type", NOMAL);

        tvNext.setOnClickListener(this);
        rlMileStone.setOnClickListener(this);
        rlTime.setOnClickListener(this);

        adapter = new PhotoGridAdapter(this);
        gvGridView.setAdapter(adapter);
        gvGridView.setOnItemClickListener((parent, v, position, id) -> {
            if (position == 0) {
                switch (publishType) {
                    case PHOTO:
                        selectImages();
                        break;
                    case VIDEO:
                        selectVideos();
                        break;

                }

            } else {
//                int relPosition = position - 1;
//                imageUrls.remove(adapter.getData().get(relPosition));
//                adapter.getData().remove(relPosition);
//                adapter.notifyDataSetChanged();
            }
        });

        switch (publishType) {
            case PHOTO:
                selectImages();
                break;
            case VIDEO:
                selectVideos();
                break;
            case DIALY:
                PhotoSelectionActivity.openForResult(
                        this,
                        "选择照片",
                        selImages,
                        1,
                        false,
                        PICTURE_DIALY, false);
                break;
        }
    }

    private void selectImages() {
//        Intent intent = new Intent(this, PickerPhotoActivity.class);
//        startActivityForResult(intent, PICTURE);

        PhotoSelectionActivity.openForResult(
                this,
                "选择照片",
                selImages,
                PHOTO_COUNT_MAX,
                false,
                PICTURE, false);
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
//                    List<String> images = data.getStringArrayListExtra(PickerPhotoActivity.KEY_SELECTED_PHOTOS);
                    selImages = data.getParcelableArrayListExtra("result_select_image_list");
                    photoRecodes.clear();
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
                    }else{
                        llSingleDate.setVisibility(View.VISIBLE);
                        contentRecyclerView.setVisibility(View.GONE);

                        if (imageUrls.size() > 0) {
                            adapter.getData().clear();
                            adapter.getData().addAll(imageUrls);
                            adapter.notifyDataSetChanged();
                        }
                    }



                    break;
                case MILESTONE:
                    milestone = (Milestone) data.getSerializableExtra("milestone");
                    tvMileStone.setText(milestone.getMilestone());
                    break;
                case TIME:
                    String time = data.getStringExtra("time");
                    tvTime.setText(time);
                    break;
                case PHOTO_RECORD_DETAIL:
                    photoRecodes = data.getParcelableArrayListExtra("result_photo_record_detail");
                    if(publishPhotoAdapter == null){
                        publishPhotoAdapter = new PublishPhotoAdapter(this, photoRecodes);
                    }
                    publishPhotoAdapter.setListData(photoRecodes);
                    publishPhotoAdapter.notifyDataSetChanged();
                    break;
                case PICTURE_DIALY:
                    selImages = data.getParcelableArrayListExtra("result_select_image_list");
                    String path = selImages.get(0).getLocalPath();

                    PhotoEditActivity.open(this,path);

                    break;

                case PICTURE_CARD:

                    break;
                case VIDEO_SELECT:

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
            case R.id.tv_next:

                Gson gson = new Gson();
                String s = gson.toJson(photoRecodes);
                System.out.println(s);

//                postRecord();
                break;
            case R.id.rl_mile_stone:
                Intent intent = new Intent(this, MileStoneActivity.class);
                startActivityForResult(intent, MILESTONE);
                break;
            case R.id.rl_time:
                Intent intent1 = new Intent(this, SelectTimeActivity.class);
                startActivityForResult(intent1, TIME);
                break;
        }

    }

    private void postRecord() {
        String value = etContent.getText().toString();

        if (value.length() < 1 && imageUrls.size() < 1) {
            Toast.makeText(this, "发点文字或图片吧", Toast.LENGTH_SHORT).show();
            return;
        }
        this.finish();


        //发布

//        Subscription s = apiService.postRecord(value, 0, 0, 0, habitObj.getId())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.io())
//                .subscribe(response -> {
//                    Log.d(TAG, "postRecord: post ok");
//
//                    doUploadService2Upload(response.getInfo());
//                    Toast.makeText(getActivity(), "记录成功", Toast.LENGTH_SHORT).show();
//                }, throwable -> {
//                    Log.e(TAG, "postRecord: ", throwable);
//                });
//        addSubscription(s);
    }
}
