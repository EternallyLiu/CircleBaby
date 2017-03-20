package cn.timeface.circle.baby.ui.circle.timelines.adapter;

import android.content.Context;
import android.support.v7.widget.SwitchCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.support.utils.DateUtil;
import cn.timeface.circle.baby.support.utils.Utils;
import cn.timeface.circle.baby.ui.circle.bean.CircleContentObj;
import cn.timeface.circle.baby.ui.circle.bean.CircleHomeworkObj;
import cn.timeface.circle.baby.ui.circle.bean.CircleSchoolTaskObj;
import cn.timeface.circle.baby.ui.circle.bean.CircleTimelineObj;
import cn.timeface.circle.baby.ui.circle.timelines.bean.ItemObj;
import cn.timeface.circle.baby.ui.circle.timelines.views.CircleGridStaggerLookup;
import cn.timeface.circle.baby.ui.circle.timelines.views.InputListenerEditText;
import cn.timeface.circle.baby.ui.timelines.Utils.LogUtil;
import cn.timeface.circle.baby.ui.timelines.Utils.SpannableUtils;
import cn.timeface.circle.baby.ui.timelines.adapters.BaseAdapter;
import cn.timeface.circle.baby.ui.timelines.adapters.ViewHolder;
import rx.Observable;

/**
 * author : wangshuai Created on 2017/3/15
 * email : wangs1992321@gmail.com
 */
public class PublishAdapter extends BaseAdapter implements InputListenerEditText.InputCallBack {

    //item类型
    public static final int ITEM_TYPE_INPUT_CONTENT = -39999;
    public static final int ITEM_TYPE_INPUT_TITLE = -39998;
    public static final int ITEM_TYPE_MEDIA = -39997;
    public static final int ITEM_TYPE_SELECT_ACTIVE = -39996;
    public static final int ITEM_TYPE_SELECT_TIME = -39995;
    public static final int ITEM_TYPE_NOTIFY_TIMELINE = -39994;

    private CircleContentObj contentObj = null;

    //适配器功能类型
    //圈动态
    public static final int TYPE_TIMELINE = -49999;
    //圈作业
    public static final int TYPE_WORK = -49998;
    public static final int TYPE_SCHOOL = -49997;
    private int type = TYPE_TIMELINE;

    private CircleGridStaggerLookup lookup;

    public PublishAdapter(Context activity) {
        super(activity);
        setType(type);
    }

    @Override
    public int getItemCount() {
        return (getType() == TYPE_TIMELINE ? lookup.isSync() ? 5 : 4 : 2) + contentObj.getMediaList().size();

    }

    @Override
    public <T> T getItem(int position) {
        return (T) contentObj;
    }

    @Override
    public void initView(View contentView, int position) {
        LogUtil.showLog("position=====" + position + "-----" + getViewType(position));
        switch (getViewType(position)) {
            case ITEM_TYPE_INPUT_TITLE:
                doTitle(contentView, position);
                break;
            case ITEM_TYPE_INPUT_CONTENT:
                doContent(contentView, position);
                break;
            case ITEM_TYPE_SELECT_ACTIVE:
                break;
            case ITEM_TYPE_SELECT_TIME:
                doTime(contentView);
                break;
            case ITEM_TYPE_NOTIFY_TIMELINE:
                doNotifyTimeLine(contentView);
                break;
            case ITEM_TYPE_MEDIA:
                break;
        }
    }

    @Override
    public int getViewLayoutID(int viewType) {
        switch (viewType) {
            case ITEM_TYPE_INPUT_TITLE:
                return R.layout.circle_publish_input_title_layout;
            case ITEM_TYPE_INPUT_CONTENT:
                return R.layout.circle_publish_input_content_layout;
            case ITEM_TYPE_SELECT_ACTIVE:
                return R.layout.circle_publish_select_active;
            case ITEM_TYPE_SELECT_TIME:
                return R.layout.circle_publish_select_time;
            case ITEM_TYPE_NOTIFY_TIMELINE:
                return R.layout.circle_publish_notify_timeline;
            case ITEM_TYPE_MEDIA:
                return R.layout.circle_publish_media;

        }
        return 0;
    }

    private void doNotifyTimeLine(View view) {
        SwitchCompat switchCompat = ViewHolder.getView(view, R.id.swich_right);
        switchCompat.setChecked(lookup.isSync());
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                lookup.setSync(isChecked);
                ((CircleTimelineObj) contentObj).setIsSync(isChecked ? 1 : 0);
                notifyDataSetChanged();
            }
        });
    }

    private void doTime(View view){
        TextView tvTime=ViewHolder.getView(view,R.id.tv_time);
        CircleTimelineObj timelineObj= (CircleTimelineObj) contentObj;
        if (timelineObj.getRecordDate()<=0)
            timelineObj.setRecordDate(System.currentTimeMillis());
        tvTime.setText(DateUtil.formatDate("yyyy.MM.dd",timelineObj.getRecordDate()));
    }

    private void doTitle(View view, int position) {
        InputListenerEditText etInput = ViewHolder.getView(view, R.id.et_input_title);
        TextView tvTextCount = ViewHolder.getView(view, R.id.tv_text_count);
        if (!TextUtils.isEmpty(contentObj.getTitle())) {
            etInput.setText(contentObj.getTitle());
        } else {
            etInput.setText("");
            tvTextCount.setText("0 / 10");
        }
        int size = Utils.getByteSize(contentObj.getTitle());
        int count = size / 2;
        if (size % 2 != 0) count++;
        SpannableStringBuilder builder = new SpannableStringBuilder(String.format("%d / 10", count));
        if (count > 10) {
            builder.setSpan(SpannableUtils.getTextColor(context(), R.color.sea_buckthorn), 0, String.valueOf(count).length() + 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        }
        tvTextCount.setText(builder);
        etInput.setTag(R.id.recycler_item_input_tag, tvTextCount);
        etInput.setInputCallBack(this);
    }

    private void doContent(View view, int position) {
        InputListenerEditText etInput = ViewHolder.getView(view, R.id.et_input);
        TextView tvTextCount = ViewHolder.getView(view, R.id.tv_text_count);
        if (!TextUtils.isEmpty(contentObj.getContent())) {
            etInput.setText(contentObj.getContent());
        } else {
            etInput.setText("");
        }
        int size = Utils.getByteSize(contentObj.getContent());
        int count = size / 2;
        if (size % 2 != 0) count++;
        SpannableStringBuilder builder = new SpannableStringBuilder(String.format("%d / %d", count, getType() == TYPE_TIMELINE ? 200 : 600));
        if (count > (getType() == TYPE_TIMELINE ? 200 : 600)) {
            builder.setSpan(SpannableUtils.getTextColor(context(), R.color.sea_buckthorn), 0, String.valueOf(count).length() + 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        }
        tvTextCount.setText(builder);
        etInput.setTag(R.id.recycler_item_input_tag, tvTextCount);
        etInput.setInputCallBack(this);
    }

    @Override
    public int getViewType(int position) {
        switch (position) {
            case 0:
                return ITEM_TYPE_INPUT_TITLE;
            case 1:
                return ITEM_TYPE_INPUT_CONTENT;
            case 2:
                return getType() == TYPE_TIMELINE ? ITEM_TYPE_SELECT_ACTIVE : ITEM_TYPE_MEDIA;
            case 3:
                return getType() == TYPE_TIMELINE ? lookup.isSync() ? ITEM_TYPE_SELECT_TIME : ITEM_TYPE_NOTIFY_TIMELINE : ITEM_TYPE_MEDIA;
            case 4:
                return getType() == TYPE_TIMELINE ? lookup.isSync() ? ITEM_TYPE_NOTIFY_TIMELINE : ITEM_TYPE_MEDIA : ITEM_TYPE_MEDIA;
            default:
                return ITEM_TYPE_MEDIA;
        }
    }

    public CircleContentObj getContentObj() {
        return contentObj;
    }

    public void setContentObj(CircleContentObj contentObj) {
        this.contentObj = contentObj;
        if (contentObj instanceof CircleHomeworkObj) setType(TYPE_WORK);
        else if (contentObj instanceof CircleSchoolTaskObj) setType(TYPE_SCHOOL);
        else if (contentObj instanceof CircleTimelineObj) setType(TYPE_TIMELINE);
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
        if (contentObj == null)
            switch (this.type) {
                case TYPE_WORK:
                    contentObj = new CircleHomeworkObj();
                case TYPE_SCHOOL:
                    contentObj = new CircleSchoolTaskObj();
                case TYPE_TIMELINE:
                    contentObj = new CircleTimelineObj();
            }
    }

    public CircleGridStaggerLookup getLookup() {
        return lookup;
    }

    public void setLookup(CircleGridStaggerLookup lookup) {
        this.lookup = lookup;
    }

    @Override
    public void callBack(EditText view, String content) {
        switch (view.getId()) {
            case R.id.et_input_title:
                Observable.defer(() -> Observable.just(content))
                        .doOnNext(s -> contentObj.setTitle(s))
                        .filter(s -> view.getTag(R.id.recycler_item_input_tag) != null)
                        .doOnNext(s -> {
                            TextView tvTextCount = (TextView) view.getTag(R.id.recycler_item_input_tag);
                            int size = Utils.getByteSize(s);
                            int count = size / 2;
                            if (size % 2 != 0) count++;
                            SpannableStringBuilder builder = new SpannableStringBuilder(String.format("%d / 10", count));
                            if (count > 10) {
                                builder.setSpan(SpannableUtils.getTextColor(context(), R.color.sea_buckthorn), 0, String.valueOf(count).length() + 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                            }
                            tvTextCount.setText(builder);
                        })
                        .subscribe(s -> LogUtil.showLog(s), throwable -> LogUtil.showError(throwable));
                break;
            case R.id.et_input:
                Observable.defer(() -> Observable.just(content))
                        .doOnNext(s -> contentObj.setContent(s))
                        .filter(s -> view.getTag(R.id.recycler_item_input_tag) != null)
                        .doOnNext(s -> {
                            TextView tvTextCount = (TextView) view.getTag(R.id.recycler_item_input_tag);
                            int size = Utils.getByteSize(s);
                            int count = size / 2;
                            if (size % 2 != 0) count++;
                            SpannableStringBuilder builder = new SpannableStringBuilder(String.format("%d / %d", count, getType() == TYPE_TIMELINE ? 200 : 600));
                            if (count > (getType() == TYPE_TIMELINE ? 200 : 600)) {
                                builder.setSpan(SpannableUtils.getTextColor(context(), R.color.sea_buckthorn), 0, String.valueOf(count).length() + 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                            }
                            tvTextCount.setText(builder);
                        })
                        .subscribe(s -> LogUtil.showLog(s), throwable -> LogUtil.showError(throwable));
                break;
        }
    }
}
