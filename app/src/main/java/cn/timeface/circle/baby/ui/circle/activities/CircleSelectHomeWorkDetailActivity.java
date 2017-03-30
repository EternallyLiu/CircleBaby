package cn.timeface.circle.baby.ui.circle.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.support.mvp.bases.BasePresenterAppCompatActivity;
import cn.timeface.circle.baby.support.utils.FastData;
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
public class CircleSelectHomeWorkDetailActivity extends BasePresenterAppCompatActivity implements View.OnClickListener {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.rv_content)
    RecyclerView rvContent;
    @Bind(R.id.tf_stateView)
    TFStateView stateView;
    @Bind(R.id.cb_all_sel)
    CheckBox cbAllSel;
    @Bind(R.id.tv_sel_count)
    TextView tvSelCount;
    @Bind(R.id.rl_photo_tip)
    RelativeLayout rlPhotoTip;

    CircleSelectHomeWorkAdapter selectHomeWorkAdapter;
    long babyId;
    String circleId;
    List<CircleHomeworkExObj> allSelHomeWorks;

    public void open(Context context, String circleId, long babyId) {
        Intent intent = new Intent(context, CircleSelectHomeWorkDetailActivity.class);
        intent.putExtra("circle_id", circleId);
        intent.putExtra("baby_id", babyId);
        context.startActivity(intent);
    }

    public static void open4Result(Context context, int reqCode, String circleId, long babyId, List<CircleHomeworkExObj> allSelHomeWorks) {
        Intent intent = new Intent(context, CircleSelectHomeWorkDetailActivity.class);
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
        getSupportActionBar().setTitle(FastData.getBabyNickName() + "的作业");
        circleId = getIntent().getStringExtra("circle_id");
        babyId = getIntent().getLongExtra("baby_id", 0);
        this.allSelHomeWorks = getIntent().getParcelableArrayListExtra("all_sel_home_works");
        cbAllSel.setOnClickListener(this);

        if (!getIntent().hasExtra("task_id")) reqData();
    }

    private void reqData() {
        stateView.loading();
        addSubscription(
                apiService.queryHomeworksByBaby(circleId, babyId)
                        .compose(SchedulersCompat.applyIoSchedulers())
                        .subscribe(
                                response -> {
                                    stateView.finish();
                                    if (response.success()) {
                                        setData(response.getDataList(), "");
                                    } else {
                                        showToast(response.info);
                                    }
                                },
                                throwable -> {
                                    stateView.showException(throwable);
                                    Log.e(TAG, throwable.getLocalizedMessage());
                                }
                        )
        );
    }

    protected void setData(List<CircleHomeWorkExWrapperObj> homeWorkExWrapperObjList, String taskId) {
        if (selectHomeWorkAdapter == null) {
            selectHomeWorkAdapter = new CircleSelectHomeWorkAdapter(
                    this, homeWorkExWrapperObjList,
                    Integer.MAX_VALUE, allSelHomeWorks, taskId);
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

        if (selectHomeWorkAdapter.getListData().isEmpty()) {
            stateView.empty();
        }

        initPhotoTip();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_publish_finish, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_complete) {
            close();
            return true;
        } else if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        close();
        super.onBackPressed();
    }

    public void initPhotoTip() {
        if(selectHomeWorkAdapter.getListData().isEmpty()){
            rlPhotoTip.setVisibility(View.GONE);
        } else {
            rlPhotoTip.setVisibility(View.VISIBLE);
            tvSelCount.setText(Html.fromHtml(String.format(getString(R.string.select_server_time_select_count), String.valueOf(allSelHomeWorks.size()))));
            cbAllSel.setChecked(allSelHomeWorks.containsAll(selectHomeWorkAdapter.getHomeworkExObjList()));
        }
    }

    private void close() {
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra("all_select_works", (ArrayList<? extends Parcelable>) selectHomeWorkAdapter.getSelImgs());
        intent.putExtra("photo_count", selectHomeWorkAdapter.getSelectCount());
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.cb_all_sel:
                if (((CheckBox) view).isChecked()) {
                    selectHomeWorkAdapter.doAllSelImg();
                } else {
                    selectHomeWorkAdapter.doAllUnSelImg();
                }
                initPhotoTip();
                break;
        }
    }
}
