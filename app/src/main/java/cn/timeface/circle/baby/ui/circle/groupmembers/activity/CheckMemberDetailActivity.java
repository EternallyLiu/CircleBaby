package cn.timeface.circle.baby.ui.circle.groupmembers.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.dialogs.TFDialog;
import cn.timeface.circle.baby.support.api.models.base.BaseResponse;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.circle.groupmembers.bean.CircleUserInfo;
import de.hdodenhof.circleimageview.CircleImageView;
import rx.Subscription;
import rx.functions.Action1;

public class CheckMemberDetailActivity extends BaseAppCompatActivity {

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

    CircleUserInfo circleUserInfo;
    cn.timeface.circle.baby.ui.circle.bean.CircleUserInfo circleUserSelf;

    public static void open(Context context, CircleUserInfo circleUserInfo) {
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

        circleUserInfo = getIntent().getParcelableExtra("circleUserInfo");
        circleUserSelf = FastData.getCircleUserInfo();
        setUpView();


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
            tvBtnUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ChangNameActivity.open(CheckMemberDetailActivity.this, circleUserInfo,1);
                }
            });
        } else if (circleUserIdself == circleUserId) {
            title.setText("我的圈资料");
            tvBtnUp.setText("修改宝宝名称");
            tvBtnDown.setVisibility(View.GONE);
            tvBtnUp.setVisibility(View.VISIBLE);
            tvBtnUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ChangNameActivity.open(CheckMemberDetailActivity.this, circleUserInfo,2);
                }
            });
        } else if (circleUserTypeSelf == 1 && circleUserType == 2) {
            title.setText("管理员成员");
            tvBtnUp.setText("移除成员");
            tvBtnDown.setText("取消教师认证");
            tvBtnDown.setVisibility(View.VISIBLE);
            tvBtnUp.setVisibility(View.VISIBLE);
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
                    tfDialog.show(getSupportFragmentManager(),"");
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
                        }
                    });
                    tfDialog.show(getSupportFragmentManager(),"");
                }
            });
        } else if (circleUserTypeSelf == 1 && circleUserType == 3) {
            title.setText("管理员成员");
            tvBtnUp.setText("移除成员");
            tvBtnDown.setText("教师认证");
            tvBtnDown.setVisibility(View.VISIBLE);
            tvBtnUp.setVisibility(View.VISIBLE);
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
                    tfDialog.show(getSupportFragmentManager(),"");
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
                        }
                    });
                    tfDialog.show(getSupportFragmentManager(),"");
                }
            });

        } else if (circleUserTypeSelf == 1 && circleUserType == 5) {
            title.setText("入圈申请");
            tvBtnUp.setText("同意加入");

            tvBtnDown.setText("拒绝加入");
            tvBtnDown.setVisibility(View.VISIBLE);
            tvBtnUp.setVisibility(View.VISIBLE);
            tvBtnUp.setOnClickListener(v -> joinCheck(1));
            tvBtnDown.setOnClickListener(v -> joinCheck(0));

        }else {
            title.setText("我的圈资料");
            tvBtnDown.setVisibility(View.GONE);
            tvBtnUp.setVisibility(View.GONE);
        }
    }

    private void joinCheck(int type){
        Subscription subscribe = apiService.joinCircleCheck(type, circleUserInfo.getCircleId(), circleUserInfo.getCircleUserId())
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(new Action1<BaseResponse>() {
                    @Override
                    public void call(BaseResponse baseResponse) {
                        if (baseResponse.success()) {
                            ToastUtil.showToast(CheckMemberDetailActivity.this,"操作成功");
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });
        addSubscription(subscribe);
    }

    private void start(int type){
        Subscription subscribe = apiService.start(type, circleUserInfo.getCircleId(), circleUserInfo.getCircleUserId())
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(new Action1<BaseResponse>() {
                    @Override
                    public void call(BaseResponse baseResponse) {
                        if (baseResponse.success()) {
                            ToastUtil.showToast(CheckMemberDetailActivity.this,"操作成功");
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });
        addSubscription(subscribe);
    }

    private void removeMember(){
        Subscription subscribe = apiService.removeMember(circleUserInfo.getCircleId(), circleUserInfo.getCircleUserId())
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(new Action1<BaseResponse>() {
                    @Override
                    public void call(BaseResponse baseResponse) {
                        if (baseResponse.success()) {
                            ToastUtil.showToast(CheckMemberDetailActivity.this,"操作成功");
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });
        addSubscription(subscribe);
    }

}
