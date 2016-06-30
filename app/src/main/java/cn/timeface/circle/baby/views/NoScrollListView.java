package cn.timeface.circle.baby.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * @author LiuCongshan
 * @from 2014-5-5上午11:20:03
 * @TODO 无上下滚动的ListView，可内嵌到ScrollView内
 * 注意在使用中，height 设置为 wrap_content
 */
public class NoScrollListView extends ListView {

    public NoScrollListView(Context context) {
        super(context);
    }

    public NoScrollListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        if (w == 0 && h == 0) {
//            ViewTreeObserver viewTreeObserver = getViewTreeObserver();
//            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                @Override
//                public void onGlobalLayout() {
//                    getViewTreeObserver().removeGlobalOnLayoutListener(this);
//                    w = getWidth();
//                    h = getHeight();
////                    Log.i("------->", "onGlobalLayout-->" + "width:" + w + "height:" + h);
//                }
//            });

        int heightSpec;
        if (getLayoutParams().height == LayoutParams.WRAP_CONTENT) {
            heightSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                    MeasureSpec.AT_MOST);
        } else {
            // Any other height should be respected as is.
            heightSpec = heightMeasureSpec;
        }
        super.onMeasure(widthMeasureSpec, heightSpec);
//        } else {
//            setMeasuredDimension(w, h);
//        }
//        Log.i("------->", "width:" + w + "height:" + h);
    }
}
