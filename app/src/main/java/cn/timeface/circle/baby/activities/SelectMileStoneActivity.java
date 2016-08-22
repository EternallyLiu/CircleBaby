package cn.timeface.circle.baby.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.adapters.SelectMilestoneAdapter;
import cn.timeface.circle.baby.api.models.objs.Milestone;
import cn.timeface.circle.baby.utils.ToastUtil;
import cn.timeface.circle.baby.utils.rxutils.SchedulersCompat;

public class SelectMileStoneActivity extends BaseAppCompatActivity implements View.OnClickListener {


    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.appbar_layout)
    AppBarLayout appbarLayout;
    @Bind(R.id.et_relationship)
    EditText etRelationship;
    @Bind(R.id.tv_create)
    TextView tvCreate;
    @Bind(R.id.content_recycler_view)
    RecyclerView contentRecyclerView;
    private SelectMilestoneAdapter adapter;
    private List<Milestone> dataList;

    public static void open(Context context) {
        Intent intent = new Intent(context, SelectMileStoneActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relationship);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        etRelationship.setHint("输入并创建里程碑");
        tvCreate.setOnClickListener(this);
        adapter = new SelectMilestoneAdapter(this, new ArrayList<>());
        adapter.setOnClickListener(this);
        contentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        contentRecyclerView.setAdapter(adapter);
        contentRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).color(Color.TRANSPARENT).sizeResId(R.dimen.view_space_small2).build());

        reqData();

    }

    private void reqData() {
        apiService.queryMilestoneList()
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(milestoneListResponse -> {
                    setDataList(milestoneListResponse.getDataList());
                }, throwable -> {
                    Log.e(TAG, "getRelationshipList:", throwable);
                });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_create:
                String milestoneName = etRelationship.getText().toString().trim();
                if (TextUtils.isEmpty(milestoneName)) {
                    ToastUtil.showToast("请输入里程碑");
                    return;
                }
                if(vd(milestoneName)){
                    ToastUtil.showToast("请输入中文");
                    return;
                }
                apiService.addMilestone(URLEncoder.encode(milestoneName))
                        .compose(SchedulersCompat.applyIoSchedulers())
                        .subscribe(milestoneResponse -> {
                            if (milestoneResponse.success()) {
                                Intent intent = new Intent();
                                intent.putExtra("milestone", milestoneResponse.getMilestone());
                                setResult(RESULT_OK, intent);
                                finish();
                            } else {
                                ToastUtil.showToast(milestoneResponse.getInfo());
                            }
                        }, throwable -> {
                            Log.e(TAG, "addMilestone:", throwable);
                        });
                break;
            case R.id.tv_relationship:
                Milestone milestone = (Milestone) v.getTag(R.string.tag_ex);
                Intent intent = new Intent();
                intent.putExtra("milestone", milestone);
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }

    public void setDataList(List<Milestone> dataList) {
        this.dataList = dataList;
        adapter.getListData().clear();
        adapter.getListData().addAll(dataList);
        adapter.notifyDataSetChanged();
    }

    //判断是否为汉字
    public boolean vd(String str){
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        char[] chars=str.toCharArray();
        boolean isGB2312=false;
        for(int i=0;i<chars.length;i++){
            Matcher m = p.matcher(chars[i]+"");
            if(m.matches()){
                //是汉字
            }else{
                isGB2312 = true;
                break;
            }
        }
        return isGB2312;
    }
}
