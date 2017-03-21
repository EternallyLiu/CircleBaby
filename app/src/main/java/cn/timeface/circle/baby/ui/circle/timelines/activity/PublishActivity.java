package cn.timeface.circle.baby.ui.circle.timelines.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.support.api.models.objs.ImgObj;
import cn.timeface.circle.baby.support.utils.DateUtil;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.circle.bean.CircleContentObj;
import cn.timeface.circle.baby.ui.circle.bean.CircleHomeworkObj;
import cn.timeface.circle.baby.ui.circle.bean.CircleSchoolTaskObj;
import cn.timeface.circle.baby.ui.circle.bean.CircleTimelineObj;
import cn.timeface.circle.baby.ui.circle.timelines.adapter.PublishAdapter;
import cn.timeface.circle.baby.ui.circle.timelines.bean.ItemObj;
import cn.timeface.circle.baby.ui.circle.timelines.events.ActiveSelectEvent;
import cn.timeface.circle.baby.ui.circle.timelines.views.CircleGridStaggerLookup;
import cn.timeface.circle.baby.ui.timelines.Utils.JSONUtils;
import cn.timeface.circle.baby.ui.timelines.Utils.LogUtil;
import rx.Observable;

/**
 * author : wangshuai Created on 2017/3/15
 * email : wangs1992321@gmail.com
 */
public class PublishActivity extends BaseAppCompatActivity {

    public static final int PICTURE = 2017;
    public static final int TIME = 2018;
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.content_recycler_view)
    RecyclerView contentRecyclerView;

    private PublishAdapter adapter = null;

    private int type = PublishAdapter.TYPE_TIMELINE;
    private CircleGridStaggerLookup lookup;

    /**
     * 发送或者编辑圈动态
     *
     * @param context
     * @param timlineObj
     */
    public static void open(Context context, CircleTimelineObj timlineObj) {
        Bundle bundle = new Bundle();
        bundle.putInt("type", PublishAdapter.TYPE_TIMELINE);
        bundle.putParcelable(CircleTimelineObj.class.getSimpleName(), timlineObj);
        context.startActivity(new Intent(context, PublishActivity.class).putExtras(bundle));
    }

    /**
     * 上下文  默认发布圈动态
     *
     * @param context
     */
    public static void open(Context context) {
        Bundle bundle = new Bundle();
        bundle.putInt("type", PublishAdapter.TYPE_TIMELINE);
        context.startActivity(new Intent(context, PublishActivity.class).putExtras(bundle));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_publish);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        adapter = new PublishAdapter(this);
        init();
        lookup = new CircleGridStaggerLookup(type, 4, false);
        adapter.setLookup(lookup);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false);
        gridLayoutManager.setSpanSizeLookup(lookup);
        adapter.setType(type);
        contentRecyclerView.setAdapter(adapter);
        contentRecyclerView.setLayoutManager(gridLayoutManager);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ActiveSelectEvent event) {
        CircleTimelineObj timelineObj = (CircleTimelineObj) adapter.getContentObj();
        timelineObj.setActivityAlbum(event.getActivityAlbumObj());
        adapter.setContentObj(timelineObj);
        adapter.notifyDataSetChanged();
    }

    private void init(){
        Bundle bundle = getIntent().getExtras();
        type = bundle.getInt("type", PublishAdapter.TYPE_TIMELINE);

        switch (type) {
            case PublishAdapter.TYPE_TIMELINE:
                title.setText(R.string.send_circle_time_line_title);
                CircleTimelineObj timelineObj = bundle.getParcelable(CircleTimelineObj.class.getSimpleName());
                adapter.setContentObj(timelineObj);
                break;
            case PublishAdapter.TYPE_WORK:
                title.setText(R.string.send_circle_homework_title);
                break;
            case PublishAdapter.TYPE_SCHOOL:
                title.setText(R.string.send_circle_school_title);
                break;

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_complete, menu);
        menu.findItem(R.id.complete).setTitle(R.string.comfirm);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.complete)
            sendTimeLine();
        return super.onOptionsItemSelected(item);
    }

    private void reqData() {
        addSubscription(apiService.queryLastSelect(FastData.getCircleId())
                .filter(createActiveResponse -> createActiveResponse.success())
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(createActiveResponse -> {
                    CircleTimelineObj timelineObj = (CircleTimelineObj) adapter.getContentObj();
                    timelineObj.setActivityAlbum(createActiveResponse.getActivityAlbum());
                    adapter.setContentObj(timelineObj);
                    adapter.notifyDataSetChanged();
                }, throwable -> LogUtil.showError(throwable)));
    }

    private void sendTimeLine() {
        addSubscription(Observable.defer(() -> Observable.just(adapter.getSendContent()))
                .map(circleContentObj -> JSONUtils.parse2JSONString(circleContentObj))
                .filter(s -> !TextUtils.isEmpty(s))
                .compose(SchedulersCompat.applyIoSchedulers())
                .flatMap(s -> apiService.sendCircleTimeLine(FastData.getCircleId(), s).compose(SchedulersCompat.applyIoSchedulers()))
                .subscribe(timeLineSendResponse -> {
                    if (timeLineSendResponse.success()) {
                        finish();
                    } else ToastUtil.showToast(this, timeLineSendResponse.getInfo());
                }, throwable -> LogUtil.showError(throwable)));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            switch (requestCode) {
                case PICTURE:
                    ArrayList<ImgObj> selImages = data.getParcelableArrayListExtra("result_select_image_list");
                    if (selImages != null) {
                        adapter.getSelImage().clear();
                        adapter.getSelImage().addAll(selImages);
                        adapter.notifyDataSetChanged();
                    }
                    break;
                case TIME:
                    long time = data.getLongExtra("timeMillis", System.currentTimeMillis());
                    String currentTimeStr = DateUtil.formatDate("HH:mm:ss", System.currentTimeMillis());
                    time += DateUtil.getTime(currentTimeStr, "HH:mm");
                    CircleTimelineObj contentObj = (CircleTimelineObj) adapter.getContentObj();
                    contentObj.setRecordDate(time);
                    adapter.setContentObj(contentObj);
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        ButterKnife.unbind(this);
        super.onDestroy();
    }


}
