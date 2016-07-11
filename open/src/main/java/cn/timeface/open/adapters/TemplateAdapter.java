package cn.timeface.open.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import cn.timeface.open.R;
import cn.timeface.open.adapters.base.BaseRecyclerAdapter;
import cn.timeface.open.api.models.objs.TFOSimpleTemplate;
import cn.timeface.open.events.SelectTemplateEvent;

/**
 * author: rayboot  Created on 16/7/4.
 * email : sy0725work@gmail.com
 */
public class TemplateAdapter extends BaseRecyclerAdapter<TFOSimpleTemplate> {
    int selTemplateId = 0;

    public TemplateAdapter(Context mContext, List<TFOSimpleTemplate> listData) {
        super(mContext, listData);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ImageView view = new ImageView(this.getContext());
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        view.setLayoutParams(lp);
        int padding = getContext().getResources().getDimensionPixelOffset(R.dimen.view_space_small);
        view.setPadding(padding, padding, padding, padding);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        TFOSimpleTemplate template = listData.get(position);
        ImageView iv = (ImageView) holder.itemView;
        Glide.with(getContext())
                .load(template.getThumbnail())
                .fitCenter()
                .into(iv);
        if (selTemplateId == template.getTemplateId()) {
            iv.setBackgroundResource(R.drawable.shape_rect_border);
        } else {
            iv.setBackgroundColor(Color.TRANSPARENT);
        }
        holder.itemView.setTag(R.string.tag_ex, template.getTemplateId());
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            String templateId = (String) v.getTag(R.string.tag_ex);
            EventBus.getDefault().post(new SelectTemplateEvent(templateId));
        }
    }

    public int getSelTemplateId() {
        return selTemplateId;
    }

    public void setSelTemplateId(int selTemplateId) {
        this.selTemplateId = selTemplateId;
        notifyDataSetChanged();
    }
}
