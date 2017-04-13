package cn.timeface.circle.baby.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.LinearLayout;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.BuildConfig;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.adapters.MilestoneInfoAdapter;
import cn.timeface.circle.baby.dialogs.MileStoneInfoMoreDialog;
import cn.timeface.circle.baby.dialogs.MileStoneMoreDialog;
import cn.timeface.circle.baby.dialogs.TFDialog;
import cn.timeface.circle.baby.events.MilestoneRefreshEvent;
import cn.timeface.circle.baby.support.api.ApiFactory;
import cn.timeface.circle.baby.support.api.models.objs.Milestone;
import cn.timeface.circle.baby.support.api.models.objs.MilestoneTimeObj;
import cn.timeface.circle.baby.support.api.models.objs.TimeLineObj;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.ShareSdkUtil;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.views.ShareDialog;

public class MileStoneInfoActivity extends BaseAppCompatActivity implements View.OnClickListener {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.appbar_layout)
    AppBarLayout appbarLayout;
    @Bind(R.id.content_recycler_view)
    RecyclerView contentRecyclerView;
    @Bind(R.id.ll_no_data)
    LinearLayout llNoData;
    private int milestoneId;
    private MilestoneInfoAdapter adapter;
    private String title;

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
//        reqData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        reqData();
    }

    private void initView() {
        Intent intent = getIntent();
        title = intent.getStringExtra("title");
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
        if (null == dataList || dataList.size() == 0) {
            llNoData.setVisibility(View.VISIBLE);
        }else{
            llNoData.setVisibility(View.GONE);
        }
        adapter.setListData(dataList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_milestone, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.home) {
            onBackPressed();
        } else if (item.getItemId() == R.id.item_more) {
            new MileStoneInfoMoreDialog(this).show(this);

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_milestone_add:
                Milestone milestone = new Milestone(title, milestoneId);
                PublishActivity.open(this, PublishActivity.VOICE, milestone);
                break;
            case R.id.tv_milestone_share:
                String title = FastData.getBabyNickName() + "成长里程碑";
                String content = FastData.getBabyNickName() + FastData.getBabyAge() + "啦！" + "一起回顾成长中的里程碑";
                String url = BuildConfig.API_URL + getString(R.string.share_url_milestone, FastData.getBabyId());
                new ShareDialog(this).share(title, content, ShareSdkUtil.getImgStrByResource(this, R.drawable.ic_laucher_quadrate), url);
                break;
            case R.id.tv_milestone_delete:
                TFDialog tfDialog = TFDialog.getInstance();
                tfDialog.setTitle("提示");
                tfDialog.setMessage("删除后不可撤销，确认要删除吗？");
                tfDialog.setNegativeButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        tfDialog.dismiss();
                    }
                });
                tfDialog.setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        tfDialog.dismiss();
                        apiService.delMilestone(milestoneId)
                                .compose(SchedulersCompat.applyIoSchedulers())
                                .subscribe(response -> {
                                    if (response.success()) {
                                        EventBus.getDefault().post(new MilestoneRefreshEvent());
                                        finish();
                                    } else {
                                        ToastUtil.showToast(response.getInfo());
                                    }
                                }, error -> {
                                    Log.e("MilestoneMenuDialog", "delMilestone:");
                                    error.printStackTrace();
                                });
                    }
                }).show(getSupportFragmentManager(),"");
                /*new AlertDialog.Builder(this).setMessage("确定删除里程碑 " + this.title + " 吗？")
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        apiService.delMilestone(milestoneId)
                                .compose(SchedulersCompat.applyIoSchedulers())
                                .subscribe(response -> {
                                    if (response.success()) {
                                        EventBus.getDefault().post(new MilestoneRefreshEvent());
                                    } else {
                                        ToastUtil.showToast(response.getInfo());
                                    }
                                }, error -> {
                                    Log.e("MilestoneMenuDialog", "delMilestone:");
                                    error.printStackTrace();
                                });
                    }
                }).show();*/
                break;
        }
    }
}
