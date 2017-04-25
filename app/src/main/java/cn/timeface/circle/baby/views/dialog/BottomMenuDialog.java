package cn.timeface.circle.baby.views.dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.timeface.circle.baby.R;

/**
 * Created by zhsheng on 2016/6/16.
 */
public class BottomMenuDialog extends DialogFragment {

    @Bind(R.id.rl_add_content)
    RelativeLayout rlAddContent;

    @Bind(R.id.rl_edit_state)
    RelativeLayout rlEditState;

    @Bind(R.id.rl_book_pre)
    RelativeLayout rlBookPre;

    @Bind(R.id.rl_delete_album)
    RelativeLayout rlDeleteAlbum;
    @Bind(R.id.cancel)
    Button cancel;
    private OnMenuClickListener menuClickListener;

    public static BottomMenuDialog getInstance(int type) {
        Bundle bundle=new Bundle();
        bundle.putInt("type",type);
        BottomMenuDialog dialog=new BottomMenuDialog();
        dialog.setArguments(bundle);
        return dialog;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_from_bottom, container, false);
        ButterKnife.bind(this, view);
        Bundle bundle=getArguments();
        setType(bundle.getInt("type"));
        return view;
    }

    @OnClick({R.id.rl_add_content, R.id.rl_edit_state, R.id.rl_book_pre, R.id.rl_delete_album, R.id.cancel})
    public void clickBottomDialog(View view) {
        dismiss();
        if (menuClickListener != null) menuClickListener.clickMenu(view.getId());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        setCancelable(true);
        Window window = getDialog().getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.DialogAnimation);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    private void setType(int type){
        rlAddContent.setVisibility(View.GONE);
        switch (type){
            case 0:
                break;
            case 1:
                rlEditState.setVisibility(View.GONE);
                rlDeleteAlbum.setVisibility(View.GONE);
                break;
            case 2:
                rlBookPre.setVisibility(View.GONE);
                break;
        }
    }

    public void setOnMenuClick(OnMenuClickListener menuClickListener) {
        this.menuClickListener = menuClickListener;
    }

    public interface OnMenuClickListener {
        void clickMenu(@IdRes int resId);
    }
}
