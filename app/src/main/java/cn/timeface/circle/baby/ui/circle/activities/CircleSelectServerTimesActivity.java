package cn.timeface.circle.baby.ui.circle.activities;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.support.mvp.bases.BasePresenterAppCompatActivity;
import cn.timeface.circle.baby.ui.circle.fragments.CircleServerTimeFragment;
import cn.timeface.circle.baby.views.TFStateView;

/**
 * 圈作品-时光书
 * author : sunyanwei Created on 17-3-20
 * email : sunyanwei@timeface.cn
 */
public class CircleSelectServerTimesActivity extends BasePresenterAppCompatActivity {

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
    CircleServerTimeFragment timeFragment;//按时间
    HashMap<String, CircleServerTimeFragment> userFragmentMap = new HashMap<>();//存储下来所有用户对应的fragment
//    SelectContentTypeDialog selectContentTypeDialog;
    int openBookType;
    int bookType;
    String bookId;
    String openBookId;
    int babyId;

//    List<MediaObj> allSelectMedias = new ArrayList<>();
//    List<TimeLineObj> allSelectTimeLines = new ArrayList<>();
//    List<String> allSelectTimeIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_select_server_times);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
    }
}
