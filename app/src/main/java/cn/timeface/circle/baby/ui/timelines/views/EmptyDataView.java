package cn.timeface.circle.baby.ui.timelines.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.timeface.circle.baby.R;

/**
 * author : wangshuai Created on 2017/1/23
 * email : wangs1992321@gmail.com
 */
public class EmptyDataView extends LinearLayout implements View.OnClickListener {

    private ImageView emptyIcon;
    private TextView errorTitle;
    private TextView errorRetry;

    private boolean isRetry = true;
    private String errorText = null;
    private String errorRetryText = null;

    public boolean isRetry() {
        return isRetry;
    }

    public void setRetry(boolean retry) {
        isRetry = retry;
        errorRetry.setVisibility(isRetry ? View.VISIBLE : View.GONE);
    }

    public String getErrorText() {
        return errorText;
    }

    public void setErrorText(String errorText) {
        this.errorText = errorText;
        errorTitle.setText(errorText);
    }

    public String getErrorRetryText() {
        return errorRetryText;
    }

    public void setErrorRetryText(String errorRetryText) {
        this.errorRetryText = errorRetryText;
        errorRetry.setText(errorRetryText);
    }

    public EmptyCallBack getEmptyCallBack() {
        return emptyCallBack;
    }

    public void setEmptyCallBack(EmptyCallBack emptyCallBack) {
        this.emptyCallBack = emptyCallBack;
    }

    public Drawable getErrorDrawable() {
        return errorDrawable;
    }

    public void setErrorDrawable(Drawable errorDrawable) {
        this.errorDrawable = errorDrawable;
        emptyIcon.setImageDrawable(errorDrawable);
    }

    public void setErrorDrawable(@DrawableRes int errorDrawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.errorDrawable = getContext().getDrawable(errorDrawable);
        } else {
            this.errorDrawable = getContext().getResources().getDrawable(errorDrawable);
        }
        emptyIcon.setImageDrawable(this.errorDrawable);
    }


    private EmptyCallBack emptyCallBack;

    private Drawable errorDrawable;

    public EmptyDataView(Context context) {
        this(context, null);
    }

    public EmptyDataView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EmptyDataView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs);
        init();
    }

    private void initAttrs(AttributeSet attributeSet) {
        if (attributeSet == null)
            return;
        TypedArray typedArray = getContext().obtainStyledAttributes(attributeSet, R.styleable.EmptyDataView);
        errorDrawable = typedArray.getDrawable(R.styleable.EmptyDataView_errorDrawable);
        errorText = typedArray.getString(R.styleable.EmptyDataView_errorText);
        errorRetryText = typedArray.getString(R.styleable.EmptyDataView_errorRetryText);
        isRetry = typedArray.getBoolean(R.styleable.EmptyDataView_isRetry, true);
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.empty_data_layout, this);
        emptyIcon = (ImageView) findViewById(R.id.icon);
        errorRetry = (TextView) findViewById(R.id.error_retry);
        errorTitle = (TextView) findViewById(R.id.error_title);
        if (!TextUtils.isEmpty(errorText))
            errorTitle.setText(errorText);
        if (!TextUtils.isEmpty(errorRetryText))
            errorRetry.setText(errorRetryText);
        errorRetry.setVisibility(isRetry ? View.VISIBLE : View.GONE);
        if (errorDrawable != null)
            emptyIcon.setImageDrawable(errorDrawable);
        errorRetry.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.error_retry:
                if (getEmptyCallBack() != null)
                    getEmptyCallBack().retry();
                break;
        }
    }

    public interface EmptyCallBack {
        public void retry();
    }

}
