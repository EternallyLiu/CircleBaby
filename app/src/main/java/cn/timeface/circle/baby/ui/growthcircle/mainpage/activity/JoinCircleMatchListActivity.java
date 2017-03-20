package cn.timeface.circle.baby.ui.growthcircle.mainpage.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.ui.circle.bean.GrowthCircleObj;
import cn.timeface.circle.baby.ui.growthcircle.mainpage.adapter.JoinCircleMatchAdapter;

/**
 * 加入圈子 -> 匹配到的圈子
 */
public class JoinCircleMatchListActivity extends BaseAppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;

    private JoinCircleMatchAdapter adapter;
    private List<GrowthCircleObj> dataList;

    public static void open(Context context, ArrayList<GrowthCircleObj> dataList) {
        Intent intent = new Intent(context, JoinCircleMatchListActivity.class);
        intent.putParcelableArrayListExtra("data_list", dataList);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_join_match_list);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dataList = getIntent().getParcelableArrayListExtra("data_list");
        setupListData(dataList);
    }

    private void setupListData(List<GrowthCircleObj> dataList) {
        if (adapter == null) {
            adapter = new JoinCircleMatchAdapter(this, dataList);
            adapter.addHeader(getLayoutInflater().inflate(R.layout.item_circle_join_match_list_title, null));
            recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            recyclerView.addItemDecoration(
                    new HorizontalDividerItemDecoration.Builder(this)
                            .color(Color.TRANSPARENT)
                            .sizeResId(R.dimen.view_space_small)
                            .build()
            );
            recyclerView.getItemAnimator().setChangeDuration(0);//fix 列表闪烁
            recyclerView.setAdapter(adapter);
        } else {
            adapter.setListData(dataList);
            adapter.notifyDataSetChanged();
        }
    }

    public void clickCircleCard(View v) {
        if (v.getTag(R.string.tag_obj) != null
                && v.getTag(R.string.tag_obj) instanceof GrowthCircleObj) {
            GrowthCircleObj item = (GrowthCircleObj) v.getTag(R.string.tag_obj);
            CircleInfoActivity.open(this, item);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
