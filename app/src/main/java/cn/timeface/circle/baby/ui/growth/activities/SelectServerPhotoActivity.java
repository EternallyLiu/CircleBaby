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
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;

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
import cn.timeface.circle.baby.support.api.models.objs.LocationObj;
import cn.timeface.circle.baby.support.api.models.objs.MediaObj;
import cn.timeface.circle.baby.support.api.models.objs.MediaWrapObj;
import cn.timeface.circle.baby.support.api.models.objs.PaintingCollectionCustomDataObj;
import cn.timeface.circle.baby.support.api.models.objs.PaintingCollectionRemarkObj;
import cn.timeface.circle.baby.support.api.models.objs.UserWrapObj;
import cn.timeface.circle.baby.support.managers.listeners.IEventBus;
import cn.timeface.circle.baby.support.mvp.bases.BasePresenterAppCompatActivity;
import cn.timeface.circle.baby.support.mvp.model.BookModel;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.growth.events.SelectMediaEvent;
import cn.timeface.circle.baby.ui.growth.events.SelectMediaListEvent;
import cn.timeface.circle.baby.ui.growth.fragments.PhotoMapFragment;
import cn.timeface.circle.baby.ui.growth.fragments.SelectUserFragment;
import cn.timeface.circle.baby.ui.growth.fragments.ServerPhotoFragment;
import cn.timeface.circle.baby.ui.growth.fragments.ServerTimeFragment;
import cn.timeface.circle.baby.views.TFStateView;
import cn.timeface.open.api.bean.obj.TFOContentObj;
import cn.timeface.open.api.bean.obj.TFOPublishObj;
import cn.timeface.open.api.bean.obj.TFOResourceObj;

/**
 * 选择服务器照片（已经上传的图片）
 * author : YW.SUN Created on 2017/2/14
 * email : sunyw10@gmail.com
 *
 *这个页面是这样的总共三层fragment，1：筛选条件（各种按）fragment 2：筛选条件或内容fragment 3：个别情况下的第三季页面
 * fl_container_type:第一层fragment，用来展示各种按（时间，发布人，地点，标签）fragment
 * fl_container:第二层fragment，
 * fl_container_ex:第三层夫人啊哥们他， canback为true的时候
 *
 */
public class SelectServerPhotoActivity extends BasePresenterAppCompatActivity implements
        SelectContentTypeDialog.SelectTypeListener, View.OnClickListener, IEventBus, PhotoMapFragment.SelectLocation {

    boolean fragmentShow = false;
    boolean canBack = false;
    @Bind(R.id.tv_content_type)
    TextView tvContentType;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.tv_content)
    TextView tvContent;
    @Bind(R.id.fl_container_ex)
    FrameLayout flContainerEx;
    @Bind(R.id.rl_photo_tip)
    RelativeLayout rlPhotoTip;
    @Bind(R.id.tv_sel_count)
    TextView tvSelectCount;
    @Bind(R.id.tv_recommend_count)
    TextView tvRecommendCount;
    @Bind(R.id.tf_stateView)
    TFStateView stateView;

    ServerPhotoFragment timePhotoFragment;//按时间
    HashMap<String, ServerPhotoFragment> userPhotoFragmentMap = new HashMap<>();//存储下来所有用户的fragment
    ServerPhotoFragment labelPhotoFragment;//按标签
    PhotoMapFragment locationPhotoFragment;//按地点
    HashMap<String, ServerPhotoFragment> locationPhotoFragmentMap = new HashMap<>();//存储下来所有地点的fragment

    SelectContentTypeDialog selectContentTypeDialog;
    SelectUserFragment selectUserFragment;
    int openBookType;
    int bookType;
    String bookId;
    String openBookId;
    int babyId;//判断这本书是否属于当前宝宝

    List<MediaObj> allSelectMedias = new ArrayList<>();


    public static void open(Context context,int bookType, int openBookType, String bookId, String openBookId, int babyId) {
        Intent intent = new Intent(context, SelectServerPhotoActivity.class);
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
        setContentView(R.layout.activity_select_server_photo);
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

        //新建一本
        if(TextUtils.isEmpty(bookId)){
            stateView.setVisibility(View.GONE);
            timePhotoFragment = ServerPhotoFragment.newInstanceEdit(
                    TypeConstants.PHOTO_TYPE_TIME,
                    FastData.getUserId(),
                    "",
                    allSelectMedias,
                    new ArrayList<>(),
                    bookType,
                    babyId);
            //绘画集默认 按标签
            if(bookType == BookModel.BOOK_TYPE_PAINTING){
                selectTypeLabel();
                //其他作品默认 按时间
            } else {
                selectTypeTime();
            }
            onClick(tvContentType);
        //编辑一本
        } else {
            stateView.setVisibility(View.VISIBLE);
            stateView.finish();
            apiService.bookMedias(bookId)
                    .compose(SchedulersCompat.applyIoSchedulers())
                    .subscribe(
                            response -> {
                                if(response.success()){
                                    this.allSelectMedias = response.getDataList();
                                    timePhotoFragment = ServerPhotoFragment.newInstanceEdit(
                                            TypeConstants.PHOTO_TYPE_TIME,
                                            FastData.getUserId(),
                                            "",
                                            response.getDataList(),
                                            new ArrayList<>(),
                                            bookType,
                                            babyId
                                    );
                                    //绘画集默认 按标签
                                    if(bookType == BookModel.BOOK_TYPE_PAINTING){
                                        selectTypeLabel();
                                    //其他作品默认 按时间
                                    } else {
                                        selectTypeTime();
                                    }
                                    onClick(tvContentType);
                                } else {
                                    rlPhotoTip.setVisibility(View.GONE);
                                    ToastUtil.showToast(response.info);
                                }
                                stateView.finish();
                            },
                            throwable -> {
                                stateView.showException(throwable);
                                rlPhotoTip.setVisibility(View.GONE);
                                Log.e(TAG, throwable.getLocalizedMessage());
                            }
                    );
        }
    }

    public void initTips(){
        if(bookType == BookModel.BOOK_TYPE_HARDCOVER_PHOTO_BOOK){
            tvRecommendCount.setText(String.format(getString(R.string.select_server_photo_recommend_count), "60~120"));
        } else if(bookType == BookModel.BOOK_TYPE_PAINTING){
            tvRecommendCount.setText(String.format(getString(R.string.select_server_photo_recommend_count), "40~120"));
        } else {
            tvRecommendCount.setVisibility(View.GONE);
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

            int pageNum = allSelectMedias.size();
            if(pageNum == 0){
                ToastUtil.showToast("请选择至少一张照片");
                return true;
            }

            addSubscription(
                    apiService.queryImageInfo(FastData.getBabyAvatar())
                            .compose(SchedulersCompat.applyIoSchedulers())
                            .subscribe(
                                    response -> {
                                        if(response.success()){
                                            //跳转开放平台POD接口；
                                            String bookName = FastData.getBabyName() + "的照片书";
                                            if(bookType == BookModel.BOOK_TYPE_PAINTING){
                                                bookName = FastData.getBabyName() + "的绘画集";
                                            }
                                            List<TFOResourceObj> tfoResourceObjs = new ArrayList<>();
                                            StringBuffer sb = new StringBuffer("{\"dataList\":[");
                                            int index = 0;
                                            for(MediaObj mediaObj : allSelectMedias){
                                                index++;
                                                TFOResourceObj tfoResourceObj = mediaObj.toTFOResourceObj();
                                                tfoResourceObjs.add(tfoResourceObj);
                                                sb.append(mediaObj.getId());
                                                if (index < allSelectMedias.size()) {
                                                    sb.append(",");
                                                } else {
                                                    sb.append("]}");
                                                }
                                            }


                                            List<TFOContentObj> tfoContentObjs1 = new ArrayList<>();
                                            TFOContentObj tfoContentObj;
                                            tfoContentObj = new TFOContentObj("", tfoResourceObjs);
                                            tfoContentObjs1.add(tfoContentObj);

                                            ArrayList<String> keys = new ArrayList<>();
                                            ArrayList<String> values = new ArrayList<>();
                                            keys.add("book_author");
                                            keys.add("book_title");

                                            values.add(FastData.getUserName());
                                            values.add(bookName);


                                            if(bookType == BookModel.BOOK_TYPE_PAINTING) {
                                                PaintingCollectionCustomDataObj customDataObj = new PaintingCollectionCustomDataObj();
                                                TFOResourceObj tfoResourceObj = new TFOResourceObj();

                                                tfoResourceObj.setImageOrientation(response.getImageRotation());
                                                tfoResourceObj.setImageUrl(FastData.getBabyAvatar());
                                                tfoResourceObj.setImageWidth(response.getImageWidth());
                                                tfoResourceObj.setImageHeight(response.getImageHeight());
                                                PaintingCollectionRemarkObj remarkObj = new PaintingCollectionRemarkObj(FastData.getBabyName(), FastData.getBabyAge());
                                                customDataObj.setRemark(remarkObj);
                                                customDataObj.setImgInfo(tfoResourceObj);

                                                Gson gson = new Gson();
                                                tfoContentObj.setCustomData(gson.toJson(customDataObj));
                                                String coverImage = gson.toJson(customDataObj.getImgInfo());
                                                coverImage = "{\"cover4\":[" + coverImage + "]}";

                                                keys.add("cover_image");
                                                values.add(coverImage);
                                            }




                                            TFOPublishObj tfoPublishObj = new TFOPublishObj(bookName, tfoContentObjs1);
                                            List<TFOPublishObj> tfoPublishObjs = new ArrayList<>();
                                            tfoPublishObjs.add(tfoPublishObj);

                                            MyPODActivity.open(this, bookId, openBookId, bookType, openBookType, tfoPublishObjs, sb.toString(),true,FastData.getBabyId(),keys,values,1);
                                            finish();
                                        }
                                    },
                                    throwable -> {
                                        Log.e(TAG, throwable.getLocalizedMessage());
                                    }
                            )
            );
            return true;
        } else if(item.getItemId() == android.R.id.home) {
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
        if(timePhotoFragment == null) {
            timePhotoFragment = ServerPhotoFragment.newInstanceEdit(
                    TypeConstants.PHOTO_TYPE_TIME,
                    FastData.getUserId(),
                    "",
                    allSelectMedias,
                    new ArrayList<>(),
                    bookType,
                    babyId);
        }
        timePhotoFragment.setMediaObjs(allSelectMedias);
        showContent(timePhotoFragment);
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
        if (locationPhotoFragment == null) {
            locationPhotoFragment = PhotoMapFragment.newInstance(this);
        }
        showContent(locationPhotoFragment);
        onClick(tvContentType);
    }

    /**
     * 按标签筛选照片
     */
    @Override
    public void selectTypeLabel() {
        tvContentType.setText("按标签");
        if(labelPhotoFragment == null) {
            labelPhotoFragment = ServerPhotoFragment.newInstanceEdit(
                    TypeConstants.PHOTO_TYPE_LABEL,
                    FastData.getUserId(),
                    "",
                    allSelectMedias,
                    new ArrayList<>(),
                    bookType,
                    babyId);
        }
        labelPhotoFragment.setMediaObjs(allSelectMedias);
        showContent(labelPhotoFragment);
        onClick(tvContentType);
    }

    @Override
    public void setTypeText(String title) {
        tvContentType.setText(title);
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

        if(currentFragment instanceof ServerPhotoFragment){
//            rlPhotoTip.setVisibility(View.VISIBLE);
            initAllSelectView(allSelectMedias.size());
        } else {
            rlPhotoTip.setVisibility(View.GONE);
        }
        ft.commitAllowingStateLoss();
    }

    Fragment currentFragmentEx = null;
    public void showContentEx(Fragment fragment){
        flContainerEx.setVisibility(View.VISIBLE);
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
        ft.commitAllowingStateLoss();

        if(currentFragmentEx instanceof ServerPhotoFragment){
//            rlPhotoTip.setVisibility(View.VISIBLE);
            initAllSelectView(allSelectMedias.size());
        } else {
            rlPhotoTip.setVisibility(View.GONE);
        }
        canBack = true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_content_type:
                showSelectContentType(!fragmentShow);
                break;

            //点击选择用户操作
            case R.id.ll_root:
                UserWrapObj userWrapObj = (UserWrapObj) view.getTag(R.string.tag_obj);
                tvContentType.setVisibility(View.GONE);
                tvContent.setVisibility(View.VISIBLE);
                tvContent.setText(userWrapObj.getUserInfo().getRelationName());

                //已经加载
                if(userPhotoFragmentMap.containsKey(userWrapObj.getUserInfo().getUserId())){
                    userPhotoFragmentMap.get(userWrapObj.getUserInfo().getUserId()).setMediaObjs(allSelectMedias);
                    showContentEx(userPhotoFragmentMap.get(userWrapObj.getUserInfo().getUserId()));
                    rlPhotoTip.setVisibility(View.VISIBLE);
                } else {
                    ServerPhotoFragment serverPhotoFragment;
                    serverPhotoFragment = ServerPhotoFragment.newInstanceEdit(
                            TypeConstants.PHOTO_TYPE_USER,
                            userWrapObj.getUserInfo().getUserId(),
                            "",
                            allSelectMedias,
                            new ArrayList<>(),
                            bookType, babyId);
                    userPhotoFragmentMap.put(userWrapObj.getUserInfo().getUserId(), serverPhotoFragment);
                    userPhotoFragmentMap.get(userWrapObj.getUserInfo().getUserId()).setMediaObjs(allSelectMedias);
                    showContentEx(serverPhotoFragment);
                }
                break;
        }
    }

    public void showSelectContentType(boolean show){
        if(fragmentShow == show) return;
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        if (!show) {
            transaction.hide(selectContentTypeDialog);
            fragmentShow = false;
        } else {
            if (selectContentTypeDialog == null) {
                selectContentTypeDialog = SelectContentTypeDialog.newInstance(this, SelectContentTypeDialog.CONTENT_TYPE_PHOTO, bookType);
                transaction.add(R.id.fl_container_type, selectContentTypeDialog);
            } else {
                transaction.show(selectContentTypeDialog);
            }
            fragmentShow = true;
        }
        transaction.commit();
    }

    private void initAllSelectView(int selectCount){
        tvSelectCount.setText(Html.fromHtml(String.format(getString(R.string.select_server_time_select_count), String.valueOf(selectCount))));
    }

    public void setPhotoTipVisibility(int visibility){
        rlPhotoTip.setVisibility(visibility);
        if(currentFragment instanceof ServerPhotoFragment) {
            initAllSelectView(allSelectMedias.size());
        }
    }

    public void clickPhotoView(View view){}

    @Subscribe
    public void bookOptionEvent(BookOptionEvent optionEvent){
        if(optionEvent.getOption() == BookOptionEvent.BOOK_OPTION_CREATE){
            finish();
        }
    }

    @Subscribe
    public void selectMediaEvent(SelectMediaEvent selectMediaEvent){
        if(selectMediaEvent.getType() != SelectMediaEvent.TYPE_MEDIA_MEDIA) return;
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

        if(canBack && currentFragmentEx instanceof ServerPhotoFragment){
            initAllSelectView(allSelectMedias.size());
        } else if(currentFragment instanceof ServerPhotoFragment) {
            initAllSelectView(allSelectMedias.size());
        }
    }

    @Subscribe
    public void selectMediaListEvent(SelectMediaListEvent mediaListEvent){
        if(mediaListEvent.getType() != SelectMediaListEvent.TYPE_MEDIA_MEDIA) return;
        //选中
        if(mediaListEvent.isSelect()){
            if(!allSelectMedias.containsAll(mediaListEvent.getMediaObjList())){
                allSelectMedias.addAll(mediaListEvent.getMediaObjList());
            }
        } else {
            if(allSelectMedias.containsAll(mediaListEvent.getMediaObjList())){
                allSelectMedias.removeAll(mediaListEvent.getMediaObjList());
            }
        }

        if(canBack && currentFragmentEx instanceof ServerPhotoFragment){
            initAllSelectView(allSelectMedias.size());
        } else if(currentFragment instanceof ServerPhotoFragment) {
            initAllSelectView(allSelectMedias.size());
        }
    }

    /**
     * 选择地图上的图片位置后展示
     */
    @Override
    public void clickLocation(LocationObj locationObj, List<MediaWrapObj> mediaWrapObjs) {
        addSubscription(
                apiService.getAddress(locationObj.getLat(), locationObj.getLog())
                        .compose(SchedulersCompat.applyIoSchedulers())
                        .subscribe(
                                response -> {
                                    if (response.success()) {

                                        String city = response.getLocationInfo().getCity();
                                        if (locationPhotoFragmentMap.containsKey(city)) {
                                            showContentEx(locationPhotoFragmentMap.get(city));
                                            locationPhotoFragmentMap.get(city).setMediaObjs(allSelectMedias);
                                        } else {
                                            ServerPhotoFragment locationFragment;
                                            locationFragment = ServerPhotoFragment.newInstanceEdit(
                                                    TypeConstants.PHOTO_TYPE_LOCATION,
                                                    FastData.getUserId(),
                                                    "",
                                                    allSelectMedias,
                                                    mediaWrapObjs,
                                                    bookType, babyId);

                                            locationPhotoFragmentMap.put(city, locationFragment);
                                            locationPhotoFragmentMap.get(city).setMediaObjs(allSelectMedias);
                                            showContentEx(locationFragment);
                                        }
                                        tvContent.setVisibility(View.VISIBLE);
                                        tvContentType.setVisibility(View.GONE);
                                        tvContent.setText(city);
                                    } else {
                                        ToastUtil.showToast(response.info);
                                    }
                                },
                                throwable -> Log.e(TAG, throwable.getLocalizedMessage())
                        )
        );
    }
}
