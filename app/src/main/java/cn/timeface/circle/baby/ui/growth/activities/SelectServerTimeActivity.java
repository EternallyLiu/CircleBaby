package cn.timeface.circle.baby.ui.growth.activities;

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
import cn.timeface.circle.baby.constants.TypeConstants;
import cn.timeface.circle.baby.dialogs.SelectContentTypeDialog;
import cn.timeface.circle.baby.events.BookOptionEvent;
import cn.timeface.circle.baby.support.api.models.objs.MediaObj;
import cn.timeface.circle.baby.support.api.models.objs.TimeLineObj;
import cn.timeface.circle.baby.support.api.models.objs.UserWrapObj;
import cn.timeface.circle.baby.support.api.models.responses.QueryProductionExtraResponse;
import cn.timeface.circle.baby.support.api.models.responses.QuerySelectedPhotoResponse;
import cn.timeface.circle.baby.support.api.models.responses.QueryTimeLineResponse;
import cn.timeface.circle.baby.support.managers.listeners.IEventBus;
import cn.timeface.circle.baby.support.mvp.bases.BasePresenterAppCompatActivity;
import cn.timeface.circle.baby.support.mvp.model.BookModel;
import cn.timeface.circle.baby.support.utils.DateUtil;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.growth.events.SelectMediaEvent;
import cn.timeface.circle.baby.ui.growth.events.SelectMediaListEvent;
import cn.timeface.circle.baby.ui.growth.events.SelectTimeLineEvent;
import cn.timeface.circle.baby.ui.growth.fragments.SelectUserFragment;
import cn.timeface.circle.baby.ui.growth.fragments.ServerTimeFragment;
import cn.timeface.circle.baby.views.TFStateView;
import cn.timeface.open.api.bean.obj.TFOContentObj;
import cn.timeface.open.api.bean.obj.TFOPublishObj;
import cn.timeface.open.api.bean.obj.TFOResourceObj;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 选择服务器照片（已经上传的图片）
 * author : YW.SUN Created on 2017/2/14
 * email : sunyw10@gmail.com
 *
 * 这个页面是这样的总共三层fragment，1：筛选条件（各种按）fragment 2：筛选条件或内容fragment 3：个别情况下的第三季页面
 * fl_container_type:第一层fragment，用来展示各种按（时间，发布人，地点，标签）fragment
 * fl_container:第二层fragment，
 * fl_container_ex:第三层fragment，canback为true的时候
 *
 */
public class SelectServerTimeActivity extends BasePresenterAppCompatActivity implements
        SelectContentTypeDialog.SelectTypeListener, View.OnClickListener, IEventBus {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.content_select_time)
    RelativeLayout contentSelectTime;
    @Bind(R.id.tv_content_type)
    TextView tvContentType;
    @Bind(R.id.tv_content)
    TextView tvContent;
    @Bind(R.id.fl_container_ex)
    FrameLayout flContainerEx;
    @Bind(R.id.tv_sel_count)
    TextView tvSelectCount;
    @Bind(R.id.cb_all_sel)
    CheckBox cbAllSel;
    @Bind(R.id.rl_photo_tip)
    RelativeLayout rlPhotoTip;
    @Bind(R.id.tf_stateView)
    TFStateView stateView;

    boolean fragmentShow = false;
    boolean canBack = false;
    ServerTimeFragment timeFragment;//按时间
    HashMap<String, ServerTimeFragment> userFragmentMap = new HashMap<>();//存储下来所有用户对应的fragment
    SelectContentTypeDialog selectContentTypeDialog;
    SelectUserFragment selectUserFragment;
    int openBookType;
    int bookType;
    String bookId;
    String openBookId;
    int babyId;

    List<MediaObj> allSelectMedias = new ArrayList<>();
    List<TimeLineObj> allSelectTimeLines = new ArrayList<>();
    List<String> allSelectTimeIds = new ArrayList<>();

    public static void open(Context context,int bookType, int openBookType, String bookId, String openBookId, int babyId) {
        Intent intent = new Intent(context, SelectServerTimeActivity.class);
        intent.putExtra("open_book_type", openBookType);
        intent.putExtra("book_type", bookType);
        intent.putExtra("book_id", bookId);
        intent.putExtra("open_book_id", openBookId);
        intent.putExtra("baby_id", babyId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_server_time);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        tvContentType.setOnClickListener(this);
        this.openBookType = getIntent().getIntExtra("open_book_type", 0);
        this.bookType = getIntent().getIntExtra("book_type", 0);
        this.bookId = getIntent().getStringExtra("book_id");
        this.openBookId = getIntent().getStringExtra("open_book_id");
        this.babyId = getIntent().getIntExtra("baby_id", 0);
        cbAllSel.setOnClickListener(this);

        //新建一本
        if(TextUtils.isEmpty(bookId)){
            stateView.setVisibility(View.GONE);
            timeFragment = ServerTimeFragment.newInstanceEdit(TypeConstants.PHOTO_TYPE_TIME, FastData.getUserId(), bookType, allSelectMedias, allSelectTimeIds, babyId, allSelectTimeLines);
            timeFragment.setTimeLineObjs(allSelectTimeLines);
            timeFragment.setMediaObjs(allSelectMedias);
            showContent(timeFragment);
        //编辑一本
        } else {
            stateView.setVisibility(View.VISIBLE);
            stateView.loading();
            //先查取扩展信息，解析出来哪些time被选中了
            addSubscription(
                    apiService.getProductionExtra(bookId)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .filter(
                                    extraResponse -> {
                                        if (extraResponse.success() && extraResponse.getExtra().contains("timeIds")){
                                            for(String s : extraResponse.getTimeIds()){
                                                allSelectTimeIds.add(s);
                                            }
                                        }
                                        return extraResponse.success();
                                    }
                            )
                            .observeOn(Schedulers.io())
                            //获取所有的时光
                            .flatMap(new Func1<QueryProductionExtraResponse, Observable<QueryTimeLineResponse>>() {
                                @Override
                                public Observable<QueryTimeLineResponse> call(QueryProductionExtraResponse queryProductionExtraResponse) {
                                    return apiService.queryTimeLineByTime(babyId, "-1");
                                }
                            })
                            .filter(queryTimeLineResponse -> queryTimeLineResponse.success())
                            .observeOn(Schedulers.io())
                            .flatMap(
                                    new Func1<QueryTimeLineResponse, Observable<QuerySelectedPhotoResponse>>() {
                                        @Override
                                        public Observable<QuerySelectedPhotoResponse> call(QueryTimeLineResponse queryTimeLineResponse) {
                                            allSelectTimeLines.clear();

                                            for(TimeLineObj timeLineObj : queryTimeLineResponse.getTimeLineObjs()){
                                                if(allSelectTimeIds.contains(String.valueOf(timeLineObj.getTimeId()))){
                                                    allSelectTimeLines.add(timeLineObj);
                                                }
                                            }
                                            return apiService.bookMedias(bookId);
                                        }
                                    }
                            )
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    response -> {
                                        if (response.success()) {
                                            this.allSelectMedias = response.getDataList();
                                            timeFragment = ServerTimeFragment.newInstanceEdit(
                                                    TypeConstants.PHOTO_TYPE_TIME,
                                                    FastData.getUserId(),
                                                    bookType,
                                                    response.getDataList(), allSelectTimeIds, babyId, allSelectTimeLines);
                                            timeFragment.setTimeLineObjs(allSelectTimeLines);
                                            timeFragment.setMediaObjs(allSelectMedias);
                                        } else {
                                            ToastUtil.showToast(response.info);
                                        }
                                        showContent(timeFragment);
                                        stateView.finish();
                                    },
                                    throwable -> {
                                        stateView.showException(throwable);
                                        Log.e(TAG, throwable.getLocalizedMessage());
                                    }
                            )
            );
        }
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
                                            HashMap<String, List<TimeLineObj>> timelineMap = new HashMap<>();
                                            for (TimeLineObj timeLineObj : allSelectTimeLines) {
                                                String key = DateUtil.formatDate("yyyy-MM", timeLineObj.getDate());
                                                if (timelineMap.containsKey(key)) {
                                                    timelineMap.get(key).add(timeLineObj);
                                                } else {
                                                    List<TimeLineObj> timeLineObjs = new ArrayList<>();
                                                    timeLineObjs.add(timeLineObj);
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
                                                List<TimeLineObj> timeLineObjs = (List<TimeLineObj>) entry.getValue();

                                                //content list，代表一个时光
                                                List<TFOContentObj> tfoContentObjs = new ArrayList<>(allSelectTimeLines.size());
                                                for (TimeLineObj timeLineObj : allSelectTimeLines) {
                                                    tfoContentObjs.add(timeLineObj.toTFOContentObj());
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
                                            for (TimeLineObj timeLineObj : allSelectTimeLines) {
                                                if (timeLineObj != null) {
                                                    sbTime.append(timeLineObj.getTimeId());
                                                    sbTime.append(",");

                                                    if (!timeLineObj.getMediaList().isEmpty()) {
                                                        for (MediaObj mediaObj : timeLineObj.getMediaList()) {
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

    @Override
    public void onBackPressed() {
        if(canBack){
            canBack = false;
            tvContent.setVisibility(View.GONE);
            tvContentType.setVisibility(View.VISIBLE);
            flContainerEx.setVisibility(View.GONE);
            rlPhotoTip.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * 按时间筛选照片
     */
    @Override
    public void selectTypeTime() {
        tvContentType.setText("按时间");
        if(timeFragment == null){
            timeFragment = ServerTimeFragment.newInstanceEdit(TypeConstants.PHOTO_TYPE_TIME, FastData.getUserId(), bookType, allSelectMedias, allSelectTimeIds, babyId, allSelectTimeLines);
        }
        timeFragment.setTimeLineObjs(allSelectTimeLines);
        timeFragment.setMediaObjs(allSelectMedias);
        showContent(timeFragment);
        onClick(tvContentType);
        rlPhotoTip.setVisibility(View.VISIBLE);
    }

    /**
     * 按发布人筛选照片
     */
    @Override
    public void selectTypeUser() {
        tvContentType.setText("按发布人");
        if (selectUserFragment == null) {
            selectUserFragment = SelectUserFragment.newInstance(this);
        }
        showContent(selectUserFragment);
        onClick(tvContentType);
    }

    /**
     * 按地点筛选照片
     */
    @Override
    public void selectTypeLocation() {
        tvContentType.setText("按地点");
        onClick(tvContentType);
    }

    /**
     * 按标签筛选照片
     */
    @Override
    public void selectTypeLabel() {}

    @Override
    public void setTypeText(String title) {
        tvContentType.setText(title);
    }

    @Override
    public void dismiss() {
        if(selectContentTypeDialog != null)setSelectContentShow(false);
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

    Fragment currentFragmentEx = null;
    public void showContentEx(Fragment fragment){
        flContainerEx.setVisibility(View.VISIBLE);
        rlPhotoTip.setVisibility(View.GONE);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if (currentFragmentEx != null) {
            ft.hide(currentFragmentEx);
        }
        if(fragment.isAdded()){
            ft.show(fragment);
        } else {
            ft.add(R.id.fl_container_ex, fragment);
        }
        currentFragmentEx = fragment;

        if(currentFragmentEx instanceof ServerTimeFragment){
//            rlPhotoTip.setVisibility(View.VISIBLE);
            initAllSelectView(((ServerTimeFragment) currentFragmentEx).isAllSelect(), allSelectTimeLines.size());
        } else {
            rlPhotoTip.setVisibility(View.GONE);
        }
        ft.commitAllowingStateLoss();
        canBack = true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_content_type:
                setSelectContentShow(!fragmentShow);
                break;

            //点击选择用户操作
            case R.id.ll_root:
                UserWrapObj userWrapObj = (UserWrapObj) view.getTag(R.string.tag_obj);
                tvContentType.setVisibility(View.GONE);
                tvContent.setVisibility(View.VISIBLE);
                tvContent.setText(userWrapObj.getUserInfo().getRelationName());
                if(userFragmentMap.containsKey(userWrapObj.getUserInfo().getUserId())){
                    userFragmentMap.get(userWrapObj.getUserInfo().getUserId()).setTimeLineObjs(allSelectTimeLines);
                    userFragmentMap.get(userWrapObj.getUserInfo().getUserId()).setMediaObjs(allSelectMedias);
                    showContentEx(userFragmentMap.get(userWrapObj.getUserInfo().getUserId()));
                    rlPhotoTip.setVisibility(View.VISIBLE);
                } else {
                    ServerTimeFragment serverTimeFragment;
                    serverTimeFragment = ServerTimeFragment.newInstanceEdit(TypeConstants.PHOTO_TYPE_USER, userWrapObj.getUserInfo().getUserId(), bookType, allSelectMedias, allSelectTimeIds, babyId, allSelectTimeLines);

                    userFragmentMap.put(userWrapObj.getUserInfo().getUserId(), serverTimeFragment);
                    userFragmentMap.get(userWrapObj.getUserInfo().getUserId()).setTimeLineObjs(allSelectTimeLines);
                    userFragmentMap.get(userWrapObj.getUserInfo().getUserId()).setMediaObjs(allSelectMedias);
                    showContentEx(serverTimeFragment);
                }
                break;


            //全选
            case R.id.cb_all_sel:
                if(cbAllSel.isChecked()){
                    if(canBack){
                        ((ServerTimeFragment) currentFragmentEx).doAllSelImg();
                    } else {
                        ((ServerTimeFragment) currentFragment).doAllSelImg();
                    }
                    if(allSelectTimeLines.size() > 0){
                        //处理全选照片处理
                        for(TimeLineObj timeLineObj : allSelectTimeLines){
                            allSelectMedias.addAll(timeLineObj.getMediaList());
                        }
                    }
                    cbAllSel.setChecked(false);
                } else {
                    if(canBack){
                        ((ServerTimeFragment) currentFragmentEx).doAllUnSelImg();
                    } else {
                        ((ServerTimeFragment) currentFragment).doAllUnSelImg();
                    }
                    cbAllSel.setChecked(true);
                    allSelectMedias.clear();
                }

                initAllSelectView(!cbAllSel.isChecked(), allSelectTimeLines.size());
                break;
        }
    }

    public void setSelectContentShow(boolean show){
        if(fragmentShow == show) return;
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        if (!show) {
            transaction.hide(selectContentTypeDialog);
            fragmentShow = false;
        } else {
            if (selectContentTypeDialog == null) {
                selectContentTypeDialog = SelectContentTypeDialog.newInstance(this, SelectContentTypeDialog.CONTENT_TYPE_TIME, bookType);
                transaction.add(R.id.fl_container_type, selectContentTypeDialog);
            } else {
                transaction.show(selectContentTypeDialog);
            }
            fragmentShow = true;
        }
        transaction.commit();
    }

    private void initAllSelectView(boolean allSelect, int selectCount){
        cbAllSel.setChecked(allSelect);
        tvSelectCount.setText(Html.fromHtml(String.format(getString(R.string.select_server_time_select_count), String.valueOf(selectCount))));
    }

    public void setPhotoTipVisibility(int visibility){
        rlPhotoTip.setVisibility(visibility);
        if(currentFragment instanceof ServerTimeFragment) {
            initAllSelectView(((ServerTimeFragment) currentFragment).isAllSelect(), allSelectTimeLines.size());
        }
    }

    @Subscribe
    public void bookOptionEvent(BookOptionEvent optionEvent){
        if(optionEvent.getOption() == BookOptionEvent.BOOK_OPTION_CREATE){
            finish();
        }
    }

    @Subscribe
    public void selectMediaEvent(SelectMediaEvent selectMediaEvent){
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
    public void selectMediaListEvent(SelectMediaListEvent mediaListEvent) {
        if (mediaListEvent.getType() != SelectMediaListEvent.TYPE_TIME_MEDIA) return;
        //选中
        if (mediaListEvent.isSelect()) {
            for (MediaObj mediaObj : mediaListEvent.getMediaObjList()) {
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
    public void selectTimeLineEvent(SelectTimeLineEvent selectTimeLineEvent){
        //选中
        if(selectTimeLineEvent.isSelect()){
            if(!allSelectTimeLines.contains(selectTimeLineEvent.getTimeLineObj())){
                allSelectTimeIds.add(String.valueOf(selectTimeLineEvent.getTimeLineObj().getTimeId()));
                allSelectTimeLines.add(selectTimeLineEvent.getTimeLineObj());
            }
        } else {
            if(allSelectTimeLines.contains(selectTimeLineEvent.getTimeLineObj())){
                allSelectTimeIds.remove(selectTimeLineEvent.getTimeLineObj().getTimeId());
                allSelectTimeLines.remove(selectTimeLineEvent.getTimeLineObj());
            }
        }

        if(canBack && currentFragmentEx instanceof ServerTimeFragment){
            initAllSelectView(((ServerTimeFragment) currentFragmentEx).isAllSelect(), allSelectTimeLines.size());
            ((ServerTimeFragment) currentFragmentEx).setTimeLineObjs(allSelectTimeLines);
        } else if(currentFragment instanceof ServerTimeFragment) {
            initAllSelectView(((ServerTimeFragment) currentFragment).isAllSelect(), allSelectTimeLines.size());
            ((ServerTimeFragment) currentFragment).setTimeLineObjs(allSelectTimeLines);
        }

    }
}
