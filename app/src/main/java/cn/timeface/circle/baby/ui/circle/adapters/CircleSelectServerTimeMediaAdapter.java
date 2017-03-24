package cn.timeface.circle.baby.ui.circle.adapters;

import android.animation.Animator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.adapters.base.BaseRecyclerAdapter;
import cn.timeface.circle.baby.ui.circle.bean.CircleMediaObj;
import cn.timeface.circle.baby.ui.circle.events.CircleSelectMediaEvent;
import cn.timeface.circle.baby.views.PhotoSelectImageView;

/**
 * 圈选择时光详情页面图片adapter
 * author : sunyanwei Created on 17-3-21
 * email : sunyanwei@timeface.cn
 */
public class CircleSelectServerTimeMediaAdapter extends BaseRecyclerAdapter<CircleMediaObj> {
    private List<CircleMediaObj> selMedias = new ArrayList<>();

    public CircleSelectServerTimeMediaAdapter(Context mContext, List<CircleMediaObj> listData, List<CircleMediaObj> selMedias) {
        super(mContext, listData);
        this.selMedias = selMedias;
    }

    @Override
    public RecyclerView.ViewHolder getViewHolder(ViewGroup viewGroup, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_select_server_time_photo, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void bindData(RecyclerView.ViewHolder viewHolder, int position) {
        final CircleMediaObj mediaObj = listData.get(position);
        ViewHolder holder = (ViewHolder) viewHolder;
        holder.ivImg.setContent(mediaObj);
        holder.ivImg.setChecked(selMedias.contains(mediaObj));
        holder.ivImg.getCbSel().setTag(R.string.tag_obj, mediaObj);
        holder.ivImg.setOnCheckedListener(onCheckedListener);
    }

    @Override
    public int getItemType(int position) {
        return 0;
    }

    @Override
    protected Animator[] getAnimators(View var1) {
        return new Animator[0];
    }

    private View.OnClickListener onCheckedListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            CircleMediaObj mediaObj = (CircleMediaObj) view.getTag(R.string.tag_obj);
            EventBus.getDefault().post(new CircleSelectMediaEvent(CircleSelectMediaEvent.TYPE_TIME_MEDIA, ((CheckBox)view).isChecked(), mediaObj));
        }
    };

    class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_img)
        PhotoSelectImageView ivImg;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
