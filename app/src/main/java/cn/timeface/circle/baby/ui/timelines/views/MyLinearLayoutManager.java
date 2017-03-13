package cn.timeface.circle.baby.ui.timelines.views;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.timeface.refreshload.overscroll.OverScrollLinearLayoutManager;

/**
 * author : wangshuai Created on 2017/1/23
 * email : wangs1992321@gmail.com
 */
public class MyLinearLayoutManager extends OverScrollLinearLayoutManager {


    public MyLinearLayoutManager(RecyclerView recyclerView) {
        super(recyclerView);
    }

    public MyLinearLayoutManager(RecyclerView recyclerView, int orientation, boolean reverseLayout) {
        super(recyclerView, orientation, reverseLayout);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try{
            super.onLayoutChildren(recycler, state);
        }catch (IndexOutOfBoundsException e){
            e.printStackTrace();
        }
    }
}
