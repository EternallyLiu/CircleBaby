package cn.timeface.circle.baby.ui.circle.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.MyPODActivity;
import cn.timeface.circle.baby.events.BookOptionEvent;
import cn.timeface.circle.baby.support.api.models.objs.MediaObj;
import cn.timeface.circle.baby.support.managers.listeners.IEventBus;
import cn.timeface.circle.baby.support.mvp.bases.BasePresenterAppCompatActivity;
import cn.timeface.circle.baby.support.mvp.model.BookModel;
import cn.timeface.circle.baby.support.utils.DateUtil;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.circle.bean.CircleHomeworkExObj;
import cn.timeface.circle.baby.ui.circle.bean.CircleMediaObj;
import cn.timeface.circle.baby.ui.circle.bean.CircleTimeLineExObj;
import cn.timeface.circle.baby.ui.circle.bean.CircleTimeLineWrapperObj;
import cn.timeface.circle.baby.ui.circle.dialogs.CircleSelectTimeTypeDialog;
import cn.timeface.circle.baby.ui.circle.events.CircleSelectMediaEvent;
import cn.timeface.circle.baby.ui.circle.events.CircleSelectMediaListEvent;
import cn.timeface.circle.baby.ui.circle.events.CircleSelectTimeLineEvent;
import cn.timeface.circle.baby.ui.circle.fragments.CircleServerTimeFragment;
import cn.timeface.circle.baby.ui.growth.events.SelectMediaEvent;
import cn.timeface.circle.baby.ui.growth.events.SelectMediaListEvent;
import cn.timeface.circle.baby.views.TFStateView;
import cn.timeface.open.api.bean.obj.TFOContentObj;
import cn.timeface.open.api.bean.obj.TFOPublishObj;
import cn.timeface.open.api.bean.obj.TFOResourceObj;

/**
 * 圈作品-时光书
 * author : sunyanwei Created on 17-3-20
 * email : sunyanwei@timeface.cn
 */
public class CircleSelectServerTimesActivity extends BasePresenterAppCompatActivity
        implements CircleSelectTimeTypeDialog.SelectTypeListener, View.OnClickListener, IEventBus {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.fl_container)
    FrameLayout flContainer;
    @Bind(R.id.cb_all_sel)
    CheckBox cbAllSel;
    @Bind(R.id.tv_sel_count)
    TextView tvSelCount;
    @Bind(R.id.rl_photo_tip)
    RelativeLayout rlPhotoTip;
    @Bind(R.id.tf_stateView)
    TFStateView tfStateView;
    @Bind(R.id.content_select_time)
    RelativeLayout contentSelectTime;
    @Bind(R.id.tv_content_type)
    TextView tvContentType;

    CircleServerTimeFragment allTimeFragment;//全部动态
    CircleServerTimeFragment mineTimeFragment;//我发布的动态
    CircleServerTimeFragment aboutBabyTimeFragment;//与我宝宝相关的动态
    CircleSelectTimeTypeDialog selectContentTypeDialog;
    int openBookType;
    int bookType;
    String bookId;
    String openBookId;
    String circleId;

    List<CircleMediaObj> allSelectMedias = new ArrayList<>();
    List<CircleTimeLineExObj> allSelectTimeLines = new ArrayList<>();

    public static void open(Context context, int bookType, int openBookType, String bookId, String openBookId, String circleId) {
        Intent intent = new Intent(context, CircleSelectServerTimesActivity.class);
        intent.putExtra("open_book_type", openBookType);
        intent.putExtra("book_type", bookType);
        intent.putExtra("book_id", bookId);
        intent.putExtra("open_book_id", openBookId);
        intent.putExtra("circle_id", circleId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_select_server_times);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        tvContentType.setOnClickListener(this);
        this.openBookType = getIntent().getIntExtra("open_book_type", 0);
        this.bookType = getIntent().getIntExtra("book_type", 0);
        this.bookId = getIntent().getStringExtra("book_id");
        this.openBookId = getIntent().getStringExtra("open_book_id");
        this.circleId = getIntent().getStringExtra("circle_id");
        cbAllSel.setOnClickListener(this);

        //新建一本
        if(TextUtils.isEmpty(bookId)){
            tfStateView.setVisibility(View.GONE);
            allTimeFragment = CircleServerTimeFragment.newInstance(
                    CircleSelectTimeTypeDialog.TIME_TYPE_ALL,
                    circleId,
                    allSelectMedias,
                    allSelectTimeLines);
            allTimeFragment.setTimeLineObjs(allSelectTimeLines);
            allTimeFragment.setMediaObjs(allSelectMedias);
            showContent(allTimeFragment);
        } else {
            addSubscription(
                    apiService.queryBookTimes(bookId)
                            .compose(SchedulersCompat.applyIoSchedulers())
                            .subscribe(
                                    response -> {
                                        if (response.success()) {
                                            this.allSelectTimeLines.addAll(response.getDataList());

                                            tfStateView.setVisibility(View.GONE);
                                            allTimeFragment = CircleServerTimeFragment.newInstance(
                                                    CircleSelectTimeTypeDialog.TIME_TYPE_ALL,
                                                    circleId,
                                                    allSelectMedias,
                                                    allSelectTimeLines);
                                            allTimeFragment.setTimeLineObjs(allSelectTimeLines);
                                            allTimeFragment.setMediaObjs(allSelectMedias);
                                            showContent(allTimeFragment);
                                        } else {
                                            showToast(response.info);
                                        }
                                    },
                                    throwable -> {
                                        Log.e(TAG, throwable.getLocalizedMessage());
                                    }
                            )
            );
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_publish_finish, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_complete){
            int pageNum = allSelectTimeLines.size();
            if(pageNum == 0){
                ToastUtil.showToast("请选择至少一条时光");
                return true;
            }

            addSubscription(
                    apiService.queryImageInfo(FastData.getBabyAvatar())
                            .compose(SchedulersCompat.applyIoSchedulers())
                            .subscribe(
                                    response -> {
                                        if (response.success()) {
                                            //跳转开放平台POD接口；
                                            String bookName = FastData.getBabyNickName() + "的圈时光书";

                                            //按月份分组时光
                                            HashMap<String, List<CircleTimeLineExObj>> timelineMap = new HashMap<>();
                                            for (CircleTimeLineExObj timeLineExObj : allSelectTimeLines) {
                                                String key = DateUtil.formatDate("yyyy-MM", timeLineExObj.getCircleTimeline().getRecordDate());
                                                if (timelineMap.containsKey(key)) {
                                                    timelineMap.get(key).add(timeLineExObj);
                                                } else {
                                                    List<CircleTimeLineExObj> timeLineObjs = new ArrayList<>();
                                                    timeLineObjs.add(timeLineExObj);
                                                    timelineMap.put(key, timeLineObjs);
                                                }
                                            }


                                            //组装发布数据
                                            //每个月一个tfopublishobj
                                            List<TFOPublishObj> tfoPublishObjs = new ArrayList<>();

                                            Iterator iterator = timelineMap.entrySet().iterator();
                                            while (iterator.hasNext()) {
                                                Map.Entry entry = (Map.Entry) iterator.next();
                                                String key = (String) entry.getKey();
                                                List<CircleTimeLineExObj> timeLineObjs = (List<CircleTimeLineExObj>) entry.getValue();

                                                //content list，代表一个时光
                                                List<TFOContentObj> tfoContentObjs = new ArrayList<>(allSelectTimeLines.size());
                                                for (CircleTimeLineExObj timeLineObj : allSelectTimeLines) {
                                                    tfoContentObjs.add(timeLineObj.getCircleTimeline().toTFOContentObj());
                                                }

                                                TFOPublishObj tfoPublishObj = new TFOPublishObj(key, tfoContentObjs);
                                                tfoPublishObjs.add(tfoPublishObj);
                                            }


                                            ArrayList<String> keys = new ArrayList<>();
                                            ArrayList<String> values = new ArrayList<>();
                                            keys.add("book_author");
                                            keys.add("book_title");
                                            values.add(FastData.getUserName());
                                            values.add(bookName);

                                            //拼接所有图片的id，作为保存书籍接口使用
                                            StringBuffer sb = new StringBuffer("{\"mediaIds\":[");
                                            StringBuffer sbTime = new StringBuffer("\"timeIds\":[");
                                            for (CircleTimeLineExObj timeLineObj : allSelectTimeLines) {
                                                if (timeLineObj != null) {
                                                    sbTime.append(timeLineObj.getCircleTimeline().getCircleTimelineId());
                                                    sbTime.append(",");

                                                    if (!timeLineObj.getCircleTimeline().getMediaList().isEmpty()) {
                                                        for (MediaObj mediaObj : timeLineObj.getCircleTimeline().getMediaList()) {
                                                            if(allSelectMedias.contains(mediaObj)){
                                                                sb.append(mediaObj.getId());
                                                                sb.append(",");
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            if(sb.lastIndexOf(",") > -1)sb.replace(sb.lastIndexOf(","), sb.length(), "]");
                                            if(sbTime.lastIndexOf(",") > -1)sbTime.replace(sbTime.lastIndexOf(","), sbTime.length(), "]");
                                            sbTime.append(",\"circleId\":").append(circleId);
                                            sb.append(",").append(sbTime).append("}");

                                            finish();
                                            MyPODActivity.open(this, bookId, openBookId, bookType, openBookType, tfoPublishObjs, sb.toString(), true, FastData.getBabyId(), keys, values, 1);
                                        }
                                    },
                                    throwable -> Log.e(TAG, throwable.getLocalizedMessage())
                            ));

            return true;
        } else if(item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    Fragment currentFragment = null;
    public void showContent(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if (currentFragment != null) {
            ft.hide(currentFragment);
        }
        if (fragment.isAdded()) {
            ft.show(fragment);
        } else {
            ft.add(R.id.fl_container, fragment);
        }
        currentFragment = fragment;

        if(currentFragment instanceof CircleServerTimeFragment){
//            rlPhotoTip.setVisibility(View.VISIBLE);
            initAllSelectView(((CircleServerTimeFragment) currentFragment).isAllSelect(), allSelectTimeLines.size());
        } else {
            rlPhotoTip.setVisibility(View.GONE);
        }

        ft.commitAllowingStateLoss();
    }

    @Override
    public void selectTypeAll() {
        tvContentType.setText("全部动态");
        if(allTimeFragment == null){
            allTimeFragment = CircleServerTimeFragment.newInstance(
                    CircleSelectTimeTypeDialog.TIME_TYPE_ALL,
                    circleId,
                    allSelectMedias,
                    allSelectTimeLines);
        }
        showContent(allTimeFragment);
    }

    @Override
    public void selectTypeMe() {
        tvContentType.setText("我发布的动态");
        if(mineTimeFragment == null){
            mineTimeFragment = CircleServerTimeFragment.newInstance(
                    CircleSelectTimeTypeDialog.TIME_TYPE_ME,
                    circleId,
                    allSelectMedias,
                    allSelectTimeLines);
        }
        showContent(mineTimeFragment);
    }

    @Override
    public void selectTypeAboutMyBaby() {
        tvContentType.setText("与我宝宝相关的动态");
        if(aboutBabyTimeFragment == null){
            aboutBabyTimeFragment = CircleServerTimeFragment.newInstance(
                    CircleSelectTimeTypeDialog.TIME_TYPE_ABOUT_BABY,
                    circleId,
                    allSelectMedias,
                    allSelectTimeLines);
        }
        showContent(aboutBabyTimeFragment);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_content_type:
                if(selectContentTypeDialog == null){
                    selectContentTypeDialog = CircleSelectTimeTypeDialog.newInstance(this);
                }
                selectContentTypeDialog.show(getSupportFragmentManager(), "tag");
                break;

            case R.id.cb_all_sel:
                if(cbAllSel.isChecked()){
                    ((CircleServerTimeFragment) currentFragment).doAllSelImg();
                    if(allSelectTimeLines.size() > 0){
                        //处理全选照片处理
                        for(CircleTimeLineExObj circleTimeLineExObj : allSelectTimeLines){
                            allSelectMedias.addAll(circleTimeLineExObj.getCircleTimeline().getMediaList());
                        }
                    }
                    cbAllSel.setChecked(false);
                } else {
                    ((CircleServerTimeFragment) currentFragment).doAllUnSelImg();
                    cbAllSel.setChecked(true);
                    allSelectMedias.clear();
                }

                initAllSelectView(!cbAllSel.isChecked(), allSelectTimeLines.size());
                break;
        }
    }

    public void setPhotoTipVisibility(int visibility){
        rlPhotoTip.setVisibility(visibility);
        if(currentFragment instanceof CircleServerTimeFragment) {
            initAllSelectView(((CircleServerTimeFragment) currentFragment).isAllSelect(), allSelectTimeLines.size());
        }
    }

    private void initAllSelectView(boolean allSelect, int selectCount){
        cbAllSel.setChecked(allSelect);
        tvSelCount.setText(Html.fromHtml(String.format(getString(R.string.select_server_time_select_count), String.valueOf(selectCount))));
    }

    @Subscribe
    public void bookOptionEvent(BookOptionEvent optionEvent){
        if(optionEvent.getOption() == BookOptionEvent.BOOK_OPTION_CREATE){
            finish();
        }
    }

    @Subscribe
    public void selectMediaEvent(CircleSelectMediaEvent selectMediaEvent){
        if(selectMediaEvent.getType() != SelectMediaEvent.TYPE_TIME_MEDIA) return;
        //选中
        if(selectMediaEvent.getSelect()){
            if(!allSelectMedias.contains(selectMediaEvent.getMediaObj())){
                allSelectMedias.add(selectMediaEvent.getMediaObj());
            }
        } else {
            if(allSelectMedias.contains(selectMediaEvent.getMediaObj())){
                allSelectMedias.remove(selectMediaEvent.getMediaObj());
            }
        }
    }

    @Subscribe
    public void selectMediaListEvent(CircleSelectMediaListEvent mediaListEvent) {
        if (mediaListEvent.getType() != SelectMediaListEvent.TYPE_TIME_MEDIA) return;
        //选中
        if (mediaListEvent.isSelect()) {
            for (CircleMediaObj mediaObj : mediaListEvent.getMediaObjList()) {
                if (!allSelectMedias.contains(mediaObj)) {
                    allSelectMedias.add(mediaObj);
                }
            }

        } else {
            for (MediaObj mediaObj : mediaListEvent.getMediaObjList()) {
                if (allSelectMedias.contains(mediaObj)) {
                    allSelectMedias.remove(mediaObj);
                }
            }

        }
    }

    @Subscribe
    public void selectTimeLineEvent(CircleSelectTimeLineEvent selectTimeLineEvent){
        //选中
        if(selectTimeLineEvent.isSelect()){
            if(!allSelectTimeLines.contains(selectTimeLineEvent.getTimeLineExObj())){
                allSelectTimeLines.add(selectTimeLineEvent.getTimeLineExObj());
            }
        } else {
            if(allSelectTimeLines.contains(selectTimeLineEvent.getTimeLineExObj())){
                allSelectTimeLines.remove(selectTimeLineEvent.getTimeLineExObj());
            }
        }

        if(currentFragment instanceof CircleServerTimeFragment){
            initAllSelectView(((CircleServerTimeFragment) currentFragment).isAllSelect(), allSelectTimeLines.size());
            ((CircleServerTimeFragment) currentFragment).setTimeLineObjs(allSelectTimeLines);
        }
    }
}
