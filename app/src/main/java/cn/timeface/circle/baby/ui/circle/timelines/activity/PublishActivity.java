package cn.timeface.circle.baby.ui.circle.timelines.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.SelectPhotoActivity;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.events.TimeEditPhotoDeleteEvent;
import cn.timeface.circle.baby.events.UploadEvent;
import cn.timeface.circle.baby.support.api.models.objs.ImgObj;
import cn.timeface.circle.baby.support.managers.services.UploadService;
import cn.timeface.circle.baby.support.utils.DateUtil;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.support.utils.Utils;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.circle.bean.CircleContentObj;
import cn.timeface.circle.baby.ui.circle.bean.CircleHomeworkObj;
import cn.timeface.circle.baby.ui.circle.bean.CircleMediaObj;
import cn.timeface.circle.baby.ui.circle.bean.CircleSchoolTaskObj;
import cn.timeface.circle.baby.ui.circle.bean.CircleTimelineObj;
import cn.timeface.circle.baby.ui.circle.bean.HomeWorkListObj;
import cn.timeface.circle.baby.ui.circle.timelines.adapter.PublishAdapter;
import cn.timeface.circle.baby.ui.circle.timelines.bean.ItemObj;
import cn.timeface.circle.baby.ui.circle.timelines.events.ActiveSelectEvent;
import cn.timeface.circle.baby.ui.circle.timelines.events.CircleMediaEvent;
import cn.timeface.circle.baby.ui.circle.timelines.events.CircleTimeLineEditEvent;
import cn.timeface.circle.baby.ui.circle.timelines.events.HomeWorkListEvent;
import cn.timeface.circle.baby.ui.circle.timelines.events.SchoolTaskEvent;
import cn.timeface.circle.baby.ui.circle.timelines.views.CircleGridStaggerLookup;
import cn.timeface.circle.baby.ui.images.views.DeleteDialog;
import cn.timeface.circle.baby.ui.timelines.Utils.JSONUtils;
import cn.timeface.circle.baby.ui.timelines.Utils.LogUtil;
import rx.Observable;

/**
 * author : wangshuai Created on 2017/3/15
 * email : wangs1992321@gmail.com
 */
public class PublishActivity extends BaseAppCompatActivity {

    public static final int PICTURE = 2017;
    public static final int TIME = 2018;

    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.content_recycler_view)
    RecyclerView contentRecyclerView;

    private PublishAdapter adapter = null;

    private int type = PublishAdapter.TYPE_TIMELINE;
    private CircleGridStaggerLookup lookup;
    private AlertDialog progressDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_publish);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        adapter = new PublishAdapter(this);
        init();
        lookup = new CircleGridStaggerLookup(type, 4, false);
        adapter.setLookup(lookup);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false);
        gridLayoutManager.setSpanSizeLookup(lookup);
        adapter.setType(type);
        contentRecyclerView.setAdapter(adapter);
        contentRecyclerView.setLayoutManager(gridLayoutManager);
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        hideProgress();
        ButterKnife.unbind(this);
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ActiveSelectEvent event) {
        CircleTimelineObj timelineObj = (CircleTimelineObj) adapter.getContentObj();
        timelineObj.setActivityAlbum(event.getActivityAlbumObj());
        adapter.setContentObj(timelineObj);
        adapter.notifyDataSetChanged();
    }

    private void init() {
        Bundle bundle = getIntent().getExtras();
        type = bundle.getInt("type", PublishAdapter.TYPE_TIMELINE);

        switch (type) {
            case PublishAdapter.TYPE_TIMELINE:
                title.setText(R.string.send_circle_time_line_title);
                CircleTimelineObj timelineObj = bundle.getParcelable(CircleTimelineObj.class.getSimpleName());
                adapter.setContentObj(timelineObj);
                if (timelineObj == null)
                    SelectPhotoActivity.openForResult(this, adapter.getSelImage(), PublishAdapter.MAX_PIC_TIMELINE_COUNT, PublishActivity.PICTURE);
                break;
            case PublishAdapter.TYPE_SCHOOL:
                title.setText(R.string.send_circle_school_title);
                CircleSchoolTaskObj schoolTaskObj = bundle.getParcelable(CircleSchoolTaskObj.class.getSimpleName());
                adapter.setContentObj(schoolTaskObj);
                break;
            case PublishAdapter.TYPE_WORK:
                title.setText(R.string.send_circle_homework_title);
                CircleHomeworkObj homeWork = bundle.getParcelable(CircleHomeworkObj.class.getSimpleName());
                adapter.setContentObj(homeWork);
                break;

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_complete, menu);
        menu.findItem(R.id.complete).setTitle(type == PublishAdapter.TYPE_WORK ? R.string.comfirm : R.string.publish);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.complete)
            sendTimeLine();
        else if (item.getItemId() == android.R.id.home) {
            DeleteDialog dialog = new DeleteDialog(this);
            dialog.setMessage("离开后编辑内容会丢失，您确定这么做吗？");
            dialog.setSubmitListener(() -> onBackPressed());
            dialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            DeleteDialog dialog = new DeleteDialog(this);
            dialog.setMessage("离开后编辑内容会丢失，您确定这么做吗？");
            dialog.setSubmitListener(() -> onBackPressed());
            dialog.show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void reqData() {
        addSubscription(apiService.queryLastSelect(FastData.getCircleId())
                .filter(createActiveResponse -> createActiveResponse.success())
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(createActiveResponse -> {
                    CircleTimelineObj timelineObj = (CircleTimelineObj) adapter.getContentObj();
                    timelineObj.setActivityAlbum(createActiveResponse.getActivityAlbum());
                    adapter.setContentObj(timelineObj);
                    adapter.notifyDataSetChanged();
                }, throwable -> LogUtil.showError(throwable)));
    }

    private void sendTimeLine() {
        if (adapter.getType() == PublishAdapter.TYPE_TIMELINE) {
            doTimeLine();
        } else if (adapter.getType() == PublishAdapter.TYPE_SCHOOL) {
            sendSchoolTask();
        } else if (adapter.getType() == PublishAdapter.TYPE_WORK) {
            sendHomwWork();
        }
    }

    private void clearGCMedia() {
        int size = adapter.getContentObj().getMediaList().size();
        for (int i = 0; i < size; ) {
            if (adapter.getContentObj().getMediaList().get(i).getId() <= 0) {
                adapter.getContentObj().getMediaList().remove(i);
                size--;
            } else i++;
        }
    }

    private void sendHomwWork() {
        CircleHomeworkObj homework = (CircleHomeworkObj) adapter.getContentObj();
        if (Utils.getByteSize(homework.getContent()) > 1200) {
            ToastUtil.showToast(this, String.format("%s" + getString(R.string.input_max_tip), "作业描述", 600));
            return;
        }
        for (ImgObj imgObj : adapter.getSelImage()) {
            if (!homework.getMediaList().contains(imgObj.getCircleMediaObj())) {
                homework.getMediaList().add(imgObj.getCircleMediaObj());
            }
        }
        List<String> list = new ArrayList<>(0);
        for (CircleMediaObj mediaObj : homework.getMediaList())
            if (mediaObj.getId() <= 0) list.add(mediaObj.getLocalPath());
        if (homework.getMediaList().size() > PublishAdapter.MAX_PIC_WORK_COUNT) {
            ToastUtil.showToast(this, String.format(getString(R.string.pic_select_max_tip), PublishAdapter.MAX_PIC_WORK_COUNT));
            return;
        }
        if (homework.getMediaList().size() <= 0 && TextUtils.isEmpty(homework.getContent())) {
            ToastUtil.showToast(this, "作业内容不能为空\n");
            return;
        }
        showProgress();
        UploadService.start(this, type, list);
    }

    private void doHomeWork() {
        CircleHomeworkObj homework = (CircleHomeworkObj) adapter.getContentObj();
        String send = new Gson().toJson(homework);
        addSubscription(apiService.homeWorkSubmit(homework.getTaskId(), send)
                .compose(SchedulersCompat.applyIoSchedulers())
                .doOnNext(homeWorkSubmitResponse -> hideProgress())
                .subscribe(homeWorkSubmitResponse -> {
                    if (homeWorkSubmitResponse.success()) {
                        EventBus.getDefault().post(new SchoolTaskEvent(SchoolTaskEvent.HOMEWORK_NEW_HOMEWORK, homeWorkSubmitResponse.getHomework()));
                        finish();
                    } else {
                        clearGCMedia();
                        ToastUtil.showToast(this, homeWorkSubmitResponse.getInfo());
                    }
                }, throwable -> {
                    clearGCMedia();
                    hideProgress();
                    LogUtil.showError(throwable);
                }));
    }

    private void sendSchoolTask() {
        CircleSchoolTaskObj schoolTask = (CircleSchoolTaskObj) adapter.getContentObj();
        if (TextUtils.isEmpty(schoolTask.getTitle())) {
            ToastUtil.showToast(this, getString(R.string.school_title_input_tip));
            return;
        } else if (Utils.getByteSize(schoolTask.getTitle()) > 20) {
            ToastUtil.showToast(this, String.format("%s" + getString(R.string.input_max_tip), "标题", 10));
            return;
        } else if (Utils.getByteSize(schoolTask.getContent()) > 1200) {
            ToastUtil.showToast(this, String.format("%s" + getString(R.string.input_max_tip), "作业描述", 600));
            return;
        }
        for (ImgObj imgObj : adapter.getSelImage()) {
            if (!schoolTask.getMediaList().contains(imgObj.getCircleMediaObj())) {
                schoolTask.getMediaList().add(imgObj.getCircleMediaObj());
            }
        }
        List<String> list = new ArrayList<>(0);
        for (CircleMediaObj mediaObj : schoolTask.getMediaList())
            if (mediaObj.getId() <= 0) list.add(mediaObj.getLocalPath());
        if (schoolTask.getMediaList().size() > PublishAdapter.MAX_PIC_WORK_COUNT) {
            ToastUtil.showToast(this, String.format(getString(R.string.pic_select_max_tip), PublishAdapter.MAX_PIC_WORK_COUNT));
            return;
        }
        if (schoolTask.getMediaList().size() <= 0 && TextUtils.isEmpty(schoolTask.getContent())) {
            ToastUtil.showToast(this, "作业内容不能为空\n");
            return;
        }
        showProgress();
        UploadService.start(this, type, list);
    }

    private void doSchool() {
        CircleSchoolTaskObj schoolTask = (CircleSchoolTaskObj) adapter.getContentObj();
        String send = new Gson().toJson(schoolTask);
        addSubscription(apiService.sendSchoolTask(FastData.getCircleId(), Uri.encode(send))
                .compose(SchedulersCompat.applyIoSchedulers())
                .doOnNext(circleSchoolTaskResponse -> hideProgress())
                .subscribe(circleSchoolTaskResponse -> {
                    if (circleSchoolTaskResponse.success()) {
                        EventBus.getDefault().post(new SchoolTaskEvent(SchoolTaskEvent.SCHOOLTASK_NEW_HOMEWORK, circleSchoolTaskResponse.getSchoolTask()));
                        finish();
                    } else {
                        clearGCMedia();
                        ToastUtil.showToast(this, circleSchoolTaskResponse.getInfo());
                    }
                }, throwable -> {
                    clearGCMedia();
                    hideProgress();
                    LogUtil.showError(throwable);
                }));
    }

    /**
     * 编辑  发布圈动态
     * 依据cirleTimeLineId是否大于0
     */
    private void doTimeLine() {
        CircleTimelineObj timelineObj = (CircleTimelineObj) adapter.getContentObj();
        if (timelineObj.getCircleTimelineId() <= 0) {
            timelineObj.setCreateDate(System.currentTimeMillis());
        }
        if (Utils.getByteSize(timelineObj.getTitle()) > 20) {
            ToastUtil.showToast(this, String.format("%s" + getString(R.string.input_max_tip), "标题", 10));
            return;
        } else if (Utils.getByteSize(timelineObj.getContent()) > 400) {
            ToastUtil.showToast(this, String.format("%s" + getString(R.string.input_max_tip), "内容", 200));
            return;
        }
        for (ImgObj imgObj : adapter.getSelImage()) {
            if (!timelineObj.getMediaList().contains(imgObj.getCircleMediaObj())) {
                timelineObj.getMediaList().add(imgObj.getCircleMediaObj());
            }
        }
        if (timelineObj.getMediaList().size() > PublishAdapter.MAX_PIC_TIMELINE_COUNT) {
            ToastUtil.showToast(this, String.format(getString(R.string.pic_select_max_tip), PublishAdapter.MAX_PIC_TIMELINE_COUNT));
            return;
        }
        List<String> list = new ArrayList<>(0);
        for (CircleMediaObj mediaObj : timelineObj.getMediaList())
            if (mediaObj.getId() <= 0) list.add(mediaObj.getLocalPath());
        if (timelineObj.getMediaList().size() <= 0) {
            ToastUtil.showToast(this, getString(R.string.please_send_some_pic));
            return;
        }
        String send = new Gson().toJson(timelineObj);
        showProgress();
        if (timelineObj.getCircleTimelineId() > 0) {
            addSubscription(apiService.editorCircleTimeLine(Uri.encode(send))
                    .compose(SchedulersCompat.applyIoSchedulers())
                    .doOnNext(circleTimeLineDetailResponse -> hideProgress())
                    .subscribe(
                            circleTimeLineDetailResponse -> {
                                LogUtil.showLog("info:" + circleTimeLineDetailResponse.getInfo());
                                if (circleTimeLineDetailResponse.success()) {
                                    EventBus.getDefault().post(new CircleTimeLineEditEvent(circleTimeLineDetailResponse.getCircleTimelineInfo()));
                                    if (list.size() > 0)
                                        UploadService.start(this, list);
                                    finish();
                                } else {
                                    clearGCMedia();
                                    ToastUtil.showToast(this, circleTimeLineDetailResponse.getInfo());
                                }
                            }
                            , throwable -> {
                                clearGCMedia();
                                hideProgress();
                                LogUtil.showError(throwable);
                            }));
        } else
            addSubscription(apiService.sendCircleTimeLine(FastData.getCircleId(), Uri.encode(send)).compose(SchedulersCompat.applyIoSchedulers())
                    .doOnNext(timeLineSendResponse -> hideProgress())
                    .subscribe(timeLineSendResponse -> {
                        if (timeLineSendResponse.success()) {
                            timelineObj.setCircleTimelineId(timeLineSendResponse.getCircleTimeline().getCircleTimelineId());
                            timelineObj.setPublisher(FastData.getCircleUserInfo());
                            if (list.size() > 0)
                                UploadService.start(this, list);
                            EventBus.getDefault().post(new CircleTimeLineEditEvent(2, timelineObj));
                            finish();
                        } else {
                            clearGCMedia();
                            ToastUtil.showToast(this, timeLineSendResponse.getInfo());
                        }
                    }, throwable -> {
                        clearGCMedia();
                        hideProgress();
                        LogUtil.showError(throwable);
                    }));
    }

    private void hideProgress() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }


    private void showProgress() {
        if (progressDialog == null) {
            progressDialog = new AlertDialog.Builder(this).setView(initProgress()).show();
            progressDialog.setCanceledOnTouchOutside(false);
            Window window = progressDialog.getWindow();
            window.setGravity(Gravity.CENTER);
            window.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
            window.setWindowAnimations(R.style.bottom_dialog_animation);
        }
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    private View initProgress() {
        View view = View.inflate(this, R.layout.view_upload_progress, null);
        ImageView ivLoad = (ImageView) view.findViewById(R.id.pb_loading);
        TextView tvProgress = (TextView) view.findViewById(R.id.tv_progress);
        tvProgress.setText("正在发送请求~~");
        ((Animatable) ivLoad.getDrawable()).start();
        return view;
    }

    @Subscribe
    public void onEvent(UploadEvent event) {
        if (event.isComplete()) {
            switch (event.getTimeId()) {
                case PublishAdapter.TYPE_SCHOOL:
                    doSchool();
                    break;
                case PublishAdapter.TYPE_WORK:
                    doHomeWork();
                    break;
            }
        }
    }

    @Subscribe
    public void onEvent(CircleMediaEvent event) {
        Observable.defer(() -> Observable.from(adapter.getSelImage()))
                .map(imgObj -> imgObj.getCircleMediaObj())
                .toList()
                .doOnNext(circleMediaObjs -> circleMediaObjs.addAll(event.getMediaObj().getId() > 0 ? adapter.getContentObj().getMediaList() : new ArrayList<CircleMediaObj>(0)))
                .flatMap(circleMediaObjs -> Observable.from(circleMediaObjs))
                .filter(mediaObj -> mediaObj.getId() == event.getMediaObj().getId() && mediaObj.getImgUrl().equals(event.getMediaObj().getImgUrl()) && mediaObj.getLocalPath().equals(event.getMediaObj().getLocalPath()))
                .subscribe(mediaObj -> {
                    if (event.getType() == 0) {
                        mediaObj.setRelateBabys(event.getMediaObj().getRelateBabys());
                        mediaObj.setTips(event.getMediaObj().getTips());
                        mediaObj.setFavoritecount(event.getMediaObj().getFavoritecount());
                        mediaObj.setIsFavorite(event.getMediaObj().getIsFavorite());
                    } else {
                        boolean remove = adapter.getSelImage().remove(event.getMediaObj().getImgObj());
                        LogUtil.showLog("remove=====" + remove);
                        adapter.getContentObj().getMediaList().remove(event.getMediaObj());
                    }
                    adapter.notifyDataSetChanged();
                }, throwable -> LogUtil.showError(throwable));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            switch (requestCode) {
                case PICTURE:
                    ArrayList<ImgObj> selImages = data.getParcelableArrayListExtra("result_select_image_list");
                    if (selImages != null) {
                        adapter.getSelImage().clear();
                        adapter.getSelImage().addAll(selImages);
                        adapter.notifyDataSetChanged();
                    } else if (type == PublishAdapter.TYPE_TIMELINE && adapter.getContentObj().getMediaList().size() <= 0)
                        finish();
                    break;
                case TIME:
                    long time = data.getLongExtra("timeMillis", System.currentTimeMillis());
                    String currentTimeStr = DateUtil.formatDate("HH:mm:ss", System.currentTimeMillis());
                    time += DateUtil.getTime(currentTimeStr, "HH:mm");
                    CircleTimelineObj contentObj = (CircleTimelineObj) adapter.getContentObj();
                    contentObj.setRecordDate(time);
                    adapter.setContentObj(contentObj);
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    }


    /**
     * 提交作业
     *
     * @param context
     * @param homeworkObj
     */
    public static void open(Context context, @NonNull CircleHomeworkObj homeworkObj) {
        if (TextUtils.isEmpty(homeworkObj.getTitle()) || homeworkObj.getTaskId() <= 0) {
            ToastUtil.showToast(context, context.getString(R.string.please_select_school_work));
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putInt("type", PublishAdapter.TYPE_WORK);
        bundle.putParcelable(CircleHomeworkObj.class.getSimpleName(), homeworkObj);
        context.startActivity(new Intent(context, PublishActivity.class).putExtras(bundle));
    }

    /**
     * 发布作业
     *
     * @param context
     */
    public static void openSchoolTask(Context context) {
        Bundle bundle = new Bundle();
        bundle.putInt("type", PublishAdapter.TYPE_SCHOOL);
        context.startActivity(new Intent(context, PublishActivity.class).putExtras(bundle));
    }

    /**
     * 布置或者编辑作业
     *
     * @param context
     * @param timlineObj
     */
    public static void open(Context context, CircleSchoolTaskObj timlineObj) {
        Bundle bundle = new Bundle();
        bundle.putInt("type", PublishAdapter.TYPE_SCHOOL);
        bundle.putParcelable(CircleSchoolTaskObj.class.getSimpleName(), timlineObj);
        context.startActivity(new Intent(context, PublishActivity.class).putExtras(bundle));
    }

    /**
     * 发送或者编辑圈动态
     *
     * @param context
     * @param timlineObj
     */
    public static void open(Context context, CircleTimelineObj timlineObj) {
        Bundle bundle = new Bundle();
        bundle.putInt("type", PublishAdapter.TYPE_TIMELINE);
        bundle.putParcelable(CircleTimelineObj.class.getSimpleName(), timlineObj);
        context.startActivity(new Intent(context, PublishActivity.class).putExtras(bundle));
    }

    /**
     * 上下文  默认发布圈动态
     *
     * @param context
     */
    public static void open(Context context) {
        Bundle bundle = new Bundle();
        bundle.putInt("type", PublishAdapter.TYPE_TIMELINE);
        context.startActivity(new Intent(context, PublishActivity.class).putExtras(bundle));
    }


}
