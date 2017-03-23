package cn.timeface.circle.baby.ui.circle.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.support.managers.listeners.IEventBus;
import cn.timeface.circle.baby.support.mvp.bases.BasePresenterAppCompatActivity;
import cn.timeface.circle.baby.support.utils.DateUtil;
import cn.timeface.circle.baby.ui.circle.adapters.CircleSelectServerTimeMediaAdapter;
import cn.timeface.circle.baby.ui.circle.bean.CircleMediaObj;
import cn.timeface.circle.baby.ui.circle.bean.CircleTimeLineExObj;
import cn.timeface.circle.baby.ui.circle.events.CircleSelectMediaEvent;
import cn.timeface.circle.baby.ui.circle.events.CircleSelectMediaListEvent;
import cn.timeface.circle.baby.ui.circle.events.CircleSelectTimeLineEvent;

/**
 * 圈选择时光详情页面
 * author : sunyanwei Created on 17-3-21
 * email : sunyanwei@timeface.cn
 */
public class CircleSelectServerTimeDetailActivity extends BasePresenterAppCompatActivity implements View.OnClickListener, IEventBus {

    @Bind(R.id.tv_cancel)
    TextView tvCancel;
    @Bind(R.id.tv_date)
    TextView tvDate;
    @Bind(R.id.tv_select_all)
    TextView tvSelectAll;
    @Bind(R.id.tv_finish)
    TextView tvFinish;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.rv_content)
    RecyclerView rvContent;
    @Bind(R.id.content_select_server_time_detail)
    LinearLayout contentSelectServerTimeDetail;

    CircleSelectServerTimeMediaAdapter serverTimesAdapter;
    CircleTimeLineExObj timeLineObj;
    List<CircleMediaObj> selMedias;

    public static void open(Context context, CircleTimeLineExObj timeLineObj, List<CircleMediaObj> selMedias) {
        Intent intent = new Intent(context, CircleSelectServerTimeDetailActivity.class);
        intent.putExtra("time_line_obj", timeLineObj);
        intent.putParcelableArrayListExtra("medias", (ArrayList<? extends Parcelable>) selMedias);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_select_server_time_detail);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        timeLineObj = getIntent().getParcelableExtra("time_line_obj");
        selMedias = getIntent().getParcelableArrayListExtra("medias");
        tvDate.setText(DateUtil.formatDate("MM月dd日", timeLineObj.getCircleTimeline().getRecordDate()) + DateUtils.formatDateTime(this, timeLineObj.getCircleTimeline().getRecordDate(), DateUtils.FORMAT_SHOW_WEEKDAY));
        tvTitle.setText(timeLineObj.getCircleTimeline().getContent());
        tvCancel.setOnClickListener(this);
        tvSelectAll.setOnClickListener(this);
        tvFinish.setOnClickListener(this);

        tvSelectAll.setText(isAllSelect() ? "取消全选" : "全选");
        setData(timeLineObj.getCircleTimeline().getMediaList());
    }

    private void setData(List<CircleMediaObj> mediaObjs) {
        if (serverTimesAdapter == null) {
            serverTimesAdapter = new CircleSelectServerTimeMediaAdapter(this, mediaObjs, selMedias);
            rvContent.setLayoutManager(new GridLayoutManager(this, 3));
            rvContent.setAdapter(serverTimesAdapter);
        } else {
            serverTimesAdapter.setListData(mediaObjs);
            serverTimesAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_cancel:
                finish();
                break;

            case R.id.tv_select_all:
                if(tvSelectAll.getText().toString().equals("全选")){
                    if(!selMedias.containsAll(timeLineObj.getCircleTimeline().getMediaList())){
                        selMedias.addAll(timeLineObj.getCircleTimeline().getMediaList());
                    }
                    EventBus.getDefault().post(new CircleSelectMediaListEvent(CircleSelectMediaListEvent.TYPE_TIME_MEDIA, true, timeLineObj.getCircleTimeline().getMediaList()));
                    tvSelectAll.setText("取消全选");
                } else {
                    if(selMedias.containsAll(timeLineObj.getCircleTimeline().getMediaList())){
                        selMedias.removeAll(timeLineObj.getCircleTimeline().getMediaList());
                    }
                    tvSelectAll.setText("全选");
                    EventBus.getDefault().post(new CircleSelectMediaListEvent(CircleSelectMediaListEvent.TYPE_TIME_MEDIA, false, timeLineObj.getCircleTimeline().getMediaList()));
                }
                EventBus.getDefault().post(new CircleSelectTimeLineEvent(isTimeSelect(), timeLineObj));
                serverTimesAdapter.notifyDataSetChanged();
                break;

            case R.id.tv_finish:
                finish();
                break;
        }
    }

    public void clickPhotoView(View view){}

    private boolean isAllSelect(){
        return selMedias.containsAll(timeLineObj.getCircleTimeline().getMediaList());
    }

    private boolean isTimeSelect(){
        boolean isTimeSelect = false;
        for(CircleMediaObj mediaObj : timeLineObj.getCircleTimeline().getMediaList()){
            if(selMedias.contains(mediaObj)){
                isTimeSelect = true;
                break;
            }
        }
        return isTimeSelect;
    }

    @Subscribe
    public void selectMediaEvent(CircleSelectMediaEvent selectMediaEvent){
        //选中
        if(selectMediaEvent.getSelect()){
            if(!selMedias.contains(selectMediaEvent.getMediaObj())){
                selMedias.add(selectMediaEvent.getMediaObj());
            }
        } else {
            if(selMedias.contains(selectMediaEvent.getMediaObj())){
                selMedias.remove(selectMediaEvent.getMediaObj());
            }
        }

        if(isAllSelect()){
            tvSelectAll.setText("取消全选");
        } else {
            tvSelectAll.setText("全选");
        }

        EventBus.getDefault().post(new CircleSelectTimeLineEvent(isTimeSelect(), timeLineObj));
    }
}
