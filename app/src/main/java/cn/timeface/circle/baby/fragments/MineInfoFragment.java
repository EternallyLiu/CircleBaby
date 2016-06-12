package cn.timeface.circle.baby.fragments;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.wechat.photopicker.PickerPhotoActivity;

import java.net.URLEncoder;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.FragmentBridgeActivity;
import cn.timeface.circle.baby.activities.LoginActivity;
import cn.timeface.circle.baby.activities.SetPasswordActivity;
import cn.timeface.circle.baby.api.models.objs.MyUploadFileObj;
import cn.timeface.circle.baby.constants.TypeConstant;
import cn.timeface.circle.baby.constants.TypeConstants;
import cn.timeface.circle.baby.fragments.base.BaseFragment;
import cn.timeface.circle.baby.oss.OSSManager;
import cn.timeface.circle.baby.oss.uploadservice.UploadFileObj;
import cn.timeface.circle.baby.utils.FastData;
import cn.timeface.circle.baby.utils.GlideUtil;
import cn.timeface.circle.baby.utils.ToastUtil;
import cn.timeface.circle.baby.utils.rxutils.SchedulersCompat;
import de.hdodenhof.circleimageview.CircleImageView;

public class MineInfoFragment extends BaseFragment implements View.OnClickListener {


    @Bind(R.id.tv_mine)
    TextView tvMine;
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

    public MineInfoFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mineinfo, container, false);
        ButterKnife.bind(this, view);
        setActionBar(toolbar);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        initData();
        rlAvtar.setOnClickListener(this);
        rlNickname.setOnClickListener(this);
        rlChangepsw.setOnClickListener(this);
        return view;
    }

    private void initData() {
        GlideUtil.displayImage(FastData.getAvatar(), ivAvatar);
        tvNickname.setText(FastData.getUserName());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_avtar:
                startPhotoPick();
                break;
            case R.id.rl_nickname:
                FragmentBridgeActivity.openChangeInfoFragment(this,TypeConstants.EDIT_NICKNAME,tvNickname.getText().toString());
                break;
            case R.id.rl_changepsw:
                SetPasswordActivity.open(getActivity(), FastData.getAccount());
                break;

        }
    }

    private void startPhotoPick() {
        Intent intent = new Intent(getActivity(), PickerPhotoActivity.class);
        intent.putExtra("SELECTED_PHOTO_SIZE", 8);
        startActivityForResult(intent, PicutreSelcted);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            String input = "";
            if (data.hasExtra("data")) {
                input = data.getStringExtra("data");
            }
            switch (requestCode) {
                case PicutreSelcted:
                    for (String path : data.getStringArrayListExtra(PickerPhotoActivity.KEY_SELECTED_PHOTOS)) {
                        GlideUtil.displayImage(path, ivAvatar);
                        uploadImage(path);
                    }
                    break;
                case TypeConstants.EDIT_NICKNAME:
                    tvNickname.setText(input);
                    changeInfo();
                    break;
            }
        }
    }
    private void uploadImage(String path) {
        if (TextUtils.isEmpty(path)) {
            return;
        }
        OSSManager ossManager = OSSManager.getOSSManager(getContext());
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
                    if(userLoginResponse.success()){
                        FastData.setUserName(userLoginResponse.getUserInfo().getNickName());
                        FastData.setAvatar(userLoginResponse.getUserInfo().getAvatar());
                    }else{
                        ToastUtil.showToast(userLoginResponse.getInfo());
                    }
                }, throwable -> {
                    Log.e(TAG, "profile:");
                });
    }
}
