package cn.timeface.circle.baby.ui.images.views;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.BaseInputConnection;

import cn.timeface.circle.baby.ui.timelines.Utils.LogUtil;

/**
 * author : wangshuai Created on 2017/1/16
 * email : wangs1992321@gmail.com
 */
public class MyInputConnection extends BaseInputConnection {

    private InputMethodCallBack inputListener = null;

    public MyInputConnection(View targetView, boolean fullEditor) {
        super(targetView, fullEditor);
    }

    private StringBuilder builder = new StringBuilder();

    @Override
    public boolean commitText(CharSequence text, int newCursorPosition) {
        LogUtil.showLog(text + "---" + newCursorPosition);
        builder.append(text);
        if (getInputListener() != null)
            getInputListener().inoutText(builder.toString());
        return true;

    }

    @Override
    public boolean sendKeyEvent(KeyEvent event) {
        return super.sendKeyEvent(event);
    }

    @Override
    public boolean deleteSurroundingText(int beforeLength, int afterLength) {
        return super.deleteSurroundingText(beforeLength, afterLength);
    }

    @Override
    public boolean finishComposingText() {
        return super.finishComposingText();
    }

    public InputMethodCallBack getInputListener() {
        return inputListener;
    }

    public void setInputListener(InputMethodCallBack inputListener) {
        this.inputListener = inputListener;
    }

    public interface InputMethodCallBack {
        public void inoutText(CharSequence text);
    }

}
