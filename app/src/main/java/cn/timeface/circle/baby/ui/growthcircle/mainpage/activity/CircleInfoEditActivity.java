package cn.timeface.circle.baby.ui.growthcircle.mainpage.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.github.rayboot.widget.ratioview.RatioImageView;
import com.google.gson.Gson;
import com.wechat.photopicker.PickerPhotoActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.CropPicActivity;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.support.api.models.objs.MyUploadFileObj;
import cn.timeface.circle.baby.support.api.services.ApiService;
import cn.timeface.circle.baby.support.managers.listeners.IEventBus;
import cn.timeface.circle.baby.support.oss.OSSManager;
import cn.timeface.circle.baby.support.oss.uploadservice.UploadFileObj;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.GlideUtil;
import cn.timeface.circle.baby.support.utils.network.NetworkError;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.circle.bean.GrowthCircleDetailObj;
import cn.timeface.circle.baby.ui.growthcircle.mainpage.event.CircleChangedEvent;
import cn.timeface.circle.baby.ui.growthcircle.mainpage.event.CircleCoverSelectedEvent;
import cn.timeface.circle.baby.views.dialog.TFProgressDialog;
import rx.Observable;
import rx.Subscription;

/**
 * 圈资料
 */
public class CircleInfoEditActivity extends BaseAppCompatActivity implements IEventBus {

    private static final int CIRCLE_INTRO_MAX_COUNT = 80;
    private static final int CIRCLE_NAME_MAX_COUNT = 16;
    private static final int CIRCLE_RULE_MAX_COUNT = 200;

    private static final int REQUEST_CODE_SELECT_WAY = 1001;
    private static final int REQUEST_CODE_SELECT_PHOTO = 1002;
    private static final int REQUEST_CODE_CROP = 1003;
    private static final int REQUEST_CODE_RECOMMEND = 1004;

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.iv_circle_cover)
    RatioImageView ivCircleCover;
    @Bind(R.id.et_circle_intro)
    EditText etCircleIntro;
    @Bind(R.id.tv_circle_intro_count)
    TextView tvCircleIntroCount;
    @Bind(R.id.et_circle_name)
    EditText etCircleName;
    @Bind(R.id.tv_circle_name_count)
    TextView tvCircleNameCount;
    @Bind(R.id.rb_publish)
    RadioButton rbPublish;
    @Bind(R.id.rb_private)
    RadioButton rbPrivate;
    @Bind(R.id.rg_publish)
    RadioGroup rgPublish;
    @Bind(R.id.et_circle_rule)
    EditText etCircleRule;
    @Bind(R.id.tv_circle_rule_count)
    TextView tvCircleRuleCount;

    private GrowthCircleDetailObj circleDetailObj;

    private TFProgressDialog progressDialog;

    private File selImageFile;
    private File outFile;

    public static void open(Context context, GrowthCircleDetailObj circleDetailObj) {
        Intent intent = new Intent(context, CircleInfoEditActivity.class);
        intent.putExtra("circle_obj", circleDetailObj);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_info_edit);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        circleDetailObj = getIntent().getParcelableExtra("circle_obj");

        if (circleDetailObj != null) {
            setupData(circleDetailObj);
        } else {
            finish();
        }
    }

    private void setupData(GrowthCircleDetailObj circleDetailObj) {
        GlideUtil.displayImage(circleDetailObj.getCircleCoverUrl(), ivCircleCover);

        setupEditor(etCircleIntro, circleDetailObj.getCircleDescription(),
                tvCircleIntroCount, CIRCLE_INTRO_MAX_COUNT);

        setupEditor(etCircleName, circleDetailObj.getCircleName(),
                tvCircleNameCount, CIRCLE_NAME_MAX_COUNT);

        setupEditor(etCircleRule, circleDetailObj.getRule(),
                tvCircleRuleCount, CIRCLE_RULE_MAX_COUNT);

        rgPublish.check(circleDetailObj.isPublic() ? R.id.rb_publish : R.id.rb_private);
    }

    private void setupEditor(EditText et, String content, TextView tv, int maxCount) {
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                tv.setText(s.length() + "/" + maxCount);
            }
        });

        if (!TextUtils.isEmpty(content)) {
            et.setText(content);
        }

        // 解决ScrollView中嵌套多行EditText不能上下滑动的问题
        et.setOnTouchListener((v, event) -> {
            v.getParent().requestDisallowInterceptTouchEvent(true);
            return false;
        });
    }

    @OnClick({R.id.iv_circle_cover, R.id.tv_submit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_circle_cover:
                CircleInfoSelectCoverActivity.openForResult(this, REQUEST_CODE_SELECT_WAY);
                break;
            case R.id.tv_submit:
                if (checkData()) {
                    showProgressDialog();
                    reqUpdateCircleInfo();
                }
                break;
        }
    }

    private boolean checkData() {
        if (TextUtils.isEmpty(etCircleName.getText())) {
            Toast.makeText(this, "请输入圈子名称", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void reqUpdateCircleInfo() {
        circleDetailObj.setCircleId(FastData.getCircleId());
        circleDetailObj.setCircleDescription(etCircleIntro.getText().toString());
        circleDetailObj.setCircleName(etCircleName.getText().toString());
        circleDetailObj.setRule(etCircleRule.getText().toString());
        circleDetailObj.setCirclePublic(rgPublish.getCheckedRadioButtonId() == R.id.rb_publish);

        // 精简数据，这个接口用不到这俩字段
        //circleDetailObj.setBabyList(null);
        circleDetailObj.setMemberList(null);

        String circleDetailInfo = new Gson().toJson(circleDetailObj);
        Subscription s = apiService.editCircleInfo(Uri.encode(circleDetailInfo))
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(
                        response -> {
                            dismissProgressDialog();
                            Toast.makeText(this, response.info, Toast.LENGTH_SHORT).show();
                            if (response.success()) {
                                EventBus.getDefault().post(
                                        new CircleChangedEvent(circleDetailObj.getCircleId(),
                                                CircleChangedEvent.TYPE_INFO_CHANGED)
                                );
                                finish();
                            }
                        },
                        throwable -> {
                            dismissProgressDialog();
                            NetworkError.showException(this, throwable);
                        }
                );
        addSubscription(s);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            switch (requestCode) {
                case REQUEST_CODE_SELECT_WAY:
                    int way = data.getIntExtra(CircleInfoSelectCoverActivity.EXTRA_DATA, 0);
                    if (way == CircleInfoSelectCoverActivity.SELECTED_ALBUM) {
                        Intent intent = new Intent(this, PickerPhotoActivity.class);
                        intent.putExtra("SELECTED_PHOTO_SIZE", 8);
                        startActivityForResult(intent, REQUEST_CODE_SELECT_PHOTO);
                    } else if (way == CircleInfoSelectCoverActivity.SELECTED_RECOMMEND) {
                        CircleInfoRecommendCoverActivity.open(this);
                    }
                    break;
                case REQUEST_CODE_SELECT_PHOTO:
                    String path = data.getStringArrayListExtra(PickerPhotoActivity.KEY_SELECTED_PHOTOS).get(0);
                    selImageFile = new File(path);
                    CropPicActivity.openForResult(this, path, 640, 334, 640, 334, REQUEST_CODE_CROP);
                    break;
                case REQUEST_CODE_CROP:
                    String outPath = data.getStringExtra("crop_path");
                    if (TextUtils.isEmpty(outPath)) {
                        outFile = selImageFile;
                    } else {
                        outFile = new File(outPath);
                    }
                    uploadImage(outFile.getAbsolutePath());
                    break;
                case REQUEST_CODE_RECOMMEND:

                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showProgressDialog() {
        showProgressDialog(getString(R.string.loading));
    }

    private void showProgressDialog(String msg) {
        if (progressDialog == null) {
            progressDialog = TFProgressDialog.getInstance();
        }
        progressDialog.setTvMessage(msg);
        progressDialog.show(getSupportFragmentManager(), "ProgressDialog");
    }

    private void dismissProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    private void uploadImage(String path) {
        if (TextUtils.isEmpty(path)) {
            return;
        }
        showProgressDialog("正在上传封面...");

        Subscription s = Observable.just(path)
                .flatMap(imagePath -> {
                    OSSManager ossManager = OSSManager.getOSSManager(this);
                    //获取上传文件
                    UploadFileObj uploadFileObj = new MyUploadFileObj(imagePath);
                    try {
                        //判断服务器是否已存在该文件
                        if (!ossManager.checkFileExist(uploadFileObj.getObjectKey())) {
                            //如果不存在则上传
                            ossManager.upload(uploadFileObj.getObjectKey(), uploadFileObj.getFinalUploadFile().getAbsolutePath());
                        }
                    } catch (ClientException | ServiceException e) {
                        e.printStackTrace();
                        return Observable.error(e);
                    }
                    return Observable.just(uploadFileObj);
                })
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(
                        uploadFileObj -> {
                            dismissProgressDialog();
                            Toast.makeText(this, "封面上传成功", Toast.LENGTH_SHORT).show();
                            updateCoverUrl(ApiService.IMAGE_BASE_URL + uploadFileObj.getObjectKey());
                        },
                        throwable -> {
                            dismissProgressDialog();
                            Toast.makeText(this, "封面上传失败", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "uploadImage: ", throwable);
                        }
                );
        addSubscription(s);
    }

    private void updateCoverUrl(String coverUrl) {
        circleDetailObj.setCircleCoverUrl(coverUrl);
        GlideUtil.displayImage(circleDetailObj.getCircleCoverUrl(), ivCircleCover);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(CircleCoverSelectedEvent event) {
        if (!TextUtils.isEmpty(event.coverUrl)) {
            updateCoverUrl(event.coverUrl);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

}
