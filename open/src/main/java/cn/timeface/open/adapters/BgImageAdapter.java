package cn.timeface.open.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

import cn.timeface.open.R;
import cn.timeface.open.adapters.base.BaseRecyclerAdapter;
import cn.timeface.open.api.models.objs.TFBookBackgroundModel;

/**
 * author: rayboot  Created on 16/7/4.
 * email : sy0725work@gmail.com
 */
public class BgImageAdapter extends BaseRecyclerAdapter<TFBookBackgroundModel> {

    private String selBgImage;

    public BgImageAdapter(Context mContext, List<TFBookBackgroundModel> listData) {
        super(mContext, listData);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(getContext()).inflate(R.layout.item_bg_view, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        TFBookBackgroundModel tfBookBackgroundModel = listData.get(position);

        if (!TextUtils.isEmpty(tfBookBackgroundModel.getBackgroundLeft())) {
            viewHolder.ivBgLeft.setVisibility(View.VISIBLE);
            Glide.with(getContext())
                    .load(tfBookBackgroundModel.getBackgroundLeft())
                    .asBitmap()
                    .atMost()
                    .fitCenter()
                    .into(viewHolder.ivBgLeft);
        } else {
            viewHolder.ivBgLeft.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(tfBookBackgroundModel.getBackgroundRight())) {
            viewHolder.ivBgRight.setVisibility(View.VISIBLE);
            Glide.with(getContext())
                    .load(tfBookBackgroundModel.getBackgroundRight())
                    .asBitmap()
                    .atMost()
                    .fitCenter()
                    .into(viewHolder.ivBgRight);
        } else {
            viewHolder.ivBgRight.setVisibility(View.GONE);
        }
        if (selBgImage != null && (selBgImage.equals(tfBookBackgroundModel.getBackgroundLeft()) || selBgImage.equals(tfBookBackgroundModel.getBackgroundRight()))) {
            holder.itemView.setBackgroundResource(R.drawable.shape_rect_border);
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
        }
        holder.itemView.setTag(R.string.tag_obj, tfBookBackgroundModel);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivBgLeft;
        ImageView ivBgRight;

        public ViewHolder(View view) {
            super(view);
            ivBgLeft = (ImageView) view.findViewById(R.id.iv_bg_left);
            ivBgRight = (ImageView) view.findViewById(R.id.iv_bg_right);
        }
    }

    public String getSelBgImage() {
        return selBgImage;
    }

    public void setSelBgImage(String bgImageUrl) {
        this.selBgImage = bgImageUrl;
        notifyDataSetChanged();
    }
}
