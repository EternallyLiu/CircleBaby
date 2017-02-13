package cn.timeface.circle.baby.ui.growth.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.rayboot.widget.ratioview.RatioRelativeLayout;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.SelectPhotoActivity;
import cn.timeface.circle.baby.support.api.models.objs.CardObj;
import cn.timeface.circle.baby.support.api.models.objs.ImgObj;
import cn.timeface.circle.baby.support.api.models.objs.KnowledgeCardObj;
import cn.timeface.circle.baby.support.mvp.bases.BasePresenterAppCompatActivity;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.ImageFactory;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.support.utils.Utils;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.growth.events.CardEditEvent;
import cn.timeface.circle.baby.views.ShareDialog;
import cn.timeface.circle.baby.views.dialog.TFProgressDialog;
import uk.co.senab.photoview.PhotoView;

/**
 * 卡片预览
 * author : YW.SUN Created on 2017/2/7
 * email : sunyw10@gmail.com
 */
public class CardPreviewActivity extends BasePresenterAppCompatActivity implements View.OnClickListener {

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
    @Bind(R.id.iv_select)
    ImageView ivSelect;

    private CardObj cardObj;
    private TFProgressDialog tfProgressDialog;

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
        if(cardObj != null)initView();//编辑 cardobj为null
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_card_preview, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //分享
            case R.id.action_share:
                new ShareDialog(this).share(
                        FastData.getBabyName() + "长大了",
                        FastData.getBabyName() + FastData.getBabyAge() + "了" + ",快来看看" + FastData.getBabyName() + "的新变化",
                        cardObj.getMedia().getImgUrl(),
                        getString(R.string.share_url_time, cardObj.getCardId()));
                break;

            //编辑
            case R.id.action_edit:
                if(cardObj instanceof KnowledgeCardObj){
                    RecognizeCardEditActivity.open(this, cardObj);
                } else {
                    Log.e(TAG, "只有识图卡片可以编辑");
                }
                break;

            //下载
            case R.id.action_download:
                saveImage();
                break;

            //删除
            case R.id.action_delete:
                apiService.delCard("[" + cardObj.getCardId() + "]")
                        .compose(SchedulersCompat.applyIoSchedulers())
                        .subscribe(
                                response -> {
                                    if(response.success()){
                                        finish();
                                    } else {
                                        showToast(response.getInfo());
                                    }
                                },

                                throwable -> {
                                    Log.e(TAG, "delCard:");
                                });
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void initView(){
        tvPinyin.setVisibility(View.GONE);
        etTitle.setVisibility(View.GONE);
        ivSelect.setSelected(cardObj.select());
        ivSelect.setOnClickListener(this);
        ivCard.setZoomable(false);
        Glide.with(this)
                .load(cardObj.getMedia().getImgUrl())
                .centerCrop()
                .placeholder(R.drawable.bg_default_holder_img)
                .error(R.drawable.bg_default_holder_img)
                .into(ivCard);
    }

    public void saveImage() {
        String path = cardObj.getMedia().getImgUrl();
        String fileName = path.substring(path.lastIndexOf("/"));
        String s = Environment.getExternalStorageDirectory().getAbsolutePath();
        File file = new File(s + "/baby");
        if (!file.exists()) {
            file.mkdirs();
        }
        File file1 = new File(file, fileName);
        if (file1.exists()) {
            Toast.makeText(this, "已保存到baby文件夹下", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Utils.isNetworkConnected(this)) {
            ToastUtil.showToast("网络异常");
            return;
        }
        if(tfProgressDialog == null) tfProgressDialog = TFProgressDialog.getInstance("");
        tfProgressDialog.setTvMessage("保存图片中…");
        tfProgressDialog.show(getSupportFragmentManager(), "");
        new Thread(() -> {
            ImageFactory.saveImage(path, file1);
            runOnUiThread(() -> {
                tfProgressDialog.dismiss();
                Toast.makeText(CardPreviewActivity.this, "已保存到baby文件夹下", Toast.LENGTH_SHORT).show();
            });
        }).start();
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.iv_select){
            cardObj.setSelect(cardObj.select() ? 0 : 1);
            ivSelect.setSelected(cardObj.select());
            EventBus.getDefault().post(new CardEditEvent(cardObj.getCardId(), cardObj.getSelect()));
        }
    }
}
