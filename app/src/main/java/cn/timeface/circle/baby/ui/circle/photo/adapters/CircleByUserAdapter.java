package cn.timeface.circle.baby.ui.circle.photo.adapters;

import android.animation.Animator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.adapters.base.BaseRecyclerAdapter;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.ui.circle.bean.QueryByCircleUserObj;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 按发布人adapter
 * Created by lidonglin on 2017/3/18.
 */
public class CircleByUserAdapter extends BaseRecyclerAdapter<QueryByCircleUserObj> {
    View.OnClickListener clickListener;

    public CircleByUserAdapter(Context mContext, List<QueryByCircleUserObj> listData, View.OnClickListener clickListener) {
        super(mContext, listData);
        this.clickListener = clickListener;
    }

    @Override
    public RecyclerView.ViewHolder getViewHolder(ViewGroup viewGroup, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_circle_byuser, null);
        return new ViewHolder(view);
    }

    @Override
    public void bindData(RecyclerView.ViewHolder viewHolder, int position) {
        final QueryByCircleUserObj obj = listData.get(position);
        ViewHolder holder = (ViewHolder) viewHolder;
        if(FastData.getCircleUserId() == obj.getUserInfo().getCircleUserId()){
            holder.ivMe.setVisibility(View.VISIBLE);
        }else{
            holder.ivMe.setVisibility(View.GONE);
        }
        holder.tvUserName.setText(obj.getUserInfo().getCircleNickName());
        holder.tvPhotoCount.setText(obj.getMediaCount() + "张");
        Glide.with(mContext)
                .load(obj.getUserInfo().getCircleAvatarUrl())
                .into(holder.ivAvatar);
        holder.llRoot.setTag(R.string.tag_obj, obj);
        if(clickListener != null) holder.llRoot.setOnClickListener(clickListener);
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
        @Bind(R.id.iv_avatar)
        CircleImageView ivAvatar;
        @Bind(R.id.tv_user_name)
        TextView tvUserName;
        @Bind(R.id.tv_photo_count)
        TextView tvPhotoCount;
        @Bind(R.id.ll_root)
        LinearLayout llRoot;
        @Bind(R.id.iv_me)
        ImageView ivMe;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
