package cn.timeface.circle.baby.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.BuildConfig;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.utils.Remember;

/**
 * @author shiyan
 * @from 2014-3-29上午12:38:01
 * @TODO 关于成长印记界面
 */
public class AboutActivity extends BaseAppCompatActivity {
    @Bind(R.id.tvVersion)
    TextView mTvVersion;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.image)
    ImageView image;

    public static void open(Context context) {
        context.startActivity(new Intent(context, AboutActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("关于成长印记");
        mTvVersion.setText(BuildConfig.VERSION_NAME);
        int width = Remember.getInt("width",0);
        ViewGroup.LayoutParams layoutParams = image.getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = width;
        image.setLayoutParams(layoutParams);
    }

    /**
     * 点击跳转至->帮助
     */
    public void clickHelp(View v) {
        FragmentBridgeActivity.openWebViewFragment(this, "http://m.timeface.cn/app/APP-Help/html/help.html", "帮助");
    }

    /**
     * 点击跳转至->反馈
     */
    public void clickFeedback(View v) {
        FeedbackActivity.open(this);
    }

    /**
     * 点击跳转至->服务条款
     */
    public void clickClause(View v) {
        FragmentBridgeActivity.openWebViewFragment(this, "http://dev1.v5time.net/baby/serviceProvision.html", "服务条款");
    }
}
