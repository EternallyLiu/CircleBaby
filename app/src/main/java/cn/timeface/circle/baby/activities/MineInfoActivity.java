package cn.timeface.circle.baby.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.wechat.photopicker.PickerPhotoActivity;

import java.io.File;
import java.net.URLEncoder;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.constants.TypeConstants;
import cn.timeface.circle.baby.support.api.models.objs.MyUploadFileObj;
import cn.timeface.circle.baby.support.oss.OSSManager;
import cn.timeface.circle.baby.support.oss.uploadservice.UploadFileObj;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.GlideUtil;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.views.dialog.TFProgressDialog;
import de.hdodenhof.circleimageview.CircleImageView;

public class MineInfoActivity extends BaseAppCompatActivity implements View.OnClickListener {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.iv_avatar)
    CircleImageView ivAvatar;
    @Bind(R.id.iv_frist)
    ImageView ivFrist;
    @Bind(R.id.rl_avtar)
    RelativeLayout rlAvtar;
    @Bind(R.id.tv_nickname)
    TextView tvNickname;
    @Bind(R.id.iv_nickname)
    ImageView ivNickname;
    @Bind(R.id.rl_nickname)
    RelativeLayout rlNickname;
    @Bind(R.id.rl_changepsw)
    RelativeLayout rlChangepsw;
    private final int PicutreSelcted = 10;
    private String objectKey;
    public static final int CROP_IMG_REQUEST_CODE = 1002;
    File selImageFile;
    File outFile;
    private TFProgressDialog tfProgressDialog;

    public static void open(Context context) {
        context.startActivity(new Intent(context, MineInfoActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_mineinfo);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("我的资料");
        tfProgressDialog = new TFProgressDialog(this);
        initData();
        rlAvtar.setOnClickListener(this);
        rlNickname.setOnClickListener(this);
        rlChangepsw.setOnClickListener(this);
    }

    private void initData() {
        GlideUtil.displayImage(FastData.getAvatar(), ivAvatar, R.drawable.ic_launcher);
        tvNickname.setText(FastData.getUserName());
        if (FastData.getUserFrom() == TypeConstants.USER_FROM_LOCAL) {
            rlChangepsw.setVisibility(View.VISIBLE);
        } else {
            rlChangepsw.setVisibility(View.GONE);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_avtar:
                startPhotoPick();
                break;
            case R.id.rl_nickname:
                FragmentBridgeActivity.openChangeInfoFragment(this, TypeConstants.EDIT_NICKNAME, tvNickname.getText().toString());
                break;
            case R.id.rl_changepsw:
                SetPasswordActivity.open(this, FastData.getAccount());
                break;

        }
    }

    private void startPhotoPick() {
        Intent intent = new Intent(this, PickerPhotoActivity.class);
        intent.putExtra("SELECTED_PHOTO_SIZE", 8);
        startActivityForResult(intent, PicutreSelcted);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            String input = "";
            if (data.hasExtra("data")) {
                input = data.getStringExtra("data");
            }
            switch (requestCode) {
                case PicutreSelcted:
                    String path = data.getStringArrayListExtra(PickerPhotoActivity.KEY_SELECTED_PHOTOS).get(0);
                    selImageFile = new File(path);
                    CropPicActivity.openForResult(this,
                            path, 1, 1, 150, 150,
                            CROP_IMG_REQUEST_CODE);
                    break;
                case TypeConstants.EDIT_NICKNAME:
                    tvNickname.setText(input);
                    tfProgressDialog.setMessage("加载中…");
                    tfProgressDialog.show();
                    changeInfo();
                    break;
                case CROP_IMG_REQUEST_CODE:
                    String outPath = data.getStringExtra("crop_path");
                    if (TextUtils.isEmpty(outPath)) {
                        outFile = selImageFile;
                    } else {
                        outFile = new File(outPath);
                    }
                    tfProgressDialog.setMessage("加载中…");
                    tfProgressDialog.show();
                    GlideUtil.displayImage(outFile.getAbsolutePath(), ivAvatar);
                    uploadImage(outFile.getAbsolutePath());
                    break;
            }
        }
    }

    private void uploadImage(String path) {
        if (TextUtils.isEmpty(path)) {
            return;
        }
        OSSManager ossManager = OSSManager.getOSSManager(this);
        new Thread() {
            @Override
            public void run() {
                try {
                    //获取上传文件
                    UploadFileObj uploadFileObj = new MyUploadFileObj(path);
                    //上传操作
                    try {
                        //判断服务器是否已存在该文件
                        if (!ossManager.checkFileExist(uploadFileObj.getObjectKey())) {
                            //如果不存在则上传
                            ossManager.upload(uploadFileObj.getObjectKey(), uploadFileObj.getFinalUploadFile().getAbsolutePath());
                        }
                        objectKey = uploadFileObj.getObjectKey();
                        changeInfo();
//                recorder.oneFileCompleted(uploadTaskInfo.getInfoId(), uploadFileObj.getObjectKey());
                    } catch (ServiceException | ClientException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {

                }
            }
        }.start();
    }

    public void changeInfo() {
        apiService.profile(URLEncoder.encode(tvNickname.getText().toString()), objectKey)
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(userLoginResponse -> {
                    tfProgressDialog.dismiss();
                    if (userLoginResponse.success()) {
                        FastData.setUserName(userLoginResponse.getUserInfo().getNickName());
                        FastData.setAvatar(userLoginResponse.getUserInfo().getAvatar());
                    } else {
                        ToastUtil.showToast(userLoginResponse.getInfo());
                    }
                }, throwable -> {
                    tfProgressDialog.dismiss();
                    Log.e(TAG, "profile:");
                });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
