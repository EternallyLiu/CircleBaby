package cn.timeface.circle.baby.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;

/**
 * @author SunYanwei (QQ:707831837)
 * @from 2014年9月5日上午9:58:26
 * @TODO 底部弹出dialogfragment
 */
public class PopBottomDialog extends DialogFragment {
    @Bind(R.id.dialog_item1_tv)
    TextView mItem1;
    @Bind(R.id.dialog_item2_tv)
    TextView mItem2;
    @Bind(R.id.dialog_item3_tv)
    TextView mItem3;

    private ClickListener mListener;

    public static PopBottomDialog newInstance(String item1, String item2, String item3) {
        PopBottomDialog dialog = new PopBottomDialog();
        Bundle b = new Bundle();
        b.putString("item1", item1);
        b.putString("item2", item2);
        b.putString("item3", item3);
        dialog.setArguments(b);

        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity(), R.style.DialogStyle);
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.fragment_bottom_pop, null, false);
        ButterKnife.bind(this, view);
        mItem1.setText(getArguments().getString("item1"));
        mItem2.setText(getArguments().getString("item2"));
        mItem3.setText(getArguments().getString("item3"));

        mItem1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                mListener.onItem1Click();
            }
        });

        mItem2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                mListener.onItem2Click();
            }
        });

        mItem3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                mListener.onItem3Click();
            }
        });

        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        window.setWindowAnimations(R.style.DialogAnimation);
        WindowManager.LayoutParams wl = window.getAttributes();
//        wl.x = 0;
        wl.width = LayoutParams.MATCH_PARENT;
//        wl.x = getActivity().getWindowManager().getDefaultDisplay().getWidth();
        wl.y = getActivity().getWindowManager().getDefaultDisplay().getHeight();
        dialog.onWindowAttributesChanged(wl);

        return dialog;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        mListener.onItem3Click();
    }

    public void setClickListener(ClickListener listener) {
        this.mListener = listener;
    }

//    @Override
//    public void dismiss()
//    {
//        // TODO Auto-generated method stub
//        super.dismiss();
//    }


    public interface ClickListener {
        public void onItem1Click();

        public void onItem2Click();

        public void onItem3Click();
    }

}