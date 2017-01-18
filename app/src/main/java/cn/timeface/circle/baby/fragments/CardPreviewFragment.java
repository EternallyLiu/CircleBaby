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
import com.github.rayboot.widget.ratioview.RatioRelativeLayout;
import com.google.gson.Gson;

import java.net.URLEncoder;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.fragments.base.BaseFragment;
import cn.timeface.circle.baby.support.api.models.objs.ImgObj;
import cn.timeface.circle.baby.support.api.models.objs.MediaObj;
import cn.timeface.circle.baby.support.api.models.objs.MyUploadFileObj;
import cn.timeface.circle.baby.support.api.models.objs.TemplateImage;
import cn.timeface.circle.baby.support.api.models.responses.KnowledgeComposedResponse;
import cn.timeface.circle.baby.support.oss.OSSManager;
import cn.timeface.circle.baby.support.oss.uploadservice.UploadFileObj;
import cn.timeface.circle.baby.support.utils.DateUtil;
import cn.timeface.circle.baby.support.utils.Pinyin4jUtil;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.support.utils.Utils;
import cn.timeface.circle.baby.views.dialog.TFProgressDialog;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import uk.co.senab.photoview.PhotoView;

public class CardPreviewFragment extends BaseFragment {


    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.et_title)
    EditText etTitle;
    @Bind(R.id.et_pinyin)
    EditText etPinyin;
    @Bind(R.id.rl_diary)
    RatioRelativeLayout rlDiary;
    private String url;
    private ImgObj imgObj;
    private String date;
    private TFProgressDialog tfProgressDialog;
    private PhotoView photoView;
    private long createTime;
    private String py;
    private String content;

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
        tfProgressDialog =TFProgressDialog.getInstance("");
        url = imgObj.getLocalPath();

        uploadImageObservable(imgObj.getLocalPath())
                .subscribe(s -> {
                        }
                        , throwable -> {
                        });

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
            content = etTitle.getText().toString();
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
            py = URLEncoder.encode(etPinyin.getText().toString());
            if (py.contains("%C4%AD")) {
                py = py.replace("%C4%AD", "%C7%90");
            }
            uploadImageObservable(url)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .filter(objKey -> !TextUtils.isEmpty(objKey))
                    .map(objKey -> {
                        //日记卡片合成
                        float imageScale = (float) imgObj.getWidth() / photoView.getDisplayRect().width();
                        int cropLeft = ((int) (Math.abs(photoView.getDisplayRect().left) * imageScale));
                        int cropTop = ((int) (Math.abs(photoView.getDisplayRect().top) * imageScale));
                        int cropW = ((int) ((photoView.getWidth() - photoView.getPaddingLeft() - photoView.getPaddingRight()) * imageScale));
                        int cropH = ((int) ((photoView.getHeight() - photoView.getPaddingTop() - photoView.getPaddingBottom()) * imageScale));
                        createTime = DateUtil.getTime(date, "yyyy.MM.dd");
                        TemplateImage templateImage = new TemplateImage(0, cropH, imgObj.getHeight(), imgObj.getWidth(), cropW, cropLeft, cropTop, objKey, createTime);
                        String imageInfo = new Gson().toJson(templateImage);
                        return imageInfo;
                    })
                    .flatMap(imageInfo -> apiService.cardComposed(URLEncoder.encode(content), imageInfo, py))
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(() -> {
                        tfProgressDialog.setTvMessage("合成卡片中…");
                        tfProgressDialog.show(getChildFragmentManager(), "");
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
                            CardPreviewFragment.this.getActivity().finish();
                        } else {
                            ToastUtil.showToast(knowledgeComposedResponse.getInfo());
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

    private Observable<String> uploadImageObservable(String imgPath) {
        return Observable.defer(() -> Observable.just(imgPath)
                .filter(s -> !TextUtils.isEmpty(s))
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .map(path -> {
                    OSSManager ossManager = OSSManager.getOSSManager(getContext());
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
                    return null;
                }));
    }


}
