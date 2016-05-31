package cn.timeface.circle.baby.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.api.models.objs.ImgObj;
import cn.timeface.circle.baby.api.models.objs.MediaObj;
import cn.timeface.circle.baby.events.MediaObjEvent;
import cn.timeface.circle.baby.managers.listeners.IEventBus;
import cn.timeface.circle.baby.utils.DateUtil;
import cn.timeface.circle.baby.utils.GlideUtil;
import cn.timeface.circle.baby.utils.Remember;
import cn.timeface.circle.baby.utils.ToastUtil;
import de.greenrobot.event.Subscribe;

public class CardPublishActivity extends BaseAppCompatActivity implements View.OnClickListener , IEventBus{


    protected final int PHOTO_COUNT_MAX = 1;


    public final int PICTURE = 0;
    @Bind(R.id.tv_back)
    TextView tvBack;
    @Bind(R.id.tv_publish)
    TextView tvPublish;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.iv_add)
    ImageView ivAdd;
    @Bind(R.id.tv_add)
    TextView tvAdd;
    private List<ImgObj> selImages = new ArrayList<>();

    public static void open(Context context) {
        Intent intent = new Intent(context, CardPublishActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        tvBack.setOnClickListener(this);
        tvPublish.setOnClickListener(this);
        ivAdd.setOnClickListener(this);
        tvAdd.setOnClickListener(this);


//        selectImages();
    }

    private void selectImages() {
        PhotoSelectionActivity.openForResult(
                this,
                "选择照片",
                selImages,
                PHOTO_COUNT_MAX,
                false,
                PICTURE, false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case PICTURE:
                    selImages = data.getParcelableArrayListExtra("result_select_image_list");
                    if (selImages.size() > 0) {
                        //跳转识图卡片预览界面
                        FragmentBridgeActivity.openCardPreviewFragment(this, selImages.get(0));
                    }
                    break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_back:
                finish();
                break;
            case R.id.tv_publish:
                break;
            case R.id.iv_add:
                selectImages();
                break;
            case R.id.tv_add:

                break;

        }

    }
    @Subscribe
    public void onEvent(Object event) {
        if(event instanceof MediaObjEvent){
            finish();
        }
    }

}
