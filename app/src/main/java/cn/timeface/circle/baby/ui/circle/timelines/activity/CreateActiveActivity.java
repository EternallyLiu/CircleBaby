package cn.timeface.circle.baby.ui.circle.timelines.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.support.utils.Utils;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.circle.timelines.events.ActiveSelectEvent;
import cn.timeface.circle.baby.ui.circle.timelines.views.InputListenerEditText;
import cn.timeface.circle.baby.ui.timelines.Utils.LogUtil;
import cn.timeface.circle.baby.ui.timelines.Utils.SpannableUtils;

/**
 * author : wangshuai Created on 2017/3/16
 * email : wangs1992321@gmail.com
 */
public class CreateActiveActivity extends BaseAppCompatActivity implements InputListenerEditText.InputCallBack {

    private static final int MAX_INPUT_LENGHT = 8;

    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.topBar)
    Toolbar toolbar;
    @Bind(R.id.et_input)
    InputListenerEditText etInput;
    @Bind(R.id.tv_text_count)
    TextView tvTextCount;
    @Bind(R.id.submit)
    Button submit;

    public static void open(Context context) {
        context.startActivity(new Intent(context, CreateActiveActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_active);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        title.setText(R.string.activity_create_active_title);
        etInput.setInputCallBack(this);
    }

    @Override
    protected void onDestroy() {
        ButterKnife.unbind(this);
        super.onDestroy();
    }

    @OnClick(R.id.submit)
    public void onClick() {
        String albumName = etInput.getText().toString();
        if (albumName != null) albumName = albumName.trim();
        if (TextUtils.isEmpty(albumName)) {
            ToastUtil.showToast(this, getString(R.string.input_content_cannot_null));
            return;
        } else if (Utils.getByteSize(albumName) > MAX_INPUT_LENGHT * 2) {
            ToastUtil.showToast(this, "对不起！您输入的活动相册名称太长");
            return;
        }
        addSubscription(apiService.createActive(albumName, FastData.getCircleId())
                .compose(SchedulersCompat.applyIoSchedulers())
                .subscribe(createActiveResponse -> {
                    if (createActiveResponse.success()) {
                        EventBus.getDefault().post(new ActiveSelectEvent(createActiveResponse.getActivityAlbum()));
                        finish();
                    } else {
                        ToastUtil.showToast(this, createActiveResponse.getInfo());
                    }
                }, throwable -> LogUtil.showError(throwable)));
    }

    @Override
    public void callBack(EditText view, String content) {
        int size = Utils.getByteSize(content);
        int count = size / 2;
        if (size % 2 != 0) count++;
        SpannableStringBuilder builder = new SpannableStringBuilder(String.format("%d / %d", count, MAX_INPUT_LENGHT));
        if (count > MAX_INPUT_LENGHT)
            builder.setSpan(SpannableUtils.getTextColor(this, R.color.sea_buckthorn), 0, String.valueOf(count).length() + 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        tvTextCount.setText(builder);
    }
}
