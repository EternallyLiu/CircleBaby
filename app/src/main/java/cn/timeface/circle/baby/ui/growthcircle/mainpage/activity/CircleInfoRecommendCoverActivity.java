package cn.timeface.circle.baby.ui.growthcircle.mainpage.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.support.api.models.objs.MediaObj;
import cn.timeface.circle.baby.support.managers.listeners.IEventBus;
import cn.timeface.circle.baby.support.utils.network.NetworkError;
import cn.timeface.circle.baby.support.utils.recyclerview.itemdecoration.GridItemDecoration;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.growthcircle.mainpage.adapter.RecommendCoverAdapter;
import cn.timeface.circle.baby.ui.growthcircle.mainpage.event.CircleCoverSelectedEvent;
import cn.timeface.circle.baby.views.dialog.TFProgressDialog;
import rx.Subscription;

/**
 * 更换圈封面-推荐图片
 */
public class CircleInfoRecommendCoverActivity extends BaseAppCompatActivity implements IEventBus {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;

    private RecommendCoverAdapter adapter;
    private ArrayList<MediaObj> dataList;

    private TFProgressDialog progressDialog;

    public static void open(Context context) {
        Intent intent = new Intent(context, CircleInfoRecommendCoverActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_info_recommend_cover);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        showProgressDialog();
        reqData();
    }

    private void reqData() {
        Subscription s = apiService.getCircleDefaultCover()
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(
                        response -> {
                            dismissProgressDialog();
                            if (response.success()) {
                                if (response.getDataList() != null
                                        && response.getDataList().size() > 0) {
                                    dataList = (ArrayList<MediaObj>) response.getDataList();
                                    setupListData(dataList);
                                }
                            } else {
                                Toast.makeText(this, response.info, Toast.LENGTH_SHORT).show();
                            }
                        },
                        throwable -> {
                            dismissProgressDialog();
                            NetworkError.showException(this, throwable);
                        }
                );
        addSubscription(s);
    }

    private void setupListData(ArrayList<MediaObj> dataList) {
        if (adapter == null) {
            adapter = new RecommendCoverAdapter(this, dataList);
            recyclerView.setLayoutManager(
                    new GridLayoutManager(this, 3, LinearLayoutManager.VERTICAL, false)
            );
            recyclerView.addItemDecoration(
                    new GridItemDecoration(this, R.dimen.view_space_small)
            );
            recyclerView.setAdapter(adapter);
        } else {
            adapter.setListData(dataList);
            adapter.notifyDataSetChanged();
        }
    }

    public void clickItem(View view) {
        if (dataList != null && view.getTag(R.string.tag_obj) != null
                && view.getTag(R.string.tag_obj) instanceof MediaObj) {
            MediaObj item = (MediaObj) view.getTag(R.string.tag_obj);
            CircleInfoRecommendCoverDetailActivity.open(this, dataList, dataList.indexOf(item));
        }
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = TFProgressDialog.getInstance();
        }
        progressDialog.show(getSupportFragmentManager(), "ProgressDialog");
    }

    private void dismissProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Subscribe()
    public void onEvent(CircleCoverSelectedEvent event) {
        if (!TextUtils.isEmpty(event.coverUrl)) {
            finish();
        }
    }

}
