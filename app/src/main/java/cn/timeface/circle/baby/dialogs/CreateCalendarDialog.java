package cn.timeface.circle.baby.dialogs;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.jakewharton.rxbinding.view.RxView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.ui.calendar.CalendarActivity;
import cn.timeface.common.utils.DeviceUtil;


/**
 * Created by JieGuo on 16/10/9.
 */

public class CreateCalendarDialog extends DialogFragment {

    private static final String TAG = "CreateCalendarDialog";

    @Bind(R.id.rg_type)
    RadioGroup rgType;
    @Bind(R.id.btn_create_now)
    AppCompatButton btnCreateNow;
    @Bind(R.id.rb_horizontal)
    RadioButton rbHorizontal;
    @Bind(R.id.rb_vertical)
    RadioButton rbVertical;
    @Bind(R.id.ic_close)
    ImageView icClose;

    private int type = 1;

    public static CreateCalendarDialog newInstance() {
        return new CreateCalendarDialog();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        int screenW = DeviceUtil.getScreenWidth(getActivity());
        getDialog().getWindow().setLayout((int) (screenW * 0.85), WindowManager.LayoutParams.WRAP_CONTENT);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.dialog_create_calendar, container, false);
        ButterKnife.bind(this, contentView);
        bindEvents();
        return contentView;
    }

    private void bindEvents() {
//        RxView.clicks(btnCreateNow).subscribe(aVoid -> {
//            CalendarActivity.open(getActivity(), type);
//            dismiss();
//            Log.v(TAG, "currentType : " + rgType.getCheckedRadioButtonId());
//        }, throwable -> {
//            Log.e(TAG, "error", throwable);
//        });

        btnCreateNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalendarActivity.open(getActivity(), type);
                dismiss();
            }
        });

        RxView.clicks(rbHorizontal).subscribe(aVoid -> {
            rbHorizontal.setChecked(true);
            rbVertical.setChecked(false);
            type = 1;
        }, throwable -> {
            Log.e(TAG, "error", throwable);
        });

        RxView.clicks(rbVertical).subscribe(aVoid -> {
            rbHorizontal.setChecked(false);
            rbVertical.setChecked(true);
            type = 2;
        }, throwable -> {
            Log.e(TAG, "error", throwable);
        });

        RxView.clicks(icClose).subscribe(aVoid -> {
            dismiss();
        }, throwable -> {

            Log.e(TAG, "error", throwable);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

}
