package com.timeface.refreshload.headfoot.impl;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.timeface.refreshload.R;
import com.timeface.refreshload.headfoot.RefreshView;
import com.timeface.refreshload.overscroll.OverScrollImpl;

/**
 * Created by linfaxin on 15/8/31.
 * Default impl of RefreshView
 */
public class DefaultRefreshView extends RefreshView {

    private static final String TAG = "DefaultRefreshView";

    private ImageView imageView;

    int[] resId = {R.drawable.ic_loading00, R.drawable.ic_loading01, R.drawable.ic_loading02, R.drawable.ic_loading03, R.drawable.ic_loading04,
            R.drawable.ic_loading05, R.drawable.ic_loading06, R.drawable.ic_loading07, R.drawable.ic_loading08,
            R.drawable.ic_loading09, R.drawable.ic_loading10, R.drawable.ic_loading11, R.drawable.ic_loading12,
            R.drawable.ic_loading13, R.drawable.ic_loading14, R.drawable.ic_loading15, R.drawable.ic_loading16,
            R.drawable.ic_loading17, R.drawable.ic_loading18, R.drawable.ic_loading19, R.drawable.ic_loading20,
            R.drawable.ic_loading21, R.drawable.ic_loading22, R.drawable.ic_loading23, R.drawable.ic_loading24,
            R.drawable.ic_loading25, R.drawable.ic_loading26, R.drawable.ic_loading27, R.drawable.ic_loading28, R.drawable.ic_loading29,
            R.drawable.ic_loading30, R.drawable.ic_loading31, R.drawable.ic_loading32, R.drawable.ic_loading33, R.drawable.ic_loading34,
            R.drawable.ic_loading35, R.drawable.ic_loading36, R.drawable.ic_loading37, R.drawable.ic_loading38,
            R.drawable.ic_loading39, R.drawable.ic_loading40, R.drawable.ic_loading41, R.drawable.ic_loading42,
            R.drawable.ic_loading43, R.drawable.ic_loading44, R.drawable.ic_loading45, R.drawable.ic_loading46,
            R.drawable.ic_loading47, R.drawable.ic_loading48, R.drawable.ic_loading49, R.drawable.ic_loading50,
            R.drawable.ic_loading51, R.drawable.ic_loading52, R.drawable.ic_loading53, R.drawable.ic_loading54,
            R.drawable.ic_loading55, R.drawable.ic_loading56, R.drawable.ic_loading57, R.drawable.ic_loading58, R.drawable.ic_loading59};
    private ProgressBar progressBar;
    private int countDest;

    public DefaultRefreshView(Context context) {
        super(context);
        init();
    }

    public DefaultRefreshView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DefaultRefreshView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public DefaultRefreshView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }


    private void init() {
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER);
        setBackgroundColor(Color.parseColor("#FFF6F7F7"));
        setMinimumHeight((int) (60 * getContext().getResources().getDisplayMetrics().density));
        imageView = new ImageView(getContext());
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.width = (int) (100 * getContext().getResources().getDisplayMetrics().density);
        params.height = (int) (60 * getContext().getResources().getDisplayMetrics().density);
        addView(imageView, params);
        progressBar = new ProgressBar(getContext());
        progressBar.setIndeterminateDrawable(getResources().getDrawable(R.drawable.refresh_loading_200));
        progressBar.setIndeterminate(true);
        addView(progressBar, params);
        progressBar.setVisibility(GONE);
        countDest = OverScrollImpl.SCROLL_HEIGHT / resId.length;
    }

    @Override
    protected void onStateChange(int newState, int oldState) {
        switch (newState) {
            case STATE_NORMAL:
                progressBar.setVisibility(GONE);
                imageView.setVisibility(VISIBLE);
                break;
            case STATE_READY:

                break;
            case STATE_LOADING:
                imageView.setVisibility(GONE);
                progressBar.setVisibility(VISIBLE);
                break;
        }
    }

    @Override
    protected void onOverScroll(int overScrollDistance, boolean isInDrag) {
        super.onOverScroll(overScrollDistance, isInDrag);
        if (overScrollDistance > 0) {
            int index = overScrollDistance / countDest;
            int newDex = (int) Math.floor(index);
            imageView.setImageResource(resId[newDex]);
        }
    }
}
