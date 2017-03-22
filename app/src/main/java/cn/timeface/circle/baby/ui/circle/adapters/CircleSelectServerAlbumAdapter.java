package cn.timeface.circle.baby.ui.circle.adapters;

import android.animation.Animator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.adapters.base.BaseRecyclerAdapter;
import cn.timeface.circle.baby.support.utils.GlideUtil;
import cn.timeface.circle.baby.ui.circle.bean.CircleActivityAlbumObjWrapper;

/**
 * 圈照片书选择活动相册adapter
 * author : sunyanwei Created on 17-3-22
 * email : sunyanwei@timeface.cn
 */
public class CircleSelectServerAlbumAdapter extends BaseRecyclerAdapter<CircleActivityAlbumObjWrapper> {
    View.OnClickListener onClickListener;

    public CircleSelectServerAlbumAdapter(Context mContext, List<CircleActivityAlbumObjWrapper> listData, View.OnClickListener onClickListener) {
        super(mContext, listData);
        this.onClickListener = onClickListener;
    }

    @Override
    public RecyclerView.ViewHolder getViewHolder(ViewGroup viewGroup, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_circle_select_server_album, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void bindData(RecyclerView.ViewHolder viewHolder, int position) {
        final CircleActivityAlbumObjWrapper albumObjWrapper = listData.get(position);
        ViewHolder holder = (ViewHolder) viewHolder;
        holder.rlRoot.setTag(R.string.tag_obj, albumObjWrapper);
        if(onClickListener != null) holder.rlRoot.setOnClickListener(onClickListener);
        holder.tvAlbumTitle.setText(albumObjWrapper.getAtcityAlbum().getAlbumName());
        holder.tvAlbumPhotoCount.setText("已有照片" + String.valueOf(albumObjWrapper.getAtcityAlbum().getMediaCount()) + "张");
        GlideUtil.displayImage(albumObjWrapper.getAtcityAlbumCoverUrl(), holder.ivAlbumCover);
    }

    @Override
    public int getItemType(int position) {
        return 0;
    }

    @Override
    protected Animator[] getAnimators(View var1) {
        return new Animator[0];
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_album_cover)
        ImageView ivAlbumCover;
        @Bind(R.id.tv_album_title)
        TextView tvAlbumTitle;
        @Bind(R.id.tv_album_photo_count)
        TextView tvAlbumPhotoCount;
        @Bind(R.id.cb_select)
        CheckBox cbSelect;
        @Bind(R.id.rl_root)
        RelativeLayout rlRoot;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
