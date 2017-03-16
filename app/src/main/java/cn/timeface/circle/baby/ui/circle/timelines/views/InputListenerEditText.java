package cn.timeface.circle.baby.ui.circle.timelines.views;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;

import cn.timeface.circle.baby.ui.timelines.Utils.LogUtil;

/**
 * author : wangshuai Created on 2017/3/16
 * email : wangs1992321@gmail.com
 */
public class InputListenerEditText extends EditText implements TextWatcher {
    public InputListenerEditText(Context context) {
        super(context);
        addTextChangedListener(this);
    }

    public InputListenerEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        addTextChangedListener(this);
    }

    public InputListenerEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        addTextChangedListener(this);
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        LogUtil.showLog("s==="+s.toString());
        if (getInputCallBack() != null) getInputCallBack().callBack(this, s.toString());
    }

    private InputCallBack inpputCallBack;

    public InputCallBack getInputCallBack() {
        return inpputCallBack;
    }

    public void setInputCallBack(InputCallBack inpputCallBack) {
        this.inpputCallBack = inpputCallBack;
    }

    public static interface InputCallBack {
        public void callBack(EditText view, String content);
    }

}
