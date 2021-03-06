package cn.timeface.circle.baby.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
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

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.sharesdk.framework.ShareSDK;
import cn.timeface.circle.baby.BuildConfig;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.dialogs.MileStoneMoreDialog;
import cn.timeface.circle.baby.dialogs.MilestoneMenuDialog;
import cn.timeface.circle.baby.events.MilestoneRefreshEvent;
import cn.timeface.circle.baby.support.api.models.objs.MediaObj;
import cn.timeface.circle.baby.support.managers.listeners.IEventBus;
import cn.timeface.circle.baby.support.api.models.objs.MilestoneTimeObj;
import cn.timeface.circle.baby.support.api.models.responses.MilestoneTimeResponse;
import cn.timeface.circle.baby.support.mvp.model.BookModel;
import cn.timeface.circle.baby.support.utils.DateUtil;
import cn.timeface.circle.baby.support.utils.DeviceUtil;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.GlideUtil;
import cn.timeface.circle.baby.support.utils.Remember;
import cn.timeface.circle.baby.support.utils.ShareSdkUtil;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.growthcircle.mainpage.dialog.CircleMoreDialog;
import cn.timeface.circle.baby.views.ShareDialog;
import cn.timeface.circle.baby.views.TFStateView;
import cn.timeface.open.api.bean.obj.TFOContentObj;
import cn.timeface.open.api.bean.obj.TFOPublishObj;
import cn.timeface.open.api.bean.obj.TFOResourceObj;
import de.hdodenhof.circleimageview.CircleImageView;

public class MileStoneActivity extends BaseAppCompatActivity implements IEventBus, View.OnClickListener {
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
    @Bind(R.id.tf_stateView)
    TFStateView tfStateView;

    private int width;
    private int measuredHeight;
    private MilestoneTimeResponse milestoneTimeResponse;
    List<MediaObj> mediaObjs = new ArrayList<>();
    private String bookName;

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
        tfStateView.setOnRetryListener(() -> reqData());
        tfStateView.loading();
        initView();
//        reqData();
        ShareSDK.initSDK(this);
    }


    private void initView() {
        width = Remember.getInt("width", 0);
        tvName.setText(FastData.getBabyNickName());
        GlideUtil.displayImage(FastData.getBabyAvatar(), ivAvatar);
        llLeft.setTranslationY((float) (width * 0.7));
    }


    private void reqData() {
        apiService.milestone()
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(milestoneTimeResponse -> {
                    if (tfStateView != null) {
                        tfStateView.finish();
                    }
                    this.milestoneTimeResponse = milestoneTimeResponse;
                    List<MilestoneTimeObj> dataList = milestoneTimeResponse.getDataList();
                    llLeft.removeAllViews();
                    llRight.removeAllViews();
                    for (MilestoneTimeObj obj : dataList) {
                        if (llLeft.getChildCount() < llRight.getChildCount()) {
                            llLeft.addView(initLeftView(obj));
                        } else {
                            llRight.addView(initRightView(obj));
                        }
                    }
                    if (llLeft.getChildCount() < llRight.getChildCount()) {
                        llLeft.addView(initEmptyViewLeft());
                    } else {
                        llRight.addView(initEmptyViewRight());
                    }

                }, throwable -> {
                    if (tfStateView != null) {
                        tfStateView.finish();
                    }
                    Log.e(TAG, "milestone:", throwable);
                    throwable.printStackTrace();
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        reqData();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                measuredHeight = rlLayout.getMeasuredHeight();
                ViewGroup.LayoutParams layoutParams = dash.getLayoutParams();
                layoutParams.height = measuredHeight;
                layoutParams.width = DeviceUtil.dpToPx(getResources(), 1);
                dash.setLayoutParams(layoutParams);
            }
        }, 1000);
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
            new MileStoneMoreDialog(this).show(this);

        }
        return super.onOptionsItemSelected(item);
    }

    public View initLeftView(MilestoneTimeObj obj) {
        View view = View.inflate(this, R.layout.view_milestone_left, null);
        TextView tvTime = (TextView) view.findViewById(R.id.tv_time);
        TextView tvMilestonename = (TextView) view.findViewById(R.id.tv_milestonename);
        ImageView ivCover = (ImageView) view.findViewById(R.id.iv_cover);

        tvTime.setText(DateUtil.formatDate("yyyy.MM.dd", obj.getDate()));
        tvMilestonename.setText(obj.getMilestone());
        GlideUtil.displayImage(obj.getImgUrl(), ivCover, R.drawable.milestone_nodata);

        ViewGroup.LayoutParams layoutParams = ivCover.getLayoutParams();
        layoutParams.height = width;
        layoutParams.width = width;
        ivCover.setLayoutParams(layoutParams);

        if (obj.getIsRead() == 1) {
            tvMilestonename.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        }
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MileStoneInfoActivity.open(MileStoneActivity.this, obj.getMilestone(), obj.getMilestoneId());
                if (obj.getIsRead() == 0) {
                    milestoneRead(obj.getMilestoneId());
                }
            }
        });
        /*if (obj.getTimelineCount() == 0) {
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    new MilestoneMenuDialog(MileStoneActivity.this).share(obj);
                    return true;
                }
            });
        }*/
        return view;
    }

    public View initRightView(MilestoneTimeObj obj) {
        View view = View.inflate(this, R.layout.view_milestone_right, null);
        TextView tvTime = (TextView) view.findViewById(R.id.tv_time);
        TextView tvMilestonename = (TextView) view.findViewById(R.id.tv_milestonename);
        ImageView ivCover = (ImageView) view.findViewById(R.id.iv_cover);

        tvTime.setText(DateUtil.formatDate("yyyy.MM.dd", obj.getDate()));
        tvMilestonename.setText(obj.getMilestone());
        GlideUtil.displayImage(obj.getImgUrl(), ivCover, R.drawable.milestone_nodata);

        ViewGroup.LayoutParams layoutParams = ivCover.getLayoutParams();
        layoutParams.height = width;
        layoutParams.width = width;
        ivCover.setLayoutParams(layoutParams);

        if (obj.getIsRead() == 1) {
            tvMilestonename.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        }
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MileStoneInfoActivity.open(MileStoneActivity.this, obj.getMilestone(), obj.getMilestoneId());
                if (obj.getIsRead() == 0) {
                    milestoneRead(obj.getMilestoneId());
                }
            }
        });
        /*if (obj.getTimelineCount() == 0) {
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    new MilestoneMenuDialog(MileStoneActivity.this).share(obj);
                    return true;
                }
            });
        }*/
        return view;
    }

    public View initEmptyViewRight() {
        View view = View.inflate(this, R.layout.view_milestone_diy_right, null);
        View tv = view.findViewById(R.id.tv_diy);
        ViewGroup.LayoutParams layoutParams = tv.getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = width;
        tv.setLayoutParams(layoutParams);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MileStoneActivity.this, MilestoneDiyActivity.class);
                startActivityForResult(intent, 1);
            }
        });
        return view;
    }

    public View initEmptyViewLeft() {
        View view = View.inflate(this, R.layout.view_milestone_diy_left, null);
        View tv = view.findViewById(R.id.tv_diy);
        ViewGroup.LayoutParams layoutParams = tv.getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = width;
        tv.setLayoutParams(layoutParams);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MileStoneActivity.this, MilestoneDiyActivity.class);
                startActivityForResult(intent, 1);
            }
        });
        return view;
    }

    public void milestoneRead(int milestoneId) {
        apiService.milestoneRead(milestoneId)
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(response -> {
                    if (response.success()) {
                        reqData();
                    }
                }, throwable -> {
                    Log.e(TAG, "milestoneRead:");
                    throwable.printStackTrace();
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && requestCode == 1) {
            reqData();
        }
    }

    @Subscribe
    public void onEvent(MilestoneRefreshEvent event) {
        reqData();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_milestone_create:
                Intent intent = new Intent(MileStoneActivity.this, MilestoneDiyActivity.class);
                startActivityForResult(intent, 1);
                break;
            case R.id.tv_milestone_share:
                String imgUrl = "";
                for (MilestoneTimeObj obj : milestoneTimeResponse.getDataList()) {
                    imgUrl = obj.getImgUrl();
                    if (!TextUtils.isEmpty(imgUrl)) {
                        break;
                    }
                }
                if (TextUtils.isEmpty(imgUrl)) {
                    imgUrl = FastData.getBabyAvatar();
                }
                String title = FastData.getBabyNickName() + "成长里程碑";
                String content = FastData.getBabyNickName() + FastData.getBabyAge() + "啦！" + "一起回顾成长中的里程碑";
                String url = BuildConfig.API_URL + getString(R.string.share_url_milestone, FastData.getBabyId());
                new ShareDialog(this).share(title, content, ShareSdkUtil.getImgStrByResource(this, R.drawable.ic_laucher_quadrate), url);
                break;
            case R.id.tv_milestone_book:

                if(mediaObjs.size() == 0){
                    ToastUtil.showToast("里程碑里还没有照片");
                    return;
                }

                List<TFOResourceObj> tfoResourceObjs = new ArrayList<>();
                StringBuffer sb = new StringBuffer("{\"dataList\":[");
                int index = 0;
                for (MediaObj mediaObj : mediaObjs) {
                    index++;
                    TFOResourceObj tfoResourceObj = mediaObj.toTFOResourceObj();
                    tfoResourceObjs.add(tfoResourceObj);
                    sb.append(mediaObj.getId());
                    if (index < mediaObjs.size()) {
                        sb.append(",");
                    } else {
                        sb.append("]}");
                    }
                }

                List<TFOContentObj> tfoContentObjs1 = new ArrayList<>();
                TFOContentObj tfoContentObj;
                tfoContentObj = new TFOContentObj("", tfoResourceObjs);
                tfoContentObjs1.add(tfoContentObj);

                ArrayList<String> keys = new ArrayList<>();
                ArrayList<String> values = new ArrayList<>();
                keys.add("book_author");
                keys.add("book_title");
                bookName = FastData.getBabyNickName() + "的成长里程碑";
                values.add(bookName);
                values.add(FastData.getUserName());

                TFOPublishObj tfoPublishObj = new TFOPublishObj(bookName, tfoContentObjs1);
                List<TFOPublishObj> tfoPublishObjs = new ArrayList<>();
                tfoPublishObjs.add(tfoPublishObj);

                MyPODActivity.open(this, "", "", BookModel.BOOK_TYPE_HARDCOVER_PHOTO_BOOK, 44, tfoPublishObjs, sb.toString(), true, FastData.getBabyId(), keys, values, 1);
                finish();

                break;
        }
    }
}
