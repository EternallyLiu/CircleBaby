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
import cn.timeface.circle.baby.ui.circle.groupmembers.bean.CircleBabyBriefObj;

public class CircleInfoMemberGridAdapter extends BaseListAdapter<CircleBabyBriefObj> {

    private static final int MAX_COUNT = 10;

    public CircleInfoMemberGridAdapter(Context context, List<CircleBabyBriefObj> listData) {
        super(context, listData);
    }

    @Override
    public int getCount() {
        if (listData == null) return 0;
        return listData.size() > MAX_COUNT ? MAX_COUNT : listData.size(); // 不超过MaxCount
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

        if (position - 1 == MAX_COUNT) {
            holder.ivAvatar.setScaleType(ImageView.ScaleType.CENTER);
            holder.ivAvatar.setImageResource(R.drawable.ic_menu);
            holder.ivAvatar.setTag(R.string.tag_obj, null);
        } else {
            CircleBabyBriefObj item = listData.get(position);

            Glide.with(mContext)
                    .load(item.getBabyAvatarUrl())
                    .centerCrop()
                    .into(holder.ivAvatar);

            holder.ivAvatar.setTag(R.string.tag_obj, item);
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
