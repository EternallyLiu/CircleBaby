package cn.timeface.open.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

import cn.timeface.open.R;
import cn.timeface.open.adapters.base.BaseRecyclerAdapter;
import cn.timeface.open.api.models.objs.TFOBookContentModel;
import cn.timeface.open.api.models.response.SimplePageTemplate;
import cn.timeface.open.managers.interfaces.ISelectModel;

/**
 * author: shiyan  Created on 8/10/16.
 * email : sy0725work@gmail.com
 */
public class LayoutAdapter extends BaseRecyclerAdapter<SimplePageTemplate> implements ISelectModel {

    private TFOBookContentModel selModel;

    public LayoutAdapter(Context context, List<SimplePageTemplate> listData) {
        super(context, listData);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(getContext()).inflate(R.layout.item_layout_view, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        SimplePageTemplate template = listData.get(position);

        Glide.with(getContext())
                .load(template.getTemplateCover())
                .asBitmap()
                .atMost()
                .fitCenter()
                .into(viewHolder.ivLayout);

//        if (selModel != null && (selModel.().equals(tfBookBackgroundModel.getBackgroundLeft()) || selModel.getPageImage().equals(tfBookBackgroundModel.getBackgroundRight()))) {
//            holder.itemView.setBackgroundResource(R.drawable.shape_rect_border);
//        } else {
//            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
//        }
        holder.itemView.setTag(R.string.tag_obj, template);
    }

    @Override
    public void setSelectModel(TFOBookContentModel model) {
        this.selModel = model;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivLayout;

        public ViewHolder(View view) {
            super(view);
            ivLayout = (ImageView) view.findViewById(R.id.iv_layout);
        }
    }
}
