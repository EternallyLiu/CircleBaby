package cn.timeface.circle.baby.fragments;


import android.app.Activity;
import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.google.gson.Gson;

import java.net.URLEncoder;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.adapters.HorizontalListViewAdapter2;
import cn.timeface.circle.baby.api.models.objs.ImgObj;
import cn.timeface.circle.baby.api.models.objs.MediaObj;
import cn.timeface.circle.baby.api.models.objs.MyUploadFileObj;
import cn.timeface.circle.baby.api.models.objs.TemplateImage;
import cn.timeface.circle.baby.api.models.responses.DiaryPaperResponse;
import cn.timeface.circle.baby.events.MediaObjEvent;
import cn.timeface.circle.baby.fragments.base.BaseFragment;
import cn.timeface.circle.baby.oss.OSSManager;
import cn.timeface.circle.baby.oss.uploadservice.UploadFileObj;
import cn.timeface.circle.baby.utils.DateUtil;
import cn.timeface.circle.baby.utils.Pinyin4jUtil;
import cn.timeface.circle.baby.utils.ToastUtil;
import cn.timeface.circle.baby.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.views.ScaleImageView;
import de.greenrobot.event.EventBus;

public class CardPreviewFragment extends BaseFragment implements View.OnClickListener {


    @Bind(R.id.tv_save)
    TextView tvSave;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.et_title)
    EditText etTitle;
    @Bind(R.id.rl_diary)
    RelativeLayout rlDiary;
    private HorizontalListViewAdapter2 adapter;
    private DiaryPaperResponse diaryPaperResponse;
    private String url;
    private ScaleImageView touchImageView;
    PointF center = new PointF();
    private float oldRotation;
    private int width;
    private int hight;
    private String objectKey;
    private ImgObj imgObj;
    private String date;

    public CardPreviewFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imgObj = (ImgObj) getArguments().getSerializable("imgObj");
        url = imgObj.getLocalPath();
        date = imgObj.getDate();
        uploadImage();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cardpreview, container, false);
        ButterKnife.bind(this, view);
        setActionBar(toolbar);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        touchImageView = new ScaleImageView(getActivity(), url);
        rlDiary.addView(touchImageView);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) touchImageView.getLayoutParams();
        layoutParams.height = RelativeLayout.LayoutParams.MATCH_PARENT;
        layoutParams.width = RelativeLayout.LayoutParams.MATCH_PARENT;
        touchImageView.setLayoutParams(layoutParams);

//        GlideUtil.displayImage(url, scaleImageView);

        tvSave.setOnClickListener(this);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_save:
                String content = etTitle.getText().toString();
                if(content.length()>6){
                    ToastUtil.showToast("标题字数不能大于6个");
                    return;
                }

                PointF leftTop = touchImageView.getLeftTop();
//                float degree = touchImageView.getDegree();
                float cropWidth = touchImageView.getCropWidth();
                float cropHeight = touchImageView.getCropHeight();
                int bitmapWidth = touchImageView.getBitmapWidth();
                int bitmapHeight = touchImageView.getBitmapHeight();
                long createTime = DateUtil.getTime(date, "yyyy.MM.dd");
                TemplateImage templateImage = new TemplateImage(0, cropHeight, bitmapHeight, bitmapWidth, cropWidth, leftTop.x, leftTop.y, objectKey, createTime);
                Gson gson = new Gson();
                String imageInfo = gson.toJson(templateImage);

                apiService.cardComposed(URLEncoder.encode(content), imageInfo, "pinyin")
                        .compose(SchedulersCompat.applyIoSchedulers())
                        .subscribe(diaryComposeResponse -> {
                            MediaObj mediaObj = diaryComposeResponse.getMediaObj();
                            System.out.println("合成的识图卡片===============" + mediaObj.getImgUrl());
                            getActivity().finish();
                            EventBus.getDefault().post(new MediaObjEvent(mediaObj));
                        }, throwable -> {
                            Log.e(TAG, "diaryPublish:");
                        });
                break;
        }
    }


    private void uploadImage() {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        OSSManager ossManager = OSSManager.getOSSManager(getContext());
        new Thread() {
            @Override
            public void run() {
                try {
                    //获取上传文件
                    UploadFileObj uploadFileObj = new MyUploadFileObj(url);
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
