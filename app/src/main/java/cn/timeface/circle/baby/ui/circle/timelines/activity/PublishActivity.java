package cn.timeface.circle.baby.ui.circle.timelines.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.ui.circle.bean.CircleHomeworkObj;
import cn.timeface.circle.baby.ui.circle.bean.CircleSchoolTaskObj;
import cn.timeface.circle.baby.ui.circle.bean.CircleTimelineObj;
import cn.timeface.circle.baby.ui.circle.timelines.adapter.PublishAdapter;
import cn.timeface.circle.baby.ui.circle.timelines.bean.ItemObj;
import cn.timeface.circle.baby.ui.circle.timelines.views.CircleGridStaggerLookup;

/**
 * author : wangshuai Created on 2017/3/15
 * email : wangs1992321@gmail.com
 */
public class PublishActivity extends BaseAppCompatActivity {
    public static final int MAX_PIC_COUNT = 99;

    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.content_recycler_view)
    RecyclerView contentRecyclerView;

    private PublishAdapter adapter = null;

    private int type = PublishAdapter.TYPE_TIMELINE;
    private CircleGridStaggerLookup lookup;

    public static void open(Context context){
        context.startActivity(new Intent(context,PublishActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_publish);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        switch (type){
            case PublishAdapter.TYPE_TIMELINE:
                title.setText(R.string.send_circle_time_line_title);
                break;
            case PublishAdapter.TYPE_WORK:
                title.setText(R.string.send_circle_homework_title);
                break;
            case PublishAdapter.TYPE_SCHOOL:
                title.setText(R.string.send_circle_school_title);
                break;

        }
        adapter = new PublishAdapter(this);
        lookup = new CircleGridStaggerLookup(type, 3, false);
        adapter.setLookup(lookup);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
        gridLayoutManager.setSpanSizeLookup(lookup);
        adapter.setType(type);
        contentRecyclerView.setAdapter(adapter);
        contentRecyclerView.setLayoutManager(gridLayoutManager);

    }

    @Override
    protected void onDestroy() {
        ButterKnife.unbind(this);
        super.onDestroy();
    }


}
