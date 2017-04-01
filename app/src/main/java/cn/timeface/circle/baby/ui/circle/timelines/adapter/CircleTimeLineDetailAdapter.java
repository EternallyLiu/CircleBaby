package cn.timeface.circle.baby.ui.circle.timelines.adapter;

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

import cn.timeface.circle.baby.App;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.support.api.models.objs.CommentObj;
import cn.timeface.circle.baby.support.api.models.objs.MediaObj;
import cn.timeface.circle.baby.support.api.models.objs.UserObj;
import cn.timeface.circle.baby.support.utils.DateUtil;
import cn.timeface.circle.baby.support.utils.GlideUtil;
import cn.timeface.circle.baby.support.utils.Remember;
import cn.timeface.circle.baby.ui.circle.bean.CircleCommentObj;
import cn.timeface.circle.baby.ui.circle.bean.CircleMediaObj;
import cn.timeface.circle.baby.ui.circle.bean.CircleUserInfo;
import cn.timeface.circle.baby.ui.circle.timelines.bean.TimeLikeUserList;
import cn.timeface.circle.baby.ui.circle.timelines.bean.TitleBean;
import cn.timeface.circle.baby.ui.circle.timelines.views.CircleTimeLineGridStagger;
import cn.timeface.circle.baby.ui.timelines.Utils.JSONUtils;
import cn.timeface.circle.baby.ui.timelines.Utils.LogUtil;
import cn.timeface.circle.baby.ui.timelines.Utils.SpannableUtils;
import cn.timeface.circle.baby.ui.timelines.adapters.BaseAdapter;
import cn.timeface.circle.baby.ui.timelines.adapters.LikeAdapter;
import cn.timeface.circle.baby.ui.timelines.adapters.ViewHolder;
import cn.timeface.circle.baby.ui.timelines.beans.LikeUserList;

/**
 * author : wangshuai Created on 2017/3/21
 * email : wangs1992321@gmail.com
 */
public class CircleTimeLineDetailAdapter extends BaseAdapter {

    private CircleTimeLineGridStagger lookup;

    private int imagePadding = 8, imageMargin = 12;
    private int columWidth;

    public CircleTimeLineDetailAdapter(Context activity) {
        super(activity);
        imagePadding = (int) context().getResources().getDimension(R.dimen.size_4);
        imageMargin = (int) context().getResources().getDimension(R.dimen.size_12);
    }

    @Override
    public int getViewLayoutID(int viewType) {
        LogUtil.showLog("viewType===" + viewType);
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
            case 5:
                return R.layout.circle_time_line_title_layout;
            default:
                return -1;
        }
    }

    @Override
    public int getViewType(int position) {
        Object item = getItem(position);
        LogUtil.showLog("comment instanceof===" + (item instanceof CircleCommentObj));
        if (item instanceof CircleMediaObj) {
            CircleMediaObj mediaObj = (CircleMediaObj) item;
            if (mediaObj.isVideo())
                return 4;
            return 0;
        } else if (item instanceof CircleCommentObj)
            return 1;
        else if (item instanceof TimeLikeUserList)
            return 2;
        else if (item instanceof TitleBean)
            return 5;
        else if (item instanceof String)
            return 3;
        else {
            LogUtil.showLog(item.toString() + "===item detail:" + JSONUtils.parse2JSONString(item));
            return -1;
        }
    }

    @Override
    public void initView(View contentView, int position) {
        Object item = getItem(position);
        if (item instanceof MediaObj) doMediaObj(contentView, position, (CircleMediaObj) item);
        else if (item instanceof CircleCommentObj)
            doCommentObj(contentView, (CircleCommentObj) item);
        else if (item instanceof TimeLikeUserList)
            doLikeList(contentView, (TimeLikeUserList) item);
        else if (item instanceof TitleBean)
            doTitle(contentView, (TitleBean) item);
        else if (item instanceof String)
            doContent(contentView, (String) item);
    }

    private void doTitle(View contentView, TitleBean item) {
        TextView title = ViewHolder.getView(contentView, R.id.title);
        TextView tvCreater = ViewHolder.getView(contentView, R.id.tv_creater);
        if (TextUtils.isEmpty(item.getTitle()))
            title.setVisibility(View.GONE);
        else {
            title.setText(item.getTitle());
            title.setVisibility(View.VISIBLE);
        }
        tvCreater.setText(String.format("%s上传", item.getUserInfo().getCircleNickName()));

    }

    private void doContent(View view, String content) {
        TextView contentView = ViewHolder.getView(view, R.id.content);
        contentView.setText(content);
    }

    private void doVideo(View contentView, int postion, CircleMediaObj mediaObj) {
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

    private void doMediaObj(View contentView, int position, CircleMediaObj mediaObj) {
        if (mediaObj.isVideo()) {
            doVideo(contentView, position, mediaObj);
            return;
        }
        int width = Remember.getInt("width", 0);
        if (mediaObj.getW() > 0)
            width = lookup.isShowSmail() ? lookup.getSpanSize(position) * columWidth : App.mScreenWidth;
        int height = lookup.isShowSmail() ? width : mediaObj.getH() > 0 ? width * mediaObj.getH() / mediaObj.getW() : Remember.getInt("width", 0);

        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) contentView.getLayoutParams();
        if (params == null) {
            params = new RecyclerView.LayoutParams(width == 0 ? RecyclerView.LayoutParams.MATCH_PARENT : width,
                    height == 0 ? RecyclerView.LayoutParams.WRAP_CONTENT : height);
        } else {
            params.width = width == 0 ? RecyclerView.LayoutParams.MATCH_PARENT : width;
            params.height = height == 0 ? RecyclerView.LayoutParams.WRAP_CONTENT : height;
        }
        contentView.setLayoutParams(params);
        if (lookup.isShowSmail())
            contentView.setPadding(position % lookup.getColumCount() == 1 ? imageMargin : 0, 0, position % lookup.getColumCount() == 0 ? imageMargin : imagePadding, imagePadding);
        else contentView.setPadding(imageMargin, 0, imageMargin, imagePadding);
        ImageView icon = ViewHolder.getView(contentView, R.id.icon);
        GlideUtil.setImage(mediaObj.getImgUrl(), icon, R.drawable.bg_default_holder_img, true);
    }

    private void doCommentObj(View contentView, CircleCommentObj comment) {
        TextView tvComment = ViewHolder.getView(contentView, R.id.tv_comment);
        TextView tvTime = ViewHolder.getView(contentView, R.id.tv_time);
        TextView tvRelative = ViewHolder.getView(contentView, R.id.tv_relative);
        String relationName = comment.getCommentUserInfo().getCircleNickName();
        SpannableStringBuilder msb = new SpannableStringBuilder();
        if (comment.getToCommentUserInfo() != null && !TextUtils.isEmpty(comment.getToCommentUserInfo().getCircleNickName())) {
            msb.append("回复");
            msb.append(comment.getToCommentUserInfo().getCircleNickName())
                    .setSpan(new ForegroundColorSpan(Color.parseColor("#727272")), msb.length() - comment.getToCommentUserInfo().getCircleNickName().length(), msb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            msb.setSpan(SpannableUtils.getTextSize(context(), R.dimen.text_small_13), msb.length() - comment.getToCommentUserInfo().getCircleNickName().length(), msb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        }
        tvRelative.setText(relationName);
        tvComment.setText(msb.append(": ").append(comment.getCommentContent()));
        tvTime.setText(DateUtil.formatDate("MM月dd日 HH:mm", comment.getCommentDate()));
    }

    private void doLikeList(View contentView, TimeLikeUserList likeUserList) {
        RecyclerView likeList = ViewHolder.getView(contentView, R.id.like_list);
        LinearLayoutManager manager = new LinearLayoutManager(context(), LinearLayoutManager.HORIZONTAL, false);
        CircleLikeAdapter adapter = new CircleLikeAdapter(context());
        likeList.setAdapter(adapter);
        likeList.setLayoutManager(manager);
        CircleUserInfo userObj = new CircleUserInfo();
        userObj.setCircleUserId(-1);
        if (!likeUserList.getUserInfos().contains(userObj))
            likeUserList.getUserInfos().add(0, userObj);
        adapter.addList(true, likeUserList.getUserInfos());
    }

    public CircleTimeLineGridStagger getLookup() {
        return lookup;
    }

    public void setLookup(CircleTimeLineGridStagger lookup) {
        this.lookup = lookup;
        this.columWidth = App.mScreenWidth / lookup.getColumCount();
    }
}
