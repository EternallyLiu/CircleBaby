package cn.timeface.circle.baby.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.api.models.PhotoRecode;
import cn.timeface.circle.baby.api.models.objs.ImgObj;
import cn.timeface.circle.baby.api.models.objs.Milestone;
import cn.timeface.circle.baby.utils.GlideUtil;
import cn.timeface.circle.baby.views.NoScrollGridView;

public class PhotoRecodeDetailActivity extends BaseAppCompatActivity implements View.OnClickListener {

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
    private PhotoGridAdapter adapter;
    private HashSet<String> imageUrls = new HashSet<>();
    private final int PICTURE = 0;
    private final int MILESTONE = 1;
    private final int TIME = 2;
    private Milestone milestone;
    private List<ImgObj> selImages = new ArrayList<>();
    private int position;
    private PhotoRecode photoRecode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_record_detail);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        photoRecodes = getIntent().getParcelableArrayListExtra("list_photorecord");
//        Bundle bundle = getIntent().getExtras();
//         photoRecode = (PhotoRecode) bundle.getSerializable("photoRecode");
//        position = bundle.getInt("position");
        photoRecode =  getIntent().getParcelableExtra("photoRecode");
        position = getIntent().getIntExtra("position", 0);
        selImages = this.photoRecode.getImgObjList();
        for (ImgObj item : selImages) {
            imageUrls.add(item.getLocalPath());
        }
        adapter = new PhotoGridAdapter(this);
        adapter.getData().addAll(imageUrls);
        gvGridView.setAdapter(adapter);
        gvGridView.setOnItemClickListener((parent, v, position, id) -> {
            if (position == 0) {
                selectImages();

            } else {
//                int relPosition = position - 1;
//                imageUrls.remove(adapter.getData().get(relPosition));
//                adapter.getData().remove(relPosition);
//                adapter.notifyDataSetChanged();
            }
        });

        tvNext.setOnClickListener(this);
        rlMileStone.setOnClickListener(this);
        rlTime.setOnClickListener(this);

        etContent.setText(this.photoRecode.getContent());
        if (this.photoRecode.getMileStone() != null)
            tvMileStone.setText(this.photoRecode.getMileStone().getMilestone());
        tvTime.setText(this.photoRecode.getTitle());

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
//                    List<String> images = data.getStringArrayListExtra(PickerPhotoActivity.KEY_SELECTED_PHOTOS);
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
                    milestone = (Milestone) data.getSerializableExtra("milestone");
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
            case R.id.tv_next:
                postRecord();
                break;
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

    private void postRecord() {
        String value = etContent.getText().toString();

        photoRecode.setContent(value);

        if (value.length() < 1 && imageUrls.size() < 1) {
            Toast.makeText(this, "发点文字或图片吧", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent();
//        Bundle bundle = new Bundle();
//        bundle.putSerializable("photoRecode",photoRecode);
//        bundle.putInt("position", position);
//        intent.putExtras(bundle);
//        intent.putParcelableArrayListExtra("result_photo_record_detail", (ArrayList<? extends Parcelable>) photoRecodes);
        intent.putExtra("photoRecode", (Serializable) photoRecode);
        intent.putExtra("position", position);
        setResult(RESULT_OK, intent);
        finish();
    }


    private static class PhotoGridAdapter extends BaseAdapter {

        private static final int TYPE_HEADER = 1;
        private static final int TYPE_BODY = 2;
        private Context context;

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
            return position == 0 ? TYPE_HEADER : TYPE_BODY;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            if (getItemViewType(position) == TYPE_HEADER) {
                view = View.inflate(context, R.layout.item_record_add_photo, null);
            } else {
                view = View.inflate(context, R.layout.item_record_photo, null);
                ImageView imageView = (ImageView) view.findViewById(R.id.iv_cover);
                GlideUtil.displayImage(data.get(position - 1), imageView);
            }
            return view;
        }
    }
}
