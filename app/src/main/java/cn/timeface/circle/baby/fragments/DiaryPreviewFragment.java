package cn.timeface.circle.baby.fragments;


import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.FloatMath;
import android.util.Log;
import android.view.Display;
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
import cn.timeface.circle.baby.utils.ToastUtil;
import cn.timeface.circle.baby.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.views.HorizontalListView;
import cn.timeface.circle.baby.views.ScaleImageView;
import cn.timeface.circle.baby.views.dialog.TFProgressDialog;

public class DiaryPreviewFragment extends BaseFragment {

    @Bind(R.id.tv_time)
    TextView tvTime;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.iv_bg)
    RatioImageView ivBg;
    @Bind(R.id.rl_cover)
    RelativeLayout rlCover;
    @Bind(R.id.rldiary)
    RelativeLayout rldiary;
    @Bind(R.id.tv_content)
    TextView tvContent;
    @Bind(R.id.lv_horizontal)
    HorizontalListView lvHorizontal;
    @Bind(R.id.iv_rotate)
    ImageView ivRotate;
    @Bind(R.id.fl)
    FrameLayout fl;

    private HorizontalListViewAdapter2 adapter;
    private DiaryPaperResponse diaryPaperResponse;
    private String time;
    private String content;
    private String url;
    private ScaleImageView touchImageView;
    private PointF center = new PointF();
    private PointF mPreMovePointF = new PointF();
    private PointF mCurMovePointF = new PointF();
    private float oldRotation;
    private int width;
    private int hight;
    private String objectKey;
    private int paperId;
    private TemplateObj templateObj;
    private ImgObj imgObj;
    private String date;
    private float degree = 0;
    private float mDegree = 0;
    private TFProgressDialog tfProgressDialog;

    public DiaryPreviewFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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
        ActionBar actionBar = getActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        tfProgressDialog = new TFProgressDialog(getActivity());
        tvTime.setText(time);
        tvContent.setText(content);
        touchImageView = new ScaleImageView(getActivity(), url);
        rlCover.addView(touchImageView, 0);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) touchImageView.getLayoutParams();
        layoutParams.height = RelativeLayout.LayoutParams.MATCH_PARENT;
        layoutParams.width = RelativeLayout.LayoutParams.MATCH_PARENT;
        touchImageView.setLayoutParams(layoutParams);

        int left = rlCover.getLeft();
        int right = rlCover.getRight();
        int top = rlCover.getTop();
        int bottom = rlCover.getBottom();
        center.x = (left + right) / 2;
        center.y = (top + bottom) / 2;
        rldiary.setOnTouchListener(new View.OnTouchListener() {
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
                            fl.setRotation(rotation);
                            fl.invalidate();
                        }

                        break;
                    case MotionEvent.ACTION_UP:

                        break;
                }
                return true;
            }
        });

//        ivRotate.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        mPreMovePointF.set(event.getX(), event.getY());
//                        break;
//                    case MotionEvent.ACTION_MOVE:
//                        mCurMovePointF.set(event.getX(), event.getY());
//
//                        // 角度
//                        double a = distance4PointF(center, mPreMovePointF);
//                        double b = distance4PointF(mPreMovePointF, mCurMovePointF);
//                        double c = distance4PointF(center, mCurMovePointF);
//
//                        double cosb = (a * a + c * c - b * b) / (2 * a * c);
//
//                        if (cosb >= 1) {
//                            cosb = 1f;
//                        }
//
//                        double radian = Math.acos(cosb);
//                        float newDegree = (float) radianToDegree(radian);
//
//                        //center -> proMove的向量， 我们使用PointF来实现
//                        PointF centerToProMove = new PointF((mPreMovePointF.x - center.x), (mPreMovePointF.y - center.y));
//
//                        //center -> curMove 的向量
//                        PointF centerToCurMove = new PointF((mCurMovePointF.x - centerToProMove.x), (mCurMovePointF.y - centerToProMove.y));
//
//                        //向量叉乘结果, 如果结果为负数， 表示为逆时针， 结果为正数表示顺时针
//                        float result = centerToProMove.x * centerToCurMove.y - centerToProMove.y * centerToCurMove.x;
//
//                        if (result < 0) {
//                            newDegree = -newDegree;
//                        }
//                        mDegree = mDegree + newDegree;
//                        if (mDegree >= -15 && mDegree <= 15) {
//                            degree = mDegree;
//                            fl.setRotation(mDegree);
//                            fl.invalidate();
//                        }else if(mDegree<-15){
//                            mDegree = -15;
//                        }else if(mDegree>15){
//                            mDegree = 15;
//                        }
//                        mPreMovePointF.set(mCurMovePointF);
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        break;
//                }
//                return true;
//            }
//        });


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
        return view;
    }

    private float distance4PointF(PointF pf1, PointF pf2) {
        float disX = pf2.x - pf1.x;
        float disY = pf2.y - pf1.y;
        return (float) Math.sqrt(disX * disX + disY * disY);
    }

    /**
     * 弧度换算成角度
     */
    public static double radianToDegree(double radian) {
        return radian * 180 / Math.PI;
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


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_complete, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.complete){
            tfProgressDialog.setMessage("合成卡片中…");
            tfProgressDialog.show();
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
                        tfProgressDialog.dismiss();
                        getActivity().finish();
                        EventBus.getDefault().post(new MediaObjEvent(mediaObj));
                    }, throwable -> {
                        Log.e(TAG, "diaryPublish:");
                    });
        }
        return super.onOptionsItemSelected(item);
    }
}
