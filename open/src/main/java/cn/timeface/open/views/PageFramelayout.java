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

    public static final int LEFT = 1;
    public static final int RIGHT = 2;
    private int currentPageSide = LEFT;

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
                    (evX > measuredWidth / 2 ? RIGHT : LEFT) != currentPageSide) {
                currentPageSide = evX > measuredWidth / 2 ? RIGHT : LEFT;
                changeFocusPageListener.onChangeFocusPage(currentPageSide);
            }

            if (evX > measuredWidth / 2) {
                currentPageSide = RIGHT;
                if (focusView != null) {
                    focusView.setTranslationX(marginLeft);
                }
            } else {
                currentPageSide = LEFT;
                if (focusView != null) {
                    focusView.setTranslationX(0);
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    public void setCurrentPageSide(int currentPageSide) {
        this.currentPageSide = currentPageSide;
        if (currentPageSide == RIGHT && focusView != null) {
            focusView.setTranslationX(marginLeft);
        } else if (currentPageSide == LEFT && focusView != null) {
            focusView.setTranslationX(0);
        }
    }

    public int getCurrentPageSide() {
        return currentPageSide;
    }

    public void setChangeFocusPageListener(IChangeFocusPageListener changeFocusPageListener) {
        this.changeFocusPageListener = changeFocusPageListener;
    }
}
