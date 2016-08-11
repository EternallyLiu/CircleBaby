package cn.timeface.open.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

import cn.timeface.open.R;
import cn.timeface.open.adapters.base.BaseRecyclerAdapter;
import cn.timeface.open.api.models.objs.TFOBookImageModel;

/**
 * author: rayboot  Created on 16/7/4.
 * email : sy0725work@gmail.com
 */
public class PendantAdapter extends BaseRecyclerAdapter<TFOBookImageModel> {

    public PendantAdapter(Context mContext, List<TFOBookImageModel> listData) {
        super(mContext, listData);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(getContext()).inflate(R.layout.item_pendant_view, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        TFOBookImageModel template = listData.get(position);

        Glide.with(getContext())
                .load(template.getImageUrl())
                .asBitmap()
                .atMost()
                .fitCenter()
                .into(viewHolder.ivPendant);

        holder.itemView.setTag(R.string.tag_obj, template);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPendant;

        public ViewHolder(View view) {
            super(view);
            ivPendant = (ImageView) view.findViewById(R.id.iv_pendant);
            ivPendant.setVisibility(View.VISIBLE);
        }
    }
}
