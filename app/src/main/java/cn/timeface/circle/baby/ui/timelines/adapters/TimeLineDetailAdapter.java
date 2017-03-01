package cn.timeface.circle.baby.ui.timelines.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import cn.timeface.circle.baby.App;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.support.api.models.objs.CommentObj;
import cn.timeface.circle.baby.support.api.models.objs.MediaObj;
import cn.timeface.circle.baby.support.api.models.objs.UserObj;
import cn.timeface.circle.baby.support.utils.DateUtil;
import cn.timeface.circle.baby.support.utils.GlideUtil;
import cn.timeface.circle.baby.support.utils.Remember;
import cn.timeface.circle.baby.ui.timelines.Utils.JSONUtils;
import cn.timeface.circle.baby.ui.timelines.Utils.LogUtil;
import cn.timeface.circle.baby.ui.timelines.beans.LikeUserList;
import cn.timeface.circle.baby.ui.timelines.views.GridStaggerLookup;

/**
 * author : wangshuai Created on 2017/2/7
 * email : wangs1992321@gmail.com
 */
public class TimeLineDetailAdapter extends BaseAdapter {
    private GridStaggerLookup lookup;

    public TimeLineDetailAdapter(Context activity) {
        super(activity);
    }

    @Override
    public int getViewLayoutID(int viewType) {
        switch (viewType) {
            case 0:
                return R.layout.tiime_line_detail_imageview;
            case 1:
                return R.layout.time_line_detail_comment;
            case 2:
                return R.layout.time_line_detail_like;
            case 3:
                return R.layout.time_line_detail_content;
            case 4:
                return R.layout.time_line_detail_video;
            default:
                return -1;
        }

    }

    @Override
    public int getViewType(int position) {
        Object item = getItem(position);
        if (item instanceof MediaObj) {
            MediaObj mediaObj = (MediaObj) item;
            if (mediaObj.isVideo())
                return 4;
            return 0;
        } else if (item instanceof CommentObj)
            return 1;
        else if (item instanceof LikeUserList)
            return 2;
        else if (item instanceof String)
            return 3;
        else return -1;
    }

    @Override
    public void initView(View contentView, int position) {
        Object item = getItem(position);
        if (item instanceof MediaObj) doMediaObj(contentView, position, (MediaObj) item);
        else if (item instanceof CommentObj)
            doCommentObj(contentView, (CommentObj) item);
        else if (item instanceof LikeUserList)
            doLikeList(contentView, (LikeUserList) item);
        else if (item instanceof String)
            doContent(contentView, (String) item);

    }

    private void doVideo(View contentView, int postion, MediaObj mediaObj) {
        ImageView ivVideo = ViewHolder.getView(contentView, R.id.iv_video);
        ImageView ivCover = ViewHolder.getView(contentView, R.id.iv_cover);
        TextView tvVideotime = ViewHolder.getView(contentView, R.id.tv_videotime);
        tvVideotime.setText("时长：" + DateUtil.getTime4(mediaObj.getLength() * 1000));
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) contentView.getLayoutParams();
        if (params == null) {
            params = new RecyclerView.LayoutParams(App.mScreenWidth,
                    Remember.getInt("width", 0));
        } else {
            params.width = App.mScreenWidth;
            params.height = (int) (Remember.getInt("width", 0) + context().getResources().getDimension(R.dimen.size_20));
        }
        contentView.setLayoutParams(params);
        ViewGroup.LayoutParams layoutParams = ivVideo.getLayoutParams();
        layoutParams.width = Remember.getInt("width", 0);
        layoutParams.height = Remember.getInt("width", 0);
        ivVideo.setLayoutParams(layoutParams);
        GlideUtil.setImage(mediaObj.getImgUrl(), ivVideo, R.drawable.bg_default_holder_img);
    }


    private void doMediaObj(View contentView, int position, MediaObj mediaObj) {
        if (mediaObj.isVideo()) {
            doVideo(contentView, position, mediaObj);
            return;
        }
        LogUtil.showLog(JSONUtils.parse2JSONString(mediaObj));
        int width = Remember.getInt("width", 0);
        if (mediaObj.getW() > 0)
            width = lookup.getSpanSize(position) * columWidth;
        int height = Remember.getInt("width", 0);
        ;
        if (mediaObj.getH() > 0)
            height = (mediaObj.getH() * width) / mediaObj.getW();
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) contentView.getLayoutParams();
        if (params == null) {
            params = new RecyclerView.LayoutParams(width == 0 ? RecyclerView.LayoutParams.MATCH_PARENT : width,
                    height == 0 ? RecyclerView.LayoutParams.WRAP_CONTENT : height);
        } else {
            params.width = width == 0 ? RecyclerView.LayoutParams.MATCH_PARENT : width;
            params.height = height == 0 ? RecyclerView.LayoutParams.WRAP_CONTENT : height;
        }
        contentView.setLayoutParams(params);
        ImageView icon = ViewHolder.getView(contentView, R.id.icon);
        LogUtil.showLog(position + "---" + mediaObj.getImgUrl());
        GlideUtil.setImage(mediaObj.getImgUrl(), icon, R.drawable.bg_default_holder_img);
    }

    private void doCommentObj(View contentView, CommentObj comment) {
        TextView tvComment = ViewHolder.getView(contentView, R.id.tv_comment);
        TextView tvTime = ViewHolder.getView(contentView, R.id.tv_time);
        TextView tvRelative = ViewHolder.getView(contentView, R.id.tv_relative);
        String relationName = comment.getUserInfo().getRelationName();
        SpannableStringBuilder msb = new SpannableStringBuilder();
        msb.append(relationName)
                .setSpan(new ForegroundColorSpan(Color.parseColor("#727272")), 0, relationName.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);//第一个人名
        if (comment.getToUserInfo() != null && !TextUtils.isEmpty(comment.getToUserInfo().getRelationName())) {
            msb.append("回复");
            msb.append(comment.getToUserInfo().getRelationName())
                    .setSpan(new ForegroundColorSpan(Color.parseColor("#727272")), msb.length() - comment.getToUserInfo().getRelationName().length(), msb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        tvRelative.setText(msb);
        tvComment.setText(comment.getContent());
        tvTime.setText(DateUtil.formatDate("MM月dd日 HH:mm", comment.getCommentDate()));
    }

    private void doLikeList(View contentView, LikeUserList likeUserList) {
        RecyclerView likeList = ViewHolder.getView(contentView, R.id.like_list);
        LinearLayoutManager manager = new LinearLayoutManager(context(), LinearLayoutManager.HORIZONTAL, false);
        LikeAdapter adapter = new LikeAdapter(context());
        likeList.setAdapter(adapter);
        likeList.setLayoutManager(manager);
        UserObj userObj = new UserObj();
        userObj.setUserId("-9999");
        userObj.setIsCreator(-1);
        if (!likeUserList.getList().contains(userObj))
            likeUserList.getList().add(0, userObj);
        adapter.addList(true, likeUserList.getList());
    }

    private void doContent(View view, String content) {
        LogUtil.showLog("content===>" + content);
        TextView contentView = ViewHolder.getView(view, R.id.content);
        contentView.setText(content);
    }

    private int columWidth = 0;

    public GridStaggerLookup getLookup() {
        return lookup;
    }

    public void setLookup(GridStaggerLookup lookup) {
        this.lookup = lookup;
        this.columWidth = App.mScreenWidth / lookup.getColumCount();
    }
}
