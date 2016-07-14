package cn.timeface.open.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

/**
 * Created by zhsheng on 2016/7/14.
 */
public class PageFrameLayout extends FrameLayout {

    private int currentPage = 1;
    public static final int LEFT = 1;
    public static final int RIGHT = 2;

    public PageFrameLayout(Context context) {
        super(context);
    }

    public PageFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PageFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PageFrameLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            float evX = ev.getX();
            int measuredWidth = getMeasuredWidth();
            if (evX > measuredWidth / 2) {
                currentPage = RIGHT;
            } else {
                currentPage = LEFT;
            }

        }
        return super.dispatchTouchEvent(ev);
    }

    public int getPageOrientation() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }
}
