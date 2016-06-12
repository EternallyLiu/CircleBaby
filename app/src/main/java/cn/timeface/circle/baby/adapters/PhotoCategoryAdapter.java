package cn.timeface.circle.baby.adapters;

import android.animation.Animator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.adapters.base.BaseRecyclerAdapter;
import cn.timeface.circle.baby.utils.mediastore.MediaStoreBucket;

/**
 * @author YW.SUN
 * @from 2016/3/23
 * @TODO
 */
public class PhotoCategoryAdapter extends BaseRecyclerAdapter<MediaStoreBucket> {
    private final String curBucketId;

    public PhotoCategoryAdapter(Context mContext, List<MediaStoreBucket> listData, String curBucketId) {
        super(mContext, listData);
        this.curBucketId = curBucketId;
    }

    @Override
    public RecyclerView.ViewHolder getViewHolder(ViewGroup viewGroup, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_photo_category, null);
        return new ViewHolder(view);
    }

    @Override
    public void bindData(RecyclerView.ViewHolder viewHolder, int position) {
        ViewHolder holder = (ViewHolder) viewHolder;
        MediaStoreBucket item = getItem(position);
        if (item != null && item.getTotalCount() > 0) {
            Glide.with(mContext)
                    .load(item.getPhotoUri())
                    .centerCrop()
                    .override(mContext.getResources().getDimensionPixelOffset(R.dimen.size_72), mContext.getResources().getDimensionPixelOffset(R.dimen.size_72))
                    .into(holder.mIvPhoto);
            holder.mTvTitle.setText(item.getName());
            holder.mTvCount.setText(String.valueOf(item.getTotalCount()));

            //所有照片
            if (TextUtils.isEmpty(curBucketId)) {
                holder.mTvTitle.setSelected(item.getId() == null);
            } else {
                holder.mTvTitle.setSelected(
                        item.getId() != null
                                && item.getId().equals(curBucketId));
            }
        }

        holder.mIvSel.setVisibility(item.getId().equals(curBucketId) ? View.VISIBLE : View.INVISIBLE);
        holder.rlItemRoot.setTag(R.string.tag_index, position);
        holder.rlItemRoot.setTag(R.string.tag_ex, holder.mTvTitle);
    }

    @Override
    public int getItemType(int position) {
        return 0;
    }

    @Override
    protected Animator[] getAnimators(View var1) {
        return new Animator[0];
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.ivPhoto)
        ImageView mIvPhoto;
        @Bind(R.id.tvTitle)
        TextView mTvTitle;
        @Bind(R.id.tvCount)
        TextView mTvCount;
        @Bind(R.id.ivSel)
        ImageView mIvSel;
        @Bind(R.id.rl_item_root)
        RelativeLayout rlItemRoot;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
