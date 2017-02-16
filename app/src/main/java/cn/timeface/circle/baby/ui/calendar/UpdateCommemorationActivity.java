package cn.timeface.circle.baby.ui.calendar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.support.api.exception.ResultException;
import cn.timeface.circle.baby.support.mvp.bases.BasePresenterAppCompatActivity;
import cn.timeface.circle.baby.support.mvp.presentations.CalendarPresentation;

import cn.timeface.circle.baby.support.mvp.presenter.CommemorationPresenter;
import cn.timeface.circle.baby.ui.calendar.adapter.DateSpinnerAdapter;
import cn.timeface.circle.baby.ui.calendar.bean.CommemorationDataManger;

import cn.timeface.circle.baby.ui.calendar.bean.DateObj;
import cn.timeface.circle.baby.ui.calendar.events.CommemorationAddedEvent;
import cn.timeface.circle.baby.ui.calendar.events.CommemorationUpdateEvent;
import cn.timeface.circle.baby.views.dialog.TFProgressDialog;


/**
 * 更新纪念日
 * <p>
 * Created by JieGuo on 16/9/30.
 */

public class UpdateCommemorationActivity extends BasePresenterAppCompatActivity implements
        Toolbar.OnMenuItemClickListener, CalendarPresentation.CommemorationPresentation.View,
        AdapterView.OnItemSelectedListener {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.et_content)
    AppCompatEditText etContent;
    @Bind(R.id.ll_time_bar)
    LinearLayout llTimeBar;
    @Bind(R.id.spinner_date)
    AppCompatSpinner spinnerDate;

    CalendarPresentation.CommemorationPresentation.Presenter presenter;

    private DateSpinnerAdapter adapter;
    private TFProgressDialog progressDialog;
    private String calendarId = "";
    private DateObj dateObj = null;
    private String month = "1";
    private CommemorationUpdateEvent updateEvent;

    public static void open(Context context, String month) {
        Intent intent = new Intent(context, UpdateCommemorationActivity.class);
        intent.putExtra("month", month);
        context.startActivity(intent);
    }

    public static void open(Context context, DateObj dateObj) {
        Intent intent = new Intent(context, UpdateCommemorationActivity.class);
        if (dateObj != null) {
            intent.putExtra("dataObj", dateObj);
        }
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commemoration_update);
        ButterKnife.bind(this);

        dateObj = getIntent().getParcelableExtra("dataObj");

        presenter = new CommemorationPresenter(this);
        progressDialog = TFProgressDialog.getInstance("正在保存..");

        toolbar.setTitle("添加纪念日");
        toolbar.setNavigationOnClickListener(v -> finish());
        toolbar.inflateMenu(R.menu.menu_activity_publish_finish);
        toolbar.setOnMenuItemClickListener(this);


        if (dateObj != null) {
            etContent.setText(dateObj.getContent());
            updateEvent = new CommemorationUpdateEvent(dateObj.getContent(), dateObj.getDay(), dateObj.getMonth());
        } else {
            month = getIntent().getStringExtra("month");
            if (TextUtils.isEmpty(month)) {
                showToast("数据错误");
                finish();
            }
        }
        try {
            initSpinnerAdapter();

            initDateSpinner();
        } catch (Exception e) {
            Log.e(TAG, "error", e);
            showToast("数据错误");
            finish();
        }
    }

    private void initDateSpinner() {
        List<Long> dateList = presenter.getAllDayInMonth(2017, Integer.valueOf(month));

        String[] arr = new String[dateList.size()];
        for (int i = 0; i < dateList.size(); i++) {
            arr[i] = adapter.getItemString(i);
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.view_spinner_item, arr);
        arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinnerDate.setAdapter(arrayAdapter);
        spinnerDate.setPopupBackgroundResource(R.color.bg_main);
        spinnerDate.setOnItemSelectedListener(this);

        if (dateObj != null) {
            int dayIndex = Integer.valueOf(dateObj.getDay()) - 1;
            spinnerDate.setSelection(dayIndex);
        } else {
            CommemorationDataManger manger = CommemorationDataManger.getInstance();
            if (manger.getSource().containsKey(month)) {
                List<DateObj> items = manger.getSource().get(month);
                if (items.size() > 0) {
                    int index = Integer.valueOf(items.get(items.size() - 1).getDay());
                    if (index < arrayAdapter.getCount()) {
                        spinnerDate.setSelection(index, true);
                    }
                }
            }
        }
    }

    private ListAdapter initSpinnerAdapter() {
        if (adapter == null) {
            adapter = new DateSpinnerAdapter(this, new ArrayList<>());
            List<Long> dateList = presenter.getAllDayInMonth(2017, Integer.valueOf(month));
            adapter.setListData(dateList);
        }
        return adapter;
    }

    public void onSave() {

        String content = etContent.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            showToast("请输入纪念日内容");
            return;
        }

        CalendarPresentation.CommemorationParamsBuilder builder =
                new CalendarPresentation.CommemorationParamsBuilder();
        builder.day(adapter.getDay(spinnerDate.getSelectedItemPosition()));
        builder.month(adapter.getMonth(spinnerDate.getSelectedItemPosition()));
        builder.year(adapter.getYear(spinnerDate.getSelectedItemPosition()));
        builder.content(content);
        builder.calendarId(calendarId);

        progressDialog.show(getSupportFragmentManager(), "save");
        presenter.add(builder, response -> {
            progressDialog.dismiss();
            if (response.success()) {

                Map<String, String> data = builder.build();
                EventBus.getDefault().post(
                        new CommemorationAddedEvent(
                                data.get("month"),
                                data.get("day"),
                                data.get("content")
                        )
                );

                finish();
            }
        }, throwable -> {
            progressDialog.dismiss();
            showToast(throwable.getMessage());
            Log.e(TAG, "error", throwable);
        });
    }

    public void onUpdate() {
        String content = etContent.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            showToast("请输入纪念日内容");
            return;
        }

        if (dateObj == null) {
            Log.e(TAG, "error : date obj is null.");
            return;
        }

        dateObj.setContent(content);
        CalendarPresentation.CommemorationParamsBuilder builder =
                new CalendarPresentation.CommemorationParamsBuilder();

        builder.day(dateObj.getDay());
        builder.month(dateObj.getMonth());
        builder.year(dateObj.getYear());
        builder.content(content);
        builder.calendarId(calendarId);

        updateEvent.setDay(dateObj.getDay());
        updateEvent.setMonth(dateObj.getMonth());
        updateEvent.setContent(dateObj.getContent());


        progressDialog.show(getSupportFragmentManager(), "update");
        presenter.update(
                builder, updateEvent.getOldDay(), response -> {
                    progressDialog.dismiss();
                    EventBus.getDefault().post(updateEvent);
                    finish();
                }, throwable -> {
                    if (throwable instanceof ResultException) {
                        showToast(throwable.getMessage());
                    }
                    progressDialog.dismiss();
                    Log.e(TAG, "error", throwable);
                }
        );
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_complete:
                if (dateObj == null) {
                    onSave();
                } else {
                    onUpdate();
                }
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        long msec = adapter.getItem(position);

        if (dateObj != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(msec);
            dateObj.setYear(calendar.get(Calendar.YEAR) + "");
            dateObj.setMonth(String.valueOf(calendar.get(Calendar.MONTH) + 1));
            dateObj.setDay(calendar.get(Calendar.DATE) + "");
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // ignore
    }
}
