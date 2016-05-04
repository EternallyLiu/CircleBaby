package cn.timeface.circle.baby.views;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import cn.timeface.circle.baby.R;


/**
 * Created by zhsheng on 2015/11/16.
 */
public class MainCircleMenuPopup extends PopupWindow implements View.OnClickListener, View.OnTouchListener {
    private Context context;
    private LinearLayout ll_main_menu;

    public MainCircleMenuPopup(Context ctx) {
        this.context = ctx;
        init();
    }

    private void init() {
        setFocusable(true);
        setTouchable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new ColorDrawable());
        setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        View menuView = View.inflate(context, R.layout.popu_main_circle_menu, null);
        ll_main_menu = (LinearLayout) menuView.findViewById(R.id.ll_main_menu);
        TextView tvPhoto = (TextView) menuView.findViewById(R.id.tv_photo);
        tvPhoto.setOnClickListener(this);
        tvPhoto.setOnTouchListener(this);
        TextView tvVideo = (TextView) menuView.findViewById(R.id.tv_video);
        tvVideo.setOnClickListener(this);
        tvVideo.setOnTouchListener(this);
        TextView tvDiary = (TextView) menuView.findViewById(R.id.tv_diary);
        tvDiary.setOnClickListener(this);
        tvDiary.setOnTouchListener(this);
        TextView tvCard = (TextView) menuView.findViewById(R.id.tv_card);
        tvCard.setOnClickListener(this);
        tvCard.setOnTouchListener(this);
        setAnimationStyle(R.style.pop_menu_anim_style);
        setContentView(menuView);
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);
    }

    @Override
    public void onClick(View v) {
        dismiss();
        if (menuClickListener != null) {
            menuClickListener.clickMenu(v);
        }
    }

    private OnMenuClickListener menuClickListener;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        TextView textView = (TextView) v;
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            textView.setTextColor(context.getResources().getColor(R.color.main_blue));
            int resId = 0;
            switch (v.getId()) {
                case R.id.tv_photo:
                    resId = R.drawable.ic_menu_batch_import_p;
                    break;
                case R.id.tv_video:
                    resId = R.drawable.ic_menu_scanner_p;
                    break;
                case R.id.tv_diary:
                    resId = R.drawable.ic_menu_album_p;
                    break;
                case R.id.tv_card:
                    resId = R.drawable.ic_menu_input_text_p;
                    break;
            }
            textView.setCompoundDrawablesWithIntrinsicBounds(null, getDrawable(resId), null, null);
        }
        if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
            initViewState(textView);
        }
        return false;
    }

    private void initViewState(TextView textView) {
        textView.setTextColor(context.getResources().getColor(R.color.white));
        int resId = 0;
        switch (textView.getId()) {
            case R.id.tv_photo:
                resId = R.drawable.ic_menu_batch_import_n;
                break;
            case R.id.tv_video:
                resId = R.drawable.ic_menu_scanner_n;
                break;
            case R.id.tv_diary:
                resId = R.drawable.ic_menu_album_n;
                break;
            case R.id.tv_card:
                resId = R.drawable.ic_menu_input_text_n;
                break;
        }
        textView.setCompoundDrawablesWithIntrinsicBounds(null, getDrawable(resId), null, null);
    }

    private Drawable getDrawable(int resId) {
        return context.getResources().getDrawable(resId);
    }

    public interface OnMenuClickListener {
        void clickMenu(View view);
    }

    public void setOnMenuClickListener(OnMenuClickListener onMenuClickListener) {
        this.menuClickListener = onMenuClickListener;
    }
}
