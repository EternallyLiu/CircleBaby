package cn.timeface.circle.baby.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.events.TimeEditPhotoDeleteEvent;
import cn.timeface.circle.baby.support.api.models.PhotoRecode;
import cn.timeface.circle.baby.support.api.models.objs.ImgObj;
import cn.timeface.circle.baby.support.api.models.objs.Milestone;
import cn.timeface.circle.baby.support.api.models.objs.TimeLineObj;
import cn.timeface.circle.baby.support.managers.listeners.IEventBus;
import cn.timeface.circle.baby.support.utils.GlideUtil;
import cn.timeface.circle.baby.support.utils.Remember;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.support.utils.Utils;
import cn.timeface.circle.baby.ui.timelines.beans.MediaUpdateEvent;
import cn.timeface.circle.baby.ui.timelines.beans.NearLocationObj;
import cn.timeface.circle.baby.ui.timelines.fragments.LocationListFragment;

public class PhotoRecodeDetailActivity extends BaseAppCompatActivity implements View.OnClickListener, IEventBus {

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
    @Bind(R.id.tv_location)
    TextView tvLocation;
    @Bind(R.id.rl_location)
    RelativeLayout rlLocation;
    @Bind(R.id.iv_video)
    ImageView ivVideo;
    @Bind(R.id.iv_cover)
    ImageView ivCover;
    @Bind(R.id.tv_videotime)
    TextView tvVideotime;
    @Bind(R.id.ll_video)
    LinearLayout llVideo;
    private PhotoGridAdapter adapter;
    private ArrayList<String> imageUrls = new ArrayList<>();
    private final int PICTURE = 0;
    private final int MILESTONE = 1;
    private final int TIME = 2;
    private Milestone milestone;
    private List<ImgObj> selImages = new ArrayList<>();
    private int position;
    private PhotoRecode photoRecode;
    private String time_shot;

    private boolean isEdit = false;
    private TimeLineObj currentTimeLineObj = null;

    public static void open(Context context, TimeLineObj timeLineObj) {
        Intent intent = new Intent(context, PhotoRecodeDetailActivity.class);
        intent.putExtra("isEdit", true);
        intent.putExtra(TimeLineObj.class.getName(), timeLineObj);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_record_detail);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        photoRecode = getIntent().getParcelableExtra("photoRecode");
        position = getIntent().getIntExtra("position", 0);
        selImages = photoRecode.getImgObjList();
        for (ImgObj item : selImages) {
            imageUrls.add(item.getLocalPath());
        }
        adapter = new PhotoGridAdapter(this);
        adapter.getData().addAll(imageUrls);
        gvGridView.setAdapter(adapter);
        adapter.setOnAddClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImages();
            }
        });

        rlMileStone.setOnClickListener(this);
        rlTime.setOnClickListener(this);

        etContent.setText(this.photoRecode.getContent());
        if (this.photoRecode.getMileStone() != null)
            tvMileStone.setText(this.photoRecode.getMileStone().getMilestone());
        tvTime.setText(this.photoRecode.getTitle());
        gvGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                FragmentBridgeActivity.openBigimageFragment(PhotoRecodeDetailActivity.this, (ArrayList<String>) adapter.getData(), position, false, true);
                FragmentBridgeActivity.openBigimageFragment(PhotoRecodeDetailActivity.this, -1, photoRecode.getMediaObjList(), (ArrayList<String>) adapter.getData(), position, false, true);
            }
        });
        initLocation();
        rlLocation.setOnClickListener(this);
    }

    private List<ImgObj> getImageObjList() {
        List<ImgObj> list = new ArrayList<>();
        if (currentTimeLineObj != null && currentTimeLineObj.getMediaList().size() > 0) {
            for (int i = 0; i < currentTimeLineObj.getMediaList().size(); i++) {
                list.add(currentTimeLineObj.getMediaList().get(i).getImgObj());
            }
        }
        return list;
    }

    private void selectImages() {
        SelectPhotoActivity.openForResult(this, selImages, PHOTO_COUNT_MAX, PICTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case PICTURE:
                    selImages = data.getParcelableArrayListExtra("result_select_image_list");
                    imageUrls.clear();
                    for (ImgObj item : selImages) {
                        imageUrls.add(item.getLocalPath());
                    }
                    if (imageUrls.size() > 0) {
                        adapter.getData().clear();
                        adapter.getData().addAll(imageUrls);
                        adapter.notifyDataSetChanged();
                    }
                    photoRecode.setImgObjList(selImages);
                    break;
                case MILESTONE:
                    milestone = (Milestone) data.getParcelableExtra("milestone");
                    tvMileStone.setText(milestone.getMilestone());
                    photoRecode.setMileStone(milestone);
                    break;
                case TIME:
                    String time = data.getStringExtra("time");
                    tvTime.setText(time);
                    photoRecode.setTime(time);
                    photoRecode.setTitle(time);
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
            case R.id.rl_location:
                Bundle bundle = new Bundle();
                if (photoRecode.getLocationObj() == null)
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
                time_shot = photoRecode.getImgObjList().get(0).getDate();
                if (TextUtils.isEmpty(time_shot)) {
                    time_shot = photoRecode.getTitle();
                }
                intent1.putExtra("time_shot", time_shot);
                intent1.putExtra("time_now", tvTime.getText().toString());
                startActivityForResult(intent1, TIME);
                break;
        }

    }

    private void postRecord() {
        String value = etContent.getText().toString();

        if (Utils.getByteSize(value) > PublishActivity.INPUT_PIC_MAX_LENGTH) {
            ToastUtil.showToast(String.format(getString(R.string.input_max_length_tip), PublishActivity.INPUT_PIC_MAX_LENGTH));
            return;
        }
        photoRecode.setContent(value);

        if (imageUrls.size() < 1) {
            Toast.makeText(this, "至少添加一张照片", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent();
        intent.putExtra("photoRecode", photoRecode);
        intent.putExtra("position", position);
        setResult(RESULT_OK, intent);
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

        public void setData(List<String> data) {
            this.data = data;
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
                ivAdd.setLayoutParams(new FrameLayout.LayoutParams(width, width));
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (listener != null) {
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

        public void setOnAddClickListener(View.OnClickListener listener) {
            this.listener = listener;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_next, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.home) {
            onBackPressed();
        } else if (item.getItemId() == R.id.next) {
            postRecord();
        }
        return super.onOptionsItemSelected(item);
    }

    @Subscribe
    public void onEvent(MediaUpdateEvent mediaUpdateEvent) {
        if (mediaUpdateEvent.getAllDetailsListPosition() >= 0)
            return;
        if (mediaUpdateEvent.getIndex() >= 0 && photoRecode.getMediaObjList().get(mediaUpdateEvent.getIndex()).getLocalIdentifier().equals(mediaUpdateEvent.getMediaObj().getLocalIdentifier())) {
            photoRecode.getMediaObjList().get(mediaUpdateEvent.getIndex()).setTips(mediaUpdateEvent.getMediaObj().getTips());
            photoRecode.getMediaObjList().get(mediaUpdateEvent.getIndex()).setFavoritecount(mediaUpdateEvent.getMediaObj().getFavoritecount());
            photoRecode.getMediaObjList().get(mediaUpdateEvent.getIndex()).setIsFavorite(mediaUpdateEvent.getMediaObj().getIsFavorite());
        }
    }

    @Subscribe
    public void onEvent(NearLocationObj location) {
        if (location.getLocation() == null)
            photoRecode.setLocationObj(null);
        else
            photoRecode.setLocationObj(location);
        initLocation();
    }

    private void initLocation() {
        if (photoRecode.getLocationObj() == null) {
            tvLocation.setText("不显示位置");
        } else tvLocation.setText(photoRecode.getLocationObj().getArea());
    }

    @Subscribe
    public void onEvent(TimeEditPhotoDeleteEvent event) {
        int position = event.getPosition();
        String url = event.getUrl();
        List<String> data = adapter.getData();
        if (data.size() > position) {
            data.remove(position);
//            selImages.remove(position);
            adapter.setData(data);
            adapter.notifyDataSetChanged();

            for (ImgObj img : selImages) {
                if (url.equals(img.getLocalPath())) {
                    selImages.remove(img);
                }
            }
            photoRecode.setImgObjList(selImages);
        }
    }
}
