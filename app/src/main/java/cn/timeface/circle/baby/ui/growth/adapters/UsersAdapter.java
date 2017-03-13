package cn.timeface.circle.baby.ui.growth.adapters;

import android.animation.Animator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.adapters.base.BaseRecyclerAdapter;
import cn.timeface.circle.baby.support.api.models.objs.UserWrapObj;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 成员信息adapter
 * author : YW.SUN Created on 2017/2/15
 * email : sunyw10@gmail.com
 */
public class UsersAdapter extends BaseRecyclerAdapter<UserWrapObj> {
    View.OnClickListener clickListener;

    public UsersAdapter(Context mContext, List<UserWrapObj> listData, View.OnClickListener clickListener) {
        super(mContext, listData);
        this.clickListener = clickListener;
    }

    @Override
    public RecyclerView.ViewHolder getViewHolder(ViewGroup viewGroup, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_user, null);
        return new ViewHolder(view);
    }

    @Override
    public void bindData(RecyclerView.ViewHolder viewHolder, int position) {
        final UserWrapObj userWrapObj = listData.get(position);
        ViewHolder holder = (ViewHolder) viewHolder;
        holder.tvUserName.setText(userWrapObj.getUserInfo().getRelationName());
        holder.tvPhotoCount.setText(userWrapObj.getUserImageCount() + "张");
        Glide.with(mContext)
                .load(userWrapObj.getUserInfo().getAvatar())
                .into(holder.ivAvatar);
        holder.llRoot.setTag(R.string.tag_obj, userWrapObj);
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

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
