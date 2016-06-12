package cn.timeface.circle.baby.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class DiaryPublishActivity extends BaseAppCompatActivity implements View.OnClickListener , IEventBus{


    protected final int PHOTO_COUNT_MAX = 1;

    public final int PICTURE = 0;
    public final int DIARYTEXT = 1;
    @Bind(R.id.tv_next)
    TextView tvNext;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.tv_time)
    TextView tvTime;
    @Bind(R.id.iv_diary)
    ImageView ivDiary;
    @Bind(R.id.tv_content)
    TextView tvContent;
    @Bind(R.id.ll_single_date)
    LinearLayout llSingleDate;
    private List<ImgObj> selImages = new ArrayList<>();

    public static void open(Context context) {
        Intent intent = new Intent(context, DiaryPublishActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diarypublish);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tvTime.setText(DateUtil.getYear2(System.currentTimeMillis()));

        int width = Remember.getInt("width",0)*3;
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) ivDiary.getLayoutParams();
        layoutParams.height = width/2;
        layoutParams.width = width/2;
        ivDiary.setLayoutParams(layoutParams);


        tvTime.setOnClickListener(this);
        tvNext.setOnClickListener(this);
        ivDiary.setOnClickListener(this);
        tvContent.setOnClickListener(this);


        selectImages();
    }

    private void selectImages() {
        SelectPhotoActivity.openForResult(this,selImages,PHOTO_COUNT_MAX,PICTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case PICTURE:
                    selImages = data.getParcelableArrayListExtra("result_select_image_list");
                    if (selImages.size() > 0) {
                        String localPath = selImages.get(0).getLocalPath();
                        GlideUtil.displayImage(localPath, ivDiary);
                    }
                    break;
                case DIARYTEXT:
                    String content = data.getStringExtra("content");
                    tvContent.setText(content);
                    break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_next:
                String content = tvContent.getText().toString();
                String time = tvTime.getText().toString();
                if (selImages.size() < 1) {
                    ToastUtil.showToast("请选择一张图片");
                    return;
                }
                if (TextUtils.isEmpty(content)) {
                    ToastUtil.showToast("记录宝宝今天的成长吧~");
                    return;
                }
                //跳转到预览界面
                ImgObj imgObj = selImages.get(0);
                FragmentBridgeActivity.openDiaryPreviewFragment(this, time, content, imgObj);

                break;
            case R.id.tv_time:
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        monthOfYear = monthOfYear + 1;
                        tvTime.setText(year + "." + monthOfYear + "." + dayOfMonth);
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                dialog.show();
                break;
            case R.id.iv_diary:
                selectImages();
                break;
            case R.id.tv_content:
                FragmentBridgeActivity.openForResult(this, "DiaryTextFragment", DIARYTEXT);
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
