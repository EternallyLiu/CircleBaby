package cn.timeface.circle.baby.ui.circle.timelines.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.wechat.photopicker.fragment.BigImageFragment;

import java.util.List;

import cn.timeface.circle.baby.App;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.FragmentBridgeActivity;
import cn.timeface.circle.baby.support.api.models.objs.MediaObj;
import cn.timeface.circle.baby.support.utils.DateUtil;
import cn.timeface.circle.baby.support.utils.GlideUtil;
import cn.timeface.circle.baby.ui.circle.adapters.BaseEmptyAdapter;
import cn.timeface.circle.baby.ui.circle.bean.CircleContentObj;
import cn.timeface.circle.baby.ui.circle.bean.CircleHomeworkObj;
import cn.timeface.circle.baby.ui.circle.bean.CircleSchoolTaskObj;
import cn.timeface.circle.baby.ui.circle.bean.HomeWorkListObj;
import cn.timeface.circle.baby.ui.timelines.Utils.LogUtil;
import cn.timeface.circle.baby.ui.timelines.adapters.BaseAdapter;
import cn.timeface.circle.baby.ui.timelines.adapters.ViewHolder;
import cn.timeface.circle.baby.ui.timelines.views.TimeLineMarker;

/**
 * author : wangshuai Created on 2017/3/25
 * email : wangs1992321@gmail.com
 */
public class SchoolTaskDetailAdapter extends BaseEmptyAdapter {

    private static final int TYPE_SCHOOL_TASK = 1001;
    private static final int TYPE_HOMEWORK = 1002;
    private final int paddingImage;

    public SchoolTaskDetailAdapter(Context activity) {
        super(activity);
        paddingImage = (int) (context().getResources().getDimension(R.dimen.size_2));
    }

    @Override
    public int getViewLayoutID(int viewType) {
        switch (viewType) {
            case TYPE_SCHOOL_TASK:
                return R.layout.schooltask_detail_header;
            case TYPE_HOMEWORK:
                return R.layout.schooltask_detail_item;
        }
        return 0;
    }

    @Override
    public int getViewType(int position) {
        switch (position) {
            case 0:
                return TYPE_SCHOOL_TASK;
            default:
                return TYPE_HOMEWORK;
        }
    }

    private void doMediaList(GridLayout gridLayout, int position, List<? extends MediaObj> list) {
        if (list.size() <= 0) return;
        LogUtil.showLog("size==" + list.size());
        if (gridLayout.getChildCount() > 0) gridLayout.removeAllViews();
        gridLayout.setColumnCount(3);
        View view;
        for (int i = 0; i < list.size(); i++) {
            view = inflater.inflate(R.layout.time_line_list_image, gridLayout, false);
            GridLayout.LayoutParams params = (GridLayout.LayoutParams) view.getLayoutParams();
            if (params == null)
                params = new GridLayout.LayoutParams();
            params.width = App.mScreenWidth / 3;
            params.height = App.mScreenWidth / 3;
            view.setLayoutParams(params);
            view.setPadding(paddingImage, paddingImage, paddingImage, paddingImage);
            ImageView icon = (ImageView) view.findViewById(R.id.icon);
            icon.setTag(R.id.recycler_item_click_tag, i);
            icon.setTag(R.id.recycler_item_input_tag, position);
            icon.setOnClickListener(this);
            GlideUtil.displayImage(list.get(i).getImgUrl(), icon, true);
            gridLayout.addView(view);
        }
    }

    private void doSchoolTask(View contentView, int position, CircleSchoolTaskObj item) {
        TextView tvDateTime = ViewHolder.getView(contentView, R.id.tv_date_time);
        TextView tvCreater = ViewHolder.getView(contentView, R.id.tv_creater);
        TextView title = ViewHolder.getView(contentView, R.id.title);
        TextView tvDetail = ViewHolder.getView(contentView, R.id.tv_detail);
        GridLayout glImageList = ViewHolder.getView(contentView, R.id.gl_image_list);

        if (item.getMediaList().size() <= 0) {
            glImageList.setVisibility(View.GONE);
        } else {
            glImageList.setVisibility(View.VISIBLE);
            doMediaList(glImageList, position, item.getMediaList());
        }

        tvDateTime.setText(DateUtil.formatDate("yyyy年MM月dd日 EEEE", item.getCreateDate()));
        tvCreater.setText(String.format("由%s发起", item.getTeacher().getCircleNickName()));
        title.setText(item.getTitle());
        tvDetail.setText(item.getContent());
        tvDetail.setVisibility(TextUtils.isEmpty(item.getContent()) ? View.GONE : View.VISIBLE);
    }

    private void doHomework(View contentView, int position) {
        CircleHomeworkObj item = getItem(position);
        TextView tvCreater = ViewHolder.getView(contentView, R.id.tv_creater);
        TextView tvDetail = ViewHolder.getView(contentView, R.id.tv_detail);
        GridLayout glImageList = ViewHolder.getView(contentView, R.id.gl_image_list);
        ImageView ivIcon = ViewHolder.getView(contentView, R.id.iv_icon);

        GlideUtil.displayImageCircle(item.getSubmitter().getCircleAvatarUrl(), ivIcon);
        if (item.getMediaList().size() <= 0) {
            glImageList.setVisibility(View.GONE);
        } else glImageList.setVisibility(View.VISIBLE);

        doMediaList(glImageList, position, item.getMediaList());

        tvCreater.setText(item.getSubmitter().getCircleNickName());
        tvDetail.setText(item.getContent());
        tvDetail.setVisibility(TextUtils.isEmpty(item.getContent()) ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.icon:
                try {
                    int position = (int) v.getTag(R.id.recycler_item_input_tag);
                    int index = (int) v.getTag(R.id.recycler_item_click_tag);
                    CircleContentObj item = getItem(position);
                    FragmentBridgeActivity.openBigimageFragment(context(), 0, MediaObj.getMediaArray(item.getMediaList()), MediaObj.getUrls(item.getMediaList()), index, BigImageFragment.CIRCLE_MEDIA_IMAGE_NONE, true, false);
                } catch (Exception e) {
                }
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    @Override
    public void addEmpty() {
        addEmpty(1);
    }

    @Override
    public void initView(View contentView, int position) {
        switch (getItemViewType(position)) {
            case EMPTY_CODE:
                doEmpty(contentView);
                break;
            case TYPE_HOMEWORK:
                doHomework(contentView, position);
                break;
            case TYPE_SCHOOL_TASK:
                doSchoolTask(contentView, position, getItem(position));
                break;
        }
    }
}
