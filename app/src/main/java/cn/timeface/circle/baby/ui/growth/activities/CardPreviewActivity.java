package cn.timeface.circle.baby.ui.growth.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.rayboot.widget.ratioview.RatioRelativeLayout;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.support.api.models.objs.CardObj;
import cn.timeface.circle.baby.support.mvp.bases.BasePresenterAppCompatActivity;
import cn.timeface.circle.baby.support.utils.FastData;
import uk.co.senab.photoview.PhotoView;

/**
 * 卡片预览和编辑
 * author : YW.SUN Created on 2017/2/7
 * email : sunyw10@gmail.com
 */
public class CardPreviewActivity extends BasePresenterAppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.tv_pinyin)
    TextView tvPinyin;
    @Bind(R.id.et_title)
    EditText etTitle;
    @Bind(R.id.iv_card)
    PhotoView ivCard;
    @Bind(R.id.rl_card)
    RatioRelativeLayout rlCard;
    @Bind(R.id.content_card_preview)
    RelativeLayout contentCardPreview;

    CardObj cardObj;

    public static void open(Context context, CardObj cardObj){
        Intent intent = new Intent(context, CardPreviewActivity.class);
        intent.putExtra("card", cardObj);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_preview);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(FastData.getBabyName() + "的识图卡片");
        cardObj = getIntent().getParcelableExtra("card");
        initView();
    }

    private void initView(){
        tvPinyin.setVisibility(View.GONE);
        etTitle.setVisibility(View.GONE);
        ivCard.setZoomable(false);
        Glide.with(this)
                .load(cardObj.getMedia().getImgUrl())
                .centerCrop()
                .placeholder(R.drawable.bg_default_holder_img)
                .error(R.drawable.bg_default_holder_img)
                .into(ivCard);
    }
}
