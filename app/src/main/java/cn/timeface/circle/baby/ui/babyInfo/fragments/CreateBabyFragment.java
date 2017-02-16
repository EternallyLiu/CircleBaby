package cn.timeface.circle.baby.ui.babyInfo.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.bumptech.glide.Glide;
import com.wechat.photopicker.PickerPhotoActivity;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.CropPicActivity;
import cn.timeface.circle.baby.activities.FragmentBridgeActivity;
import cn.timeface.circle.baby.activities.RelationshipActivity;
import cn.timeface.circle.baby.activities.TabMainActivity;
import cn.timeface.circle.baby.constants.TypeConstants;
import cn.timeface.circle.baby.fragments.base.BaseFragment;
import cn.timeface.circle.baby.support.api.models.objs.BabyObj;
import cn.timeface.circle.baby.support.api.models.objs.MyUploadFileObj;
import cn.timeface.circle.baby.support.oss.OSSManager;
import cn.timeface.circle.baby.support.oss.uploadservice.UploadFileObj;
import cn.timeface.circle.baby.support.utils.DateUtil;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.GlideUtil;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.support.utils.Utils;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.babyInfo.views.GenderDialog;
import cn.timeface.circle.baby.ui.timelines.Utils.LogUtil;
import cn.timeface.circle.baby.views.dialog.TFProgressDialog;

/**
 * author : wangshuai Created on 2017/1/20
 * email : wangs1992321@gmail.com
 */
public class CreateBabyFragment extends BaseFragment implements View.OnClickListener, GenderDialog.GenderSelectedListener {


    private static final int RELATIONSHIP = 1;
    private static final String KEY_SELECTED_PHOTO_SIZE = "SELECTED_PHOTO_SIZE";
    private static final int PICTURESELECTED = 10;
    private static final int CROP_IMG_REQUEST_CODE = 1002;
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.iv_avatar)
    ImageView ivAvatar;
    @Bind(R.id.tv_name)
    TextView tvName;
    @Bind(R.id.iv_name)
    ImageView ivName;
    @Bind(R.id.rl_name)
    RelativeLayout rlName;
    @Bind(R.id.tv_brithday)
    TextView tvBrithday;
    @Bind(R.id.iv_brithday)
    ImageView ivBrithday;
    @Bind(R.id.rl_brithday)
    RelativeLayout rlBrithday;
    @Bind(R.id.tv_gender)
    TextView tvGender;
    @Bind(R.id.iv_gender)
    ImageView ivGender;
    @Bind(R.id.rl_gender)
    RelativeLayout rlGender;
    @Bind(R.id.tv_relative)
    TextView tvRelative;
    @Bind(R.id.iv_relative)
    ImageView ivRelative;
    @Bind(R.id.rl_relative)
    RelativeLayout rlRelative;
    private GenderDialog genderDialog = null;

    private BabyObj babyObj = null;
    private int relationId;

    File selImageFile;
    File outFile;
    private TFProgressDialog tfProgressDialog = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_baby, container, false);

        ButterKnife.bind(this, view);
        setActionBar(toolbar);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }
        title.setText("创建宝宝");
        babyObj = new BabyObj();

        rlName.setOnClickListener(this);
        rlGender.setOnClickListener(this);
        rlBrithday.setOnClickListener(this);
        rlRelative.setOnClickListener(this);

        ivAvatar.setOnClickListener(this);

        GlideUtil.displayImageCircle(R.drawable.baby_avater, ivAvatar);
        tvBrithday.setText(DateUtil.getFormattedDate("yyyy-MM-dd"));
        tvGender.setText(babyObj.getGender() == 0 ? "女" : babyObj.getGender() == 1 ? "男" : "龙凤胎");
        tvName.setText("未填写");
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_complete, menu);
        MenuItem item = menu.findItem(R.id.complete);
        item.setTitle("完成");
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.complete:
                completeCreate();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void completeCreate() {
        String name = babyObj.getName();
        String birthday = tvBrithday.getText().toString().trim();
        String relation = tvRelative.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(getActivity(), "请填写宝宝小名", Toast.LENGTH_SHORT).show();
            return;
        }
        if (Utils.isHz(name)) {
            if (name.length() > 16) {
                Toast.makeText(getActivity(), "宝宝小名不能超过16个英文字母，请修改", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            if (name.length() > 8) {
                Toast.makeText(getActivity(), "宝宝小名不能超过8个汉字，请修改", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        if (TextUtils.isEmpty(birthday)) {
            Toast.makeText(getActivity(), "请填写宝宝生日", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(relation)) {
            Toast.makeText(getActivity(), "请设置与宝宝关系", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(babyObj.getAvatar())) {
            ToastUtil.showToast("给宝宝设置一个好看的头像吧~");
            return;
        }
        long time = DateUtil.getTime(birthday, "yyyy-MM-dd");
        String encode = null;
        try {
            encode = URLEncoder.encode(name, Charset.defaultCharset().name());
            apiService.createBaby(time, babyObj.getGender(), babyObj.getAvatar(), encode, relationId)
                    .compose(SchedulersCompat.applyIoSchedulers())
                    .subscribe(userLoginResponse -> {
                        if (userLoginResponse.success()) {
                            TabMainActivity.open(getActivity());
                            FastData.setUserInfo(userLoginResponse.getUserInfo());
                            getActivity().finish();
                        } else {
                            ToastUtil.showToast(userLoginResponse.getInfo());
                        }
                    }, throwable -> {
                        Log.e(TAG, "createBaby:", throwable);
                    });
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "createBaby", e);
        }
    }

    private void initRelative() {
        Intent intent = new Intent(getActivity(), RelationshipActivity.class);
        startActivityForResult(intent, RELATIONSHIP);
    }

    private void showProgress(String message) {
        if (tfProgressDialog == null)
            tfProgressDialog = new TFProgressDialog();
        if (tfProgressDialog.isHidden()) {
            tfProgressDialog.setTvMessage(message);
            tfProgressDialog.show(getFragmentManager(), "");
        }
    }

    /**
     * 设置宝宝的小名
     */
    private void initName() {
        String name = tvName.getText().toString();
        FragmentBridgeActivity.openChangeInfoFragment(this, TypeConstants.EDIT_NAME, name);
    }

    /**
     * 设置或修改生日
     */
    private void setBiethday() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                monthOfYear = monthOfYear + 1;
                tvBrithday.setText(year + "-" + monthOfYear + "-" + dayOfMonth);
                String brithday = tvBrithday.getText().toString();
                long time = DateUtil.getTime(brithday, "yyyy-MM-dd");
                babyObj.setBithday(time);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    /**
     * 设置或修改性别
     */
    private void showGender() {
        if (genderDialog == null) {
            genderDialog = new GenderDialog(getActivity());
            genderDialog.setgenderSelectedListener(this);
        }
        genderDialog.show();
    }

    /**
     * 设置头像
     */
    private void startPhotoPick() {
        Intent intent = new Intent(getActivity(), PickerPhotoActivity.class);
        intent.putExtra(KEY_SELECTED_PHOTO_SIZE, 8);
        startActivityForResult(intent, PICTURESELECTED);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.showLog(requestCode + "---" + resultCode);
        if (resultCode == Activity.RESULT_OK && data != null) {
            String input = "";
            if (data.hasExtra("data")) {
                input = data.getStringExtra("data");
            }
            switch (requestCode) {
                case PICTURESELECTED:
                    String path = data.getStringArrayListExtra(PickerPhotoActivity.KEY_SELECTED_PHOTOS).get(0);
                    selImageFile = new File(path);
//                    CropPicActivity.openForResult(getActivity(),
//                            path, 1, 1, 150, 150,
//                            CROP_IMG_REQUEST_CODE);
                    Intent intent = new Intent(getActivity(), CropPicActivity.class);
                    intent.putExtra("path", path);
                    intent.putExtra("ratio_w", 1);
                    intent.putExtra("ratio_h", 1);
                    intent.putExtra("out_w", 150);
                    intent.putExtra("out_h", 150);
                    startActivityForResult(intent, CROP_IMG_REQUEST_CODE);
                    break;
                //回调宝宝小名
                case TypeConstants.EDIT_NAME:
                    tvName.setText(input);
                    babyObj.setName(input);
                    break;
                //回调宝宝的关系
                case RELATIONSHIP:
                    relationId = data.getIntExtra("relationId", 0);
                    String relationName = data.getStringExtra("relationName");
                    tvRelative.setText(relationName);
                    break;
                case CROP_IMG_REQUEST_CODE:
                    String outPath = data.getStringExtra("crop_path");
                    if (TextUtils.isEmpty(outPath)) {
                        outFile = selImageFile;
                    } else {
                        outFile = new File(outPath);
                    }
                    GlideUtil.displayImageCircle(outFile.getAbsolutePath(),ivAvatar);
//                    Glide.with(this).load(outFile).into(ivAvatar);
//                    tvNext.setText("上传中");
//                    tvNext.setEnabled(false);
                    showProgress("上传中……");
                    uploadImage(outFile.getAbsolutePath());
                    break;
            }
        }
    }

    private void uploadImage(String path) {
        if (TextUtils.isEmpty(path)) {
            return;
        }
        OSSManager ossManager = OSSManager.getOSSManager(getActivity());
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
                        babyObj.setAvatar(uploadFileObj.getObjectKey());
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (tfProgressDialog != null && !tfProgressDialog.isHidden())
                                    tfProgressDialog.dismiss();
                            }
                        });
//                recorder.oneFileCompleted(uploadTaskInfo.getInfoId(), uploadFileObj.getObjectKey());
                    } catch (ServiceException | ClientException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {

                }
            }
        }.start();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_name:
                initName();
                break;
            case R.id.rl_brithday:
                setBiethday();
                break;
            case R.id.rl_gender:
                showGender();
                break;
            case R.id.rl_relative:
                initRelative();
                break;
            case R.id.iv_avatar:
                startPhotoPick();
                break;
        }
    }

    @Override
    public void genderSelected(int gender) {
        babyObj.setGender(gender);
        tvGender.setText(gender == 0 ? "女" : gender == 1 ? "男" : "龙凤胎");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
