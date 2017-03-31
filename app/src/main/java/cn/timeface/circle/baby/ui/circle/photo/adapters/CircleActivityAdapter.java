package cn.timeface.circle.baby.ui.circle.photo.adapters;

import android.animation.Animator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.adapters.base.BaseRecyclerAdapter;
import cn.timeface.circle.baby.ui.circle.bean.CircleActivityAlbumObj;
import cn.timeface.circle.baby.ui.circle.photo.bean.QueryByCircleActivityObj;

/**
 * 活动列表adapter
 * Created by lidonglin on 2017/3/15.
 */
public class CircleActivityAdapter extends BaseRecyclerAdapter<CircleActivityAlbumObj> {
    View.OnClickListener clickListener;

    public CircleActivityAdapter(Context mContext, List<CircleActivityAlbumObj> listData, View.OnClickListener clickListener) {
        super(mContext, listData);
        this.clickListener = clickListener;
    }

    @Override
    public RecyclerView.ViewHolder getViewHolder(ViewGroup viewGroup, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_circle_activity, null);
        return new ViewHolder(view);
    }

    @Override
    public void bindData(RecyclerView.ViewHolder viewHolder, int position) {
        final CircleActivityAlbumObj albumObj = listData.get(position);
        ViewHolder holder = (ViewHolder) viewHolder;
        holder.tvCirclracticityName.setText(albumObj.getAlbumName());
        holder.tvCirclracticityCount.setText(mContext.getString(R.string.circle_activity_photo_count,albumObj.getMediaCount()));
        Glide.with(mContext)
                .load(albumObj.getAlbumUrl())
                .into(holder.ivCircleActivity);
        holder.llCircleActivity.setTag(R.string.tag_obj, albumObj);
        if (clickListener != null) holder.llCircleActivity.setOnClickListener(clickListener);
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
        @Bind(R.id.iv_circle_activity)
        RoundedImageView ivCircleActivity;
        @Bind(R.id.tv_circlracticity_name)
        TextView tvCirclracticityName;
        @Bind(R.id.tv_circlracticity_count)
        TextView tvCirclracticityCount;
        @Bind(R.id.ll_circle_activity)
        RelativeLayout llCircleActivity;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
