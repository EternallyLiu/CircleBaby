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
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.ui.circle.groupmembers.bean.CircleUserInfo;
import de.hdodenhof.circleimageview.CircleImageView;

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
        } else if (circleUserIdself == circleUserId) {
            title.setText("我的圈资料");
            tvBtnUp.setText("修改宝宝名称");
            tvBtnDown.setVisibility(View.GONE);
            tvBtnUp.setVisibility(View.VISIBLE);
        } else if (circleUserTypeSelf == 1 && circleUserType == 2) {
            title.setText("管理员成员");
            tvBtnUp.setText("移除成员");
            tvBtnDown.setText("取消教师认证");
            tvBtnDown.setVisibility(View.VISIBLE);
            tvBtnUp.setVisibility(View.VISIBLE);
        } else if (circleUserTypeSelf == 1 && circleUserType == 3) {
            title.setText("管理员成员");
            tvBtnUp.setText("移除成员");
            tvBtnDown.setText("教师认证");
            tvBtnDown.setVisibility(View.VISIBLE);
            tvBtnUp.setVisibility(View.VISIBLE);
        } else if (circleUserTypeSelf == 1 && circleUserType == 5) {
            title.setText("入圈申请");
            tvBtnUp.setText("同意加入");
            tvBtnDown.setText("拒绝加入");
            tvBtnDown.setVisibility(View.VISIBLE);
            tvBtnUp.setVisibility(View.VISIBLE);
        }else {
            title.setText("我的圈资料");
            tvBtnDown.setVisibility(View.GONE);
            tvBtnUp.setVisibility(View.GONE);
        }
    }
}
