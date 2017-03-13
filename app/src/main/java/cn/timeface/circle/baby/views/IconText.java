package cn.timeface.circle.baby.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.AttributeSet;
import cn.timeface.circle.baby.R;

/**
 * display text use icon font
 * <p>
 * Created by JieGuo on 16/8/24.
 */
public class IconText extends AppCompatTextView {

    private String preText = "";
    private String subText = "";

    public IconText(Context context) {
        super(context, null);
        init(context, null, 0, 0);
    }

    public IconText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }

    public IconText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        Typeface iconfont = Typeface.createFromAsset(
                getContext().getAssets()
                , "iconfont/iconfont.ttf"
        );
        setTypeface(iconfont);

        if (attrs != null) {
            final TypedArray a = context.obtainStyledAttributes(
                    attrs, R.styleable.IconText, defStyleAttr, defStyleRes);

            if (a.hasValue(R.styleable.IconText_preText)) {
                preText = a.getString(R.styleable.IconText_preText);
            }

            if (a.hasValue(R.styleable.IconText_subText)) {
                subText = a.getString(R.styleable.IconText_subText);
            }
            a.recycle();
        }

        setText(getText());
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(formatText(text), type);
    }

    private String formatText(CharSequence text) {
        String formatString = "";
        if (!TextUtils.isEmpty(preText)) {
            formatString = preText + text;
        } else {
            formatString = text.toString();
        }
        if (!TextUtils.isEmpty(subText)) {
            formatString += subText;
        }
        return formatString;
    }
}
