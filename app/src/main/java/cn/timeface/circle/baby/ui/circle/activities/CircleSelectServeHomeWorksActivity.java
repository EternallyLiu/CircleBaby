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
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.MyPODActivity;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.constants.TypeConstants;
import cn.timeface.circle.baby.support.api.models.objs.MediaObj;
import cn.timeface.circle.baby.support.mvp.model.BookModel;
import cn.timeface.circle.baby.support.utils.DateUtil;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.circle.bean.CircleHomeworkExObj;
import cn.timeface.circle.baby.ui.circle.bean.GrowthCircleObj;
import cn.timeface.circle.baby.ui.circle.dialogs.CircleSelectSchoolTaskTypeDialog;
import cn.timeface.circle.baby.ui.circle.fragments.CircleSelectBabyFragment;
import cn.timeface.circle.baby.ui.circle.fragments.CircleSelectSchoolTaskFragment;
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
    @Bind(R.id.tv_sel_count)
    TextView tvSelCount;
    @Bind(R.id.rl_photo_tip)
    RelativeLayout rlPhotoTip;

    CircleSelectBabyFragment selectBabyFragment;
    CircleSelectSchoolTaskFragment selectSchoolTaskFragment;
    CircleSelectSchoolTaskTypeDialog selectSchoolTaskTypeDialog;
    String circleId;
    public final int REQUEST_CODE_SELECT_HOME_WORK = 100;
    List<CircleHomeworkExObj> allSelHomeWorks = new ArrayList<>();
    boolean edit = false;
    String bookId = "";
    String openBookId = "";

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

        if (!edit) {
            tvContentType.setOnClickListener(this);
            if (selectBabyFragment == null) {
                selectBabyFragment = CircleSelectBabyFragment.newInstance(circleId, allSelHomeWorks);
            }
            showContent(selectBabyFragment);
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
            if(allSelHomeWorks.size() <= 0){
                ToastUtil.showToast("请选择至少一条记录");
                return true;
            }

            addSubscription(
                    apiService.queryImageInfo(FastData.getBabyAvatar())
                            .compose(SchedulersCompat.applyIoSchedulers())
                            .subscribe(
                                    response -> {
                                        if (response.success()) {
                                            Collections.sort(allSelHomeWorks);

                                            //跳转开放平台POD接口；
                                            String author = FastData.getUserName();
                                            String bookName = FastData.getBabyNickName() + "的家校纪念册";
                                            //组装发布数据
                                            //每条作业是一个tfopublishobj
                                            List<TFOPublishObj> tfoPublishObjs = new ArrayList<>();
                                            CircleHomeworkExObj minHomework = null;
                                            CircleHomeworkExObj maxHomework = null;
                                            if(allSelHomeWorks.size() > 0) {
                                                minHomework = maxHomework = allSelHomeWorks.get(0);
                                            }

                                            //转换成发布obj and 取出最大小值 and 拼接所有homework的id，作为保存书籍接口使用
                                            StringBuffer sbTaskIds = new StringBuffer("{\"mediaIds\":[");
                                            StringBuffer sbHomeworkIds = new StringBuffer("\"homeworkIds\":[");
                                            for(CircleHomeworkExObj circleHomeworkExObj : allSelHomeWorks){
                                                tfoPublishObjs.add(circleHomeworkExObj.toTFOPublishObj());
                                                if(circleHomeworkExObj.getHomework().getCreateDate() >= maxHomework.getHomework().getCreateDate()){
                                                    maxHomework = circleHomeworkExObj;
                                                } else {
                                                    minHomework = circleHomeworkExObj;
                                                }

                                                //拼接所有的media id
                                                for(MediaObj mediaObj : circleHomeworkExObj.getHomework().getMediaList()) {
                                                    sbTaskIds.append(mediaObj.getId());
                                                    sbTaskIds.append(",");
                                                }
                                                sbHomeworkIds.append(circleHomeworkExObj.getHomework().getHomeworkId());
                                                sbHomeworkIds.append(",");
                                            }

                                            sbTaskIds.replace(sbTaskIds.lastIndexOf(","), sbTaskIds.lastIndexOf(",") + 1, "],");
                                            sbHomeworkIds.replace(sbHomeworkIds.lastIndexOf(","), sbHomeworkIds.lastIndexOf(",") + 1, "],");
                                            sbTaskIds.append(sbHomeworkIds)
                                                    .append("\"circleId\":").append(circleId).append("}");

                                            ArrayList<String> keys = new ArrayList<>();
                                            ArrayList<String> values = new ArrayList<>();
                                            keys.add("book_author");
                                            keys.add("book_title");
                                            keys.add("book_period");
                                            keys.add("stu_class");
                                            keys.add("stu_name");
                                            values.add(author);
                                            values.add(bookName);
                                            values.add(DateUtil.getAgeMonth(FastData.getBabyBithday(), minHomework.getHomework().getCreateDate())
                                                    + "～"
                                                    + DateUtil.getAgeMonth(FastData.getBabyBithday(), maxHomework.getHomework().getCreateDate()));
                                            values.add("班级：" + GrowthCircleObj.getInstance().getCircleName());
                                            values.add("姓名：" + FastData.getBabyNickName());

                                            //成长语录插页数据，content_list第一条数据为寄语
                                            TFOPublishObj tfoPublishObjWord = new TFOPublishObj();
                                            tfoPublishObjWord.setContent("“成长”是一个色彩斑斓的字眼，在这成长的过程中，喜悦与烦恼在不经意间在这成长的路上烙上了一个又一个的脚印，可谓是“让我欢喜让我忧”。成长像一条小河，而成长就像一个鱼网，捕捉着成长中的喜悦与忧愁，而这些忧愁就好象入口的茉莉花，在口中不断地回味着，回味着……<br/>" +
                                                    "　　而在这成长的路上，正因为有了忧愁，正因为有了快乐，才构成了这条五彩缤纷的人生路，也许昨天给予我的是无穷的忧愁，但人不能总背着那么多的包袱，我们不该带着忧愁去面对今天，不管是今天，还是昨天，我只知道“希望在明天”，相信吧：明天的阳光一定比今天要灿烂夺目。<br/>" +
                                                    "　　我们的未来仍然等着我们去填充颜色，让我们握着手中的画笔，用自己的双手。<br/>" +
                                                    "　　“成长”是一个色彩斑斓的字眼，在这成长的过程中，喜悦与烦恼在不经意间在这成长的路上烙上了一个又一个的脚印，可谓是“让我欢喜让我忧”。入口的茉莉花，在口中不断地回味着，回味着……<br/>" +
                                                    "　　而在这成长的路上，正因为有了忧愁，正因为有了快乐，才构成了这条五彩缤纷的人生路，也许昨天给予我的是无穷的忧愁，但人不能总背着那么多的包袱，我们不该带着忧愁去面对今天，不管是今天，还是昨天，我只知道“希望在明天”，相信吧：明天的阳光一定比今天要灿烂夺目。<br/>" +
                                                    "　　我们的未来仍然等着我们去填充颜色，让我们握着手中的画笔，用自己的双手。");
                                            tfoPublishObjs.add(0, tfoPublishObjWord);//插入寄语信息

                                            //成长语录插页数据，content_list第二条数据为简介
                                            TFOPublishObj tfoPublishObjSummary = new TFOPublishObj();
                                            List<TFOResourceObj> avatarResources = new ArrayList<>(1);
                                            TFOResourceObj avatarResourceObj = new TFOResourceObj();
                                            avatarResourceObj.setImageUrl(FastData.getBabyAvatar());
                                            avatarResourceObj.setImageOrientation(response.getImageRotation());
                                            avatarResourceObj.setImageHeight(response.getImageHeight());
                                            avatarResourceObj.setImageWidth(response.getImageWidth());
                                            avatarResources.add(avatarResourceObj);
                                            tfoPublishObjSummary.setContent(
                                                    FastData.getBabyNickName() + FastData.getBabyAge() + "，是一个活波可爱的小宝宝，在" +
                                                            FastData.getBabyNickName() + "成长的过程中经常会“语出惊人”，有时让我们很吃惊，宝宝小小的脑袋瓜怎么会冒出这么有意思的想法，在这里我们记录了洋洋仔成长中的童言趣语，一起来看看吧~");
                                            tfoPublishObjSummary.setResourceList(avatarResources);
                                            tfoPublishObjs.add(1, tfoPublishObjSummary);

                                            finish();
                                            MyPODActivity.open(
                                                    this, bookId, openBookId,
                                                    BookModel.CIRCLE_BOOK_TYPE_FAMILY_SCHOOL,
                                                    TypeConstants.OPEN_BOOK_TYPE_CIRCLE_HOME_SCHOOL_BOOK, tfoPublishObjs, sbTaskIds.toString(),
                                                    true, FastData.getBabyId(), keys, values,
                                                    TextUtils.isEmpty(bookId) ? 1 : 2);
                                        } else {
                                            ToastUtil.showToast(response.info);
                                        }
                                    },
                                    throwable -> Log.e(TAG, throwable.getLocalizedMessage())
                            ));
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
        tvContentType.setText("按成员");
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
        tvContentType.setText("按作业");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_OK || data == null){
            return;
        }

        if(requestCode == REQUEST_CODE_SELECT_HOME_WORK){
            allSelHomeWorks = data.getParcelableArrayListExtra("all_select_works");
        }

        if(currentFragment instanceof CircleSelectBabyFragment){
            ((CircleSelectBabyFragment) currentFragment).setActivityResult(requestCode, resultCode, data);
            ((CircleSelectBabyFragment) currentFragment).setAllSelHomeWorks(allSelHomeWorks);
        } else if(currentFragment instanceof CircleSelectSchoolTaskFragment){
            ((CircleSelectSchoolTaskFragment) currentFragment).setActivityResult(requestCode, resultCode, data);
            ((CircleSelectSchoolTaskFragment) currentFragment).setAllSelHomeWorks(allSelHomeWorks);
        } else {
            Log.e(TAG, "not known fragment");
        }

        if(allSelHomeWorks.size() > 0){
            rlPhotoTip.setVisibility(View.VISIBLE);
            tvSelCount.setText(Html.fromHtml(String.format(getString(R.string.select_server_time_select_count), String.valueOf(allSelHomeWorks.size()))));
        } else {
            rlPhotoTip.setVisibility(View.GONE);
        }
    }
}
