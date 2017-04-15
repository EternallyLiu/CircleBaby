package cn.timeface.circle.baby.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
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
import org.greenrobot.eventbus.Subscribe;

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
import cn.timeface.circle.baby.ui.babyInfo.activity.BigNameActivity;
import cn.timeface.circle.baby.ui.babyInfo.fragments.IconHistoryFragment;
import cn.timeface.circle.baby.ui.babyInfo.views.GenderDialog;
import cn.timeface.circle.baby.ui.images.views.DeleteDialog;
import cn.timeface.circle.baby.ui.timelines.Utils.LogUtil;
import cn.timeface.circle.baby.ui.timelines.Utils.SpannableUtils;

public class BabyInfoActivity extends BaseAppCompatActivity implements View.OnClickListener, DeleteDialog.SubmitListener, GenderDialog.GenderSelectedListener {

    @Bind(R.id.btn_save)
    Button btnSave;
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.iv_avatar)
    ImageView ivAvatar;
    @Bind(R.id.tv_age)
    TextView tvAge;
    @Bind(R.id.tv_name)
    TextView tvName;
    @Bind(R.id.iv_name)
    ImageView ivName;
    @Bind(R.id.rl_name)
    RelativeLayout rlName;
    @Bind(R.id.tv_real_name)
    TextView tvRealName;
    @Bind(R.id.iv_real_name)
    ImageView ivRealName;
    @Bind(R.id.rl_real_name)
    RelativeLayout rlRealName;
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
    @Bind(R.id.rl_icon_his)
    RelativeLayout rlIconHis;
    private GenderDialog genderDialog;

    public int gender;
    @Bind(R.id.tv_gender)
    TextView tvGender;
    @Bind(R.id.iv_gender)
    ImageView ivGender;
    private BabyObj babyObj;
    public static final String KEY_SELECTED_PHOTO_SIZE = "SELECTED_PHOTO_SIZE";
    private final int PicutreSelcted = 10;
    public static final int CROP_IMG_REQUEST_CODE = 1002;
    private String objectKey;
    private AlertDialog alertDialog;
    File selImageFile;
    File outFile;
    private UserObj userInfo;
    private DeleteDialog deleteDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_babyinfo);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        title.setText("宝宝信息");

        userInfo = FastData.getUserInfo();
        babyObj = userInfo.getBabyObj();
        gender = this.babyObj.getGender();
        initBaby();

        initView(userInfo.getIsCreator() == 1);
    }

    private void initBaby() {
        String s = babyObj.getAvatar();
        int baby = s.indexOf("baby");
        if (baby > 0)
            objectKey = s.substring(baby);

        GlideUtil.displayImageCircle(this.babyObj.getAvatar(),R.drawable.ic_launcher, ivAvatar);
        tvName.setText(this.babyObj.getName());
        tvRealName.setText(this.babyObj.getRealName());

        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(this.babyObj.getNickName()).append("\n").append(this.babyObj.getAge());
        AbsoluteSizeSpan sizeSpan = new AbsoluteSizeSpan((int) getResources().getDimension(R.dimen.text_large));
        StyleSpan styleSpan = new StyleSpan(Typeface.BOLD);

        builder.setSpan(sizeSpan, 0, this.babyObj.getNickName().length() + 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        builder.setSpan(styleSpan, 0, this.babyObj.getNickName().length() + 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        tvAge.setText(builder);
//        tvAge.setText(this.babyObj.getNickName() + "\n" + this.babyObj.getAge());
        tvBrithday.setText(DateUtil.getYear(this.babyObj.getBithday()));
        tvBlood.setText(TextUtils.isEmpty(this.babyObj.getBlood()) ? "未填写" : this.babyObj.getBlood());
//        rbGirl.setChecked(this.babyObj.getGender() == 0);
//        rbBoy.setChecked(this.babyObj.getGender() == 1);
        tvGender.setText(gender == 0 ? "女" : gender == 1 ? "男" : "龙凤胎");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
    }

    public static void open(Context context) {
        context.startActivity(new Intent(context, BabyInfoActivity.class));
    }

    public void initView(boolean isCreator) {
        if (isCreator) {
            //创建者
            ivName.setVisibility(View.VISIBLE);
            ivBrithday.setVisibility(View.VISIBLE);
            ivBlood.setVisibility(View.VISIBLE);
            ivRealName.setVisibility(View.VISIBLE);
            btnSave.setVisibility(View.VISIBLE);
            ivGender.setVisibility(View.VISIBLE);

            rlName.setOnClickListener(this);
            rlBrithday.setOnClickListener(this);
            rlBlood.setOnClickListener(this);
            rbGirl.setOnClickListener(this);
            rbBoy.setOnClickListener(this);
            ivAvatar.setOnClickListener(this);
            btnSave.setOnClickListener(this);
            rlRealName.setOnClickListener(this);
            rlGender.setOnClickListener(this);
        } else {
            ivGender.setVisibility(View.GONE);
            //关注者
            ivRealName.setVisibility(View.INVISIBLE);
            ivName.setVisibility(View.INVISIBLE);
            ivBrithday.setVisibility(View.INVISIBLE);
            rlBlood.setVisibility(View.GONE);
            rbBoy.setClickable(false);
            rbGirl.setClickable(false);
        }
        rlIconHis.setOnClickListener(this);
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
                    this.babyObj.setName(input);
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
                    GlideUtil.displayImageCircle(outFile.getAbsolutePath(),R.drawable.ic_launcher, ivAvatar);
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
                        LogUtil.showError(e);
                    }
                } catch (Exception e) {

                }
            }
        }.start();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_gender:
                if (genderDialog == null) {
                    genderDialog = new GenderDialog(this);
                    genderDialog.setgenderSelectedListener(this);
                }
                genderDialog.show();
                break;
            case R.id.rl_icon_his:
                FragmentBridgeActivity.open(this, IconHistoryFragment.class.getSimpleName());
                break;
            case R.id.rl_real_name:
                BigNameActivity.open(this, this.babyObj);
                break;
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
                        String brithday = year + "-" + monthOfYear + "-" + dayOfMonth;
                        tvBrithday.setText(brithday);
                        long time = DateUtil.getTime(brithday, "yyyy-MM-dd");
                        babyObj.setBithday(time);
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
//                dialog.getDatePicker().setMinDate(DateUtil.getTime("2000-01-01","yyyy-MM-dd"));
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
                        babyObj.setBlood(charSequences[which].toString());
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

                updateBabyInfo(true);
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

    private void updateBabyInfo(boolean isFinish) {
        String n = tvName.getText().toString();
        String brithday = tvBrithday.getText().toString();
        long time = DateUtil.getTime(brithday, "yyyy-MM-dd");
        String b = tvBlood.getText().toString();
        if (TextUtils.isEmpty(b)) b = "";
        btnSave.setEnabled(false);
        apiService.editBabyInfo(time, URLEncoder.encode(b), gender, URLEncoder.encode(n),
                URLEncoder.encode(this.babyObj.getRealName()),
                URLEncoder.encode(this.babyObj.getShowRealName() + ""),
                objectKey)
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(response -> {
                    if (response.success()) {
                        apiService.queryBabyInfoDetail(babyObj.getBabyId())
                                .compose(SchedulersCompat.applyIoSchedulers())
                                .subscribe(babyInfoResponse -> {
                                    if (babyInfoResponse.success()) {
                                        babyObj = babyInfoResponse.getBabyInfo();
                                        initBaby();
                                        FastData.setBabyObj(babyObj);
                                        EventBus.getDefault().post(new HomeRefreshEvent());
                                        if (isFinish)
                                            finish();
                                    }
                                }, throwable -> {
                                    Log.e(TAG, "queryBabyInfoDetail:", throwable);
                                });
                    }
                    btnSave.setEnabled(true);
                }, throwable -> {
                    LogUtil.showError(throwable);
//                    Log.e(TAG, "editBabyInfo:", throwable);
                });
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
                if (deleteDialog == null) {
                    deleteDialog = new DeleteDialog(this);
                    deleteDialog.setTitle("提示");
                    String contentMessage = "你确定要删除宝宝";


                    SpannableStringBuilder builder = new SpannableStringBuilder(contentMessage);
                    ForegroundColorSpan colorSpan = SpannableUtils.getTextColor(this, R.color.sea_buckthorn);
                    builder.append(" ").append(this.babyObj.getNickName()).append(" ");
                    builder.append("吗？").append("\n");
//                    int largeLength = builder.length();
                    builder.append("这会导致你不能继续查看宝宝相关").append("\n").append("的内容。");
//                    //标红宝宝名字
//                    builder.setSpan(colorSpan, contentMessage.length() + 1, contentMessage.length() + 1 + this.babyObj.getNickName().length() + 1,
//                            Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
//                    //关键提示语加粗
//                    StyleSpan styleSpan = new StyleSpan(Typeface.BOLD);
//                    builder.setSpan(styleSpan, 0, largeLength, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);


                    deleteDialog.setMessage(builder);
                    deleteDialog.setSubmitListener(this);
                }
                deleteDialog.show();
            } else {
                alertDialog = new AlertDialog.Builder(this).setView(initUnFocusView()).show();
                alertDialog.setCanceledOnTouchOutside(false);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteBaby() {
        apiService.delBabyInfo(babyObj.getBabyId())
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(response -> {
                    if (response.success()) {
                        BabyObj.delete(babyObj.getBabyId());
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

    @Subscribe
    public void onEvent(BabyObj event) {
        this.babyObj = event;
        initBaby();
    }

    private View initUnFocusView() {
        View view = View.inflate(this, R.layout.view_dialog, null);
        TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);
        TextView tvMsg = (TextView) view.findViewById(R.id.tv_msg);
        Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);
        Button btnOk = (Button) view.findViewById(R.id.btn_ok);

        String babyNickName = FastData.getBabyObj().getNickName();
        StringBuilder builder = new StringBuilder("你确定不再关注宝宝 ").append(babyNickName).append(" 吗?这会导致你不能继续查看宝宝相关内容。");
        SpannableStringBuilder content = new SpannableStringBuilder();
        content.append(builder.toString());

        ForegroundColorSpan babyColorSpan = new ForegroundColorSpan(Color.RED);
        StyleSpan styleSpan = new StyleSpan(Typeface.BOLD);
        int index = builder.indexOf(babyNickName);
        content.setSpan(babyColorSpan, index, index + babyNickName.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        content.setSpan(styleSpan, index, index + babyNickName.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

        tvMsg.setText(content);
        btnCancel.setOnClickListener(this);
        btnOk.setOnClickListener(this);
        return view;
    }

    @Override
    public void submit() {
        deleteBaby();
        if (deleteDialog != null && deleteDialog.isShowing())
            deleteDialog.dismiss();
    }

    @Override
    public void genderSelected(int gender) {
        this.gender = gender;
        updateBabyInfo(false);
    }
}
