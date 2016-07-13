package cn.timeface.open.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import cn.timeface.open.R;
import cn.timeface.open.adapters.base.BaseRecyclerAdapter;
import cn.timeface.open.events.SelectColorEvent;
import cn.timeface.open.views.CircleImageView;

/**
 * Created by wswd on 2015/5/28.
 */
public class CoverColorAdapter extends BaseRecyclerAdapter<String> {


    public void setSelectedColor(String selectedColor) {
        this.selectedColor = selectedColor;
    }

    private String selectedColor = "#ffffff";
    private int focusPosition;

    public CoverColorAdapter(Context mContext, List<String> listData) {
        super(mContext, listData);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CircleImageView view = new CircleImageView(this.getContext());
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT);
        lp.height = getContext().getResources().getDimensionPixelOffset(R.dimen.size_56);
        lp.width = getContext().getResources().getDimensionPixelOffset(R.dimen.size_56);
        lp.topMargin = getContext().getResources().getDimensionPixelOffset(R.dimen.view_space_small);
        view.setLayoutParams(lp);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        String color = listData.get(position);
        CircleImageView iv = (CircleImageView) holder.itemView;
        if (selectedColor.equals(color)) {
            iv.setBorderColor(Color.RED);
        } else {
            iv.setBorderColor(Color.WHITE);
        }
        GradientDrawable drawable = (GradientDrawable) getContext().getResources().getDrawable(R.drawable.cover_bg_color_boder);
        drawable.setColor(Color.parseColor(color));
        iv.setImageDrawable(drawable);
        holder.itemView.setTag(R.string.tag_ex, color);
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            String color = (String) v.getTag(R.string.tag_ex);
            EventBus.getDefault().post(new SelectColorEvent(color));
        }
    }

    public int getFocusPosition() {
        return focusPosition;
    }

}
