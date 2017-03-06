package cn.timeface.circle.baby.ui.growth.adapters;

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
import cn.timeface.circle.baby.support.api.models.objs.MediaObj;
import cn.timeface.circle.baby.ui.growth.events.SelectMediaEvent;
import cn.timeface.circle.baby.views.PhotoSelectImageView;

/**
 * 选择时光详情页面图片adapter
 * author : YW.SUN Created on 2017/2/20
 * email : sunyw10@gmail.com
 */
public class SelectServerTimeMediaAdapter extends BaseRecyclerAdapter<MediaObj> {
    private List<MediaObj> selMedias = new ArrayList<>();

    public SelectServerTimeMediaAdapter(Context mContext, List<MediaObj> listData, List<MediaObj> selMedias) {
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
        final MediaObj mediaObj = listData.get(position);
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
            MediaObj mediaObj = (MediaObj) view.getTag(R.string.tag_obj);
            EventBus.getDefault().post(new SelectMediaEvent(((CheckBox)view).isChecked(), mediaObj));
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
