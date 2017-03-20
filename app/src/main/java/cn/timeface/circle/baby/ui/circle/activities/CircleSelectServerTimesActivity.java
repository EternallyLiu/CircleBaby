package cn.timeface.circle.baby.ui.circle.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.support.api.models.objs.MediaObj;
import cn.timeface.circle.baby.support.mvp.bases.BasePresenterAppCompatActivity;
import cn.timeface.circle.baby.ui.circle.bean.CircleTimeLineExObj;
import cn.timeface.circle.baby.ui.circle.dialogs.CircleSelectTimeTypeDialog;
import cn.timeface.circle.baby.ui.circle.fragments.CircleServerTimeFragment;
import cn.timeface.circle.baby.views.TFStateView;

/**
 * 圈作品-时光书
 * author : sunyanwei Created on 17-3-20
 * email : sunyanwei@timeface.cn
 */
public class CircleSelectServerTimesActivity extends BasePresenterAppCompatActivity implements CircleSelectTimeTypeDialog.SelectTypeListener {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.fl_container)
    FrameLayout flContainer;
    @Bind(R.id.cb_all_sel)
    CheckBox cbAllSel;
    @Bind(R.id.tv_sel_count)
    TextView tvSelCount;
    @Bind(R.id.rl_photo_tip)
    RelativeLayout rlPhotoTip;
    @Bind(R.id.tf_stateView)
    TFStateView tfStateView;
    @Bind(R.id.content_select_time)
    RelativeLayout contentSelectTime;
    @Bind(R.id.tv_content_type)
    TextView tvContentType;
    @Bind(R.id.tv_content)
    TextView tvContent;

    boolean fragmentShow = false;
    boolean canBack = false;
    CircleServerTimeFragment allTimeFragment;//全部动态
    CircleServerTimeFragment mineTimeFragment;//我发布的动态
    CircleServerTimeFragment aboutBabyTimeFragment;//与我宝宝相关的动态
    CircleSelectTimeTypeDialog selectContentTypeDialog;
    int openBookType;
    int bookType;
    String bookId;
    String openBookId;
    String circleId;

    List<MediaObj> allSelectMedias = new ArrayList<>();
    List<CircleTimeLineExObj> allSelectTimeLines = new ArrayList<>();
    List<String> allSelectTimeIds = new ArrayList<>();

    public static void open(Context context, int bookType, int openBookType, String bookId, String openBookId, String circleId) {
        Intent intent = new Intent(context, CircleSelectServerTimesActivity.class);
        intent.putExtra("open_book_type", openBookType);
        intent.putExtra("book_type", bookType);
        intent.putExtra("book_id", bookId);
        intent.putExtra("open_book_id", openBookId);
        intent.putExtra("circle_id", circleId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_select_server_times);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
    }

    @Override
    public void selectTypeAll() {

    }

    @Override
    public void selectTypeMe() {

    }

    @Override
    public void selectTypeAboutMyBaby() {

    }
}
