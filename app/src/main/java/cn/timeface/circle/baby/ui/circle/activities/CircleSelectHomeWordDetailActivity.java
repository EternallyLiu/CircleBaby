package cn.timeface.circle.baby.ui.circle.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.BuildConfig;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.support.api.models.objs.MediaObj;
import cn.timeface.circle.baby.support.mvp.bases.BasePresenterAppCompatActivity;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.circle.adapters.CircleSelectHomeWorkAdapter;
import cn.timeface.circle.baby.ui.circle.bean.CircleHomeWorkExWrapperObj;
import cn.timeface.circle.baby.ui.circle.bean.CircleHomeworkExObj;
import cn.timeface.circle.baby.ui.circle.bean.CircleHomeworkObj;
import cn.timeface.circle.baby.ui.circle.bean.CircleMediaObj;
import cn.timeface.circle.baby.views.TFStateView;

/**
 * 圈作品选择作业列表详情页面
 * author : sunyanwei Created on 17-3-22
 * email : sunyanwei@timeface.cn
 */
public class CircleSelectHomeWordDetailActivity extends BasePresenterAppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.rv_content)
    RecyclerView rvContent;
    @Bind(R.id.tf_stateView)
    TFStateView stateView;

    CircleSelectHomeWorkAdapter selectHomeWorkAdapter;
    int babyId;
    String circleId;

    public static void open(Context context, String circleId, int babyId){
        Intent intent= new Intent(context, CircleSelectHomeWordDetailActivity.class);
        intent.putExtra("circle_id", circleId);
        intent.putExtra("baby_id", babyId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_select_home_word_detail);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("作业详情");

        reqData();
    }

    private void reqData(){
        stateView.loading();
        if(BuildConfig.DEBUG){
            List<CircleHomeWorkExWrapperObj> homeWorkExWrapperObjList = new ArrayList<>();
            for(int i = 0; i < 10; i++){
                List<CircleHomeworkExObj> circleHomeworkExObjList = new ArrayList<>();
                CircleHomeWorkExWrapperObj homeWorkExWrapperObj = new CircleHomeWorkExWrapperObj();
                homeWorkExWrapperObj.setDate("2017年03月");
                homeWorkExWrapperObjList.add(homeWorkExWrapperObj);
                homeWorkExWrapperObj.setHomeworkList(circleHomeworkExObjList);

                for(int j = 0; j < 10; j++){
                    CircleHomeworkExObj homeworkExObj = new CircleHomeworkExObj();
                    CircleHomeworkObj homeworkObj = new CircleHomeworkObj();
                    homeworkObj.setTitle("title");
                    circleHomeworkExObjList.add(homeworkExObj);
                    homeworkExObj.setSchoolTaskName("寒假为父母做一件事情");
                    List<CircleMediaObj> mediaObjList = new ArrayList<>();
                    homeworkObj.setMediaList(mediaObjList);
                    homeworkExObj.setHomework(homeworkObj);
                    for(int k = 0; k < 10; k++){
                        CircleMediaObj circleMediaObj = new CircleMediaObj();
                        circleMediaObj.setImgUrl("http://img1.timeface.cn/baby/45e71214e0af15a36d270f5cb381a37c.jpg");
//                        circleMediaObj.setContent(mediaObj.getContent());
//                        circleMediaObj.setBaseType(mediaObj.getBaseType());
//                        circleMediaObj.setDate(mediaObj.getDate());
//                        circleMediaObj.setFavoritecount(mediaObj.getFavoritecount());
//                        circleMediaObj.setH(mediaObj.getH());
//                        circleMediaObj.setId(mediaObj.getId());
//                        circleMediaObj.setImageOrientation(mediaObj.getImageOrientation());
//                        circleMediaObj.setLength(mediaObj.getLength());
//                        circleMediaObj.setLocalIdentifier(mediaObj.getLocalIdentifier());
//                        circleMediaObj.setIsFavorite(mediaObj.getIsFavorite());
//                        circleMediaObj.setTimeId(mediaObj.getTimeId());
//                        circleMediaObj.setLocalPath(mediaObj.getLocalPath());
//                        circleMediaObj.setLocation(mediaObj.getLocation());
//                        circleMediaObj.setTip(mediaObj.getTip());
//                        circleMediaObj.setTips(mediaObj.getTips());
//                        circleMediaObj.setW(mediaObj.getW());
                        mediaObjList.add(circleMediaObj);
                    }

                }
            }

            setData(homeWorkExWrapperObjList);
            stateView.finish();
        } else
        addSubscription(
                apiService.queryHomeworksByBaby(circleId, babyId)
                        .compose(SchedulersCompat.applyIoSchedulers())
                        .doOnCompleted(() -> stateView.finish())
                        .subscribe(
                                response -> {
                                    if(response.success()){
                                        setData(response.getDataList());
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

    private void setData(List<CircleHomeWorkExWrapperObj> homeWorkExWrapperObjList){
        if(selectHomeWorkAdapter == null){
            selectHomeWorkAdapter = new CircleSelectHomeWorkAdapter(this, homeWorkExWrapperObjList, Integer.MAX_VALUE, new ArrayList<>());
            rvContent.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            rvContent.addItemDecoration(
                    new HorizontalDividerItemDecoration.Builder(this)
                            .colorResId(R.color.line_color)
                            .size(1)
                            .build());
            rvContent.setAdapter(selectHomeWorkAdapter);
        } else {
            selectHomeWorkAdapter.setListData(homeWorkExWrapperObjList);
            selectHomeWorkAdapter.notifyDataSetChanged();
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
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
