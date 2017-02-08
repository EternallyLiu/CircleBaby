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
            case 1:
                return R.layout.tiime_line_detail_imageview;
            case 3:
                return R.layout.time_line_detail_content;
            case 0:
                return R.layout.time_line_detail_comment;
            case 2:
                return R.layout.time_line_detail_like;
            default:
                return -1;
        }

    }

    @Override
    public int getViewType(int position) {
        Object item = getItem(position);
        if (item instanceof MediaObj) {
            return 1;
        } else if (item instanceof CommentObj)
            return 0;
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

    private void doMediaObj(View contentView, int position, MediaObj mediaObj) {
        int width = lookup.getSpanSize(position) * columWidth;
        int height = (mediaObj.getH() * width) / mediaObj.getW();
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) contentView.getLayoutParams();
        if (params == null) {
            params = new RecyclerView.LayoutParams(width, height);
        } else {
            params.width = width;
            params.height = height;
        }
        LogUtil.showLog("width:" + width + "----height:" + height);
        contentView.setLayoutParams(params);
        ImageView icon = ViewHolder.getView(contentView, R.id.icon);
        LogUtil.showLog(position + "---" + mediaObj.getImgUrl());
        Glide.with(context())
                .load(mediaObj.getImgUrl())
                .crossFade().placeholder(R.drawable.bg_default_holder_img)
                .error(R.drawable.bg_default_holder_img)
                .into(icon);
    }

    private void doCommentObj(View contentView, CommentObj comment) {
        TextView tvComment = ViewHolder.getView(contentView, R.id.tv_comment);
        TextView tvTime = ViewHolder.getView(contentView, R.id.tv_time);
        SpannableStringBuilder msb = new SpannableStringBuilder();
        String relationName = comment.getUserInfo().getRelationName();
        msb.append(relationName)
                .setSpan(new ForegroundColorSpan(Color.parseColor("#727272")), 0, relationName.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);//第一个人名
        if (comment.getToUserInfo() != null && !TextUtils.isEmpty(comment.getToUserInfo().getRelationName())) {
            msb.append("回复");
            msb.append(comment.getToUserInfo().getRelationName())
                    .setSpan(new ForegroundColorSpan(Color.parseColor("#727272")), msb.length() - comment.getToUserInfo().getRelationName().length(), msb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        msb.append("：").append(comment.getContent());
        tvComment.setText(msb);
        tvTime.setText(DateUtil.formatDate("MM-dd kk:mm", comment.getCommentDate()));
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
