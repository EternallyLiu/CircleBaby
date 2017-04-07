package cn.timeface.circle.baby.ui.growthcircle.mainpage.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.rayboot.widget.ratioview.RatioImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.constants.TypeConstants;
import cn.timeface.circle.baby.dialogs.TFDialog;
import cn.timeface.circle.baby.support.managers.listeners.IEventBus;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.GlideUtil;
import cn.timeface.circle.baby.support.utils.network.NetworkError;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.circle.bean.GrowthCircleDetailObj;
import cn.timeface.circle.baby.ui.circle.bean.GrowthCircleObj;
import cn.timeface.circle.baby.ui.circle.bean.MemberDataObj;
import cn.timeface.circle.baby.ui.circle.groupmembers.activity.GroupMembersActivity;
import cn.timeface.circle.baby.ui.growthcircle.mainpage.adapter.CircleInfoMemberGridAdapter;
import cn.timeface.circle.baby.ui.growthcircle.mainpage.dialog.JoinCircleDialog;
import cn.timeface.circle.baby.ui.growthcircle.mainpage.event.CircleChangedEvent;
import cn.timeface.circle.baby.ui.growthcircle.mainpage.event.JoinCircleCommitEvent;
import cn.timeface.circle.baby.views.NoScrollGridView;
import cn.timeface.circle.baby.views.dialog.TFProgressDialog;
import rx.Subscription;

/**
 * 圈资料
 */
public class CircleInfoActivity extends BaseAppCompatActivity implements IEventBus {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.iv_circle_cover)
    RatioImageView ivCircleCover;
    @Bind(R.id.tv_circle_intro)
    TextView tvCircleIntro;
    @Bind(R.id.tv_open_type)
    TextView tvOpenType;
    @Bind(R.id.tv_circle_number)
    TextView tvCircleNumber;
    @Bind(R.id.tv_circle_member_count)
    TextView tvCircleMemberCount;
    @Bind(R.id.member_list)
    NoScrollGridView memberList;
    @Bind(R.id.tv_circle_photo_count)
    TextView tvCirclePhotoCount;
    @Bind(R.id.tv_circle_photo_percent)
    TextView tvCirclePhotoPercent;
    @Bind(R.id.tv_circle_product_count)
    TextView tvCircleProductCount;
    @Bind(R.id.tv_circle_product_percent)
    TextView tvCircleProductPercent;
    @Bind(R.id.tv_circle_rule)
    TextView tvCircleRule;
    @Bind(R.id.tv_submit)
    TextView tvSubmit;

    private GrowthCircleObj circleObj;
    private GrowthCircleDetailObj circleDetailObj;
    private boolean isJoined;

    private TFProgressDialog progressDialog;
    private JoinCircleDialog joinDialog;
    private List<MemberDataObj> memberInfoList;

    public static void open(Context context, GrowthCircleObj circleObj) {
        Intent intent = new Intent(context, CircleInfoActivity.class);
        intent.putExtra("circle_obj", circleObj);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_info);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        circleObj = getIntent().getParcelableExtra("circle_obj");
        memberInfoList = new ArrayList<>();

        if (circleObj != null) {
            setupData(circleObj);

            showProgressDialog();
            reqData(circleObj.getCircleId());
        } else {
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (circleObj != null && circleObj.isCreator()) {
            getMenuInflater().inflate(R.menu.menu_circle_edit_circle_info, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                if (circleDetailObj != null) {
                    CircleInfoEditActivity.open(this, circleDetailObj);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void reqData(long circleId) {
        Subscription s = apiService.circleDetail(circleId)
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(
                        response -> {
                            dismissProgressDialog();
                            if (response.success()) {
                                isJoined = response.isJoined();
                                circleDetailObj = response.getCircleDetailInfo();
                                setupDetailData(circleDetailObj);
                            } else {
                                Toast.makeText(this, response.info, Toast.LENGTH_SHORT).show();
                            }
                        },
                        throwable -> {
                            dismissProgressDialog();
                            NetworkError.showException(this, throwable);
                        }
                );
        addSubscription(s);
    }

    private void setupData(GrowthCircleObj circleObj) {
        getSupportActionBar().setTitle(circleObj.getCircleName());
        tvOpenType.setText(circleObj.isPublic() ? "公开" : "不公开");
        tvCircleMemberCount.setText(circleObj.getMemberCount()
                + "/" + TypeConstants.GROWTH_CIRCLE_MEMBER_MAX_COUNT);
        tvCircleNumber.setText(circleObj.getCircleNumber());
        tvCirclePhotoCount.setText(circleObj.getMediaCount() + "张");
        tvCircleProductCount.setText(circleObj.getWorkCount() + "本");

        GlideUtil.displayImage(circleObj.getCircleCoverUrl(), ivCircleCover);
    }

    private void setupDetailData(GrowthCircleDetailObj circleDetailObj) {
        setupData(circleDetailObj);

        tvCircleIntro.setText(TextUtils.isEmpty(circleDetailObj.getCircleDescription()) ?
                "暂无简介" : circleDetailObj.getCircleDescription());

        tvCirclePhotoPercent.setText("超过" + circleDetailObj.getMediaAchievement() + "%的圈子");
        tvCircleProductPercent.setText("超过" + circleDetailObj.getWrokAchievement() + "%的圈子");

        tvCircleRule.setText(TextUtils.isEmpty(circleDetailObj.getRule()) ?
                "暂无圈规则" : circleDetailObj.getRule());

        if (circleDetailObj.getMemberList() != null
                && circleDetailObj.getMemberList().size() > 0) {
            memberInfoList.addAll(circleDetailObj.getMemberList());
            memberList.setAdapter(new CircleInfoMemberGridAdapter(this, memberInfoList));
        }

        tvSubmit.setVisibility(View.VISIBLE);
        if (circleObj.isCreator()) {
            tvSubmit.setText(R.string.disband_growth_circle);
        } else {
            tvSubmit.setText(isJoined ? R.string.quit_growth_circle : R.string.join_growth_circle);
        }
        tvSubmit.setTextColor(ContextCompat.getColor(this, isJoined ?
                R.color.text_color16 : android.R.color.white));
        tvSubmit.setBackgroundResource(isJoined ? R.drawable.selector_ios_btn_empty_no_margin
                : R.drawable.selector_btn_login);
    }

    public void clickCircleMember(View view) {
        // 非圈成员不能查看圈成员管理页面
        if (isJoined) {
            // 进入成员管理页面
            GroupMembersActivity.open(this, circleObj);
        }
    }

    @OnClick(R.id.tv_submit)
    public void onClick() {
        if (isJoined) {
            if (circleObj.isCreator()) {
                // 解散圈子
                TFDialog disbandDialog = TFDialog.getInstance();
                disbandDialog.setTitle("是否解散“" + circleObj.getCircleName() + "”");
                disbandDialog.setMessage("警告：解散后，圈内容将被清空");
                disbandDialog.setNegativeButton("取消", v -> disbandDialog.dismiss());
                disbandDialog.setPositiveButton("确定", v -> {
                    disbandDialog.dismiss();
                    showProgressDialog();
                    reqDisbandCircle(circleObj.getCircleId());
                });
                disbandDialog.show(getSupportFragmentManager(), "DisbandDialog");
            } else {
                // 退出圈子
                TFDialog quitDialog = TFDialog.getInstance();
                quitDialog.setTitle("是否退出“" + circleObj.getCircleName() + "”");
                quitDialog.setMessage("警告：退出后，您发布的信息将被清空");
                quitDialog.setNegativeButton("取消", v -> quitDialog.dismiss());
                quitDialog.setPositiveButton("确定", v -> {
                    quitDialog.dismiss();
                    showProgressDialog();
                    reqQuitCircle(circleObj.getCircleId());
                });
                quitDialog.show(getSupportFragmentManager(), "QuitDialog");
            }
        } else {
            if (joinDialog == null) {
                joinDialog = JoinCircleDialog.getInstance();
            }

            if (!TextUtils.isEmpty(FastData.getBabyRealName())) {
                joinDialog.setJoinMessage("我是" + FastData.getBabyRealName()
                        + "的" + FastData.getRelationName());
                joinDialog.setChildrenName(FastData.getBabyRealName());
            }
            joinDialog.setPositiveListener(v -> {
                if (TextUtils.isEmpty(joinDialog.getJoinMessage())) {
                    Toast.makeText(this, "请输入加圈留言", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(joinDialog.getChildrenName())) {
                    Toast.makeText(this, "请输入孩子的真实姓名", Toast.LENGTH_SHORT).show();
                    return;
                }

                joinDialog.dismiss();
                showProgressDialog();
                reqJoinCircle(circleObj.getCircleId(),
                        joinDialog.getJoinMessage(), joinDialog.getChildrenName());
            });
            joinDialog.show(getSupportFragmentManager(), "JoinCircleDialog");
        }
    }

    private void reqJoinCircle(long circleId, String leaveWords, String babyRealName) {
        Subscription s = apiService.joinCircle(circleId, babyRealName, leaveWords)
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(
                        response -> {
                            dismissProgressDialog();
                            Toast.makeText(this, response.info, Toast.LENGTH_SHORT).show();
                            if (response.success()) {
                                // 更新宝宝真实姓名
                                if (TextUtils.isEmpty(FastData.getBabyRealName())) {
                                    FastData.setBabyRealName(babyRealName);
                                }
                                EventBus.getDefault().post(new JoinCircleCommitEvent());
                                finish();
                            }
                        },
                        throwable -> {
                            dismissProgressDialog();
                            NetworkError.showException(this, throwable);
                        }
                );
        addSubscription(s);
    }

    private void reqQuitCircle(long circleId) {
        Subscription s = apiService.quitCircle(circleId)
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(
                        response -> {
                            dismissProgressDialog();
                            Toast.makeText(this, response.info, Toast.LENGTH_SHORT).show();
                            if (response.success()) {
                                EventBus.getDefault().post(
                                        new CircleChangedEvent(circleId,
                                                CircleChangedEvent.TYPE_QUIT)
                                );
                                finish();
                            }
                        },
                        throwable -> {
                            dismissProgressDialog();
                            NetworkError.showException(this, throwable);
                        }
                );
        addSubscription(s);
    }

    private void reqDisbandCircle(long circleId) {
        Subscription s = apiService.disbandCircle(circleId)
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(
                        response -> {
                            dismissProgressDialog();
                            Toast.makeText(this, response.info, Toast.LENGTH_SHORT).show();
                            if (response.success() && response.getErrorCode() == 0) {
                                EventBus.getDefault().post(
                                        new CircleChangedEvent(circleId,
                                                CircleChangedEvent.TYPE_DISBANDED)
                                );
                                finish();
                            }
                        },
                        throwable -> {
                            dismissProgressDialog();
                            NetworkError.showException(this, throwable);
                        }
                );
        addSubscription(s);
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = TFProgressDialog.getInstance();
        }
        progressDialog.show(getSupportFragmentManager(), "ProgressDialog");
    }

    private void dismissProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(CircleChangedEvent event) {
        if (event.type == CircleChangedEvent.TYPE_INFO_CHANGED) {
            showProgressDialog();
            reqData(circleObj.getCircleId());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
