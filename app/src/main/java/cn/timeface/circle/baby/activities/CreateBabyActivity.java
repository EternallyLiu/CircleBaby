package cn.timeface.circle.baby.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.wechat.photopicker.PickerPhotoActivity;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;
import java.net.URLEncoder;
import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.api.models.objs.MyUploadFileObj;
import cn.timeface.circle.baby.api.models.objs.Relationship;
import cn.timeface.circle.baby.oss.OSSManager;
import cn.timeface.circle.baby.oss.uploadservice.UploadFileObj;
import cn.timeface.circle.baby.utils.DateUtil;
import cn.timeface.circle.baby.utils.FastData;
import cn.timeface.circle.baby.utils.GlideUtil;
import cn.timeface.circle.baby.utils.ToastUtil;
import cn.timeface.circle.baby.utils.rxutils.SchedulersCompat;
import de.hdodenhof.circleimageview.CircleImageView;

public class CreateBabyActivity extends BaseAppCompatActivity implements View.OnClickListener {


    @Bind(R.id.tv_back)
    TextView tvBack;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.tv_next)
    TextView tvNext;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.iv_avatar)
    CircleImageView ivAvatar;
    @Bind(R.id.et_name)
    EditText etName;
    @Bind(R.id.rb_girl)
    RadioButton rbGirl;
    @Bind(R.id.rb_boy)
    RadioButton rbBoy;
    @Bind(R.id.et_birthday)
    TextView etBirthday;
    @Bind(R.id.et_relationship)
    TextView etRelationship;
    @Bind(R.id.tv_focus)
    TextView tvFocus;
    @Bind(R.id.rl_focus)
    RelativeLayout rlFocus;


    public static final String KEY_SELECTED_PHOTO_SIZE = "SELECTED_PHOTO_SIZE";
    private final int PicutreSelcted = 10;
    private final int RELATIONSHIP = 1;
    private int gender;

    private String imagePath = "";
    private String objectKey = "";
    private int relationId;
    private String relationName;
    private boolean showFocus;

    public static void open(Context context,boolean showFocus) {
        Intent intent = new Intent(context, CreateBabyActivity.class);
        intent.putExtra("showFocus",showFocus);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createbaby);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        showFocus = getIntent().getBooleanExtra("showFocus", true);
        if(showFocus){
            rlFocus.setVisibility(View.VISIBLE);
        }else{
            rlFocus.setVisibility(View.GONE);
        }

        tvBack.setOnClickListener(this);
        tvNext.setOnClickListener(this);
        ivAvatar.setOnClickListener(this);
        rbGirl.setOnClickListener(this);
        rbBoy.setOnClickListener(this);
        etBirthday.setOnClickListener(this);
        rlFocus.setOnClickListener(this);
        etRelationship.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_back:
                this.finish();
                break;
            case R.id.tv_next:
                String name = etName.getText().toString().trim();
                String birthday = etBirthday.getText().toString().trim();
                String relation = etRelationship.getText().toString().trim();
                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(this, "请填写宝宝小名", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (name.length() > 20) {
                    Toast.makeText(this, "宝宝小名不能超过20个字，请修改", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(birthday)) {
                    Toast.makeText(this, "请填写宝宝生日", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(relation)) {
                    Toast.makeText(this, "请设置与宝宝关系", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(objectKey)){
                    ToastUtil.showToast("给宝宝设置一个好看的头像吧~");
                    return;
                }
                long time = DateUtil.getTime(birthday, "yyyy-MM-dd");
                apiService.createBaby(time, gender, objectKey, URLEncoder.encode(name), relationId)
                        .compose(SchedulersCompat.applyIoSchedulers())
                        .subscribe(userLoginResponse -> {
//                            FastData.setUserInfo(userLoginResponse.getUserInfo());
//                            TabMainActivity.open(this);
                            finish();
                        }, throwable -> {
                            Log.e(TAG, "createBaby:", throwable);
                        });
                break;
            case R.id.iv_avatar:
                startPhotoPick();
                break;
            case R.id.rb_girl:
                gender = 0;
                rbBoy.setChecked(false);
                break;
            case R.id.rb_boy:
                gender = 1;
                rbGirl.setChecked(false);
                break;
            case R.id.et_birthday:
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        monthOfYear = monthOfYear + 1;
                        etBirthday.setText(year + "-" + monthOfYear + "-" + dayOfMonth);
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                dialog.show();
                break;
            case R.id.et_relationship:
                Intent intent = new Intent(this, RelationshipActivity.class);
                startActivityForResult(intent, RELATIONSHIP);
                break;
            case R.id.rl_focus:
                startActivity(new Intent(this, InviteCodeActivity.class));

                break;

        }
    }

    private void startPhotoPick() {
        Intent intent = new Intent(this, PickerPhotoActivity.class);
        intent.putExtra(KEY_SELECTED_PHOTO_SIZE, 8);
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
                    for (String path : data.getStringArrayListExtra(PickerPhotoActivity.KEY_SELECTED_PHOTOS)) {
                        imagePath = path;
                        GlideUtil.displayImage(imagePath, ivAvatar);
                        uploadImage();
                    }
                    break;
                case RELATIONSHIP:
                    relationId = data.getIntExtra("relationId", 0);
                    relationName = data.getStringExtra("relationName");
                    etRelationship.setText(relationName);
                    break;
            }
        }

    }

    private void uploadImage() {
        if (TextUtils.isEmpty(imagePath)) {
            return;
        }
        OSSManager ossManager = OSSManager.getOSSManager(this);
        new Thread() {
            @Override
            public void run() {
                try {
                    //获取上传文件
                    UploadFileObj uploadFileObj = new MyUploadFileObj(imagePath);
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
}
