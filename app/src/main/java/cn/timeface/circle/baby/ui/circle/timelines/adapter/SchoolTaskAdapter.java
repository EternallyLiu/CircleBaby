package cn.timeface.circle.baby.ui.circle.timelines.adapter;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import cn.timeface.circle.baby.App;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.support.api.models.objs.MediaObj;
import cn.timeface.circle.baby.support.utils.DateUtil;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.GlideUtil;
import cn.timeface.circle.baby.ui.circle.bean.CircleSchoolTaskObj;
import cn.timeface.circle.baby.ui.circle.bean.HomeWorkListObj;
import cn.timeface.circle.baby.ui.circle.timelines.activity.PublishActivity;
import cn.timeface.circle.baby.ui.circle.timelines.bean.CircleHomeWorkHeader;
import cn.timeface.circle.baby.ui.timelines.adapters.BaseAdapter;
import cn.timeface.circle.baby.ui.timelines.adapters.ViewHolder;
import cn.timeface.circle.baby.ui.timelines.views.TimeLineMarker;

/**
 * author : wangshuai Created on 2017/3/22
 * email : wangs1992321@gmail.com
 */
public class SchoolTaskAdapter extends BaseAdapter {

    public static final int TYPE_CIRCLE_HEADER = 1992;
    private final int paddingImage;
    private final int width;

    public SchoolTaskAdapter(Context activity) {
        super(activity);
        paddingImage = (int) (context().getResources().getDimension(R.dimen.size_2));
        width = App.mScreenWidth / 3;
    }

    private boolean hashSubmit = false;

    @Override
    public int getViewLayoutID(int viewType) {
        switch (viewType) {
            case TYPE_CIRCLE_HEADER:
                return R.layout.schooltask_list_header;
        }
        return R.layout.schooltask_list_item;
    }

    @Override
    public int getViewType(int position) {
        Object item = getItem(position);
        if (item instanceof CircleHomeWorkHeader)
            return TYPE_CIRCLE_HEADER;
        return 0;
    }

    @Override
    public void initView(View contentView, int position) {
        Object it = getItem(position);
        if (it instanceof HomeWorkListObj) {
            doSchoolTask(contentView, position);
        } else if (it instanceof CircleHomeWorkHeader) {
            CircleHomeWorkHeader header = (CircleHomeWorkHeader) it;
            ImageView ivIcon = ViewHolder.getView(contentView, R.id.iv_icon);
            TextView tvName = ViewHolder.getView(contentView, R.id.tv_name);
            Button btnPublishSchooltask = ViewHolder.getView(contentView, R.id.btn_publish_schooltask);
            btnPublishSchooltask.setVisibility(FastData.getCircleUserInfo().getCircleUserType() == 2 ? View.VISIBLE : View.GONE);
            btnPublishSchooltask.setOnClickListener(this);
            TextView tvLastTask = ViewHolder.getView(contentView, R.id.tv_last_homework);
            tvLastTask.setVisibility(header.getLastSubmitHomework() == null ? View.GONE : View.VISIBLE);
            GlideUtil.displayImage(header.getGrowthCircle().getCircleCoverUrl(), ivIcon, true);
            tvName.setText(header.getGrowthCircle().getCircleName());
            tvLastTask.setText(String.format("%s上传了作业 \"%s\"", header.getLastSubmitHomework().getSubmitter().getCircleNickName(), header.getLastSubmitHomework().getTitle()));
        }

    }

    private void doSchoolTask(View contentView, int position) {
        HomeWorkListObj item = getItem(position);
        TimeLineMarker marker = ViewHolder.getView(contentView, R.id.line);
        TextView tvDateTime = ViewHolder.getView(contentView, R.id.tv_date_time);
        TextView tvCreater = ViewHolder.getView(contentView, R.id.tv_creater);
        TextView title = ViewHolder.getView(contentView, R.id.title);
        TextView tvCommited = ViewHolder.getView(contentView, R.id.tv_commited);
        TextView tvDetail = ViewHolder.getView(contentView, R.id.tv_detail);
        GridLayout glImageList = ViewHolder.getView(contentView, R.id.gl_image_list);
        TextView tvSubmitCount = ViewHolder.getView(contentView, R.id.tv_commit_count);

        if (item.getSchoolTask().getMediaList().size() <= 0) {
            glImageList.setVisibility(View.GONE);
        } else glImageList.setVisibility(View.VISIBLE);

        doMediaList(glImageList, position, item.getSchoolTask().getMediaList());

        tvSubmitCount.setText(String.format("已有%d人提交", item.getSubmitCount()));
        tvDateTime.setText(DateUtil.formatDate("yyyy年MM月dd日 EEEE", item.getSchoolTask().getCreateDate()));
        tvCreater.setText(String.format("由%s发起", item.getSchoolTask().getTeacher().getCircleNickName()));
        title.setText(item.getSchoolTask().getTitle());
        tvCommited.setVisibility(item.getSchoolTask().isCommit() == 0 ? View.GONE : View.VISIBLE);
        tvDetail.setText(item.getSchoolTask().getContent());
        marker.setDrawEnd(true);
        if (hashSubmit) {
            marker.setDrawBegin(true);
        } else marker.setDrawBegin(position == 1);
    }

    private void doMediaList(GridLayout gridLayout, int position, List<? extends MediaObj> list) {
        if (list.size() <= 0) return;
        if (gridLayout.getChildCount() > 0) gridLayout.removeAllViews();
        gridLayout.setColumnCount(3);
        View view;
        for (int i = 0; i < list.size(); i++) {
            view = inflater.inflate(R.layout.time_line_list_image, gridLayout, false);
            GridLayout.LayoutParams params = (GridLayout.LayoutParams) view.getLayoutParams();
            if (params == null)
                params = new GridLayout.LayoutParams();
            params.width = width;
            params.height = width;
            view.setLayoutParams(params);
            view.setPadding(paddingImage, paddingImage, paddingImage, paddingImage);
            ImageView icon = (ImageView) view.findViewById(R.id.icon);
            GlideUtil.displayImage(list.get(i).getImgUrl(), icon, true);
            gridLayout.addView(view);
        }
    }

    public boolean isHashSubmit() {
        return hashSubmit;
    }

    public void setHashSubmit(boolean hashSubmit) {
        this.hashSubmit = hashSubmit;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_publish_schooltask:
                PublishActivity.openSchoolTask(context());
                break;
            default:
                super.onClick(v);
                break;
        }
    }
}
