package cn.timeface.circle.baby.activities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.api.models.objs.Milestone;
import cn.timeface.circle.baby.utils.DateUtil;
import cn.timeface.circle.baby.utils.Remember;

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

        String time = getIntent().getStringExtra("time");
        tvTimeShot.setText(time);

        llTimeNow.setOnClickListener(this);
        llTimeShot.setOnClickListener(this);
        llTimeSet.setOnClickListener(this);

        tvTimeNow.setText(DateUtil.getYear2(System.currentTimeMillis()));
        setChecked(Remember.getInt("setChecked", 0));
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_time_now:
                setChecked(0);
                time = tvTimeNow.getText().toString();

                Intent intent = new Intent();
                intent.putExtra("time",time);
                setResult(RESULT_OK, intent);
                finish();

                break;
            case R.id.ll_time_shot:
                setChecked(1);
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        monthOfYear = monthOfYear + 1;
                        tvTimeShot.setText(year + "." + monthOfYear + "." + dayOfMonth);
                        time = tvTimeShot.getText().toString();

                        Intent intent = new Intent();
                        intent.putExtra("time",time);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                dialog.show();
                break;
            case R.id.ll_time_set:
                setChecked(2);
                Calendar cal = Calendar.getInstance();
                new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        monthOfYear = monthOfYear + 1;
                        tvTimeSet.setText(year + "." + monthOfYear + "." + dayOfMonth);
                        time = tvTimeSet.getText().toString();

                        Intent intent = new Intent();
                        intent.putExtra("time",time);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
                break;
        }
    }

    public void setChecked(int index) {
        Remember.putInt("setChecked",index);

        for (CheckBox cb : checkBoxs) {
            cb.setVisibility(View.INVISIBLE);
            cb.setChecked(false);
        }
        checkBoxs[index].setVisibility(View.VISIBLE);
        checkBoxs[index].setChecked(true);
    }
}
