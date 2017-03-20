package cn.timeface.circle.baby.ui.growthcircle.mainpage.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.adapters.base.BaseListAdapter;
import cn.timeface.circle.baby.ui.circle.bean.CircleUserInfo;

public class CircleInfoMemberGridAdapter extends BaseListAdapter<CircleUserInfo> {

    public CircleInfoMemberGridAdapter(Context context, List<CircleUserInfo> listData) {
        super(context, listData);
    }

    @Override
    public int getCount() {
        return listData != null ? listData.size() + 1 : 0; // 添加列表末尾进入成员管理按钮
    }

    @Override
    public Object getItem(int position) {
        return listData != null && listData.size() > position ? listData.get(position) : null;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.item_circle_info_member_grid, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (listData.size() > position) {
            CircleUserInfo item = listData.get(position);

            Glide.with(mContext)
                    .load(item.getCircleAvatarUrl())
                    .centerCrop()
                    .into(holder.ivAvatar);

            holder.ivAvatar.setTag(R.string.tag_obj, item);
        } else {
            holder.ivAvatar.setScaleType(ImageView.ScaleType.CENTER);
            holder.ivAvatar.setImageResource(R.drawable.ic_menu);
            holder.ivAvatar.setTag(R.string.tag_obj, null);
        }

        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.iv_avatar)
        RoundedImageView ivAvatar;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
