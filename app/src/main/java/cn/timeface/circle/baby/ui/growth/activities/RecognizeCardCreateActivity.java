package cn.timeface.circle.baby.ui.growth.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.FragmentBridgeActivity;
import cn.timeface.circle.baby.activities.PublishActivity;
import cn.timeface.circle.baby.activities.SelectPhotoActivity;
import cn.timeface.circle.baby.events.PublishRefreshEvent;
import cn.timeface.circle.baby.support.api.models.objs.ImgObj;
import cn.timeface.circle.baby.support.mvp.bases.BasePresenterAppCompatActivity;
import cn.timeface.circle.baby.support.utils.ToastUtil;

/**
 * 制作识图卡片
 * author : YW.SUN Created on 2017/2/13
 * email : sunyw10@gmail.com
 */
public class RecognizeCardCreateActivity extends BasePresenterAppCompatActivity implements View.OnClickListener {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.iv_add)
    ImageView ivAdd;

    private final int REQUEST_CODE_SELECT_IMAGE = 100;

    public static void open(Context context) {
        Intent intent = new Intent(context, RecognizeCardCreateActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognize_card_create);
        ButterKnife.bind(this);
        ivAdd.setOnClickListener(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_complete, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.home) {
            onBackPressed();
        } else if (item.getItemId() == R.id.complete) {}
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.iv_add){
            SelectPhotoActivity.openForResult(this, new ArrayList<>(), 1, REQUEST_CODE_SELECT_IMAGE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode == RESULT_OK && data != null){
            //跳转识图卡片预览界面
            FragmentBridgeActivity.openCardPreviewFragment(this, (ImgObj) data.getParcelableArrayListExtra("result_select_image_list").get(0));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}