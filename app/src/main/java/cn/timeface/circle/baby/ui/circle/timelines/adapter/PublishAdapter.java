package cn.timeface.circle.baby.ui.circle.timelines.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.wechat.photopicker.fragment.BigImageFragment;

import java.util.ArrayList;
import java.util.List;

import cn.timeface.circle.baby.App;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.FragmentBridgeActivity;
import cn.timeface.circle.baby.activities.SelectPhotoActivity;
import cn.timeface.circle.baby.activities.SelectTimeActivity;
import cn.timeface.circle.baby.support.api.models.objs.ImgObj;
import cn.timeface.circle.baby.support.api.models.objs.MediaObj;
import cn.timeface.circle.baby.support.managers.services.UploadService;
import cn.timeface.circle.baby.support.utils.DateUtil;
import cn.timeface.circle.baby.support.utils.GlideUtil;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.support.utils.Utils;
import cn.timeface.circle.baby.support.utils.rxutils.SchedulersCompat;
import cn.timeface.circle.baby.ui.circle.bean.CircleContentObj;
import cn.timeface.circle.baby.ui.circle.bean.CircleHomeworkObj;
import cn.timeface.circle.baby.ui.circle.bean.CircleMediaObj;
import cn.timeface.circle.baby.ui.circle.bean.CircleSchoolTaskObj;
import cn.timeface.circle.baby.ui.circle.bean.CircleTimelineObj;
import cn.timeface.circle.baby.ui.circle.timelines.activity.PublishActivity;
import cn.timeface.circle.baby.ui.circle.timelines.activity.SelectActiveActivity;
import cn.timeface.circle.baby.ui.circle.timelines.views.CircleGridStaggerLookup;
import cn.timeface.circle.baby.ui.circle.timelines.views.InputListenerEditText;
import cn.timeface.circle.baby.ui.timelines.Utils.JSONUtils;
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

    public static final int MAX_PIC_TIMELINE_COUNT = 99;
    public static final int MAX_PIC_WORK_COUNT = 9;

    //item类型
    public static final int ITEM_TYPE_INPUT_CONTENT = -39999;
    public static final int ITEM_TYPE_INPUT_TITLE = -39998;
    public static final int ITEM_TYPE_MEDIA = -39997;
    public static final int ITEM_TYPE_SELECT_ACTIVE = -39996;
    public static final int ITEM_TYPE_SELECT_TIME = -39995;
    public static final int ITEM_TYPE_NOTIFY_TIMELINE = -39994;
    public static final int ITEM_TYPE_MEDIA_TIP = -39993;

    private CircleContentObj contentObj = null;
    private List<ImgObj> selImage = new ArrayList<>(0);

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
        return (getType() == TYPE_TIMELINE ? lookup.isSync() ? 5 : 4 : 3) + contentObj.getMediaList().size() + getSelImage().size() + (getType() == TYPE_TIMELINE ? contentObj.getMediaList().size() >= MAX_PIC_TIMELINE_COUNT ? 0 : 1 : contentObj.getMediaList().size() >= MAX_PIC_WORK_COUNT ? 0 : 1);

    }

    @Override
    public <T> T getItem(int position) {
        return (T) contentObj;
    }

    @Override
    public void initView(View contentView, int position) {
        switch (getViewType(position)) {
            case ITEM_TYPE_INPUT_TITLE:
                doTitle(contentView, position);
                break;
            case ITEM_TYPE_INPUT_CONTENT:
                doContent(contentView, position);
                break;
            case ITEM_TYPE_SELECT_ACTIVE:
                doActivce(contentView);
                break;
            case ITEM_TYPE_SELECT_TIME:
                doTime(contentView);
                break;
            case ITEM_TYPE_NOTIFY_TIMELINE:
                doNotifyTimeLine(contentView);
                break;
            case ITEM_TYPE_MEDIA:
                doMedia(contentView, position);
                break;
        }
    }

    @Override
    public int getViewLayoutID(int viewType) {
        switch (viewType) {
            case ITEM_TYPE_INPUT_TITLE:
                return getType() == TYPE_WORK ? R.layout.circle_publish_home_title : R.layout.circle_publish_input_title_layout;
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
            case ITEM_TYPE_MEDIA_TIP:
                return R.layout.circle_send_media_tip;

        }
        return 0;
    }


    private void doMedia(View view, int position) {
        ImageView ivImg = ViewHolder.getView(view, R.id.iv_img);

        //计算宽度和高度
        int width = App.mScreenWidth / lookup.getColumCount() * lookup.getSpanSize(position);
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
        if (params == null)
            params = new RecyclerView.LayoutParams(width, width);
        params.width = width;
        params.height = width;
        view.setLayoutParams(params);


        int index = (getType() == TYPE_TIMELINE ? lookup.isSync() ? position - 5 : position - 4 : position - 3);
        if (index >= 0 && index >= contentObj.getMediaList().size()) {
            index = index - contentObj.getMediaList().size();
            if (index >= 0 && index < selImage.size()) {
                ivImg.setTag(R.id.recycler_item_click_tag, index + contentObj.getMediaList().size());
                GlideUtil.displayImage(selImage.get(index).getLocalPath(), ivImg, false);
            } else {
                ivImg.setTag(R.id.recycler_item_click_tag, -1);
                GlideUtil.setImage(null, ivImg, R.drawable.ic_publish_add_nor, false);
            }
        } else if (index >= 0) {
            ivImg.setTag(R.id.recycler_item_click_tag, index);
            CircleMediaObj mediaObj = contentObj.getMediaList().get(index);
            GlideUtil.displayImage(mediaObj.getImgUrl(), ivImg, false);
        }
        ivImg.setOnClickListener(this);
    }

    private void doActivce(View view) {
        TextView tvActive = ViewHolder.getView(view, R.id.tv_active);
        view.setOnClickListener(this);
        CircleTimelineObj timelineObj = (CircleTimelineObj) contentObj;
        if (timelineObj.getActivityAlbum() == null || TextUtils.isEmpty(timelineObj.getActivityAlbum().getAlbumName())) {
            tvActive.setText(R.string.unjoin_active_tip);
        } else tvActive.setText(timelineObj.getActivityAlbum().getAlbumName());
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

    private void doTime(View view) {
        TextView tvTime = ViewHolder.getView(view, R.id.tv_time);
        CircleTimelineObj timelineObj = (CircleTimelineObj) contentObj;
        if (timelineObj.getRecordDate() <= 0)
            timelineObj.setRecordDate(System.currentTimeMillis());
        tvTime.setText(DateUtil.formatDate("yyyy.MM.dd", timelineObj.getRecordDate()));
    }

    private void doTitle(View view, int position) {
        if (getType() == TYPE_WORK) {
            TextView title = ViewHolder.getView(view, R.id.title);
            title.setText(contentObj.getTitle());
        } else {
            InputListenerEditText etInput = ViewHolder.getView(view, R.id.et_input_title);
            TextView tvTextCount = ViewHolder.getView(view, R.id.tv_text_count);
            if (!TextUtils.isEmpty(contentObj.getTitle())) {
                etInput.setText(contentObj.getTitle());
            } else {
                etInput.setText("");
                tvTextCount.setText("0 / 10");
                etInput.setHint(getType() == TYPE_TIMELINE ? "请输入标题（可不填）" : "请输入作业标题（必填）");
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
    }

    private void doContent(View view, int position) {
        InputListenerEditText etInput = ViewHolder.getView(view, R.id.et_input);
        TextView tvTextCount = ViewHolder.getView(view, R.id.tv_text_count);
        if (!TextUtils.isEmpty(contentObj.getContent())) {
            etInput.setText(contentObj.getContent());
        } else {
            etInput.setText("");
        }
        if (getType() != TYPE_TIMELINE) {
            etInput.setHint("请输入作业描述");
        } else etInput.setHint(R.string.please_input_want_say);
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
                return getType() == TYPE_TIMELINE ? ITEM_TYPE_SELECT_ACTIVE : ITEM_TYPE_MEDIA_TIP;
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
                    break;
                case TYPE_SCHOOL:
                    contentObj = new CircleSchoolTaskObj();
                    break;
                case TYPE_TIMELINE:
                    contentObj = new CircleTimelineObj();
                    break;
            }
    }

    public List<ImgObj> getSelImage() {
        return selImage;
    }

    public void setSelImage(List<ImgObj> selImage) {
        this.selImage = selImage;
    }

    public CircleGridStaggerLookup getLookup() {
        return lookup;
    }

    public void setLookup(CircleGridStaggerLookup lookup) {
        this.lookup = lookup;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_active:
                SelectActiveActivity.open(context());
                break;
            case R.id.rl_time:
                Intent intent1 = new Intent(context(), SelectTimeActivity.class);
                intent1.putExtra("time_shot", DateUtil.formatDate(cn.timeface.circle.baby.activities.PublishActivity.TIME_FORMAT, System.currentTimeMillis()));
                intent1.putExtra("time_now", DateUtil.formatDate(cn.timeface.circle.baby.activities.PublishActivity.TIME_FORMAT, System.currentTimeMillis()));
                ActivityCompat.startActivityForResult((Activity) context(), intent1, PublishActivity.TIME, null);
//                SelectTimeActivity.open(context());
                break;
            case R.id.iv_img:
                Observable.defer(() -> Observable.from(contentObj.getMediaList()))
                        .map(circleMediaObj -> circleMediaObj.getImgObj())
                        .toList()
                        .compose(SchedulersCompat.applyIoSchedulers())
                        .subscribe(imgObjs -> {
                            int index = (int) v.getTag(R.id.recycler_item_click_tag);
                            if (index < 0) {
                                int maxCount = (getType() == TYPE_TIMELINE ? MAX_PIC_TIMELINE_COUNT : MAX_PIC_WORK_COUNT);
                                maxCount -= contentObj.getMediaList().size();
                                if (maxCount <= 0) {
                                    ToastUtil.showToast(context(), String.format(context().getString(R.string.pic_select_max_tip), getType() == TYPE_TIMELINE ? MAX_PIC_TIMELINE_COUNT : MAX_PIC_WORK_COUNT));
                                } else
                                    SelectPhotoActivity.openForResult(context(), selImage, maxCount, PublishActivity.PICTURE);
                            } else {
                                ArrayList<MediaObj> list = new ArrayList<MediaObj>(0);
                                list.addAll(MediaObj.getMediaArray(contentObj.getMediaList()));
                                for (ImgObj imgObj : selImage) {
                                    list.add(imgObj.getCircleMediaObj());
                                }
                                FragmentBridgeActivity.openBigimageFragment(context(), 0, list, MediaObj.getUrls(list), index, getType() == TYPE_TIMELINE ? BigImageFragment.CIRCLE_MEDIA_IMAGE_EDITOR : BigImageFragment.CIRCLE_MEDIA_IMAGE_NONE, false, true);
                            }
                        }, throwable -> LogUtil.showError(throwable));
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    @Override
    public void callBack(EditText view, String content) {
        switch (view.getId()) {
            case R.id.et_input_title:
                Observable.defer(() -> Observable.just(content))
                        .filter(s -> {
                            if (!s.equals(contentObj.getTitle()) && Utils.getByteSize(s) > 20 && s.length() > 10) {
                                s = contentObj.getTitle();
                                view.setText(s);
                                view.setSelection(s.length());
                                return false;
                            }
                            return true;
                        })
                        .doOnNext(s -> contentObj.setTitle(s))
                        .filter(s -> view.getTag(R.id.recycler_item_input_tag) != null)
                        .doOnNext(s -> {
                            TextView tvTextCount = (TextView) view.getTag(R.id.recycler_item_input_tag);
                            int size = Utils.getByteSize(s);
                            int count = size / 2;
                            if (size % 2 != 0) count++;
                            SpannableStringBuilder builder = new SpannableStringBuilder(String.format("%d / 10", count > 10 ? 10 : count));
                            if (count > 10) {
                                builder.setSpan(SpannableUtils.getTextColor(context(), R.color.sea_buckthorn), 0, String.valueOf(count).length() + 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                            }
                            tvTextCount.setText(builder);
                        })
                        .subscribe(s -> LogUtil.showLog(s), throwable -> LogUtil.showError(throwable));
                break;
            case R.id.et_input:
                Observable.defer(() -> Observable.just(content))
                        .filter(s -> {
                            if (!s.equals(contentObj.getTitle()) && Utils.getByteSize(s) > (getType() == TYPE_TIMELINE ? 400 : 1200) && s.length() > (getType() == TYPE_TIMELINE ? 200 : 600)) {
                                s = contentObj.getContent();
                                view.setText(s);
                                view.setSelection(s.length());
                                return false;
                            }
                            return true;
                        })
                        .doOnNext(s -> contentObj.setContent(s))
                        .filter(s -> view.getTag(R.id.recycler_item_input_tag) != null)
                        .doOnNext(s -> {
                            TextView tvTextCount = (TextView) view.getTag(R.id.recycler_item_input_tag);
                            int size = Utils.getByteSize(s);
                            int count = size / 2;
                            if (size % 2 != 0) count++;
                            SpannableStringBuilder builder = new SpannableStringBuilder(String.format("%d / %d", count > (getType() == TYPE_TIMELINE ? 200 : 600) ? getType() == TYPE_TIMELINE ? 200 : 600 : count, getType() == TYPE_TIMELINE ? 200 : 600));
                            if (count > (getType() == TYPE_TIMELINE ? 200 : 600)) {
                                builder.setSpan(SpannableUtils.getTextColor(context(), R.color.sea_buckthorn), 0, String.valueOf(count).length() + 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                            }
                            tvTextCount.setText(builder);
                        })
                        .subscribe(s -> LogUtil.showLog(s), throwable -> LogUtil.showError(throwable));
                break;
        }

    }

    public Observable<CircleContentObj> getSendContent() {
        if (contentObj.getMediaList().size() > 0)
            return Observable.from(contentObj.getMediaList())
                    .doOnNext(circleMediaObj -> {
                        if (circleMediaObj.getId() < 0) {
                            contentObj.getMediaList().remove(circleMediaObj);
                        }
                    })
                    .toList()
                    .flatMap(circleMediaObjs -> Observable.from(selImage).filter(imgObj -> imgObj != null).doOnNext(imgObj -> circleMediaObjs.add(imgObj.getCircleMediaObj())).map(imgObj -> circleMediaObjs))
                    .doOnNext(circleMediaObjs -> contentObj.setMediaList(circleMediaObjs))
                    .map(circleMediaObjs -> contentObj)
                    .doOnNext(circleContentObj -> circleContentObj.setCreateDate(System.currentTimeMillis()));
        else if (selImage.size() > 0) {
            List<String> list = new ArrayList<>(0);
            return Observable.from(selImage)
                    .filter(imgObj -> imgObj != null)
                    .doOnNext(imgObj -> list.add(imgObj.getLocalPath()))
                    .doOnNext(imgObj -> contentObj.getMediaList().add(imgObj.getCircleMediaObj()))
                    .toList()
                    .doOnNext(imgObjs -> {
                        LogUtil.showLog("list=====" + JSONUtils.parse2JSONString(list));
                        UploadService.start(context(), list);
                    })
                    .map(imgObjs -> contentObj)
                    .doOnNext(circleContentObj -> circleContentObj.setCreateDate(System.currentTimeMillis()));
        } else return Observable.just(contentObj)
                .doOnNext(circleContentObj -> circleContentObj.setCreateDate(System.currentTimeMillis()));
    }
}
