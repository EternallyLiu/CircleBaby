package cn.timeface.circle.baby.fragments;


import android.graphics.PointF;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.PublishActivity;
import cn.timeface.circle.baby.adapters.HorizontalListViewAdapter2;
import cn.timeface.circle.baby.api.models.objs.ImgObj;
import cn.timeface.circle.baby.api.models.objs.MediaObj;
import cn.timeface.circle.baby.api.models.objs.MyUploadFileObj;
import cn.timeface.circle.baby.api.models.objs.TemplateAreaObj;
import cn.timeface.circle.baby.api.models.objs.TemplateImage;
import cn.timeface.circle.baby.api.models.objs.TemplateObj;
import cn.timeface.circle.baby.api.models.responses.DiaryPaperResponse;
import cn.timeface.circle.baby.events.DiaryPublishEvent;
import cn.timeface.circle.baby.fragments.base.BaseFragment;
import cn.timeface.circle.baby.oss.OSSManager;
import cn.timeface.circle.baby.oss.uploadservice.UploadFileObj;
import cn.timeface.circle.baby.utils.GlideUtil;
import cn.timeface.circle.baby.utils.ToastUtil;
import cn.timeface.circle.baby.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.views.HorizontalListView;
import cn.timeface.circle.baby.views.dialog.TFProgressDialog;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import uk.co.senab.photoview.PhotoView;

public class DiaryPreviewFragment extends BaseFragment {


    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.fl_container)
    FrameLayout flContainer;
    @Bind(R.id.fl_main)
    FrameLayout flMain;
    @Bind(R.id.lv_horizontal)
    HorizontalListView lvHorizontal;
    private HorizontalListViewAdapter2 adapter;
    private DiaryPaperResponse diaryPaperResponse;
    private String title;
    private String content;
    private TemplateObj templateObj;
    private ImgObj imgObj;
    private float degree = 0;
    private TFProgressDialog tfProgressDialog;
    private int ROTATION_SIZE;
    private List<String> contentList = new ArrayList<>(3);
    private PhotoView photoView;
    private PointF center = new PointF();

    public DiaryPreviewFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        ROTATION_SIZE = getResources().getDimensionPixelSize(R.dimen.size_24);
        title = getArguments().getString("title");
        content = getArguments().getString("content");
        imgObj = getArguments().getParcelable("imgObj");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_diarypreview, container, false);
        ButterKnife.bind(this, view);
        setActionBar(toolbar);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setTitle("宝宝日记");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        tfProgressDialog = new TFProgressDialog(getActivity());

        uploadImageObservable(imgObj.getLocalPath())
                .subscribe(s -> {
                        }
                        , throwable -> {
                        });

        reqPaperList();
        lvHorizontal.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.setSelectIndex(position);
                adapter.notifyDataSetChanged();
                setViewData(diaryPaperResponse.getDataList().get(position));
            }
        });
        return view;
    }

    private void reqPaperList() {
        apiService.getPaperList()
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(diaryPaperResponse -> {
                    this.diaryPaperResponse = diaryPaperResponse;
                    setDataList(diaryPaperResponse.getDataList());
                }, throwable -> {
                    Log.e(TAG, "getPaperList:", throwable);
                });

    }

    private void setViewData(TemplateObj template) {

        String[] split = content.split("\r\n");
        if (split[0].equals(content)) {
            split = content.split("\n");
        }
        contentList = new ArrayList(Arrays.asList(split));

        templateObj = template;
        flContainer.removeAllViews();
        float scale = Math.min(((float) flMain.getHeight()) / template.getPaperHeight(), ((float) flMain.getWidth()) / template.getPaperWidth());

        int containerW = (int) (template.getPaperWidth() * scale);
        int containerH = (int) (template.getPaperHeight() * scale);

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(containerW, containerH);
        lp.gravity = Gravity.CENTER;
        flContainer.setLayoutParams(lp);

        ImageView ivBg = new ImageView(getActivity());
        lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        ivBg.setLayoutParams(lp);

        GlideUtil.displayImage(template.getPaperUrl(), ivBg);
        flContainer.addView(ivBg);

        Glide.with(getActivity())
                .load(template.getPaperUrl())
                .fitCenter()
                .into(ivBg);

        for (TemplateAreaObj areaObj : template.getTemplateList()) {
            View view = areaObj.getView(getActivity(), scale);
            if (view instanceof PhotoView) {
                photoView = (PhotoView) view;
                FrameLayout imageContainerFrameLayout = new FrameLayout(getActivity());
                FrameLayout.LayoutParams imageLP = (FrameLayout.LayoutParams) view.getLayoutParams();
                FrameLayout.LayoutParams imageContainerLP = new FrameLayout.LayoutParams(imageLP.width + ROTATION_SIZE, imageLP.height + ROTATION_SIZE);
                imageContainerLP.leftMargin = imageLP.leftMargin - ROTATION_SIZE / 2;
                imageContainerLP.topMargin = imageLP.topMargin - ROTATION_SIZE / 2;
                imageLP.leftMargin = ROTATION_SIZE / 2;
                imageLP.topMargin = ROTATION_SIZE / 2;
                view.setLayoutParams(imageLP);

                Glide.with(getActivity())
                        .load(imgObj.getLocalPath())
                        .fitCenter()
                        .into(((ImageView) view));

                ImageView rotationView = new ImageView(getActivity());
                FrameLayout.LayoutParams rotationLP = new FrameLayout.LayoutParams(ROTATION_SIZE, ROTATION_SIZE);
                rotationLP.gravity = Gravity.BOTTOM | Gravity.END;
                Glide.with(getActivity())
                        .load(R.drawable.cardrotate)
                        .fitCenter()
                        .into(rotationView);
                rotationView.setLayoutParams(rotationLP);

                imageContainerFrameLayout.setLayoutParams(imageContainerLP);
                imageContainerFrameLayout.addView(view);
                imageContainerFrameLayout.addView(rotationView);
                flContainer.addView(imageContainerFrameLayout);

                int left = imageContainerFrameLayout.getLeft();
                int right = imageContainerFrameLayout.getRight();
                int top = imageContainerFrameLayout.getTop();
                int bottom = imageContainerFrameLayout.getBottom();
                center.x = (left + right) / 2;
                center.y = (top + bottom) / 2;
                flContainer.setOnTouchListener(new View.OnTouchListener() {

                    private float oldRotation;

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                oldRotation = getRotate(event);
                                break;
                            case MotionEvent.ACTION_MOVE:
                                float rotation = getRotate(event) - oldRotation;
                                if (rotation > -5 && rotation < 5) {
                                    degree = rotation;
                                    imageContainerFrameLayout.setRotation(rotation);
                                    imageContainerFrameLayout.invalidate();
                                }

                                break;
                            case MotionEvent.ACTION_UP:

                                break;
                        }
                        return true;
                    }
                });
            } else if (view instanceof TextView) {
                flContainer.addView(view);
                if (areaObj.getTextType() == TemplateAreaObj.TEXT_TYPE_CONTENT) {
                    if (contentList != null && contentList.size() > 0) {
                        ((TextView) view).setText(contentList.get(0));
                        contentList.remove(0);
                    }
                } else if (areaObj.getTextType() == TemplateAreaObj.TEXT_TYPE_TITLE) {
                    ((TextView) view).setText(title);
                }
                areaObj.setText(((TextView) view).getText().toString());
            }
        }
        photoView.setScaleType(ImageView.ScaleType.CENTER_CROP);
    }

    private void setDataList(List<TemplateObj> dataList) {
        if (adapter == null) {
            adapter = new HorizontalListViewAdapter2(getActivity(), dataList);
            setViewData(dataList.get(0));

            adapter.setSelectIndex(0);
        } else {
            adapter.setList(dataList);
            adapter.notifyDataSetChanged();
        }
        lvHorizontal.setAdapter(adapter);
    }

    private void onRefresh() {
        onCreate(null);
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_complete, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.complete) {
            uploadImageObservable(imgObj.getLocalPath())
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

                        TemplateImage templateImage = new TemplateImage(degree, cropH, imgObj.getHeight(), imgObj.getWidth(), cropW, cropLeft, cropTop, objKey, System.currentTimeMillis());
                        for (TemplateAreaObj areaObj : templateObj.getTemplateList()) {
                            if (areaObj.getType() == TemplateAreaObj.TYPE_IMAGE) {
                                areaObj.setTemplateImage(templateImage);
                                break;
                            }
                        }
                        return templateObj;
                    })
                    .flatMap(templateObj1 -> apiService.diaryComposed(URLEncoder.encode(new Gson().toJson(templateObj1))))
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(() -> {
                        tfProgressDialog.setMessage("合成卡片中…");
                        tfProgressDialog.show();
                    })
                    .doOnTerminate(() -> {
                        if (tfProgressDialog != null && tfProgressDialog.isShowing()) {
                            tfProgressDialog.dismiss();
                        }
                    })
                    .subscribe(diaryComposedResponse -> {
                                if (diaryComposedResponse.success()) {
                                    MediaObj mediaObj = diaryComposedResponse.getMediaObj();
                                    mediaObj.setPhotographTime(System.currentTimeMillis());
                                    tfProgressDialog.dismiss();
                                    PublishActivity.open(getContext(), mediaObj);
                                    getActivity().finish();
                                    EventBus.getDefault().post(new DiaryPublishEvent());
                                } else {
                                    ToastUtil.showToast(diaryComposedResponse.getInfo());
                                }
                            }
                            , throwable -> {
                                Log.e(TAG, "diaryPublish:");
                            });
        }
        return super.onOptionsItemSelected(item);
    }

    // 取旋转角度
    private float getRotate(MotionEvent event) {
        double delta_x = (event.getX() - center.x);
        double delta_y = (event.getY() - center.y);
        double radians = Math.atan2(delta_y, delta_x);
        float degree = (float) Math.toDegrees(radians);
        return degree;
    }

    @Override
    public void onDestroy() {
        adapter = null;
        contentList = null;
        super.onDestroy();
    }
}
