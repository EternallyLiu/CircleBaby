package cn.timeface.circle.baby.ui.growth.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.net.URLEncoder;
import java.util.ArrayList;

import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.SelectPhotoActivity;
import cn.timeface.circle.baby.support.api.models.objs.CardObj;
import cn.timeface.circle.baby.support.api.models.objs.ImgObj;
import cn.timeface.circle.baby.support.api.models.objs.KnowledgeCardObj;
import cn.timeface.circle.baby.support.api.models.objs.MediaObj;
import cn.timeface.circle.baby.support.api.models.objs.MyUploadFileObj;
import cn.timeface.circle.baby.support.api.models.objs.TemplateImage;
import cn.timeface.circle.baby.support.oss.OSSManager;
import cn.timeface.circle.baby.support.oss.uploadservice.UploadFileObj;
import cn.timeface.circle.baby.support.utils.DateUtil;
import cn.timeface.circle.baby.support.utils.Pinyin4jUtil;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.support.utils.Utils;
import cn.timeface.circle.baby.views.dialog.TFProgressDialog;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 识图卡片编辑页面
 * author : YW.SUN Created on 2017/2/8
 * email : sunyw10@gmail.com
 */
public class RecognizeCardEditActivity extends CardPreviewActivity implements View.OnClickListener {
    private final int REQUEST_CODE_SELECT_IMAGE = 100;
    private TFProgressDialog tfProgressDialog;
    KnowledgeCardObj knowledgeCardObj;
    long createTime;
    String py;
    ImgObj imgObj;

    public static void open(Context context, CardObj cardObj){
        Intent intent = new Intent(context, RecognizeCardEditActivity.class);
        intent.putExtra("recognize_card", cardObj);
        context.startActivity(intent);
    }

    public static void open4Result(Context context, CardObj cardObj, int reqCode){
        Intent intent = new Intent(context, RecognizeCardEditActivity.class);
        intent.putExtra("recognize_card", cardObj);
        ((Activity) context).startActivityForResult(intent, reqCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        knowledgeCardObj = getIntent().getParcelableExtra("recognize_card");
        initView();
        tfProgressDialog =TFProgressDialog.getInstance("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_edit_complete, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //编辑完成
        if(item.getItemId() == R.id.edit_complete){
            String content = etTitle.getText().toString();
            if (Utils.isHz(content)) {
                ToastUtil.showToast("请输入中文");
                return true;
            }
            if (content.length() > 4) {
                ToastUtil.showToast("标题字数不能大于4个");
                return true;
            } else if (TextUtils.isEmpty(content)) {
                ToastUtil.showToast("识图卡片标题不能为空");
                return true;
            }
            py = URLEncoder.encode(tvPinyin.getText().toString());
            if (py.contains("%C4%AD")) {
                py = py.replace("%C4%AD", "%C7%90");
            }
            uploadImageObservable(imgObj != null ? imgObj.getLocalPath() : knowledgeCardObj.getImageInfo().getYurl())
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .filter(objKey -> !TextUtils.isEmpty(objKey))
                    .map(objKey -> {
                        //日记卡片合成
                        float imageScale = (float) knowledgeCardObj.getImageInfo().getImageH() / ivCard.getDisplayRect().height();
                        int cropLeft = ((int) ((ivCard.getDisplayRect().left > 0 ? 0 : (Math.abs(ivCard.getDisplayRect().left))) * imageScale));
                        int cropTop = ((int) ((ivCard.getDisplayRect().top > 0 ? 0 : (Math.abs(ivCard.getDisplayRect().top))) * imageScale));
                        int cropW = ((int) ((ivCard.getWidth()) * imageScale));
                        int cropH = ((int) ((ivCard.getHeight()) * imageScale));
                        createTime = DateUtil.getTime(String.valueOf(knowledgeCardObj.getImageInfo().getCreationDate()), "yyyy.MM.dd");
                        TemplateImage templateImage = new TemplateImage(
                                0,
                                cropH,
                                knowledgeCardObj.getImageInfo().getImageH(),
                                knowledgeCardObj.getImageInfo().getImageW(),
                                cropW, cropLeft, cropTop, objKey, createTime);
                        String imageInfo = new Gson().toJson(templateImage);
                        return imageInfo;
                    })
                    .flatMap(imageInfo -> apiService.cardComposed(String.valueOf(knowledgeCardObj.getCardId()), URLEncoder.encode(content), imageInfo, py))
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(() -> {
                        tfProgressDialog.setTvMessage("合成卡片中…");
                        tfProgressDialog.show(getSupportFragmentManager(), "");
                    })
                    .doOnTerminate(() -> {
                        if (tfProgressDialog != null && tfProgressDialog.isVisible()) {
                            tfProgressDialog.dismiss();
                        }
                    })
                    .subscribe(knowledgeComposedResponse -> {
                        tfProgressDialog.dismiss();
                        if (knowledgeComposedResponse.success()) {
                            MediaObj mediaObj = knowledgeComposedResponse.getKnowledgeCardObj().getMedia();
                            mediaObj.setPhotographTime(createTime);

                            Intent intent = new Intent();
                            intent.putExtra("card_obj", knowledgeComposedResponse.getKnowledgeCardObj());
                            setResult(RESULT_OK, intent);
                            finish();
                        } else {
                            ToastUtil.showToast(knowledgeComposedResponse.getInfo());
                        }
                    }, throwable -> {
                        Log.e(TAG, "diaryPublish:");
                    });

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void initView() {
        ivSelect.setVisibility(View.GONE);
        tvPinyin.setVisibility(View.VISIBLE);
        etTitle.setVisibility(View.VISIBLE);
        tvPinyin.setText(knowledgeCardObj.getPinyin());
        etTitle.setText(knowledgeCardObj.getContent());
        etTitle.setSelection(knowledgeCardObj.getContent().length());
        etTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (!TextUtils.isEmpty(s.toString())) {
                    String pinyin = Pinyin4jUtil.toPinYin(s.toString());
                    tvPinyin.setText(pinyin);
                } else {
                    tvPinyin.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        Glide.with(this)
                .load(knowledgeCardObj.getImageInfo().getYurl())
                .placeholder(R.drawable.bg_default_holder_img)
                .error(R.drawable.bg_default_holder_img)
                .into(ivCard);
        rlCard.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.rl_card){
            SelectPhotoActivity.openForResult(this, new ArrayList<>(), 1, REQUEST_CODE_SELECT_IMAGE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK && data != null){
            imgObj = (ImgObj) data.getParcelableArrayListExtra("result_select_image_list").get(0);
            Glide.with(this)
                    .load(imgObj.getLocalPath())
                    .placeholder(R.drawable.bg_default_holder_img)
                    .error(R.drawable.bg_default_holder_img)
                    .into(ivCard);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private Observable<String> uploadImageObservable(String imgPath) {
        return Observable.defer(() -> Observable.just(imgPath)
                .filter(s -> !TextUtils.isEmpty(s))
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .map(path -> {
                    //本地图片
                    if (!imgPath.contains(apiService.IMAGE_BASE_URL)) {
                        OSSManager ossManager = OSSManager.getOSSManager(this);
                        //获取上传文件
                        UploadFileObj uploadFileObj = new MyUploadFileObj(path);
                        //上传操作
                        try {
                            //判断服务器是否已存在该文件
                            if (!ossManager.checkFileExist(uploadFileObj.getObjectKey())) {
                                //如果不存在则上传
                                ossManager.upload(uploadFileObj.getObjectKey(), uploadFileObj.getFinalUploadFile().getAbsolutePath());
                            }
                            return uploadFileObj.getObjectKey();
                        } catch (ServiceException | ClientException e) {
                            e.printStackTrace();
                        }
                    //远程图片
                    } else {
                        return imgPath.substring(imgPath.indexOf(apiService.IMAGE_BASE_URL));
                    }

                    return null;
                }));
    }
}
