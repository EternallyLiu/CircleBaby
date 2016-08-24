package cn.timeface.circle.baby.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.adapters.MilestoneInfoAdapter;
import cn.timeface.circle.baby.api.models.objs.TimeLineObj;
import cn.timeface.circle.baby.utils.Remember;
import cn.timeface.circle.baby.utils.rxutils.SchedulersCompat;

public class MileStoneInfoActivity extends BaseAppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.appbar_layout)
    AppBarLayout appbarLayout;
    @Bind(R.id.content_recycler_view)
    RecyclerView contentRecyclerView;
    @Bind(R.id.ll_no_data)
    LinearLayout llNoData;
    private int width;
    private int milestoneId;
    private MilestoneInfoAdapter adapter;

    public static void open(Context context, String title, int milestoneId) {
        Intent intent = new Intent(context, MileStoneInfoActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("milestoneId", milestoneId);
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
                    throwable.printStackTrace();
                });
    }

    private void setDataList(List<TimeLineObj> dataList) {
        if(null==dataList||dataList.size()==0){
            llNoData.setVisibility(View.VISIBLE);
        }
        adapter.setListData(dataList);
        adapter.notifyDataSetChanged();
    }


}
