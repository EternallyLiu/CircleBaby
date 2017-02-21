package cn.timeface.circle.baby.ui.growth.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.MyPODActivity;
import cn.timeface.circle.baby.constants.TypeConstants;
import cn.timeface.circle.baby.dialogs.SelectContentTypeDialog;
import cn.timeface.circle.baby.events.BookOptionEvent;
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
    boolean userFragmentShow = false;
    @Bind(R.id.tv_content_type)
    TextView tvContentType;
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    ServerPhotoFragment timePhotoFragment;//按时间
    ServerPhotoFragment userPhotoFragment;//按发布人
    ServerPhotoFragment labelPhotoFragment;//按标签
    PhotoMapFragment locationPhotoFragment;//按地点

    SelectContentTypeDialog selectContentTypeDialog;
    SelectUserFragment selectUserFragment;
    int openBookType;
    int bookType;


    public static void open(Context context,int bookType, int openBookType) {
        Intent intent = new Intent(context, SelectServerPhotoActivity.class);
        intent.putExtra("open_book_type", openBookType);
        intent.putExtra("book_type", bookType);
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
        timePhotoFragment = ServerPhotoFragment.newInstance(TypeConstants.PHOTO_TYPE_TIME, FastData.getUserId());
        this.openBookType = getIntent().getIntExtra("open_book_type", 0);
        this.bookType = getIntent().getIntExtra("book_type", 0);
        showContent(timePhotoFragment, false);
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
            if(userPhotoFragment != null){
                selectedMedias.addAll(userPhotoFragment.getSelectedMedias());
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


                                            MyPODActivity.open(this, "", "", bookType, openBookType, tfoPublishObjs, sb.toString(),true,FastData.getBabyId(),keys,values,1);
                                        }
                                    },
                                    throwable -> {
                                        Log.e(TAG, throwable.getLocalizedMessage());
                                    }
                            )
            );
            return true;
        } else {
            onBackPressed();
            return true;
        }
    }

    /**
     * 按时间筛选照片
     */
    @Override
    public void selectTypeTime() {
        if(userFragmentShow)setSelectUserFragmentHide();
        tvContentType.setText("按时间");
        if(timePhotoFragment == null){
            timePhotoFragment = ServerPhotoFragment.newInstance(TypeConstants.PHOTO_TYPE_TIME, FastData.getUserId());
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

        //展示选择发布人页面
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        if (userFragmentShow) {
            transaction.hide(selectUserFragment);
            userFragmentShow = false;
        } else {
            if (selectUserFragment == null) {
                selectUserFragment = SelectUserFragment.newInstance(this);
                transaction.add(R.id.fl_container_user, selectUserFragment);
            } else {
                transaction.show(selectUserFragment);
            }
            userFragmentShow = true;
        }
        transaction.commit();
        onClick(tvContentType);
    }

    /**
     * 按地点筛选照片
     */
    @Override
    public void selectTypeLocation() {
        if(userFragmentShow)setSelectUserFragmentHide();
        tvContentType.setText("按地点");
//        if(locationPhotoFragment == null){
            locationPhotoFragment = PhotoMapFragment.newInstance(this);
//        }
        showContent(locationPhotoFragment, true);
        onClick(tvContentType);
    }

    /**
     * 按标签筛选照片
     */
    @Override
    public void selectTypeLabel() {
        if(userFragmentShow)setSelectUserFragmentHide();
        tvContentType.setText("按标签");
        if(labelPhotoFragment == null){
            labelPhotoFragment = ServerPhotoFragment.newInstance(TypeConstants.PHOTO_TYPE_LABEL, FastData.getUserId());
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

    private void setSelectUserFragmentHide(){
        userFragmentShow = false;
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        transaction.hide(selectUserFragment);
        transaction.commit();
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
                if(userFragmentShow)setSelectUserFragmentHide();
                UserWrapObj userWrapObj = (UserWrapObj) view.getTag(R.string.tag_obj);
                if (userPhotoFragment == null) {
                    userPhotoFragment = ServerPhotoFragment.newInstance(TypeConstants.PHOTO_TYPE_USER, userWrapObj.getUserInfo().getUserId());
                }
                showContent(userPhotoFragment, true);
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
    public void clickLocation(String addressName, List<MediaWrapObj> mediaWrapObjs) {
        showContent(ServerPhotoFragment.newInstance(TypeConstants.PHOTO_TYPE_LOCATION, FastData.getUserId(), addressName, mediaWrapObjs), true);
    }
}
