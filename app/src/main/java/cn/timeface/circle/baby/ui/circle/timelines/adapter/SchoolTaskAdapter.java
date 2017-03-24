package cn.timeface.circle.baby.ui.circle.timelines.adapter;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.support.utils.DateUtil;
import cn.timeface.circle.baby.support.utils.GlideUtil;
import cn.timeface.circle.baby.ui.circle.bean.CircleSchoolTaskObj;
import cn.timeface.circle.baby.ui.circle.bean.HomeWorkListObj;
import cn.timeface.circle.baby.ui.circle.timelines.bean.CircleHomeWorkHeader;
import cn.timeface.circle.baby.ui.timelines.adapters.BaseAdapter;
import cn.timeface.circle.baby.ui.timelines.adapters.ViewHolder;

/**
 * author : wangshuai Created on 2017/3/22
 * email : wangs1992321@gmail.com
 */
public class SchoolTaskAdapter extends BaseAdapter {

    public static final int TYPE_CIRCLE_HEADER = 1992;

    public SchoolTaskAdapter(Context activity) {
        super(activity);
    }

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
            TextView tvLastTask = ViewHolder.getView(contentView, R.id.tv_last_homework);
            GlideUtil.displayImage(header.getGrowthCircle().getCircleCoverUrl(), ivIcon, true);
            tvName.setText(header.getGrowthCircle().getCircleName());
            tvLastTask.setText(String.format("%s上传了作业 \"%s\"", header.getLastSubmitHomework().getSubmitter().getCircleNickName(), header.getLastSubmitHomework().getTitle()));
        }

    }

    private void doSchoolTask(View contentView, int position) {
        HomeWorkListObj item = getItem(position);
        TextView tvDateTime = ViewHolder.getView(contentView, R.id.tv_date_time);
        TextView tvCreater = ViewHolder.getView(contentView, R.id.tv_creater);
        TextView title = ViewHolder.getView(contentView, R.id.title);
        TextView tvCommited = ViewHolder.getView(contentView, R.id.tv_commited);
        TextView tvDetail = ViewHolder.getView(contentView, R.id.tv_detail);
        LinearLayout llHomeworkList = ViewHolder.getView(contentView, R.id.ll_homework_list);
        TextView tvMore = ViewHolder.getView(contentView, R.id.tv_more);
        LinearLayout llImageList = ViewHolder.getView(contentView, R.id.ll_image_list);
        ImageView image_1 = ViewHolder.getView(contentView, R.id.image_1);
        ImageView image_2 = ViewHolder.getView(contentView, R.id.image_2);
        ImageView image_3 = ViewHolder.getView(contentView, R.id.image_3);

        tvDateTime.setText(DateUtil.formatDate("yyyy年MM月dd日 E", item.getSchoolTask().getCreateDate()));
        tvCreater.setText(String.format("由%s发起", item.getSchoolTask().getTeacher().getCircleNickName()));
        title.setText(item.getSchoolTask().getTitle());
        tvCommited.setVisibility(item.getSchoolTask().isCommit() == 0 ? View.GONE : View.VISIBLE);
        tvDetail.setText(item.getSchoolTask().getContent());
        int mediaSize = item.getMeidaList().size();
        llHomeworkList.setVisibility(mediaSize > 0 ? View.VISIBLE : View.GONE);
        llImageList.setVisibility(mediaSize > 0 ? View.VISIBLE : View.GONE);
        image_1.setVisibility(View.GONE);
        image_2.setVisibility(View.GONE);
        image_3.setVisibility(View.GONE);
        tvMore.setVisibility(mediaSize > 3 ? View.VISIBLE : View.GONE);
        if (mediaSize == 1) {
            GlideUtil.displayImage(item.getMeidaList().get(0).getImgUrl(), image_1, true);
            image_1.setVisibility(View.VISIBLE);
        } else if (mediaSize == 2) {
            GlideUtil.displayImage(item.getMeidaList().get(0).getImgUrl(), image_1, true);
            GlideUtil.displayImage(item.getMeidaList().get(1).getImgUrl(), image_2, true);
            image_1.setVisibility(View.VISIBLE);
            image_2.setVisibility(View.VISIBLE);
        } else if (mediaSize >= 3) {
            GlideUtil.displayImage(item.getMeidaList().get(0).getImgUrl(), image_1, true);
            GlideUtil.displayImage(item.getMeidaList().get(1).getImgUrl(), image_2, true);
            GlideUtil.displayImage(item.getMeidaList().get(2).getImgUrl(), image_3, true);
            image_1.setVisibility(View.VISIBLE);
            image_2.setVisibility(View.VISIBLE);
            image_3.setVisibility(View.VISIBLE);
        }
    }
}
