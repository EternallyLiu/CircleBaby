package cn.timeface.circle.baby.ui.settings.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.FragmentBridgeActivity;
import cn.timeface.circle.baby.events.UnreadMsgEvent;
import cn.timeface.circle.baby.fragments.base.BaseFragment;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.support.utils.ptr.IPTRRecyclerListener;
import cn.timeface.circle.baby.support.utils.ptr.TFPTRRecyclerViewHelper;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.settings.adapters.MyMessageAdapter;
import cn.timeface.circle.baby.ui.settings.beans.MessageBean;
import cn.timeface.circle.baby.ui.timelines.Utils.JSONUtils;
import cn.timeface.circle.baby.ui.timelines.Utils.LogUtil;
import cn.timeface.circle.baby.ui.timelines.adapters.BaseAdapter;
import cn.timeface.circle.baby.ui.timelines.adapters.EmptyItem;
import cn.timeface.circle.baby.views.DividerItemDecoration;

/**
 * author : wangshuai Created on 2017/4/13
 * email : wangs1992321@gmail.com
 */
public class MyMessageFragment extends BaseFragment implements BaseAdapter.OnItemClickLister {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.content_recycler_view)
    RecyclerView contentRecyclerView;
    @Bind(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefresh;

    private MyMessageAdapter adapter;
    private TFPTRRecyclerViewHelper helper;
    private final int PAGESIZE = 20;
    private int currentPage = 1;

    private int type = 0;//消息列表,1001、系统消息

    public static void open(Context content) {
        FragmentBridgeActivity.open(content, MyMessageFragment.class.getSimpleName());
    }

    public static void openSystem(Context context) {
        Bundle bundle = new Bundle();
        bundle.putInt("type", 1001);
        FragmentBridgeActivity.open(context, MyMessageFragment.class.getSimpleName(), bundle);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle.containsKey("type"))
            type = bundle.getInt("type");
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_message_list, container, false);
        ButterKnife.bind(this, contentView);
        setActionBar(toolbar);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle(R.string.notification_message);
        }
        init();
        return contentView;
    }

    private void init() {
        adapter = new MyMessageAdapter(getActivity());
        adapter.setEmptyItem(new EmptyItem(2001));
        adapter.getEmptyItem().setOperationType(1);
        adapter.setItemClickLister(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), layoutManager.getOrientation(), R.color.divider_color);
        contentRecyclerView.addItemDecoration(itemDecoration);
        contentRecyclerView.setLayoutManager(layoutManager);
        contentRecyclerView.setAdapter(adapter);
        helper = new TFPTRRecyclerViewHelper(getActivity(), contentRecyclerView, swipeRefresh);
        helper.setTFPTRMode(TFPTRRecyclerViewHelper.Mode.PULL_FORM_START)
                .tfPtrListener(new IPTRRecyclerListener() {
                    @Override
                    public void onTFPullDownToRefresh(View refreshView) {
                        adapter.getEmptyItem().setOperationType(1);
                        adapter.notifyDataSetChanged();
                        currentPage = 1;
                        reqData();
                    }

                    @Override
                    public void onTFPullUpToRefresh(View refreshView) {
                        if (type != 1001) {
                            currentPage++;
                            reqData();
                        }
                    }

                    @Override
                    public void onScrollUp(int firstVisibleItem) {
                    }

                    @Override
                    public void onScrollDown(int firstVisibleItem) {
                    }
                });
        reqData();
    }

    private void setDataList(List<MessageBean> list) {
        if (type == 1001) adapter.addList(true, list);
        else
            adapter.addList(currentPage == 1, list);
    }

    private void reqData() {
        if (type == 1001)
            addSubscription(apiService.querySystemMessage().compose(SchedulersCompat.applyIoSchedulers())
                    .doOnNext(myMessageResponse -> helper.finishTFPTRRefresh())
                    .subscribe(myMessageResponse -> {
                        adapter.getEmptyItem().setOperationType(0);
                        adapter.notifyDataSetChanged();
                        if (myMessageResponse.success())
                            setDataList(myMessageResponse.getDataList());
                        else ToastUtil.showToast(getActivity(), myMessageResponse.getInfo());
                    }, throwable -> {
                        adapter.getEmptyItem().setOperationType(0);
                        adapter.getEmptyItem().setThrowable(throwable);
                        adapter.notifyDataSetChanged();
                        helper.finishTFPTRRefresh();
                    }));
        else
            addSubscription(apiService.queryMsgList(currentPage, PAGESIZE).compose(SchedulersCompat.applyIoSchedulers())
                    .doOnNext(myMessageResponse -> helper.finishTFPTRRefresh())
                    .subscribe(myMessageResponse -> {
                        adapter.getEmptyItem().setOperationType(0);
                        adapter.notifyDataSetChanged();
                        if (myMessageResponse.success())
                            setDataList(myMessageResponse.getDataList());
                        else ToastUtil.showToast(getActivity(), myMessageResponse.getInfo());
                    }, throwable -> {
                        adapter.getEmptyItem().setOperationType(0);
                        adapter.getEmptyItem().setThrowable(throwable);
                        adapter.notifyDataSetChanged();
                        helper.finishTFPTRRefresh();
                    }));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_msg, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.read) {
            apiService.read(0, type != 1001 ? 1 : 2)
                    .compose(SchedulersCompat.applyIoSchedulers())
                    .subscribe(response -> {
                        ToastUtil.showToast(response.getInfo());
                        if (response.success()) {
                            currentPage = 1;
                            reqData();
                            EventBus.getDefault().post(new UnreadMsgEvent());
                        }
                    }, error -> {
                        Log.e(TAG, "read:");
                    });

        } else if (item.getItemId() == R.id.del) {
            new AlertDialog.Builder(getContext())
                    .setTitle("确定删除全部消息?")
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    apiService.delMsg(0, type != 1001 ? 0 : 1)
                            .compose(SchedulersCompat.applyIoSchedulers())
                            .subscribe(response -> {
                                ToastUtil.showToast(response.getInfo());
                                if (response.success()) {
                                    currentPage = 1;
                                    reqData();
                                    EventBus.getDefault().post(new UnreadMsgEvent());
                                }
                            }, error -> {
                                Log.e(TAG, "delMsg:");
                            });
                }
            }).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onItemClick(View view, int position) {
        if (adapter.getItemViewType(position) == 1001) {
            MessageBean item = adapter.getItem(position);
            if (type != 1001 && item.getIdentifier() >= 3000 && item.getIdentifier() < 4000) {
                openSystem(getActivity());
            } else {
                addSubscription(apiService.read(item.getMessageId(), 0).compose(SchedulersCompat.applyIoSchedulers())
                        .subscribe(baseResponse -> {
                            if (baseResponse.success()) {
                                item.setIsRead(1);
                                adapter.updateItem(item);
                            }
                        }, throwable -> LogUtil.showError(throwable)));

                item.skip(getActivity());

            }
        }
    }
}
