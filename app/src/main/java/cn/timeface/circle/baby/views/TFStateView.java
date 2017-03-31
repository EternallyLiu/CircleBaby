package cn.timeface.circle.baby.views;

import android.content.Context;
import android.util.AttributeSet;

import java.net.UnknownHostException;

import cn.timeface.circle.baby.R;
import retrofit2.adapter.rxjava.HttpException;

/**
 * @author wxw
 * @from 2015/12/31
 * @TODO
 */
public class TFStateView extends StateView {

    public TFStateView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void showException(Throwable throwable) {
        if (throwable != null) {
            if (throwable instanceof UnknownHostException) {
                setState(ErrorViewContent.getContentObj(HttpStatusCodes.NO_CONNECT));
            } else if (throwable instanceof HttpException) {
                setState(ErrorViewContent.getContentObj(((HttpException) throwable).code()));
            } else {
                setState(ErrorViewContent.getContentObj(HttpStatusCodes.NO_CONNECT));
            }
            setImageResource(R.drawable.net_empty);
        }
    }

    public void loading() {
        setState(ErrorViewContent.getContentObj(HttpStatusCodes.LOADING));
    }

    public void finish() {
        setState(ErrorViewContent.getContentObj(HttpStatusCodes.FINISH));
    }

    public void empty() {
        setState(ErrorViewContent.getContentObj(HttpStatusCodes.NO_MORE_DATA));
    }

    public void empty(int resId) {
        setState(new ErrorViewContent(HttpStatusCodes.NO_MORE_DATA, R.drawable.nodata, resId, null, 0, 0));
    }

    public void empty(int resId, int drawableId) {
        setState(new ErrorViewContent(HttpStatusCodes.NO_MORE_DATA, drawableId, resId, null, 0, 0));
    }

}
