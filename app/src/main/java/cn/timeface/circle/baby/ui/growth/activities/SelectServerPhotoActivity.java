package cn.timeface.circle.baby.ui.growth.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import cn.timeface.circle.baby.ui.growth.fragments.PhotoMapFragment;
import cn.timeface.circle.baby.ui.growth.fragments.SelectUserFragment;
import cn.timeface.circle.baby.ui.growth.fragments.ServerPhotoFragment;
import cn.timeface.open.api.bean.obj.TFOContentObj;
import cn.timeface.open.api.bean.obj.TFOPublishObj;
import cn.timeface.open.api.bean.obj.TFOResourceObj;

/**
 * 选择服务器照片（已经上传的图片）
 * author : YW.SUN Created on 2017/2/14
 * email : sunyw10@gmail.com
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

    List<MediaObj> allSelectMedias = new ArrayList<>();


    public static void open(Context context,int bookType, int openBookType, String bookId, String openBookId) {
        Intent intent = new Intent(context, SelectServerPhotoActivity.class);
        intent.putExtra("open_book_type", openBookType);
        intent.putExtra("book_type", bookType);
        intent.putExtra("book_id", bookId);
        intent.putExtra("open_book_id", openBookId);
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

        //新建一本
        if(TextUtils.isEmpty(bookId)){
            timePhotoFragment = ServerPhotoFragment.newInstance(TypeConstants.PHOTO_TYPE_TIME, FastData.getUserId());
            showContent(timePhotoFragment, false);
        //编辑一本
        } else {
            apiService.bookMedias(bookId)
                    .compose(SchedulersCompat.applyIoSchedulers())
                    .subscribe(
                            response -> {
                                if(response.success()){
                                    this.allSelectMedias = response.getDataList();
                                    timePhotoFragment = ServerPhotoFragment.newInstanceEdit(
                                            TypeConstants.PHOTO_TYPE_TIME,
                                            FastData.getUserId(),
                                            response.getDataList()
                                    );
                                    showContent(timePhotoFragment, false);
                                } else {
                                    ToastUtil.showToast(response.info);
                                }
                            },
                            throwable -> {
                                Log.e(TAG, throwable.getLocalizedMessage());
                            }
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
            List<MediaObj> selectedMedias = new ArrayList<>();
            if(timePhotoFragment != null){
                selectedMedias.addAll(timePhotoFragment.getSelectedMedias());
            }

            //取出所有用户发的选中的照片
            Iterator iterator = userPhotoFragmentMap.entrySet().iterator();
            while (iterator.hasNext()){
                Map.Entry entry = (Map.Entry) iterator.next();
                ServerPhotoFragment photoFragment = (ServerPhotoFragment) entry.getValue();
                selectedMedias.addAll(photoFragment.getSelectedMedias());
            }

            //取出所有从地点筛选选中的照片
            Iterator iteratorLocation = locationPhotoFragmentMap.entrySet().iterator();
            while (iteratorLocation.hasNext()){
                Map.Entry entry = (Map.Entry) iteratorLocation.next();
                ServerPhotoFragment photoFragment = (ServerPhotoFragment) entry.getValue();
                selectedMedias.addAll(photoFragment.getSelectedMedias());
            }

            if(labelPhotoFragment != null){
                selectedMedias.addAll(labelPhotoFragment.getSelectedMedias());
            }

            int pageNum = selectedMedias.size();
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
                                            String bookName = FastData.getBabyName() + "的精装照片书";
                                            if(bookType == BookModel.BOOK_TYPE_PAINTING){
                                                bookName = FastData.getBabyName() + "的绘画集";
                                            }
                                            List<TFOResourceObj> tfoResourceObjs = new ArrayList<>();
                                            StringBuffer sb = new StringBuffer("{\"dataList\":[");
                                            int index = 0;
                                            for(MediaObj mediaObj : selectedMedias){
                                                index++;
                                                TFOResourceObj tfoResourceObj = mediaObj.toTFOResourceObj();
                                                tfoResourceObjs.add(tfoResourceObj);
                                                sb.append(mediaObj.getId());
                                                if (index < selectedMedias.size()) {
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
            tvContent.setVisibility(View.GONE);
            tvContentType.setVisibility(View.VISIBLE);
        } else {
            finish();
        }
        super.onBackPressed();
    }

    /**
     * 按时间筛选照片
     */
    @Override
    public void selectTypeTime() {
        tvContentType.setText("按时间");
        if(timePhotoFragment == null){
            if(TextUtils.isEmpty(bookId)){
                timePhotoFragment = ServerPhotoFragment.newInstance(TypeConstants.PHOTO_TYPE_TIME, FastData.getUserId());
            } else {
                timePhotoFragment = ServerPhotoFragment.newInstanceEdit(TypeConstants.PHOTO_TYPE_TIME, FastData.getUserId(), allSelectMedias);
            }
        }
        showContent(timePhotoFragment, false);
        onClick(tvContentType);
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
        showContent(selectUserFragment, false);
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
        showContent(locationPhotoFragment, false);
        onClick(tvContentType);
    }

    /**
     * 按标签筛选照片
     */
    @Override
    public void selectTypeLabel() {
        tvContentType.setText("按标签");
        if(labelPhotoFragment == null){
            if(TextUtils.isEmpty(bookId)){
                labelPhotoFragment = ServerPhotoFragment.newInstance(TypeConstants.PHOTO_TYPE_LABEL, FastData.getUserId());
            } else {
                labelPhotoFragment = ServerPhotoFragment.newInstanceEdit(TypeConstants.PHOTO_TYPE_LABEL, FastData.getUserId(), allSelectMedias);
            }
        }
        showContent(labelPhotoFragment, false);
        onClick(tvContentType);
    }

    @Override
    public void setTypeText(String title) {
        tvContentType.setText(title);
    }

    Fragment currentFragment = null;
    public void showContent(Fragment fragment, boolean canBack) {
        this.canBack = canBack;
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
        if(canBack){
            ft.addToBackStack(null);
        }
        ft.commitAllowingStateLoss();
        invalidateOptionsMenu();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_content_type:
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                if (fragmentShow) {
                    transaction.hide(selectContentTypeDialog);
                    fragmentShow = false;
                } else {
                    if (selectContentTypeDialog == null) {
                        selectContentTypeDialog = SelectContentTypeDialog.newInstance(this, SelectContentTypeDialog.CONTENT_TYPE_PHOTO);
                        transaction.add(R.id.fl_container_type, selectContentTypeDialog);
                    } else {
                        transaction.show(selectContentTypeDialog);
                    }
                    fragmentShow = true;
                }
                transaction.commit();
                break;

            //点击选择用户操作
            case R.id.ll_root:
                UserWrapObj userWrapObj = (UserWrapObj) view.getTag(R.string.tag_obj);
                tvContentType.setVisibility(View.GONE);
                tvContent.setVisibility(View.VISIBLE);
                tvContent.setText(userWrapObj.getUserInfo().getRelationName());

                //已经加载
                if(userPhotoFragmentMap.containsKey(userWrapObj.getUserInfo().getUserId())){
                    showContent(userPhotoFragmentMap.get(userWrapObj.getUserInfo().getUserId()), true);
                } else {
                    ServerPhotoFragment serverPhotoFragment;
                    if(TextUtils.isEmpty(bookId)){
                        serverPhotoFragment = ServerPhotoFragment.newInstance(TypeConstants.PHOTO_TYPE_USER, userWrapObj.getUserInfo().getUserId());
                    } else {
                        serverPhotoFragment = ServerPhotoFragment.newInstanceEdit(TypeConstants.PHOTO_TYPE_USER, userWrapObj.getUserInfo().getUserId(), allSelectMedias);
                    }
                    userPhotoFragmentMap.put(userWrapObj.getUserInfo().getUserId(), serverPhotoFragment);
                    showContent(serverPhotoFragment, true);
                }
                break;
        }
    }

    public void clickPhotoView(View view){}

    @Subscribe
    public void bookOptionEvent(BookOptionEvent optionEvent){
        if(optionEvent.getOption() == BookOptionEvent.BOOK_OPTION_CREATE){
            finish();
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
                                            showContent(locationPhotoFragmentMap.get(city), true);
                                        } else {
                                            ServerPhotoFragment locationFragment;
                                            if (TextUtils.isEmpty(bookId)) {
                                                locationFragment = ServerPhotoFragment.newInstance(TypeConstants.PHOTO_TYPE_LOCATION, FastData.getUserId(), response.getLocationInfo().getCity(), mediaWrapObjs);
                                            } else {
                                                locationFragment = ServerPhotoFragment.newInstanceEdit(TypeConstants.PHOTO_TYPE_LOCATION, FastData.getUserId(), allSelectMedias, mediaWrapObjs);
                                            }

                                            locationPhotoFragmentMap.put(city, locationFragment);
                                            showContent(locationFragment, true);
                                        }
                                        tvContent.setVisibility(View.VISIBLE);
                                        tvContentType.setVisibility(View.GONE);
                                        tvContent.setText(city);
                                    } else {
                                        ToastUtil.showToast(response.info);
                                    }
                                },
                                throwable -> {
                                    Log.e(TAG, throwable.getLocalizedMessage());
                                }
                        )
        );
    }
}
