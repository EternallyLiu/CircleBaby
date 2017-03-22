package cn.timeface.circle.baby.ui.circle.groupmembers.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.dialogs.TFDialog;
import cn.timeface.circle.baby.events.UpdateMemberDetailEvent;
import cn.timeface.circle.baby.events.UpdateMemberEvent;
import cn.timeface.circle.baby.support.api.models.base.BaseResponse;
import cn.timeface.circle.baby.support.api.models.objs.MediaObj;
import cn.timeface.circle.baby.support.managers.listeners.IEventBus;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.GlideUtil;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.circle.groupmembers.adapter.PhotosShowAdapter;
import cn.timeface.circle.baby.ui.circle.groupmembers.bean.CircleUserInfo;
import cn.timeface.circle.baby.ui.circle.groupmembers.bean.MenemberInfo;
import cn.timeface.circle.baby.ui.circle.groupmembers.responses.MediasResponse;
import cn.timeface.circle.baby.ui.circle.photo.activities.CirclePhotoActivity;
import de.hdodenhof.circleimageview.CircleImageView;
import rx.Subscription;
import rx.functions.Action1;

public class CheckMemberDetailActivity extends BaseAppCompatActivity implements IEventBus {

    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.iv_content_img)
    CircleImageView ivContentImg;
    @Bind(R.id.iv_child_img)
    CircleImageView ivChildImg;
    @Bind(R.id.ll_root)
    RelativeLayout llRoot;
    @Bind(R.id.tv_user_name)
    TextView tvUserName;
    @Bind(R.id.tv_user_connect)
    TextView tvUserConnect;
    @Bind(R.id.tv_btn_up)
    TextView tvBtnUp;
    @Bind(R.id.tv_btn_down)
    TextView tvBtnDown;
    @Bind(R.id.tv_close_circle)
    TextView tvCloseCircle;
    @Bind(R.id.rc_content)
    RecyclerView rcContent;
    @Bind(R.id.iv_empty)
    ImageView ivEmpty;
    @Bind(R.id.rv_close_circle)
    RelativeLayout rvCloseCircle;
    @Bind(R.id.tv_empty)
    TextView tvEmpty;
    @Bind(R.id.tv_want_reason)
    TextView tvWantReason;

    CircleUserInfo circleUserInfo;
    cn.timeface.circle.baby.ui.circle.bean.CircleUserInfo circleUserSelf;
    PhotosShowAdapter adapter;
    ArrayList<String> mPathList;
    MenemberInfo menemberInfo;
    @Bind(R.id.tv_go_see)
    TextView tvGoSee;


    public static void open(Context context, MenemberInfo circleUserInfo) {
        Intent intent = new Intent(context, CheckMemberDetailActivity.class);
        intent.putExtra("circleUserInfo", circleUserInfo);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_member_detail);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mPathList = new ArrayList<>();
        menemberInfo = getIntent().getParcelableExtra("circleUserInfo");
        circleUserInfo = menemberInfo.getUserInfo();
        circleUserSelf = FastData.getCircleUserInfo();
        setPhotoView();

        tvUserName.setText(circleUserInfo.getCircleNickName());
        tvUserConnect.setText("关联宝贝:" + menemberInfo.getBabyBrief().getBabyName());
        setUpView();

    }

    private void setPhotoView() {
        GlideUtil.displayImage(menemberInfo.getBabyBrief().getBabyAvatarUrl(), ivChildImg);
        Glide.with(this)
                .load(menemberInfo.getUserInfo().getCircleAvatarUrl())
                .into(ivContentImg);
    }

    private void setUpView() {

        long circleUserIdself = circleUserSelf.getCircleUserId();
        int circleUserTypeSelf = circleUserSelf.getCircleUserType();

        int circleUserId = circleUserInfo.getCircleUserId();
        int circleUserType = circleUserInfo.getCircleUserType();
        if (circleUserIdself == circleUserId && circleUserTypeSelf == 2) {
            title.setText("我的圈资料");
            tvBtnUp.setText("修改昵称");
            tvBtnDown.setVisibility(View.GONE);
            tvBtnUp.setVisibility(View.VISIBLE);
            rvCloseCircle.setVisibility(View.GONE);
            tvWantReason.setVisibility(View.GONE);
            tvBtnUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ChangNameActivity.open(CheckMemberDetailActivity.this, circleUserInfo, 1);
                }
            });
        } else if (circleUserIdself == circleUserId && circleUserType == 2) {
            title.setText("我的圈资料");
            tvBtnUp.setText("修改昵称");
            tvBtnDown.setVisibility(View.GONE);
            tvBtnUp.setVisibility(View.VISIBLE);
            rvCloseCircle.setVisibility(View.GONE);
            tvWantReason.setVisibility(View.GONE);
            tvBtnUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ChangNameActivity.open(CheckMemberDetailActivity.this, circleUserInfo, 1);
                }
            });
        } else if (circleUserIdself == circleUserId) {
            title.setText("我的圈资料");
            tvBtnUp.setText("修改宝宝名称");
            tvBtnDown.setVisibility(View.GONE);
            tvBtnUp.setVisibility(View.VISIBLE);
            rvCloseCircle.setVisibility(View.GONE);
            tvWantReason.setVisibility(View.GONE);
            tvBtnUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ChangNameActivity.open(CheckMemberDetailActivity.this, circleUserInfo, 2);
                }
            });
        } else if (circleUserTypeSelf == 1 && circleUserType == 2) {
            title.setText("管理圈成员");
            tvBtnUp.setText("移除成员");
            tvBtnDown.setText("取消教师认证");
            tvBtnDown.setVisibility(View.VISIBLE);
            tvBtnUp.setVisibility(View.VISIBLE);
            rvCloseCircle.setVisibility(View.GONE);
            tvWantReason.setVisibility(View.GONE);
            tvBtnUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TFDialog tfDialog = TFDialog.getInstance();
                    tfDialog.setMessage("是否移除" + circleUserInfo.getCircleNickName());
                    tfDialog.setNegativeButton("取消", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            tfDialog.dismiss();
                        }
                    });
                    tfDialog.setPositiveButton("确定", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            removeMember();
                        }
                    });
                    tfDialog.show(getSupportFragmentManager(), "");
                }
            });
            tvBtnDown.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TFDialog tfDialog = TFDialog.getInstance();
                    tfDialog.setMessage("是否取消认证" + circleUserInfo.getCircleNickName() + "的教师资格");
                    tfDialog.setNegativeButton("取消", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            tfDialog.dismiss();
                        }
                    });
                    tfDialog.setPositiveButton("确定", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            start(0);
                            tfDialog.dismiss();
                        }
                    });
                    tfDialog.show(getSupportFragmentManager(), "");
                }
            });
        } else if (circleUserTypeSelf == 1 && circleUserType == 3) {
            title.setText("管理圈成员");
            tvBtnUp.setText("移除成员");
            tvBtnDown.setText("教师认证");
            tvBtnDown.setVisibility(View.VISIBLE);
            tvBtnUp.setVisibility(View.VISIBLE);
            rvCloseCircle.setVisibility(View.GONE);
            tvWantReason.setVisibility(View.GONE);
            tvBtnUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TFDialog tfDialog = TFDialog.getInstance();
                    tfDialog.setMessage("是否移除" + circleUserInfo.getCircleNickName());
                    tfDialog.setNegativeButton("取消", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            tfDialog.dismiss();
                        }
                    });
                    tfDialog.setPositiveButton("确定", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            removeMember();
                            tfDialog.dismiss();
                        }
                    });
                    tfDialog.show(getSupportFragmentManager(), "");
                }
            });
            tvBtnDown.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TFDialog tfDialog = TFDialog.getInstance();
                    tfDialog.setMessage("是否发起认证" + circleUserInfo.getCircleNickName() + "的教师资格");
                    tfDialog.setNegativeButton("取消", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            tfDialog.dismiss();
                        }
                    });
                    tfDialog.setPositiveButton("确定", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            start(1);
                            tfDialog.dismiss();
                        }
                    });
                    tfDialog.show(getSupportFragmentManager(), "");
                }
            });

        } else if (circleUserTypeSelf == 1 && circleUserType == 5) {
            title.setText("入圈申请");
            tvBtnUp.setText("同意加入");

            tvBtnDown.setText("拒绝加入");
            tvBtnDown.setVisibility(View.VISIBLE);
            tvBtnUp.setVisibility(View.VISIBLE);
            rvCloseCircle.setVisibility(View.GONE);
            tvWantReason.setVisibility(View.VISIBLE);
            tvWantReason.setText("申请留言:" + menemberInfo.getLeaveMessage());
            tvBtnUp.setOnClickListener(v -> joinCheck(1));
            tvBtnDown.setOnClickListener(v -> joinCheck(0));

        } else {
            title.setText("我的圈资料");
            tvBtnDown.setVisibility(View.GONE);
            tvBtnUp.setVisibility(View.GONE);
            rvCloseCircle.setVisibility(View.VISIBLE);
            tvWantReason.setVisibility(View.GONE);
            tvGoSee.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CirclePhotoActivity.open(CheckMemberDetailActivity.this, circleUserInfo, true);
                }
            });
            reqPersonalPhotos();
        }
    }

    private void reqPersonalPhotos() {
        Subscription subscribe = apiService.getNewestPic(circleUserInfo.getCircleId(), circleUserInfo.getCircleUserId())
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(new Action1<MediasResponse>() {
                    @Override
                    public void call(MediasResponse mediasResponse) {
                        if (mediasResponse.success()) {
                            List<MediaObj> dataList = mediasResponse.getDataList();
                            if (dataList.size() != 0) {
                                rvCloseCircle.setVisibility(View.VISIBLE);
                                rcContent.setVisibility(View.VISIBLE);
                                setPersonalView();
                                mPathList.clear();
                                for (int i = 0; i < dataList.size(); i++) {
                                    mPathList.add(dataList.get(i).getImgUrl());
                                }
                                adapter.addAllDate(mPathList);
                            } else {
                                rvCloseCircle.setVisibility(View.VISIBLE);
                                ivEmpty.setVisibility(View.VISIBLE);
                                tvEmpty.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });
        addSubscription(subscribe);
    }

    private void setPersonalView() {
        rcContent.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        adapter = new PhotosShowAdapter(this, mPathList);
        rcContent.setNestedScrollingEnabled(false);
        rcContent.setAdapter(adapter);
    }

    private void joinCheck(int type) {
        Subscription subscribe = apiService.joinCircleCheck(type, circleUserInfo.getCircleId(), circleUserInfo.getCircleUserId())
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(new Action1<BaseResponse>() {
                    @Override
                    public void call(BaseResponse baseResponse) {
                        if (baseResponse.success()) {
                            if (type == 1) {
                                ToastUtil.showToast(CheckMemberDetailActivity.this, "操作成功");
                                EventBus.getDefault().post(new UpdateMemberEvent());
                                finish();
                            } else {
                                ToastUtil.showToast(CheckMemberDetailActivity.this, "操作成功");
                                EventBus.getDefault().post(new UpdateMemberEvent());
                                finish();
                            }
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });
        addSubscription(subscribe);
    }

    private void start(int type) {
        Subscription subscribe = apiService.start(type, circleUserInfo.getCircleId(), circleUserInfo.getCircleUserId())
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(new Action1<BaseResponse>() {
                    @Override
                    public void call(BaseResponse baseResponse) {
                        if (baseResponse.success()) {
                            if (type == 1) {
                                ToastUtil.showToast(CheckMemberDetailActivity.this, "认证成功,等待审核");
                                EventBus.getDefault().post(new UpdateMemberEvent());
                                finish();
                            } else {
                                ToastUtil.showToast(CheckMemberDetailActivity.this, "取消认证成功,等待审核");
                                EventBus.getDefault().post(new UpdateMemberEvent());
                                finish();
                            }
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });
        addSubscription(subscribe);
    }

    private void removeMember() {
        Subscription subscribe = apiService.removeMember(circleUserInfo.getCircleId(), circleUserInfo.getCircleUserId())
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(new Action1<BaseResponse>() {
                    @Override
                    public void call(BaseResponse baseResponse) {
                        if (baseResponse.success()) {
                            ToastUtil.showToast(CheckMemberDetailActivity.this, "操作成功");
                            EventBus.getDefault().post(new UpdateMemberEvent());
                            finish();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });
        addSubscription(subscribe);
    }

    @Subscribe
    public void onEvent(UpdateMemberDetailEvent event) {
        MenemberInfo menemberInfo = event.getMenemberInfo();
        tvUserName.setText(menemberInfo.getUserInfo().getCircleNickName());
        tvUserConnect.setText("关联宝贝:" + menemberInfo.getBabyBrief().getBabyName());
    }
}
