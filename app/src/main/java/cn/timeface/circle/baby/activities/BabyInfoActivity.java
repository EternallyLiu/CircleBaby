package cn.timeface.circle.baby.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.wechat.photopicker.PickerPhotoActivity;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.net.URLEncoder;
import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.constants.TypeConstants;
import cn.timeface.circle.baby.events.HomeRefreshEvent;
import cn.timeface.circle.baby.support.api.models.objs.BabyObj;
import cn.timeface.circle.baby.support.api.models.objs.MyUploadFileObj;
import cn.timeface.circle.baby.support.api.models.objs.UserObj;
import cn.timeface.circle.baby.support.oss.OSSManager;
import cn.timeface.circle.baby.support.oss.uploadservice.UploadFileObj;
import cn.timeface.circle.baby.support.utils.DateUtil;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.GlideUtil;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import de.hdodenhof.circleimageview.CircleImageView;

public class BabyInfoActivity extends BaseAppCompatActivity implements View.OnClickListener {
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
    public int gender;
    private BabyObj babyObj;
    public static final String KEY_SELECTED_PHOTO_SIZE = "SELECTED_PHOTO_SIZE";
    private final int PicutreSelcted = 10;
    public static final int CROP_IMG_REQUEST_CODE = 1002;
    private String objectKey;
    private AlertDialog alertDialog;
    File selImageFile;
    File outFile;
    private UserObj userInfo;

    public static void open(Context context) {
        context.startActivity(new Intent(context, BabyInfoActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_babyinfo);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("宝宝信息");

        userInfo = FastData.getUserInfo();
        babyObj = userInfo.getBabyObj();

        String s = babyObj.getAvatar();
        int baby = s.indexOf("baby");
        if (baby > 0)
            objectKey = s.substring(baby);

        GlideUtil.displayImage(this.babyObj.getAvatar(), ivAvatar);
        tvName.setText(this.babyObj.getName());
        tvAge.setText(this.babyObj.getAge());
        tvBrithday.setText(DateUtil.getYear(this.babyObj.getBithday()));
        tvBlood.setText(TextUtils.isEmpty(this.babyObj.getBlood()) ? "未填写" : this.babyObj.getBlood());
        rbGirl.setChecked(this.babyObj.getGender() == 0);
        rbBoy.setChecked(this.babyObj.getGender() == 1);

        initView(userInfo.getIsCreator() == 1);
    }

    public void initView(boolean isCreator) {
        if (isCreator) {
            //创建者
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
            ivName.setVisibility(View.INVISIBLE);
            ivBrithday.setVisibility(View.INVISIBLE);
            rlBlood.setVisibility(View.GONE);
            rbBoy.setClickable(false);
            rbGirl.setClickable(false);
        }
    }


    private void startPhotoPick() {
        Intent intent = new Intent(this, PickerPhotoActivity.class);
        intent.putExtra(KEY_SELECTED_PHOTO_SIZE, 8);
        intent.putExtra("local", true);
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
                case TypeConstants.EDIT_NAME:
                    tvName.setText(input);
                    break;
                case TypeConstants.EDIT_BLOOD:
                    tvBlood.setText(input);
                    break;
                case PicutreSelcted:
                    String path = data.getStringArrayListExtra(PickerPhotoActivity.KEY_SELECTED_PHOTOS).get(0);
                    selImageFile = new File(path);
                    CropPicActivity.openForResult(this,
                            path, 1, 1, 150, 150,
                            CROP_IMG_REQUEST_CODE);
                    break;
                case CROP_IMG_REQUEST_CODE:
                    String outPath = data.getStringExtra("crop_path");
                    if (TextUtils.isEmpty(outPath)) {
                        outFile = selImageFile;
                    } else {
                        outFile = new File(outPath);
                    }
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
        btnSave.setEnabled(false);
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
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                btnSave.setEnabled(true);
                            }
                        });
                    } catch (ServiceException | ClientException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {

                }
            }
        }.start();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
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
                DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        monthOfYear = monthOfYear + 1;
                        tvBrithday.setText(year + "-" + monthOfYear + "-" + dayOfMonth);
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                dialog.show();
                break;
            case R.id.rl_blood:
                CharSequence[] charSequences = new CharSequence[]{"O", "A", "B", "AB", "其他"};
                CharSequence text = tvBlood.getText();
                int index = 0;
                for (int i = 0; i < charSequences.length; i++) {
                    if (charSequences[i].equals(text)) {
                        index = i;
                    }
                }
                new AlertDialog.Builder(this).setSingleChoiceItems(charSequences, index, new DialogInterface.OnClickListener() {
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
            case R.id.btn_save:
                String n = tvName.getText().toString();
                String brithday = tvBrithday.getText().toString();
                long time = DateUtil.getTime(brithday, "yyyy-MM-dd");
                String b = tvBlood.getText().toString();
                btnSave.setEnabled(false);
                apiService.editBabyInfo(time, URLEncoder.encode(b), gender, URLEncoder.encode(n), objectKey)
                        .compose(SchedulersCompat.applyIoSchedulers())
                        .subscribe(response -> {
                            if (response.success()) {
                                apiService.queryBabyInfoDetail(babyObj.getBabyId())
                                        .compose(SchedulersCompat.applyIoSchedulers())
                                        .subscribe(babyInfoResponse -> {
                                            if (babyInfoResponse.success()) {
                                                babyObj = babyInfoResponse.getBabyInfo();
                                                FastData.setBabyObj(babyObj);
                                                EventBus.getDefault().post(new HomeRefreshEvent());
                                                finish();
                                            }
                                        }, throwable -> {
                                            Log.e(TAG, "queryBabyInfoDetail:", throwable);
                                        });
                            }
                            btnSave.setEnabled(true);
                        }, throwable -> {
                            Log.e(TAG, "editBabyInfo:", throwable);
                        });
                break;
            case R.id.btn_cancel:
                alertDialog.dismiss();
                break;
            case R.id.btn_ok:
                alertDialog.dismiss();
                //取消关注宝宝
                apiService.attentionCancel(babyObj.getBabyId())
                        .compose(SchedulersCompat.applyIoSchedulers())
                        .subscribe(response -> {
                            if (response.success()) {
                                FastData.setBabyId(0);
                                ChangeBabyActivity.open(this);
                                finish();
                            } else {
                                ToastUtil.showToast(response.getInfo());
                            }
                        }, throwable -> {
                            Log.e(TAG, "attentionCancel:", throwable);
                        });
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_complete, menu);
        MenuItem item = menu.findItem(R.id.complete);
        if (userInfo.getIsCreator() == 1) {
            item.setTitle("删除");
        } else {
            item.setTitle("取消关注");
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.complete) {
            if (userInfo.getIsCreator() == 1) {
                //删除宝宝
                new AlertDialog.Builder(this)
                        .setTitle("确定删除宝宝 " + babyObj.getName() + " 吗?")
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
                                        FastData.setBabyId(0);
                                        ChangeBabyActivity.open(BabyInfoActivity.this);
                                        finish();
                                    } else {
                                        ToastUtil.showToast(response.getInfo());
                                    }
                                }, throwable -> {
                                    Log.e(TAG, "delBabyInfo:", throwable);
                                });
                    }
                }).show();
            } else {
                alertDialog = new AlertDialog.Builder(this).setView(initUnFocusView()).show();
                alertDialog.setCanceledOnTouchOutside(false);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private View initUnFocusView() {
        View view = View.inflate(this, R.layout.view_dialog, null);
        TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);
        TextView tvMsg = (TextView) view.findViewById(R.id.tv_msg);
        Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);
        Button btnOk = (Button) view.findViewById(R.id.btn_ok);

        tvMsg.setText("你确定不再关注宝宝 " + FastData.getBabyName() + " 吗?这会导致你不能继续查看宝宝相关内容。");
        btnCancel.setOnClickListener(this);
        btnOk.setOnClickListener(this);
        return view;
    }
}
