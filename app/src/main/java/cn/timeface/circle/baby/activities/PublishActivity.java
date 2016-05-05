package cn.timeface.circle.baby.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;

public class PublishActivity extends BaseAppCompatActivity implements View.OnClickListener {


    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.iv_back)
    ImageView ivBack;
    @Bind(R.id.tv_photo)
    TextView tvPhoto;
    @Bind(R.id.tv_video)
    TextView tvVideo;
    @Bind(R.id.tv_diary)
    TextView tvDiary;
    @Bind(R.id.tv_card)
    TextView tvCard;
    @Bind(R.id.ll_publish_menu)
    LinearLayout llPublishMenu;

    public static void open(Context context) {
        Intent intent = new Intent(context, PublishActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ivBack.setOnClickListener(this);
        tvPhoto.setOnClickListener(this);
        tvVideo.setOnClickListener(this);
        tvDiary.setOnClickListener(this);
        tvCard.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_photo:
                break;
            case R.id.tv_video:
                break;
            case R.id.tv_diary:
                break;
            case R.id.tv_card:
                break;
        }

    }
}
