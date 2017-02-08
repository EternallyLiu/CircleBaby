package cn.timeface.circle.baby.ui.growth.activities;

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

import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.constants.TypeConstant;
import cn.timeface.circle.baby.fragments.CardPreviewFragment;
import cn.timeface.circle.baby.support.api.models.objs.CardObj;
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
public class RecognizeCardEditActivity extends CardPreviewActivity {
    private TFProgressDialog tfProgressDialog;
    KnowledgeCardObj knowledgeCardObj;
    long createTime;
    String py;

    public static void open(Context context, CardObj cardObj){
        Intent intent = new Intent(context, RecognizeCardEditActivity.class);
        intent.putExtra("recognize_card", cardObj);
        context.startActivity(intent);
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
            uploadImageObservable(knowledgeCardObj.getImageInfo().getYurl())
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
                                knowledgeCardObj.getImageInfo().getImageH(),
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
