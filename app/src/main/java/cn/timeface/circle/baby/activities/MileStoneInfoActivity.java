package cn.timeface.circle.baby.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.adapters.MilestoneAdapter;
import cn.timeface.circle.baby.adapters.MilestoneInfoAdapter;
import cn.timeface.circle.baby.api.models.objs.MilestoneTimeObj;
import cn.timeface.circle.baby.api.models.objs.TimeLineObj;
import cn.timeface.circle.baby.utils.DateUtil;
import cn.timeface.circle.baby.utils.FastData;
import cn.timeface.circle.baby.utils.GlideUtil;
import cn.timeface.circle.baby.utils.Remember;
import cn.timeface.circle.baby.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.views.ShareDialog;
import cn.timeface.common.utils.ShareSdkUtil;

public class MileStoneInfoActivity extends BaseAppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.appbar_layout)
    AppBarLayout appbarLayout;
    @Bind(R.id.content_recycler_view)
    RecyclerView contentRecyclerView;
    private int width;
    private int milestoneId;
    private MilestoneInfoAdapter adapter;

    public static void open(Context context,String title,int milestoneId) {
        Intent intent = new Intent(context, MileStoneInfoActivity.class);
        intent.putExtra("title",title);
        intent.putExtra("milestoneId",milestoneId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_milestoneinfo);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initView();
        reqData();

    }

    private void initView() {
        width = Remember.getInt("width", 0);
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        milestoneId = intent.getIntExtra("milestoneId", 0);
        getSupportActionBar().setTitle(title);

        adapter = new MilestoneInfoAdapter(this, new ArrayList<>());
        contentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        contentRecyclerView.setAdapter(adapter);
    }


    private void reqData() {
        apiService.milestoneInfo(milestoneId)
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(milestoneInfoResponse -> {
                    setDataList(milestoneInfoResponse.getDataList());
                }, throwable -> {
                    Log.e(TAG, "milestone:");
                });
    }

    private void setDataList(List<TimeLineObj> dataList) {
        adapter.setListData(dataList);
        adapter.notifyDataSetChanged();
    }


}
