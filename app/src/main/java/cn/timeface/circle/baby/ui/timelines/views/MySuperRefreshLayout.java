package cn.timeface.circle.baby.ui.timelines.views;

import android.content.Context;
import android.util.AttributeSet;

/**
 * author : wangshuai Created on 2017/1/22
 * email : wangs1992321@gmail.com
 */
public class MySuperRefreshLayout extends SuperSwipeRefreshLayout implements SuperSwipeRefreshLayout.OnPullRefreshListener
        , SuperSwipeRefreshLayout.OnPushLoadMoreListener {
    private RefreshHeaderLayout headerView;
    private RefreshHeaderLayout footView;

    private boolean isHeaderRefreshing;
    private boolean isFooterRefreshing;


    private loadListener listener;
    private boolean loadMoreEnable;
    private boolean pullRefreshEnable;

    public loadListener getListener() {
        return listener;
    }

    public void setListener(loadListener listener) {
        this.listener = listener;
    }

    public boolean isLoadMoreEnable() {
        return loadMoreEnable;
    }

    public void setLoadMoreEnable(boolean loadMoreEnable) {
        this.loadMoreEnable = loadMoreEnable;
        if (isLoadMoreEnable())
            createFooterView();
    }

    public boolean isPullRefreshEnable() {
        return pullRefreshEnable;
    }

    public void setPullRefreshEnable(boolean pullRefreshEnable) {
        this.pullRefreshEnable = pullRefreshEnable;
        if (isPullRefreshEnable())
            createHearderView();
    }

    public MySuperRefreshLayout(Context context) {
        super(context);
    }

    public MySuperRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private void createHearderView() {
        if (headerView == null) {
            headerView = new RefreshHeaderLayout(getContext());
        }
        this.setHeaderView(headerView.getRootView());
        setOnPullRefreshListener(this);
    }

    private void createFooterView() {
        if (footView == null) {
            footView = new RefreshHeaderLayout(getContext());
        }
        this.setFooterView(footView.getRootView());
        setOnPushLoadMoreListener(this);
    }

    @Override
    public void onRefresh() {
        headerView.startAmination();
        headerView.setTitle("正在刷新……");
        setHeaderRefreshing(true);
        if (getListener() != null) getListener().pullRefresh();
    }

    @Override
    public void onPullDistance(int distance) {
        headerView.setRotation(headerView.getRotationValue(distance));
    }

    @Override
    public void onPullEnable(boolean enable) {
        if (enable)
            headerView.setTitle("放开刷新");
        else headerView.setTitle("下拉刷新");
    }

    @Override
    public void onLoadMore() {
        footView.startAmination();
        footView.setTitle("正在加载…………");
        if (getListener() != null)
            getListener().loadMore();
        setFooterRefreshing(true);
    }

    @Override
    public void onPushDistance(int distance) {
        footView.setRotation(footView.getRotationValue(distance));
    }

    @Override
    public void onPushEnable(boolean enable) {
        if (enable)
            footView.setTitle("放开加载更多");
        else footView.setTitle("上拉加载更多");
    }

    public boolean isHeaderRefreshing() {
        return isHeaderRefreshing;
    }

    public void setHeaderRefreshing(boolean headerRefreshing) {
        isHeaderRefreshing = headerRefreshing;
        if (!isHeaderRefreshing())
            setRefreshing(false);
    }

    public boolean isFooterRefreshing() {
        return isFooterRefreshing;
    }

    public void setFooterRefreshing(boolean footerRefreshing) {
        isFooterRefreshing = footerRefreshing;
        if (!isFooterRefreshing())
            setLoadMore(false);
    }

    public interface loadListener {
        public void pullRefresh();


        public void loadMore();
    }

}
