package cn.timeface.circle.baby.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by JieGuo on 1/28/16.
 */
public class IconTextView extends TextView {

    private static final String TYPEFACE_FILE = "iconfont.ttf";
    private static Typeface typeface = null;

    public IconTextView(Context context) {
        super(context);
        init();
    }

    public IconTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public IconTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public IconTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    protected void init() {

        if (typeface == null) {
            typeface = Typeface.createFromAsset(getContext().getAssets(),
                    TYPEFACE_FILE);
        }
        setTypeface(typeface);
    }
}