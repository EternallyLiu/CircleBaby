package cn.timeface.circle.baby.ui.growthcircle.mainpage.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.rayboot.widget.ratioview.RatioImageView;

import org.greenrobot.eventbus.EventBus;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.constants.TypeConstants;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.network.NetworkError;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.circle.bean.CircleUserInfo;
import cn.timeface.circle.baby.ui.circle.bean.GrowthCircleDetailObj;
import cn.timeface.circle.baby.ui.circle.bean.GrowthCircleObj;
import cn.timeface.circle.baby.ui.growthcircle.mainpage.adapter.CircleInfoMemberGridAdapter;
import cn.timeface.circle.baby.ui.growthcircle.mainpage.dialog.JoinCircleDialog;
import cn.timeface.circle.baby.ui.growthcircle.mainpage.event.CircleJoinedEvent;
import cn.timeface.circle.baby.views.NoScrollGridView;
import cn.timeface.circle.baby.views.dialog.TFProgressDialog;
import rx.Subscription;

/**
 * 圈资料
 */
public class CircleInfoActivity extends BaseAppCompatActivity {

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
    private boolean isJoined;

    private TFProgressDialog progressDialog;
    private JoinCircleDialog joinDialog;

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

        if (circleObj != null) {
            setupData(circleObj);

            showProgressDialog();
            reqData(circleObj.getCircleId());
        } else {
            finish();
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
                                setupDetailData(response.getCircleDetailInfo());
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
    }

    private void setupDetailData(GrowthCircleDetailObj circleDetailObj) {
        tvCircleIntro.setText(circleDetailObj.getCircleDescription());

        tvCirclePhotoPercent.setText("超过" + circleDetailObj.getMediaAchievement() + "%的圈子");
        tvCircleProductPercent.setText("超过" + circleDetailObj.getWrokAchievement() + "%的圈子");

        tvCircleRule.setText(circleDetailObj.getRule());

        if (circleDetailObj.getMemberList() != null
                && circleDetailObj.getMemberList().size() > 0) {
            memberList.setAdapter(new CircleInfoMemberGridAdapter(this, circleDetailObj.getMemberList()));
        }

        tvSubmit.setVisibility(View.VISIBLE);
        tvSubmit.setText(isJoined ? R.string.quit_growth_circle : R.string.join_growth_circle);
        tvSubmit.setTextColor(ContextCompat.getColor(this, isJoined ?
                R.color.text_color16 : android.R.color.white));
        tvSubmit.setBackgroundResource(isJoined ? R.drawable.selector_ios_btn_empty
                : R.drawable.selector_btn_login);
    }

    public void clickCircleMember(View view) {
        if (view.getTag(R.string.tag_obj) != null) {
            if (view.getTag(R.string.tag_obj) instanceof CircleUserInfo) {
                // 进入成员页面
                CircleUserInfo item = (CircleUserInfo) view.getTag(R.string.tag_obj);

            }
        } else {
            // 进入成员管理页面

        }
    }

    @OnClick(R.id.tv_submit)
    public void onClick() {
        if (isJoined) {

        } else {
            if (joinDialog == null) {
                joinDialog = JoinCircleDialog.getInstance();
            }
            joinDialog.setJoinMessage("我是" + FastData.getBabyNickName()
                    + "的" + FastData.getRelationName());
            joinDialog.setChildrenName(FastData.getBabyRealName());
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
                reqJoinCircle(joinDialog.getJoinMessage(), joinDialog.getChildrenName());
            });
            joinDialog.show(getSupportFragmentManager(), "JoinCircleDialog");
        }
    }

    private void reqJoinCircle(String leaveWords, String babyRealName) {
        Subscription s = apiService.joinCircle(circleObj.getCircleId(), babyRealName, leaveWords)
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(
                        response -> {
                            dismissProgressDialog();
                            if (response.success()) {
                                EventBus.getDefault().post(new CircleJoinedEvent());
                                showProgressDialog();
                                reqData(circleObj.getCircleId());
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
