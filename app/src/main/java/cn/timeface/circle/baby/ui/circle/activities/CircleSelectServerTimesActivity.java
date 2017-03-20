package cn.timeface.circle.baby.ui.circle.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.MyPODActivity;
import cn.timeface.circle.baby.support.api.models.objs.MediaObj;
import cn.timeface.circle.baby.support.mvp.bases.BasePresenterAppCompatActivity;
import cn.timeface.circle.baby.support.mvp.model.BookModel;
import cn.timeface.circle.baby.support.utils.DateUtil;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.circle.bean.CircleTimeLineExObj;
import cn.timeface.circle.baby.ui.circle.dialogs.CircleSelectTimeTypeDialog;
import cn.timeface.circle.baby.ui.circle.fragments.CircleServerTimeFragment;
import cn.timeface.circle.baby.ui.growth.fragments.ServerTimeFragment;
import cn.timeface.circle.baby.views.TFStateView;
import cn.timeface.open.api.bean.obj.TFOContentObj;
import cn.timeface.open.api.bean.obj.TFOPublishObj;
import cn.timeface.open.api.bean.obj.TFOResourceObj;

/**
 * 圈作品-时光书
 * author : sunyanwei Created on 17-3-20
 * email : sunyanwei@timeface.cn
 */
public class CircleSelectServerTimesActivity extends BasePresenterAppCompatActivity implements CircleSelectTimeTypeDialog.SelectTypeListener, View.OnClickListener {

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
    @Bind(R.id.tv_content)
    TextView tvContent;

    boolean fragmentShow = false;
    boolean canBack = false;
    CircleServerTimeFragment allTimeFragment;//全部动态
    CircleServerTimeFragment mineTimeFragment;//我发布的动态
    CircleServerTimeFragment aboutBabyTimeFragment;//与我宝宝相关的动态
    CircleSelectTimeTypeDialog selectContentTypeDialog;
    int openBookType;
    int bookType;
    String bookId;
    String openBookId;
    String circleId;

    List<MediaObj> allSelectMedias = new ArrayList<>();
    List<CircleTimeLineExObj> allSelectTimeLines = new ArrayList<>();
    List<String> allSelectTimeIds = new ArrayList<>();

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
        cbAllSel.setOnClickListener(this);

        //新建一本
        tfStateView.setVisibility(View.GONE);
        allTimeFragment = CircleServerTimeFragment.newInstance(
                CircleSelectTimeTypeDialog.TIME_TYPE_ALL,
                circleId,
                allSelectMedias,
                allSelectTimeIds,
                allSelectTimeLines);
        allTimeFragment.setTimeLineObjs(allSelectTimeLines);
        allTimeFragment.setMediaObjs(allSelectMedias);
        showContent(allTimeFragment);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_select_server_photo, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.save){
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
                                            String bookName = FastData.getBabyNickName() + "的成长纪念册";
                                            if (bookType == BookModel.BOOK_TYPE_GROWTH_QUOTATIONS) {
                                                bookName = FastData.getBabyNickName() + "的成长语录";
                                            }


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

                                            if (bookType == BookModel.BOOK_TYPE_GROWTH_QUOTATIONS) {
                                                //成长语录插页数据，content_list第一条数据为插页信息
                                                List<TFOResourceObj> insertPageResources = new ArrayList<>(1);
                                                TFOResourceObj insertPageResourceObj = new TFOResourceObj();
                                                insertPageResourceObj.setImageUrl(FastData.getBabyAvatar());
                                                insertPageResourceObj.setImageOrientation(response.getImageRotation());
                                                insertPageResourceObj.setImageHeight(response.getImageHeight());
                                                insertPageResourceObj.setImageWidth(response.getImageWidth());
                                                insertPageResources.add(insertPageResourceObj);
                                                TFOContentObj insertPageContent = new TFOContentObj("", insertPageResources);//没有subtitile
                                                String insertContent = FastData.getBabyNickName()
                                                        + ","
                                                        + FastData.getBabyAge()
                                                        + ","
                                                        + "是一个活泼可爱的小宝宝，在"
                                                        + FastData.getBabyNickName()
                                                        + "成长的过程中经常会\"语出惊人\"，有时让我们很吃惊，宝宝小小的脑袋瓜怎么会冒出这么有意思的想法，在这里我们记录了"
                                                        + FastData.getBabyNickName()
                                                        + "成长中的童言趣语，一起来看看吧~";
                                                insertPageContent.setContent(insertContent);
                                                tfoPublishObjs.get(0).getContentList().add(0, insertPageContent);//插入插页信息
                                            }

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
        canBack = false;
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

        if(currentFragment instanceof ServerTimeFragment){
//            rlPhotoTip.setVisibility(View.VISIBLE);
            initAllSelectView(((ServerTimeFragment) currentFragment).isAllSelect(), allSelectTimeLines.size());
        } else {
            rlPhotoTip.setVisibility(View.GONE);
        }

        ft.commitAllowingStateLoss();
    }

    @Override
    public void selectTypeAll() {

    }

    @Override
    public void selectTypeMe() {

    }

    @Override
    public void selectTypeAboutMyBaby() {

    }

    @Override
    public void onClick(View view) {

    }


    private void initAllSelectView(boolean allSelect, int selectCount){
        cbAllSel.setChecked(allSelect);
        tvSelCount.setText(Html.fromHtml(String.format(getString(R.string.select_server_time_select_count), String.valueOf(selectCount))));
    }
}
