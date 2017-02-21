package cn.timeface.circle.baby.ui.growth.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.support.api.models.objs.MediaObj;
import cn.timeface.circle.baby.support.api.models.objs.TimeLineObj;
import cn.timeface.circle.baby.support.mvp.bases.BasePresenterAppCompatActivity;
import cn.timeface.circle.baby.support.utils.DateUtil;
import cn.timeface.circle.baby.ui.growth.adapters.SelectServerTimeMediaAdapter;

/**
 * 选择时光详情页面
 * author : YW.SUN Created on 2017/2/20
 * email : sunyw10@gmail.com
 */
public class SelectServerTimeDetailActivity extends BasePresenterAppCompatActivity implements View.OnClickListener {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.rv_content)
    RecyclerView rvContent;
    @Bind(R.id.tv_cancel)
    TextView tvCancel;
    @Bind(R.id.tv_date)
    TextView tvDate;
    @Bind(R.id.tv_select_all)
    TextView tvSelectAll;
    @Bind(R.id.tv_finish)
    TextView tvFinish;
    @Bind(R.id.content_select_server_time_detail)
    LinearLayout contentSelectServerTimeDetail;


    SelectServerTimeMediaAdapter serverPhotosAdapter;
    TimeLineObj timeLineObj;

    public static void open(Context context, TimeLineObj timeLineObj) {
        Intent intent = new Intent(context, SelectServerTimeDetailActivity.class);
        intent.putExtra("time_line_obj", timeLineObj);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_server_time_detail);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        timeLineObj = getIntent().getParcelableExtra("time_line_obj");
        tvDate.setText(DateUtil.formatDate("MM月dd日", timeLineObj.getDate()) + DateUtils.formatDateTime(this, timeLineObj.getDate(), DateUtils.FORMAT_SHOW_WEEKDAY));
        tvTitle.setText(timeLineObj.getContent());
        tvCancel.setOnClickListener(this);
        tvSelectAll.setOnClickListener(this);
        tvFinish.setOnClickListener(this);
        setData(timeLineObj.getMediaList());
    }

    private void setData(List<MediaObj> mediaObjs) {
        if (serverPhotosAdapter == null) {
            serverPhotosAdapter = new SelectServerTimeMediaAdapter(this, mediaObjs);
            rvContent.setLayoutManager(new GridLayoutManager(this, 3));
            rvContent.setAdapter(serverPhotosAdapter);
        } else {
            serverPhotosAdapter.setListData(mediaObjs);
            serverPhotosAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_cancel:
                finish();
                break;

            case R.id.tv_select_all:
                tvSelectAll.setText(tvSelectAll.getText().toString().equals("全选") ? "取消全选" : "全选");
                for(MediaObj mediaObj : timeLineObj.getMediaList()){
                    mediaObj.setSelected(mediaObj.select() ? 0 : 1);
                }
                serverPhotosAdapter.notifyDataSetChanged();
                break;

            case R.id.tv_finish:
                finish();
                break;
        }
    }
}
