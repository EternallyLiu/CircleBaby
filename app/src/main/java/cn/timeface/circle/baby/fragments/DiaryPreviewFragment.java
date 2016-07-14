package cn.timeface.circle.baby.fragments;


import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.ServiceException;
import com.github.rayboot.widget.ratioview.RatioImageView;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.adapters.HorizontalListViewAdapter2;
import cn.timeface.circle.baby.api.models.objs.ImgObj;
import cn.timeface.circle.baby.api.models.objs.MediaObj;
import cn.timeface.circle.baby.api.models.objs.MyUploadFileObj;
import cn.timeface.circle.baby.api.models.objs.TemplateAreaObj;
import cn.timeface.circle.baby.api.models.objs.TemplateImage;
import cn.timeface.circle.baby.api.models.objs.TemplateObj;
import cn.timeface.circle.baby.api.models.responses.DiaryPaperResponse;
import cn.timeface.circle.baby.events.MediaObjEvent;
import cn.timeface.circle.baby.fragments.base.BaseFragment;
import cn.timeface.circle.baby.oss.OSSManager;
import cn.timeface.circle.baby.oss.uploadservice.UploadFileObj;
import cn.timeface.circle.baby.utils.DateUtil;
import cn.timeface.circle.baby.utils.GlideUtil;
import cn.timeface.circle.baby.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.views.HorizontalListView;
import cn.timeface.circle.baby.views.ScaleImageView;

public class DiaryPreviewFragment extends BaseFragment implements View.OnClickListener {

    @Bind(R.id.tv_complete)
    TextView tvComplete;
    @Bind(R.id.tv_time)
    TextView tvTime;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.iv_bg)
    RatioImageView ivBg;
    @Bind(R.id.rl_diary)
    RelativeLayout rlDiary;
    @Bind(R.id.rldiary)
    RelativeLayout rldiary;
    @Bind(R.id.tv_content)
    TextView tvContent;
    @Bind(R.id.lv_horizontal)
    HorizontalListView lvHorizontal;
    @Bind(R.id.iv_rotate)
    ImageView ivRotate;

    private HorizontalListViewAdapter2 adapter;
    private DiaryPaperResponse diaryPaperResponse;
    private String time;
    private String content;
    private String url;
    private ScaleImageView touchImageView;
    PointF center = new PointF();
    private float oldRotation;
    private int width;
    private int hight;
    private String objectKey;
    private int paperId;
    private TemplateObj templateObj;
    private ImgObj imgObj;
    private String date;
    private float degree;

    public DiaryPreviewFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        time = getArguments().getString("time");
        content = getArguments().getString("content");
        imgObj = getArguments().getParcelable("imgObj");
        url = imgObj.getLocalPath();
        date = imgObj.getDate();
        uploadImage();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_diarypreview, container, false);
        ButterKnife.bind(this, view);
        setActionBar(toolbar);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        tvTime.setText(time);
        tvContent.setText(content);
        touchImageView = new ScaleImageView(getActivity(), url);
        rlDiary.addView(touchImageView,0);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) touchImageView.getLayoutParams();
        layoutParams.height = RelativeLayout.LayoutParams.MATCH_PARENT;
        layoutParams.width = RelativeLayout.LayoutParams.MATCH_PARENT;
        touchImageView.setLayoutParams(layoutParams);


        int left = rlDiary.getLeft();
        int right = rlDiary.getRight();
        int top = rlDiary.getTop();
        int bottom = rlDiary.getBottom();
        center.x = left + right / 2;
        center.y = top + bottom / 2;
//        rldiary.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        oldRotation = getRotate(event);
//                        break;
//                    case MotionEvent.ACTION_MOVE:
//                        float rotation = getRotate(event) - oldRotation;
//                        if (rotation > -15 && rotation < 15) {
//                            degree = rotation;
//                            rlDiary.setRotation(rotation);
//                            rlDiary.invalidate();
//                        }
//
//                        break;
//                    case MotionEvent.ACTION_UP:
//
//                        break;
//                }
//                return true;
//            }
//        });

        ivRotate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        oldRotation = getRotate(event);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float rotation = getRotate(event) - oldRotation;
                        if (rotation > -15 && rotation < 15) {
                            degree = rotation;
                            rlDiary.setRotation(rotation);
                            rlDiary.invalidate();

                        }

                        break;
                    case MotionEvent.ACTION_UP:

                        break;
                }
                return true;
            }
        });


//        GlideUtil.displayImage(url, scaleImageView);

        reqPaperList();

        lvHorizontal.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GlideUtil.displayImage(diaryPaperResponse.getDataList().get(position).getPaperUrl(), ivBg);
                adapter.setSelectIndex(position);
                adapter.notifyDataSetChanged();
                paperId = diaryPaperResponse.getDataList().get(position).getPaperId();
                templateObj = diaryPaperResponse.getDataList().get(position);
            }
        });
        tvComplete.setOnClickListener(this);


        return view;
    }

    // 取旋转角度
    private float getRotate(MotionEvent event) {
        double delta_x = (event.getX() - center.x);
        double delta_y = (event.getY() - center.y);
        double radians = Math.atan2(delta_y, delta_x);
        float degree = (float) Math.toDegrees(radians);
//        if (degree < -15) {
//            return -15;
//        } else if (degree >= -15 && degree < 15) {
//            return degree;
//        } else {
//            return 15;
//        }
        return degree;
    }


    public Bitmap myShot(Activity activity) {
        // 获取windows中最顶层的view
        View view = activity.getWindow().getDecorView();
        view.buildDrawingCache();
        // 获取状态栏高度
        Rect rect = new Rect();
        view.getWindowVisibleDisplayFrame(rect);
        int statusBarHeights = rect.top;
        Display display = activity.getWindowManager().getDefaultDisplay();
        // 获取屏幕宽和高
        int widths = display.getWidth();
        int heights = display.getHeight();
        // 允许当前窗口保存缓存信息
        view.setDrawingCacheEnabled(true);
        // 去掉状态栏
        Bitmap bmp = Bitmap.createBitmap(view.getDrawingCache(), 0,
                statusBarHeights, widths, heights - statusBarHeights);
        // 销毁缓存信息
        view.destroyDrawingCache();
        return bmp;
    }


    private void reqPaperList() {
        apiService.getPaperList()
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(diaryPaperResponse -> {
                    this.diaryPaperResponse = diaryPaperResponse;
                    setDataList(diaryPaperResponse.getDataList());
                }, throwable -> {
                    Log.e(TAG, "getPaperList:");
                });

    }

    private void setDataList(List<TemplateObj> dataList) {
        if (adapter == null) {
            adapter = new HorizontalListViewAdapter2(getActivity(), dataList);
            GlideUtil.displayImage(dataList.get(0).getPaperUrl(), ivBg);
            adapter.setSelectIndex(0);
            paperId = dataList.get(0).getPaperId();
            templateObj = dataList.get(0);
        } else {
            adapter.setList(dataList);
            adapter.notifyDataSetChanged();
        }
        lvHorizontal.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_complete:
                //日记卡片合成
//                BitmapDrawable bd = new BitmapDrawable(myShot(getActivity()));
//                ivBg.setBackgroundDrawable(bd);
//                ivBg.setImageBitmap(myShot(getActivity()));


                PointF leftTop = touchImageView.getLeftTop();
//                float degree = touchImageView.getDegree();
                float cropWidth = touchImageView.getCropWidth();
                float cropHeight = touchImageView.getCropHeight();
                int bitmapWidth = touchImageView.getBitmapWidth();
                int bitmapHeight = touchImageView.getBitmapHeight();
                String content = tvContent.getText().toString();
                String time = tvTime.getText().toString();

                List<String> contents = new ArrayList<>();
                contents.add(time);
                while (content.length() > 18) {
                    char c = content.charAt(17);
                    String[] split = content.split(String.valueOf(c));
                    contents.add(split[0]);
                    content = split[1];
                }
                contents.add(content);

//                DiaryImageInfo diaryImageInfo = new DiaryImageInfo(objectKey, bitmapWidth, bitmapHeight);
//                Gson gson = new Gson();
//                String imageInfo = gson.toJson(diaryImageInfo);

//                apiService.diaryPublish(content, imageInfo, time, cropWidth, cropHeight, leftTop.x, leftTop.y, paperId, degree)
//                        .compose(SchedulersCompat.applyIoSchedulers())
//                        .subscribe(diaryComposeResponse -> {
//                            ImgObj imgObj = diaryComposeResponse.getImgObj();
//                        }, throwable -> {
//                            Log.e(TAG, "diaryPublish:");
//                        });
                long createTime = DateUtil.getTime(date, "yyyy.MM.dd");
                TemplateImage templateImage = new TemplateImage(degree, cropHeight, bitmapHeight, bitmapWidth, cropWidth, leftTop.x, leftTop.y, objectKey, createTime);

                List<TemplateAreaObj> templateList = templateObj.getTemplateList();

                for (TemplateAreaObj templateAreaObj : templateList) {
                    if (templateAreaObj.getType() == 3)
                        templateAreaObj.setTemplateImage(templateImage);
                    if (contents.size() > 0 && templateAreaObj.getType() == 2) {
                        templateAreaObj.setText(contents.get(0));
                        contents.remove(0);
                    }
                }
                Gson gson = new Gson();
                String s = gson.toJson(templateObj);
                System.out.println(s);
                apiService.diaryComposed(URLEncoder.encode(s))
                        .compose(SchedulersCompat.applyIoSchedulers())
                        .subscribe(diaryComposedResponse -> {
                            MediaObj mediaObj = diaryComposedResponse.getMediaObj();
                            System.out.println("合成的日记图片===============" + mediaObj.getImgUrl());
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
