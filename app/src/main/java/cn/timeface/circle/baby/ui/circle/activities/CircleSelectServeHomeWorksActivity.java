package cn.timeface.circle.baby.ui.circle.activities;

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
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.MyPODActivity;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.support.api.models.objs.MediaObj;
import cn.timeface.circle.baby.support.mvp.model.BookModel;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.circle.bean.CircleHomeworkExObj;
import cn.timeface.circle.baby.ui.circle.dialogs.CircleSelectSchoolTaskTypeDialog;
import cn.timeface.circle.baby.ui.circle.fragments.CircleSelectBabyFragment;
import cn.timeface.circle.baby.ui.circle.fragments.CircleSelectSchoolTaskFragment;
import cn.timeface.open.api.bean.obj.TFOContentObj;
import cn.timeface.open.api.bean.obj.TFOPublishObj;
import cn.timeface.open.api.bean.obj.TFOResourceObj;

/**
 * 圈作品家校纪念册选择作业
 * author : sunyanwei Created on 17-3-22
 * email : sunyanwei@timeface.cn
 */
public class CircleSelectServeHomeWorksActivity extends BaseAppCompatActivity
        implements View.OnClickListener, CircleSelectSchoolTaskTypeDialog.SelectTypeListener {

    @Bind(R.id.tv_content_type)
    TextView tvContentType;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.fl_container)
    FrameLayout flContainer;

    CircleSelectBabyFragment selectBabyFragment;
    CircleSelectSchoolTaskFragment selectSchoolTaskFragment;
    CircleSelectSchoolTaskTypeDialog selectSchoolTaskTypeDialog;
    String circleId;
    public final int REQUEST_CODE_SELECT_HOME_WORK = 100;
    List<CircleHomeworkExObj> allSelHomeWorks = new ArrayList<>();

    public static void open(Context context, String circleId){
        Intent intent = new Intent(context, CircleSelectServeHomeWorksActivity.class);
        intent.putExtra("circle_id", circleId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_select_serve_home_works);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        circleId = getIntent().getStringExtra("circle_id");
        tvContentType.setOnClickListener(this);

        if(selectBabyFragment == null) {
            selectBabyFragment = CircleSelectBabyFragment.newInstance(circleId, allSelHomeWorks);
        }
        showContent(selectBabyFragment);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_publish_finish, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.menu_complete){
            if(allSelHomeWorks.size() <= 0){
                ToastUtil.showToast("请选择至少一条记录");
                return true;
            }


//            addSubscription(
//                    apiService.queryImageInfo(FastData.getBabyAvatar())
//                            .compose(SchedulersCompat.applyIoSchedulers())
//                            .subscribe(
//                                    response -> {
//                                        if (response.success()) {
//                                            //跳转开放平台POD接口；
//
//                                            //组装发布数据
//                                            //每条作业是一个tfopublishobj
//                                            List<TFOPublishObj> tfoPublishObjs = new ArrayList<>();
//
//                                                //content list，代表一个时光
//                                                List<TFOContentObj> tfoContentObjs = new ArrayList<>(allSelHomeWorks.size());
//                                                for (CircleHomeworkExObj timeLineObj : allSelHomeWorks) {
//                                                    tfoContentObjs.add(timeLineObj.toTFOContentObj());
//                                                }
//
//                                                TFOPublishObj tfoPublishObj = new TFOPublishObj(key, tfoContentObjs);
//                                                tfoPublishObjs.add(tfoPublishObj);
//
//
//                                            ArrayList<String> keys = new ArrayList<>();
//                                            ArrayList<String> values = new ArrayList<>();
//                                            keys.add("book_author");
//                                            keys.add("book_title");
//                                            values.add(author);
//                                            values.add(bookName);
//
//                                            if (bookType == BookModel.BOOK_TYPE_GROWTH_QUOTATIONS) {
//                                                //成长语录插页数据，content_list第一条数据为插页信息
//                                                List<TFOResourceObj> insertPageResources = new ArrayList<>(1);
//                                                TFOResourceObj insertPageResourceObj = new TFOResourceObj();
//                                                insertPageResourceObj.setImageUrl(FastData.getBabyAvatar());
//                                                insertPageResourceObj.setImageOrientation(response.getImageRotation());
//                                                insertPageResourceObj.setImageHeight(response.getImageHeight());
//                                                insertPageResourceObj.setImageWidth(response.getImageWidth());
//                                                insertPageResources.add(insertPageResourceObj);
//                                                TFOContentObj insertPageContent = new TFOContentObj("", insertPageResources);//没有subtitile
//                                                String insertContent = FastData.getBabyNickName()
//                                                        + ","
//                                                        + FastData.getBabyAge()
//                                                        + ","
//                                                        + "是一个活泼可爱的小宝宝，在"
//                                                        + FastData.getBabyNickName()
//                                                        + "成长的过程中经常会\"语出惊人\"，有时让我们很吃惊，宝宝小小的脑袋瓜怎么会冒出这么有意思的想法，在这里我们记录了"
//                                                        + FastData.getBabyNickName()
//                                                        + "成长中的童言趣语，一起来看看吧~";
//                                                insertPageContent.setContent(insertContent);
//                                                tfoPublishObjs.get(0).getContentList().add(0, insertPageContent);//插入插页信息
//                                            }
//
//                                            //拼接所有图片的id，作为保存书籍接口使用
//                                            StringBuffer sb = new StringBuffer("{\"mediaIds\":[");
//                                            StringBuffer sbTime = new StringBuffer("\"timeIds\":[");
//                                            for (CircleHomeworkExObj timeLineObj : allSelHomeWorks) {
//                                                if (timeLineObj != null) {
//                                                    sbTime.append(timeLineObj.getTimeId());
//                                                    sbTime.append(",");
//
//                                                    if (!timeLineObj.getHomework().getMediaList().isEmpty()) {
//                                                        for (MediaObj mediaObj : timeLineObj.getHomework().getMediaList()) {
//                                                            if(allSelectMedias.contains(mediaObj)){
//                                                                sb.append(mediaObj.getId());
//                                                                sb.append(",");
//                                                            }
//                                                        }
//                                                    }
//                                                }
//                                            }
//                                            if(sb.lastIndexOf(",") > -1)sb.replace(sb.lastIndexOf(","), sb.length(), "]");
//                                            if(sbTime.lastIndexOf(",") > -1)sbTime.replace(sbTime.lastIndexOf(","), sbTime.length(), "]");
//                                            sb.append(",").append(sbTime).append("}");
//
//                                            finish();
//                                            MyPODActivity.open(this, bookId, openBookId, bookType, openBookType, tfoPublishObjs, sb.toString(), true, FastData.getBabyId(), keys, values, TextUtils.isEmpty(bookId) ? 1 : 2);
//                                        }
//                                    },
//                                    throwable -> Log.e(TAG, throwable.getLocalizedMessage())
//                            ));
















        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_content_type:
                if(selectSchoolTaskTypeDialog == null){
                    selectSchoolTaskTypeDialog = CircleSelectSchoolTaskTypeDialog.newInstance(this);
                }
                selectSchoolTaskTypeDialog.show(getSupportFragmentManager(), "dialog");
                break;
        }
    }

    @Override
    public void selectTypeBaby() {
        if(selectBabyFragment == null) {
            selectBabyFragment = CircleSelectBabyFragment.newInstance(circleId, allSelHomeWorks);
        }
        selectBabyFragment.setAllSelHomeWorks(allSelHomeWorks);
        showContent(selectBabyFragment);
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
    }

    @Override
    public void selectTypeTask() {
        if(selectSchoolTaskFragment == null) {
            selectSchoolTaskFragment = CircleSelectSchoolTaskFragment.newInstance(circleId, allSelHomeWorks);
        }
        selectSchoolTaskFragment.setAllSelHomeWorks(allSelHomeWorks);
        showContent(selectSchoolTaskFragment);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_OK || data == null){
            return;
        }

        allSelHomeWorks = getIntent().getParcelableArrayListExtra("all_sel_home_works");
    }
}
