package cn.timeface.circle.baby.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.net.URLEncoder;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.api.models.objs.ImgObj;
import cn.timeface.circle.baby.api.models.objs.MediaObj;
import cn.timeface.circle.baby.api.models.objs.MyUploadFileObj;
import cn.timeface.circle.baby.api.models.objs.TemplateImage;
import cn.timeface.circle.baby.fragments.base.BaseFragment;
import cn.timeface.circle.baby.oss.OSSManager;
import cn.timeface.circle.baby.oss.uploadservice.UploadFileObj;
import cn.timeface.circle.baby.utils.DateUtil;
import cn.timeface.circle.baby.utils.Pinyin4jUtil;
import cn.timeface.circle.baby.utils.ToastUtil;
import cn.timeface.circle.baby.utils.Utils;
import cn.timeface.circle.baby.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.views.dialog.TFProgressDialog;
import uk.co.senab.photoview.PhotoView;

public class CardPreviewFragment extends BaseFragment {


    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.et_title)
    EditText etTitle;
    @Bind(R.id.et_pinyin)
    EditText etPinyin;
    @Bind(R.id.rl_diary)
    RelativeLayout rlDiary;
    private String url;
    private String objectKey = "";
    private ImgObj imgObj;
    private String date;
    private TFProgressDialog tfProgressDialog;
    private PhotoView photoView;

    public CardPreviewFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        imgObj = getArguments().getParcelable("imgObj");
        date = imgObj.getDate();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cardpreview, container, false);
        ButterKnife.bind(this, view);
        setActionBar(toolbar);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setTitle("识图卡片");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        tfProgressDialog = new TFProgressDialog(getActivity());
        url = imgObj.getLocalPath();
        new Thread(new Runnable() {
            @Override
            public void run() {
                uploadImage();
            }
        }).run();
        photoView = getImageView(getContext());
        rlDiary.addView(photoView);
        photoView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        etTitle.requestFocus();
        etTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (!TextUtils.isEmpty(s.toString())) {
                    String pinyin = Pinyin4jUtil.toPinYin(s.toString());
                    etPinyin.setText(pinyin);
                } else {
                    etPinyin.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
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
                    } catch (ServiceException | ClientException e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {

                }
            }
        }.start();

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_next, menu);
        MenuItem item = menu.findItem(R.id.next);
        item.setTitle("保存");
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.next) {
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
            tfProgressDialog.setMessage("合成图片中…");
            tfProgressDialog.show();
            float imageScale = (float) imgObj.getWidth() / photoView.getDisplayRect().width();
            int cropLeft = ((int) (Math.abs(photoView.getDisplayRect().left) * imageScale));
            int cropTop = ((int) (Math.abs(photoView.getDisplayRect().top) * imageScale));
            int cropW = ((int) ((photoView.getWidth() - photoView.getPaddingLeft() - photoView.getPaddingRight()) * imageScale));
            int cropH = ((int) ((photoView.getHeight() - photoView.getPaddingTop() - photoView.getPaddingBottom()) * imageScale));
            long createTime = DateUtil.getTime(date, "yyyy.MM.dd");

            while (TextUtils.isEmpty(objectKey)) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            TemplateImage templateImage = new TemplateImage(0, cropH, imgObj.getHeight(), imgObj.getWidth(), cropW, cropLeft, cropTop, objectKey, createTime);
            String imageInfo = new Gson().toJson(templateImage);

            String py = URLEncoder.encode(etPinyin.getText().toString());
            if (py.contains("%C4%AD")) {
                py = py.replace("%C4%AD", "%C7%90");
            }
            apiService.cardComposed(URLEncoder.encode(content), imageInfo, py)
                    .compose(SchedulersCompat.applyIoSchedulers())
                    .subscribe(diaryComposeResponse -> {
                        tfProgressDialog.dismiss();
                        if (diaryComposeResponse.success()) {
                            MediaObj mediaObj = diaryComposeResponse.getMediaObj();
                            mediaObj.setPhotographTime(createTime);
                            getActivity().finish();
                        } else {
                            ToastUtil.showToast(diaryComposeResponse.getInfo());
                        }

                    }, throwable -> {
                        Log.e(TAG, "diaryPublish:");
                    });
        }
        return super.onOptionsItemSelected(item);
    }

    public PhotoView getImageView(Context context) {
        PhotoView imageView = new PhotoView(context);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        imageView.setLayoutParams(lp);
        Glide.with(context)
                .load(imgObj.getLocalPath())
                .into(imageView);
        return imageView;
    }
}
