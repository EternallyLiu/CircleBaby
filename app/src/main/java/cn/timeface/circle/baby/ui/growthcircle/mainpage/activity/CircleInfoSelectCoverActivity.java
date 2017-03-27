package cn.timeface.circle.baby.ui.growthcircle.mainpage.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;

/**
 * 更换圈封面
 */
public class CircleInfoSelectCoverActivity extends BaseAppCompatActivity {

    public static final String EXTRA_DATA = "extra_data";

    public static final int SELECTED_ALBUM = 1;
    public static final int SELECTED_RECOMMEND = 2;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    public static void openForResult(Context context, int requestCode) {
        Intent intent = new Intent(context, CircleInfoSelectCoverActivity.class);
        ((Activity) context).startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_info_select_cover);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.fl_album, R.id.fl_recommend})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fl_album:
                goBack(SELECTED_ALBUM);
                break;
            case R.id.fl_recommend:
                goBack(SELECTED_RECOMMEND);
                break;
        }
    }

    private void goBack(int selectWay) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_DATA, selectWay);
        setResult(RESULT_OK, intent);
        finish();
    }
}
