package cn.timeface.circle.baby.views;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * @author wxw
 * @from 2015/8/11
 * @TODO 垂直RecyclerView 空白Divider
 */
public class VerticalSpaceItemDecoration extends RecyclerView.ItemDecoration {

    boolean headerDecoration = true;

    private int mVerticalSpaceHeight;
    private int firstItemTopDividerHeight;//第一条Item顶部间隔线高度
    private int lastItemBottomDividerHeight;//最后一条Item底部间隔线高度

    public VerticalSpaceItemDecoration(int mVerticalSpaceHeight) {
        this.mVerticalSpaceHeight = mVerticalSpaceHeight;
    }

    public VerticalSpaceItemDecoration(int mVerticalSpaceHeight, int firstItemTopDividerHeight) {
        this.mVerticalSpaceHeight = mVerticalSpaceHeight;
        this.firstItemTopDividerHeight = firstItemTopDividerHeight;
    }

    public VerticalSpaceItemDecoration(int mVerticalSpaceHeight, int firstItemTopDividerHeight, int lastItemBottomDividerHeight) {
        this.mVerticalSpaceHeight = mVerticalSpaceHeight;
        this.firstItemTopDividerHeight = firstItemTopDividerHeight;
        this.lastItemBottomDividerHeight = lastItemBottomDividerHeight;
    }

    public void setHeadDecoration(boolean headDecoration){
        this.headerDecoration = headDecoration;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        int childPosition = parent.getChildLayoutPosition(view);
        if (firstItemTopDividerHeight > 0 && childPosition == 0) {
            // 第一条Item
            outRect.top = firstItemTopDividerHeight;
        }

        if (lastItemBottomDividerHeight > 0 && childPosition == parent.getAdapter().getItemCount()) {
//            if (parent.getAdapter() instanceof BaseRecyclerAdapter
//                    && childPosition == ((BaseRecyclerAdapter) parent.getAdapter()).getCount()){
//
//            } else {
//
//            }
            //最后一条Item
            outRect.bottom = lastItemBottomDividerHeight;
        } else {
            //其他Item底部间隔
            outRect.bottom = mVerticalSpaceHeight;
        }

        if(childPosition == 0 && !headerDecoration){
            outRect.top = 0;
            outRect.bottom = 0;
        }
    }
}
