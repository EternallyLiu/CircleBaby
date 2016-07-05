package cn.timeface.circle.baby.fragments;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.google.gson.Gson;
import com.wechat.photopicker.PickerPhotoActivity;

import java.io.Serializable;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.FragmentBridgeActivity;
import cn.timeface.circle.baby.activities.TabMainActivity;
import cn.timeface.circle.baby.adapters.MessageAdapter;
import cn.timeface.circle.baby.api.models.objs.BabyObj;
import cn.timeface.circle.baby.api.models.objs.Msg;
import cn.timeface.circle.baby.api.models.objs.MyUploadFileObj;
import cn.timeface.circle.baby.api.models.objs.UserObj;
import cn.timeface.circle.baby.constants.TypeConstants;
import cn.timeface.circle.baby.fragments.base.BaseFragment;
import cn.timeface.circle.baby.oss.OSSManager;
import cn.timeface.circle.baby.oss.uploadservice.UploadFileObj;
import cn.timeface.circle.baby.utils.DateUtil;
import cn.timeface.circle.baby.utils.FastData;
import cn.timeface.circle.baby.utils.GlideUtil;
import cn.timeface.circle.baby.utils.ToastUtil;
import cn.timeface.circle.baby.utils.rxutils.SchedulersCompat;
import de.hdodenhof.circleimageview.CircleImageView;

public class BabyInfoFragment extends BaseFragment implements View.OnClickListener {

    @Bind(R.id.tv_delete)
    TextView tvDelete;
    @Bind(R.id.btn_save)
    Button btnSave;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.iv_avatar)
    CircleImageView ivAvatar;
    @Bind(R.id.tv_age)
    TextView tvAge;
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
    @Bind(R.id.rb_girl)
    RadioButton rbGirl;
    @Bind(R.id.rb_boy)
    RadioButton rbBoy;
    @Bind(R.id.rl_gender)
    RelativeLayout rlGender;
    @Bind(R.id.tv_blood)
    TextView tvBlood;
    @Bind(R.id.iv_blood)
    ImageView ivBlood;
    @Bind(R.id.rl_blood)
    RelativeLayout rlBlood;
    private MessageAdapter adapter;
    private UserObj user;
    public int gender;
    private BabyObj babyObj;
    public static final String KEY_SELECTED_PHOTO_SIZE = "SELECTED_PHOTO_SIZE";
    private final int PicutreSelcted = 10;
    private String objectKey;

    public BabyInfoFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String userObj = getArguments().getString("userObj");
        Gson gson = new Gson();
        user = gson.fromJson(userObj, UserObj.class);
        babyObj = user.getBabyObj();
        String s = babyObj.getAvatar();
        int i = s.lastIndexOf("/");
        objectKey = s.substring(i - 5);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_babyinfo, container, false);
        ButterKnife.bind(this, view);
        setActionBar(toolbar);
        getActionBar().setDisplayHomeAsUpEnabled(true);


        GlideUtil.displayImage(babyObj.getAvatar(), ivAvatar);
        tvName.setText(babyObj.getName());
        tvAge.setText(babyObj.getAge());
        tvBrithday.setText(DateUtil.getYear(babyObj.getBithday()));
        tvBlood.setText(TextUtils.isEmpty(babyObj.getBlood()) ? "未填写" : babyObj.getBlood());
        rbGirl.setChecked(babyObj.getGender() == 0);
        rbBoy.setChecked(babyObj.getGender() == 1);

        initView(user.getIsCreator() == 1);
        tvDelete.setOnClickListener(this);

        return view;
    }

    public void initView(boolean isCreator) {
        if (isCreator) {
            //创建者
            tvDelete.setText("删除");
            ivName.setVisibility(View.VISIBLE);
            ivBrithday.setVisibility(View.VISIBLE);
            ivBlood.setVisibility(View.VISIBLE);
            btnSave.setVisibility(View.VISIBLE);

            rlName.setOnClickListener(this);
            rlBrithday.setOnClickListener(this);
            rlBlood.setOnClickListener(this);
            rbGirl.setOnClickListener(this);
            rbBoy.setOnClickListener(this);
            ivAvatar.setOnClickListener(this);
            btnSave.setOnClickListener(this);

        } else {
            //关注者
            tvDelete.setText("取消关注");
            ivName.setVisibility(View.INVISIBLE);
            ivBrithday.setVisibility(View.INVISIBLE);
            rlBlood.setVisibility(View.GONE);
            rbBoy.setClickable(false);
            rbGirl.setClickable(false);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_name:
                String name = tvName.getText().toString();
                FragmentBridgeActivity.openChangeInfoFragment(this, TypeConstants.EDIT_NAME, name);
                break;
            case R.id.rl_brithday:
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog dialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        monthOfYear = monthOfYear + 1;
                        tvBrithday.setText(year + "-" + monthOfYear + "-" + dayOfMonth);
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                dialog.show();
                break;
            case R.id.rl_blood:
//                String blood = tvBlood.getText().toString();
//                FragmentBridgeActivity.openChangeInfoFragment(this, TypeConstants.EDIT_BLOOD, blood);
                CharSequence[] charSequences = new CharSequence[]{"O","A","B","AB","其他"};
                CharSequence text = tvBlood.getText();
                int index = 0;
                for (int i=0;i<charSequences.length;i++){
                    if(charSequences[i].equals(text)){
                        index = i;
                    }
                }
                new AlertDialog.Builder(getContext()).setSingleChoiceItems(charSequences, index , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        tvBlood.setText(charSequences[which]);
                    }
                }).show();
                break;
            case R.id.rb_boy:
                gender = 1;
                rbGirl.setChecked(false);
                break;
            case R.id.rb_girl:
                gender = 0;
                rbBoy.setChecked(false);
                break;
            case R.id.iv_avatar:
                startPhotoPick();
                break;
            case R.id.tv_delete:
                if (user.getIsCreator() == 1) {
                    //删除宝宝
                    new AlertDialog.Builder(getContext())
                            .setTitle("确定删除本宝宝吗?")
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            apiService.delBabyInfo(babyObj.getBabyId())
                                    .compose(SchedulersCompat.applyIoSchedulers())
                                    .subscribe(response -> {
                                        if (response.success()) {
                                            getActivity().finish();
                                        }
                                    }, throwable -> {
                                        Log.e(TAG, "delBabyInfo:", throwable);
                                    });
                        }
                    }).show();
                } else {
                    //取消关注宝宝
                    apiService.attentionCancel(babyObj.getBabyId())
                            .compose(SchedulersCompat.applyIoSchedulers())
                            .subscribe(response -> {
                                if (response.success()) {
                                    getActivity().finish();
                                }
                            }, throwable -> {
                                Log.e(TAG, "attentionCancel:", throwable);
                            });
                }

                break;
            case R.id.btn_save:
                String n = tvName.getText().toString();
                String brithday = tvBrithday.getText().toString();
                long time = DateUtil.getTime(brithday, "yyyy-MM-dd");
                String b = tvBlood.getText().toString();
                apiService.editBabyInfo(time, URLEncoder.encode(b), gender, URLEncoder.encode(n), objectKey)
                        .compose(SchedulersCompat.applyIoSchedulers())
                        .subscribe(response -> {
                            if (response.success()) {
                                FastData.setBabyName(n);
                                FastData.setBabyBithday(time);
                                FastData.setBabyBlood(b);
                                FastData.setBabyAvatar("http://img1.timeface.cn/" + objectKey);
                                FastData.setBabyGender(gender);

                                Gson gson = new Gson();
                                FastData.putString("userObj", gson.toJson(FastData.getUserInfo()));
                                getActivity().finish();
                            }
                        }, throwable -> {
                            Log.e(TAG, "editBabyInfo:", throwable);
                        });
                break;
        }
    }

    private void startPhotoPick() {
        Intent intent = new Intent(getActivity(), PickerPhotoActivity.class);
        intent.putExtra(KEY_SELECTED_PHOTO_SIZE, 8);
        intent.putExtra("local",true);
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
                case TypeConstants.EDIT_NAME:
                    tvName.setText(input);
                    break;
                case TypeConstants.EDIT_BLOOD:
                    tvBlood.setText(input);
                    break;
                case PicutreSelcted:
                    for (String path : data.getStringArrayListExtra(PickerPhotoActivity.KEY_SELECTED_PHOTOS)) {
                        GlideUtil.displayImage(path, ivAvatar);
                        uploadImage(path);
                    }
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
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
