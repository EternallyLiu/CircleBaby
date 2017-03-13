package cn.timeface.circle.baby.ui.timelines.views;

import android.content.Context;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.Bind;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.views.ClearableEditText;
import cn.timeface.circle.baby.views.dialog.BaseDialog;

/**
 * author : wangshuai Created on 2017/2/13
 * email : wangs1992321@gmail.com
 */
public class LocationSearchDialog extends BaseDialog implements View.OnClickListener, TextView.OnEditorActionListener {
    ClearableEditText input;
    LinearLayout llInput;
    Button cancel;

    public LocationSearchDialog(Context context) {
        super(context);
        init();
    }

    public LocationSearchDialog(Context context, int theme) {
        super(context, theme);
        init();
    }

    public LocationSearchDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init();
    }

    private void init() {
        View mView = LayoutInflater.from(getContext()).inflate(R.layout.location_search_dialog, null);
        setContentView(mView);
        input = (ClearableEditText) mView.findViewById(R.id.input);
        cancel = (Button) mView.findViewById(R.id.cancel);
        input.setOnEditorActionListener(this);

        cancel.setOnClickListener(this);
        Window window = getWindow();
        WindowManager m = window.getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = window.getAttributes();
        p.width = d.getWidth();
        p.dimAmount = 0.1f;
        window.setAttributes(p);
        window.setGravity(Gravity.TOP);
        window.setWindowAnimations(R.style.bottom_dialog_animation);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel:
                dismiss();
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            if (TextUtils.isEmpty(input.getText().toString()))
                ToastUtil.showToast("请输入搜索关键字");
            else if (getSearchCallBack() != null) {
                getSearchCallBack().searchCall(input.getText().toString().trim());
                dismiss();
            }
        }
        return false;
    }

    private SearchCallBack searchCallBack;

    public SearchCallBack getSearchCallBack() {
        return searchCallBack;
    }

    public void setSearchCallBack(SearchCallBack searchCallBack) {
        this.searchCallBack = searchCallBack;
    }

    public interface SearchCallBack {
        public void searchCall(String text);
    }
}
