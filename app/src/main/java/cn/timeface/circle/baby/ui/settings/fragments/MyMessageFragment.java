package cn.timeface.circle.baby.ui.settings.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.fragments.base.BaseFragment;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.support.utils.ptr.IPTRRecyclerListener;
import cn.timeface.circle.baby.support.utils.ptr.TFPTRRecyclerViewHelper;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.settings.adapters.MyMessageAdapter;
import cn.timeface.circle.baby.ui.settings.beans.MessageBean;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                        currentPage=1;
                        reqData();
                    }

                    @Override
                    public void onTFPullUpToRefresh(View refreshView) {
                        currentPage++;
                        reqData();
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
        adapter.addList(currentPage == 1, list);
    }

    private void reqData() {
        addSubscription(apiService.queryMsgList(currentPage, PAGESIZE).compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(myMessageResponse -> {
                    adapter.getEmptyItem().setOperationType(0);
                    adapter.notifyDataSetChanged();
                    if (myMessageResponse.success()) setDataList(myMessageResponse.getDataList());
                    else ToastUtil.showToast(getActivity(), myMessageResponse.getInfo());
                }, throwable -> {
                    adapter.getEmptyItem().setOperationType(0);
                    adapter.getEmptyItem().setThrowable(throwable);
                    adapter.notifyDataSetChanged();
                    helper.finishTFPTRRefresh();
                }));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onItemClick(View view, int position) {
        if (adapter.getItemViewType(position)==)
    }
}
