package cn.timeface.circle.baby.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URLEncoder;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.utils.FastData;
import cn.timeface.circle.baby.utils.encode.AES;
import cn.timeface.circle.baby.utils.rxutils.SchedulersCompat;
import rx.Subscription;

public class SelectPublishActivity extends BaseAppCompatActivity implements View.OnClickListener {


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
        Intent intent = new Intent(context, SelectPublishActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectpublish);
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
                PublishActivity.open(this , PublishActivity.PHOTO);
                break;
            case R.id.tv_video:
                PublishActivity.open(this , PublishActivity.VIDEO);
                break;
            case R.id.tv_diary:
                PublishActivity.open(this , PublishActivity.DIALY);
                break;
            case R.id.tv_card:
                PublishActivity.open(this , PublishActivity.CARD);
                break;
        }

    }
}
