package cn.timeface.circle.baby.views;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Display;
import android.widget.RelativeLayout;

/**
 * Created by Administrator on 2016/7/1.
 */
public class InputMethodRelative extends RelativeLayout{
        private int width;
        protected OnSizeChangedListener onSizeChangedListener;
        private boolean sizeChanged  = false; //变化的标志
        private int height;
        private int screenWidth; //屏幕宽度
        private int screenHeight; //屏幕高度

        public InputMethodRelative(Context paramContext,
                                         AttributeSet paramAttributeSet) {
            super(paramContext, paramAttributeSet);
            Display localDisplay = ((Activity) paramContext).getWindowManager()
                    .getDefaultDisplay();
            this.screenWidth = localDisplay.getWidth() ;
            this.screenHeight = localDisplay.getHeight();
        }

        public InputMethodRelative(Context paramContext,
                                         AttributeSet paramAttributeSet, int paramInt) {
            super(paramContext, paramAttributeSet, paramInt);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            this.width = widthMeasureSpec;
            this.height = heightMeasureSpec;
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }

        @Override
        public void onSizeChanged(int w, int h, int oldw,
                                  int oldh) {
            //监听不为空、宽度不变、当前高度与历史高度不为0
            if ((this.onSizeChangedListener!= null) && (w == oldw) && (oldw != 0)
                    && (oldh != 0)) {
                if ((h >= oldh)
                        || (Math.abs(h - oldh) <= screenHeight / 4)) {
                    if ((h <= oldh)
                            || (Math.abs(h - oldh) <=  screenHeight / 4))
                        return;
                    this.sizeChanged  = false;
                } else {
                    this.sizeChanged  = true;
                }
                this.onSizeChangedListener.onSizeChange(this.sizeChanged ,oldh, h);
                measure(width - w + getWidth(), height - h + getHeight());
            }
        }
        /**
         * 设置监听事件
         * @param paramonSizeChangedListenner
         */
        public void setOnSizeChangedListenner(OnSizeChangedListener paramonSizeChangedListenner) {
            this.onSizeChangedListener = paramonSizeChangedListenner;
        }
        /**
         * 大小改变的内部接口
         * @author junjun
         *
         */
        public interface OnSizeChangedListener {
            void onSizeChange(boolean paramBoolean, int w,int h);
        }
}
