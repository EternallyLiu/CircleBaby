package cn.timeface.circle.baby.views.dialog;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.api.models.objs.MineBookObj;

/**
 * Created by zhsheng on 2016/5/13.
 */
public class LittleWindow extends PopupWindow implements View.OnClickListener {

//    private final TextView tvShare;
    private final TextView tvDel;
    private final int[] mLocation = new int[2];
    private final View rootView;
    private ItemClickListener listener;
    private MineBookObj bookObj;

    public LittleWindow(Context context) {
        super(context);
        setFocusable(true);
        setTouchable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new ColorDrawable());
        setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        rootView = LayoutInflater.from(context).inflate(R.layout.layout_little_popu, null);
        setContentView(rootView);
//        tvShare = (TextView) rootView.findViewById(R.id.share);
//        tvShare.setOnClickListener(this);
        tvDel = (TextView) rootView.findViewById(R.id.del);
        tvDel.setOnClickListener(this);
    }

    /**
     * 以某个控件为锚点显示
     *
     * @param anchor
     */
    public void show(final View anchor) {
        anchor.getLocationOnScreen(mLocation);//参照控件的坐标
        Rect anchorRect = new Rect(mLocation[0], mLocation[1], mLocation[0] + anchor.getWidth(), mLocation[1] + anchor.getHeight());
        rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        rootView.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int rootWidth = rootView.getMeasuredWidth();
        int xPos = anchorRect.left - (rootWidth - anchor.getWidth());
        int yPos = anchorRect.bottom;
        showAtLocation(anchor, Gravity.NO_GRAVITY, xPos, yPos);
    }

    public void setContentData(MineBookObj bookObj) {
        this.bookObj = bookObj;
    }

    @Override
    public void onClick(View v) {
        dismiss();
        if (listener != null) listener.onClickItem(v.getId(), bookObj);
    }

    public interface ItemClickListener {
        void onClickItem(int id, MineBookObj bookObj);
    }

    public void setOnClickItemListener(ItemClickListener listener) {
        this.listener = listener;
    }
}
