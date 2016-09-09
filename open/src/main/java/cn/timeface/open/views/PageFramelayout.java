package cn.timeface.open.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import cn.timeface.open.managers.interfaces.IChangeFocusPageListener;

/**
 * Created by zhsheng on 2016/7/14.
 */
public class PageFrameLayout extends FrameLayout {

    private int currentPage = 1;
    public static final int LEFT = 1;
    public static final int RIGHT = 2;

    IChangeFocusPageListener changeFocusPageListener;

    View focusView;
    int marginLeft = 0;

    public PageFrameLayout(Context context) {
        super(context);
    }

    public PageFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PageFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setFocusView(View focusView, int marginLeft) {
        this.marginLeft = marginLeft;
        this.focusView = focusView;
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

            if (changeFocusPageListener != null &&
                    (evX > measuredWidth / 2 ? RIGHT : LEFT) != currentPage) {
                currentPage = evX > measuredWidth / 2 ? RIGHT : LEFT;
                changeFocusPageListener.onChangeFocusPage(currentPage);
            }

            if (evX > measuredWidth / 2) {
                currentPage = RIGHT;
                if (focusView != null) {
                    focusView.setTranslationX(marginLeft);
                }
            } else {
                currentPage = LEFT;
                if (focusView != null) {
                    focusView.setTranslationX(0);
                }
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

    public int getCurrentPage() {
        return currentPage;
    }

    public void setChangeFocusPageListener(IChangeFocusPageListener changeFocusPageListener) {
        this.changeFocusPageListener = changeFocusPageListener;
    }
}
