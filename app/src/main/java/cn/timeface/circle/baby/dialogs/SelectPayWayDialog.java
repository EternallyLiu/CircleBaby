package cn.timeface.circle.baby.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.RadioButton;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.constants.TypeConstant;

/**
 * @author SUN
 * @from 2014/12/23
 * @TODO
 */
public class SelectPayWayDialog extends DialogFragment {
    @Bind(R.id.rb_zhifb)
    RadioButton mZhifub;
    @Bind(R.id.rb_wexin)
    RadioButton mWexin;
    @Bind(R.id.rb_e)
    RadioButton mEPay;

    private ClickListener listener;
    private int from = 0;

    public SelectPayWayDialog(int from) {
        Bundle bundle = new Bundle();
        bundle.putInt("from", from);
        setArguments(bundle);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        View view = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.dialog_select_pay_way, null);
        ButterKnife.bind(this, view);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);
        from = getArguments().getInt("from", 0);
        if (from != 0) {
            mZhifub.setVisibility(View.GONE);
            mWexin.setVisibility(View.GONE);
        }

        if(from == TypeConstant.FROM_EVENT){
            mZhifub.setVisibility(View.VISIBLE);
            mWexin.setVisibility(View.VISIBLE);
            mEPay.setVisibility(View.GONE);
        }
        return dialog;
    }

    public void setClickListener(ClickListener listener) {
        this.listener = listener;
    }

    @OnClick({R.id.btn_ok, R.id.btn_cancel})
    public void btnClick(View view) {
        switch (view.getId()) {
            case R.id.btn_ok:
                if (listener != null)
                    listener.okClick(mZhifub.isChecked() ? 1 : (mWexin.isChecked() ? 2 : 3));
                break;

            case R.id.btn_cancel:
                if (listener != null) listener.cancelClick();
                break;
        }
    }

    public interface ClickListener {
        /**
         * 确定
         *
         * @param payType 1：支付宝 2：微信支付 3:翼支付
         */
        public void okClick(int payType);

        public void cancelClick();
    }
}
