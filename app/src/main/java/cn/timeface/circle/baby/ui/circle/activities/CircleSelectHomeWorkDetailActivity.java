package cn.timeface.circle.baby.ui.circle.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.support.mvp.bases.BasePresenterAppCompatActivity;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.circle.adapters.CircleSelectHomeWorkAdapter;
import cn.timeface.circle.baby.ui.circle.bean.CircleHomeWorkExWrapperObj;
import cn.timeface.circle.baby.ui.circle.bean.CircleHomeworkExObj;
import cn.timeface.circle.baby.views.TFStateView;

/**
 * 圈作品选择作业列表详情页面
 * author : sunyanwei Created on 17-3-22
 * email : sunyanwei@timeface.cn
 */
public class CircleSelectHomeWorkDetailActivity extends BasePresenterAppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.rv_content)
    RecyclerView rvContent;
    @Bind(R.id.tf_stateView)
    TFStateView stateView;

    CircleSelectHomeWorkAdapter selectHomeWorkAdapter;
    long babyId;
    String circleId;
    List<CircleHomeworkExObj> allSelHomeWorks;

    public void open(Context context, String circleId, long babyId){
        Intent intent= new Intent(context, CircleSelectHomeWorkDetailActivity.class);
        intent.putExtra("circle_id", circleId);
        intent.putExtra("baby_id", babyId);
        context.startActivity(intent);
    }

    public static void open4Result(Context context, int reqCode, String circleId, long babyId, List<CircleHomeworkExObj> allSelHomeWorks){
        Intent intent= new Intent(context, CircleSelectHomeWorkDetailActivity.class);
        intent.putExtra("circle_id", circleId);
        intent.putExtra("baby_id", babyId);
        intent.putParcelableArrayListExtra("all_sel_home_works", (ArrayList<? extends Parcelable>) allSelHomeWorks);
        ((Activity) context).startActivityForResult(intent, reqCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_select_home_word_detail);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("作业详情");
        circleId = getIntent().getStringExtra("circle_id");
        babyId = getIntent().getLongExtra("baby_id", 0);
        this.allSelHomeWorks = getIntent().getParcelableArrayListExtra("all_sel_home_works");

        if(!TextUtils.isEmpty(circleId))reqData();
    }

    private void reqData() {
        stateView.loading();
        addSubscription(
                apiService.queryHomeworksByBaby(circleId, babyId)
                        .compose(SchedulersCompat.applyIoSchedulers())
                        .doOnCompleted(() -> stateView.finish())
                        .subscribe(
                                response -> {
                                    if (response.success()) {
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

    protected void setData(List<CircleHomeWorkExWrapperObj> homeWorkExWrapperObjList){
        if(selectHomeWorkAdapter == null){
            selectHomeWorkAdapter = new CircleSelectHomeWorkAdapter(this, homeWorkExWrapperObjList, Integer.MAX_VALUE, allSelHomeWorks);
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

        if(selectHomeWorkAdapter.getListData().isEmpty()){
            stateView.empty();
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
            close();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        close();
    }

    private void close(){
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra("all_select_works", (ArrayList<? extends Parcelable>) selectHomeWorkAdapter.getSelImgs());
        intent.putExtra("photo_count", selectHomeWorkAdapter.getSelImgs().size());
        setResult(RESULT_OK, intent);
        finish();
    }
}
