package cn.timeface.circle.baby.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.api.models.objs.MilestoneTimeObj;
import cn.timeface.circle.baby.api.models.responses.MilestoneInfoResponse;
import cn.timeface.circle.baby.dialogs.CartPrintPropertyDialog;
import cn.timeface.circle.baby.events.CartBuyNowEvent;
import cn.timeface.circle.baby.events.CartItemClickEvent;
import cn.timeface.circle.baby.utils.DateUtil;
import cn.timeface.circle.baby.utils.FastData;
import cn.timeface.circle.baby.utils.GlideUtil;
import cn.timeface.circle.baby.utils.Remember;
import cn.timeface.circle.baby.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.views.ShareDialog;
import cn.timeface.common.utils.ShareSdkUtil;
import de.hdodenhof.circleimageview.CircleImageView;

public class MileStoneActivity extends BaseAppCompatActivity {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.appbar_layout)
    AppBarLayout appbarLayout;
    @Bind(R.id.tv_name)
    TextView tvName;
    @Bind(R.id.iv_avatar)
    CircleImageView ivAvatar;
    @Bind(R.id.ll_left)
    LinearLayout llLeft;
    @Bind(R.id.dash)
    View dash;
    @Bind(R.id.ll_right)
    LinearLayout llRight;
    @Bind(R.id.sv)
    ScrollView sv;
    @Bind(R.id.rl_layout)
    RelativeLayout rlLayout;

    private int width;

    public static void open(Context context) {
        context.startActivity(new Intent(context, MileStoneActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_milestone);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initView();
        reqData();

    }

    private void initView() {
        width = Remember.getInt("width", 0);
        tvName.setText(FastData.getBabyName());
        GlideUtil.displayImage(FastData.getBabyAvatar(),ivAvatar);
        llLeft.setTranslationY(width/2);
    }


    private void reqData() {
        apiService.milestone()
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(milestoneTimeResponse -> {
                    List<MilestoneTimeObj> dataList = milestoneTimeResponse.getDataList();
                    for (MilestoneTimeObj obj : dataList) {
                        if (llLeft.getChildCount() < llRight.getChildCount()) {
                            llLeft.addView(initLeftView(obj));
                        } else {
                            llRight.addView(initRightView(obj));
                        }
                    }
                }, throwable -> {
                    Log.e(TAG, "milestone:");
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_milestone, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        new ShareDialog(this).share("宝宝时光，让家庭充满和谐，让教育充满温馨。", "宝宝时光，让家庭充满和谐，让教育充满温馨。",
                ShareSdkUtil.getImgStrByResource(this, R.drawable.ic_log),
                ShareSdkUtil.getImgStrByResource(this, R.drawable.ic_log),
                "http://www.timeface.cn/tf_mobile/download.html");
        return super.onOptionsItemSelected(item);
    }

    public View initLeftView(MilestoneTimeObj obj) {
        View view = View.inflate(this, R.layout.view_milestone_left, null);
        TextView tvTime = (TextView) view.findViewById(R.id.tv_time);
        TextView tvMilestonename = (TextView) view.findViewById(R.id.tv_milestonename);
        ImageView ivCover = (ImageView) view.findViewById(R.id.iv_cover);

        tvTime.setText(DateUtil.formatDate("yyyy.MM.dd", obj.getDate()));
        tvMilestonename.setText(obj.getMilestone());
        GlideUtil.displayImage(obj.getImgUrl(), ivCover);

        ViewGroup.LayoutParams layoutParams = ivCover.getLayoutParams();
        layoutParams.height = width;
        layoutParams.width = width;
        ivCover.setLayoutParams(layoutParams);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MileStoneInfoActivity.open(MileStoneActivity.this,obj.getMilestone(),obj.getMilestoneId());
            }
        });
        return view;
    }

    public View initRightView(MilestoneTimeObj obj) {
        View view = View.inflate(this, R.layout.view_milestone_right, null);
        TextView tvTime = (TextView) view.findViewById(R.id.tv_time);
        TextView tvMilestonename = (TextView) view.findViewById(R.id.tv_milestonename);
        ImageView ivCover = (ImageView) view.findViewById(R.id.iv_cover);

        tvTime.setText(DateUtil.formatDate("yyyy.MM.dd", obj.getDate()));
        tvMilestonename.setText(obj.getMilestone());
        GlideUtil.displayImage(obj.getImgUrl(), ivCover);

        ViewGroup.LayoutParams layoutParams = ivCover.getLayoutParams();
        layoutParams.height = width;
        layoutParams.width = width;
        ivCover.setLayoutParams(layoutParams);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MileStoneInfoActivity.open(MileStoneActivity.this,obj.getMilestone(),obj.getMilestoneId());
            }
        });
        return view;
    }

    public View initEmptyView() {
        View view = View.inflate(this, R.layout.view_milestone_empty, null);
        View tv = view.findViewById(R.id.tv);
        ViewGroup.LayoutParams layoutParams = tv.getLayoutParams();
        layoutParams.height = width;
        layoutParams.width = width;
        tv.setLayoutParams(layoutParams);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return view;
    }
}
