package cn.timeface.circle.baby.ui.growthcircle.mainpage.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.adapters.base.BaseListAdapter;
import cn.timeface.circle.baby.ui.circle.bean.MemberDataObj;
import de.hdodenhof.circleimageview.CircleImageView;

public class CircleInfoMemberGridAdapter extends BaseListAdapter<MemberDataObj> {

    private static final int MAX_COUNT = 10;



    public CircleInfoMemberGridAdapter(Context context, List<MemberDataObj> listData) {
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

        if (position + 1 == MAX_COUNT) {
            holder.ivChildImg.setVisibility(View.GONE);
            holder.ivContentImg.setVisibility(View.GONE);
            holder.iv_menu.setVisibility(View.VISIBLE);
            holder.iv_menu.setTag(R.string.tag_obj, null);
        } else {
            holder.ivChildImg.setVisibility(View.VISIBLE);
            holder.ivContentImg.setVisibility(View.VISIBLE);
            holder.iv_menu.setVisibility(View.GONE);
            MemberDataObj item = listData.get(position);
            Glide.with(mContext)
                    .load(item.getUserInfo().getCircleAvatarUrl())
                    .centerCrop()
                    .into(holder.ivContentImg);
            Glide.with(mContext)
                    .load(item.getBabyBrief().getBabyAvatarUrl())
                    .centerCrop()
                    .into(holder.ivChildImg);

            holder.ivContentImg.setTag(R.string.tag_obj, item);
        }

        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.iv_content_img)
        CircleImageView ivContentImg;
        @Bind(R.id.iv_child_img)
        CircleImageView ivChildImg;
        @Bind(R.id.iv_menu)
        ImageView iv_menu;
        @Bind(R.id.ll_root)
        RelativeLayout llRoot;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
