package com.timeface.refreshload;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.timeface.refreshload.headfoot.LoadMoreView;
import com.timeface.refreshload.headfoot.RefreshView;
import com.timeface.refreshload.headfoot.impl.DefaultLoadMoreView;
import com.timeface.refreshload.headfoot.impl.DefaultRefreshView;
import com.timeface.refreshload.overscroll.OverScrollGridManager;
import com.timeface.refreshload.overscroll.OverScrollImpl;
import com.timeface.refreshload.overscroll.OverScrollLinearLayoutManager;

public class PullRefreshLoadRecyclerView extends FrameLayout implements RefreshView.StateListener, LoadMoreView.StateListener {
    RecyclerView recyclerView;
    LoadMoreView loadMoreView;
    RefreshView refreshView;
    LoadRefreshListener loadRefreshListener;

    public static final int REQ_PAGE = 30;

    public PullRefreshLoadRecyclerView(Context context) {
        super(context);
        init();
    }

    public PullRefreshLoadRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PullRefreshLoadRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PullRefreshLoadRecyclerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        recyclerView = new InnerRecyclerView(getContext());
        recyclerView.setLayoutManager(new OverScrollLinearLayoutManager(recyclerView));
        addView(recyclerView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        setLoadMoreView(new DefaultLoadMoreView(getContext()));
        setRefreshView(new DefaultRefreshView(getContext()));
    }

    public void setLoadMoreView(LoadMoreView loadMoreView) {
        if (this.loadMoreView != null) {
            this.loadMoreView.setStateListener(null);
            this.loadMoreView.unBind();
            removeView(this.loadMoreView);
        }
        this.loadMoreView = loadMoreView;

        if (loadMoreView != null) {
            addView(loadMoreView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL));
            loadMoreView.bindWith(recyclerView);
            loadMoreView.setStateListener(this);
        }
    }

    public void setRefreshView(RefreshView refreshView) {
        if (this.refreshView != null) {
            this.refreshView.setStateListener(null);
            this.refreshView.unBind();
            removeView(this.refreshView);
        }
        this.refreshView = refreshView;
        if (refreshView != null) {
            addView(refreshView, new FrameLayout.LayoutParams(-1, -2, Gravity.TOP));
            refreshView.bindWith(recyclerView);
            refreshView.setStateListener(this);
        }
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public LoadMoreView getLoadMoreView() {
        return loadMoreView;
    }

    public RefreshView getRefreshView() {
        return refreshView;
    }


    public boolean isLoading() {
        return (loadMoreView != null && loadMoreView.getState() == LoadMoreView.STATE_LOADING) ||
                (refreshView != null && refreshView.getState() == RefreshView.STATE_LOADING);
    }

    /**
     * @param refreshView
     * @param state
     * @param oldState
     * @return 不是正常状态且正在加载中时，返回true
     */
    @Override
    public boolean interceptStateChange(RefreshView refreshView, int state, int oldState) {
        if (state != RefreshView.STATE_NORMAL &&
                loadMoreView != null && loadMoreView.getState() == LoadMoreView.STATE_LOADING) {
            return true;
        }
        return false;
    }

    @Override
    public void onStateChange(RefreshView refreshView, int state) {
        if (loadRefreshListener != null && state == RefreshView.STATE_LOADING) {//正在刷新
            loadRefreshListener.onRefresh(this, refreshView);
            if (loadMoreView != null && loadMoreView.getState() == LoadMoreView.STATE_LOADING) {//关闭正在加载
                loadMoreView.setState(LoadMoreView.STATE_NORMAL);
            }
        }
    }

    /**
     * @param loadMoreView
     * @param state
     * @param oldState
     * @return 加载不是正常状态且刷新正在加载中时，返回true
     */
    @Override
    public boolean interceptStateChange(LoadMoreView loadMoreView, int state, int oldState) {
        if (state == LoadMoreView.STATE_LOADING &&
                refreshView != null && refreshView.getState() == RefreshView.STATE_LOADING) {
            return true;
        }
        return false;
    }


    @Override
    public void onStateChange(LoadMoreView loadMoreView, int state) {

        if (loadRefreshListener != null && state == LoadMoreView.STATE_LOADING) {
            loadRefreshListener.onLoadMore(this, loadMoreView);//正在加载
            if (refreshView != null && refreshView.getState() == RefreshView.STATE_LOADING) {//关闭正在刷新
                refreshView.setState(RefreshView.STATE_NORMAL);
            }
        }
    }

    public void complete() {
        finishRefresh();
        finishLoadMore();
    }

    /**
     * 刷新完成
     */
    public void finishRefresh() {
        if (refreshView != null) {
            refreshView.setState(RefreshView.STATE_NORMAL);
        }
    }

    /**
     * 加载更多完成
     */
    public void finishLoadMore() {
        if (loadMoreView != null) {
            loadMoreView.setState(LoadMoreView.STATE_NORMAL);
        }
    }

    /**
     * 没有更多数据
     */
    public void finishNoMore() {
        if (loadMoreView != null) {
            loadMoreView.setState(LoadMoreView.STATE_NO_MORE);
        }
    }

    /**
     * 请求失败时调用，完成刷新、加载
     */
    public void loadFailState() {
        if (refreshView != null && refreshView.getState() == RefreshView.STATE_LOADING) {
            finishRefresh();
        }
        if (loadMoreView != null && loadMoreView.getState() == LoadMoreView.STATE_LOADING) {
            loadMoreView.setState(LoadMoreView.STATE_LOAD_FAIL);
        }
    }

    public void setLoadRefreshListener(LoadRefreshListener loadRefreshListener) {
        this.loadRefreshListener = loadRefreshListener;
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        recyclerView.setAdapter(adapter);
    }

    public void setAdapter(LoadRefreshAdapter adapter) {
        recyclerView.setAdapter(adapter);
        setLoadRefreshListener(adapter);
    }

    public static abstract class LoadRefreshAdapter<T extends RecyclerView.ViewHolder>
            extends RecyclerView.Adapter<T> implements LoadRefreshListener {
    }

    public interface LoadRefreshListener {
        void onRefresh(PullRefreshLoadRecyclerView pullRefreshLoadRecyclerView, RefreshView refreshView);

        void onLoadMore(PullRefreshLoadRecyclerView pullRefreshLoadRecyclerView, LoadMoreView loadMoreView);
    }

    private static class InnerRecyclerView extends RecyclerView {
        public InnerRecyclerView(Context context) {
            super(context);
        }

        @Override
        public void setLayoutManager(LayoutManager layout) {
            if (layout != null && !(layout instanceof OverScrollImpl.OverScrollLayoutManager)) {
                if (layout.getClass().equals(GridLayoutManager.class)) {
                    GridLayoutManager grid = (GridLayoutManager) layout;
                    layout = new OverScrollGridManager(this, grid.getSpanCount(), grid.getOrientation(), grid.getReverseLayout());
                } else if (layout.getClass().equals(LinearLayoutManager.class)) {
                    LinearLayoutManager linear = (LinearLayoutManager) layout;
                    layout = new OverScrollLinearLayoutManager(this, linear.getOrientation(), linear.getReverseLayout());
                } else {
                    throw new IllegalArgumentException("LayoutManager " + layout +
                            " should be subclass of: " + OverScrollLinearLayoutManager.class.getName());
                }
            }
            super.setLayoutManager(layout);
        }

        @Override
        public int computeVerticalScrollOffset() {
            int overScrollDistance = 0;
            LayoutManager layoutManager = getLayoutManager();
            if (layoutManager instanceof OverScrollImpl.OverScrollLayoutManager) {
                overScrollDistance = ((OverScrollImpl.OverScrollLayoutManager) layoutManager).getOverScrollDistance();
            }
            return super.computeVerticalScrollOffset() - overScrollDistance;
        }
    }
}
