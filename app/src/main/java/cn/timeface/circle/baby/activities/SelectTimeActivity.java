package cn.timeface.circle.baby.activities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.support.utils.DateUtil;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.Remember;
import cn.timeface.circle.baby.support.utils.ToastUtil;

public class SelectTimeActivity extends BaseAppCompatActivity implements View.OnClickListener {


    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind({R.id.cb_time_now, R.id.cb_time_shot, R.id.cb_time_set})
    CheckBox[] checkBoxs;
    @Bind(R.id.ll_time_now)
    RelativeLayout llTimeNow;
    @Bind(R.id.ll_time_shot)
    RelativeLayout llTimeShot;
    @Bind(R.id.ll_time_set)
    RelativeLayout llTimeSet;
    @Bind(R.id.tv_time_shot)
    TextView tvTimeShot;
    @Bind(R.id.tv_time_set)
    TextView tvTimeSet;
    @Bind(R.id.tv_time_now)
    TextView tvTimeNow;
    private String time;
    private String time_shot;

    public static void open(Context context) {
        Intent intent = new Intent(context, SelectTimeActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selecttime);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        time_shot = getIntent().getStringExtra("time_shot");
        String time = getIntent().getStringExtra("time_now");

        llTimeNow.setOnClickListener(this);
        llTimeShot.setOnClickListener(this);
        llTimeSet.setOnClickListener(this);

        tvTimeNow.setText(DateUtil.getYear2(System.currentTimeMillis()));
        tvTimeShot.setText(TextUtils.isEmpty(time_shot.trim()) ? time : time_shot);
        tvTimeSet.setText(time);

        if (time.equals(tvTimeNow.getText().toString())) {
            setChecked(0);
        } else if (time.equals(tvTimeShot.getText().toString())) {
            setChecked(1);
        } else if (time.equals(tvTimeSet.getText().toString())) {
            setChecked(2);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_time_now:
                setChecked(0);
                time = tvTimeNow.getText().toString();

                Intent intent = new Intent();
                intent.putExtra("time", time);
                setResult(RESULT_OK, intent);
                finish();

                break;
            case R.id.ll_time_shot:
                setChecked(1);
                time = tvTimeShot.getText().toString();

                Intent intent1 = new Intent();
                intent1.putExtra("time", time);
                setResult(RESULT_OK, intent1);
                finish();
                break;
            case R.id.ll_time_set:
                setChecked(2);
                Calendar cal = Calendar.getInstance();
                DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        monthOfYear = monthOfYear + 1;
                        String m = String.valueOf(monthOfYear);
                        String d = String.valueOf(dayOfMonth);
                        if (m.length() == 1) {
                            m = "0" + m;
                        }
                        if (d.length() == 1) {
                            d = "0" + d;
                        }
                        time = year + "-" + m + "-" + d;
                        tvTimeSet.setText(time);

                        if (DateUtil.getTime(time, "yyyy.MM.dd") > System.currentTimeMillis()) {
                            ToastUtil.showToast("选择的时间不能超过当前时间");
                            return;
                        }
                        if (DateUtil.getTime(time, "yyyy.MM.dd") < FastData.getBabyBithday()) {
                            ToastUtil.showToast("您选择的时间在宝宝生日之前，时光列表中将看不到哦~");
                        }

                        Intent intent = new Intent();
                        intent.putExtra("time", time);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
                dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                dialog.show();
                break;
        }
    }

    public void setChecked(int index) {
        Remember.putInt("setChecked", index);

        for (CheckBox cb : checkBoxs) {
            cb.setVisibility(View.INVISIBLE);
            cb.setChecked(false);
        }
        checkBoxs[index].setVisibility(View.VISIBLE);
        checkBoxs[index].setChecked(true);
    }
}
