package cn.timeface.circle.baby.ui.growth.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import cn.timeface.circle.baby.support.api.models.objs.TimeLineObj;
import cn.timeface.circle.baby.support.api.models.objs.UserWrapObj;
import cn.timeface.circle.baby.support.managers.listeners.IEventBus;
import cn.timeface.circle.baby.support.mvp.bases.BasePresenterAppCompatActivity;
import cn.timeface.circle.baby.support.mvp.model.BookModel;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.ui.growth.fragments.SelectUserFragment;
import cn.timeface.circle.baby.ui.growth.fragments.ServerTimeFragment;
import cn.timeface.open.api.bean.obj.TFOContentObj;
import cn.timeface.open.api.bean.obj.TFOPublishObj;
import cn.timeface.open.api.bean.obj.TFOResourceObj;

/**
 * 选择时光界面（从服务器拉取数据）
 * author : YW.SUN Created on 2017/2/15
 * email : sunyw10@gmail.com
 */
public class SelectServerTimeActivity extends BasePresenterAppCompatActivity implements
        SelectContentTypeDialog.SelectTypeListener, View.OnClickListener, IEventBus {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.content_select_time)
    RelativeLayout contentSelectTime;
    @Bind(R.id.tv_content_type)
    TextView tvContentType;

    boolean fragmentShow = false;
    boolean userFragmentShow = false;
    ServerTimeFragment timeFragment;//按时间
    ServerTimeFragment userFragment;//按发布人
    SelectContentTypeDialog selectContentTypeDialog;
    SelectUserFragment selectUserFragment;
    int openBookType;
    int bookType;

    public static void open(Context context,int bookType, int openBookType) {
        Intent intent = new Intent(context, SelectServerTimeActivity.class);
        intent.putExtra("open_book_type", openBookType);
        intent.putExtra("book_type", bookType);
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
        timeFragment = ServerTimeFragment.newInstance(TypeConstants.PHOTO_TYPE_TIME, FastData.getUserId(), bookType);
        this.openBookType = getIntent().getIntExtra("open_book_type", 0);
        this.bookType = getIntent().getIntExtra("book_type", 0);
        showContent(timeFragment);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_select_server_photo, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.save){
            List<TimeLineObj> selectedMedias = new ArrayList<>();
            if(timeFragment != null){
                selectedMedias.addAll(timeFragment.getSelectedMedias());
            }
            if(userFragment != null){
                selectedMedias.addAll(userFragment.getSelectedMedias());
            }

            int pageNum = selectedMedias.size();
            if(pageNum == 0){
                ToastUtil.showToast("请选择至少一张照片");
                return true;
            }

            //跳转开放平台POD接口；
            String bookName = FastData.getBabyName() + "的精装照片书";
            if(bookType == BookModel.BOOK_TYPE_PAINTING){
                bookName = FastData.getBabyName() + "的绘画集";
            }
            List<TFOResourceObj> tfoResourceObjs = new ArrayList<TFOResourceObj>();
            StringBuffer sb = new StringBuffer("{\"dataList\":[");
            int index = 0;
//            for(TimeLineObj mediaObj : selectedMedias){
//                index++;
//                TFOResourceObj tfoResourceObj = mediaObj.toTFOResourceObj();
//                tfoResourceObjs.add(tfoResourceObj);
//                sb.append(mediaObj.getId());
//                if (index < selectedMedias.size()) {
//                    sb.append(",");
//                } else {
//                    sb.append("]}");
//                }
//            }


            TFOContentObj tfoContentObj = new TFOContentObj("", tfoResourceObjs);
            List<TFOContentObj> tfoContentObjs1 = new ArrayList<>();
            tfoContentObjs1.add(tfoContentObj);
            TFOPublishObj tfoPublishObj = new TFOPublishObj(bookName, tfoContentObjs1);
            List<TFOPublishObj> tfoPublishObjs = new ArrayList<>();
            tfoPublishObjs.add(tfoPublishObj);

            ArrayList<String> keys = new ArrayList<>();
            ArrayList<String> values = new ArrayList<>();
            keys.add("book_author");
            keys.add("book_title");
            values.add(FastData.getUserName());
            values.add(bookName);

            MyPODActivity.open(this, "", "", bookType, openBookType, tfoPublishObjs, sb.toString(),true,FastData.getBabyId(),keys,values,1);
//            MyPODActivity.open(this, bookId, openBookId, openBookType, tfoPublishObjs, s,true,FastData.getBabyId(),keys,values,1);
//            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 按时间筛选照片
     */
    @Override
    public void selectTypeTime() {
        if(userFragmentShow)setSelectUserFragmentHide();
        tvContentType.setText("按时间");
        if(timeFragment == null){
            timeFragment = ServerTimeFragment.newInstance(TypeConstants.PHOTO_TYPE_TIME, FastData.getUserId(), bookType);
        }
        showContent(timeFragment);
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
                        selectContentTypeDialog = SelectContentTypeDialog.newInstance(this);
                        selectContentTypeDialog.setTypeLabelVisibilty(View.INVISIBLE);
                        selectContentTypeDialog.setTypeLocationVisibilty(View.INVISIBLE);
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
                if (userFragment == null) {
                    userFragment = ServerTimeFragment.newInstance(TypeConstants.PHOTO_TYPE_USER, userWrapObj.getUserInfo().getUserId(), bookType);
                }
                showContent(userFragment);
                break;
        }
    }

    @Subscribe
    public void bookOptionEvent(BookOptionEvent optionEvent){
        if(optionEvent.getOption() == BookOptionEvent.BOOK_OPTION_CREATE){
            finish();
        }
    }
}
