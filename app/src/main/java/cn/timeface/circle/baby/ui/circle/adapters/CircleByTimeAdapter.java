package cn.timeface.circle.baby.ui.circle.adapters;

import android.animation.Animator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.adapters.base.BaseRecyclerAdapter;
import cn.timeface.circle.baby.ui.circle.bean.CirclePhotoMonthObj;
import cn.timeface.circle.baby.ui.circle.bean.QueryByCircleTimeObj;

/**
 * 圈照片按时间adapter
 * Created by lidonglin on 2017/3/18.
 */
public class CircleByTimeAdapter extends BaseRecyclerAdapter<QueryByCircleTimeObj> {
    View.OnClickListener clickListener;
    private final int TYPE_YEAR = 0;
    private final int TYPE_MONTH = 1;
    List<Integer> titles = new ArrayList<>();
    List<CirclePhotoMonthObj> monthList = new ArrayList<>();


    public CircleByTimeAdapter(Context mContext, List<QueryByCircleTimeObj> listData, View.OnClickListener clickListener) {
        super(mContext, listData);
        this.clickListener = clickListener;
        int count = -1;
        for (QueryByCircleTimeObj timeObj : listData) {
            if (timeObj.getMonthList().size() > 0) {
                count++;
                titles.add(count);
                count = count + timeObj.getMonthList().size();

                monthList.add(new CirclePhotoMonthObj(timeObj.getMediaCount(), timeObj.getYear()));
                for (CirclePhotoMonthObj monthObj : timeObj.getMonthList()) {
                    monthObj.setYear(timeObj.getYear());
                    monthList.add(monthObj);
                }
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder getViewHolder(ViewGroup viewGroup, int viewType) {
        View view = null;
        if (viewType == TYPE_YEAR) {
            view = mLayoutInflater.inflate(R.layout.item_circlebytimetitle, null);
            return new TitleViewHolder(view);
        } else{
            view = mLayoutInflater.inflate(R.layout.item_circlebytime, null);
            return new ViewHolder(view);
        }
    }

    @Override
    public void bindData(RecyclerView.ViewHolder viewHolder, int position) {
        final CirclePhotoMonthObj obj = monthList.get(position);
//        ViewHolder holder = (ViewHolder) viewHolder;
//        holder.tvUserName.setText(obj.getUserInfo().getCircleNickName());
//        holder.tvPhotoCount.setText(obj.getMediaCount() + "张");
//        Glide.with(mContext)
//                .load(obj.getUserInfo().getCircleAvatarUrl())
//                .into(holder.ivAvatar);
//        holder.llRoot.setTag(R.string.tag_obj, obj);
//        if (clickListener != null) holder.llRoot.setOnClickListener(clickListener);


        if (viewHolder instanceof TitleViewHolder) {
            ((TitleViewHolder) viewHolder).tvYear.setText(obj.getMonth());
            ((TitleViewHolder) viewHolder).tvCount.setText(mContext.getString(R.string.circlephoto_count,obj.getMediaCount()));
        } else {
            ((ViewHolder) viewHolder).tvMonth.setText(obj.getMonth());
            ((ViewHolder) viewHolder).tvCount.setText(mContext.getString(R.string.circlephoto_count,obj.getMediaCount()));
            Glide.with(mContext)
                    .load(obj.getMediaUrl())
                    .into(((ViewHolder) viewHolder).ivMonth);
            ((ViewHolder) viewHolder).rlMonth.setTag(R.string.tag_obj, obj);
            if (clickListener != null) ((ViewHolder) viewHolder).rlMonth.setOnClickListener(clickListener);
        }

    }

    @Override
    public int getItemType(int position) {
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        if (titles.size() > 0 && titles.contains(position)) {
            return TYPE_YEAR;
        } else {
            return TYPE_MONTH;
        }
    }

    @Override
    public int getCount() {
        return monthList.size();
    }

    @Override
    protected Animator[] getAnimators(View var1) {
        return new Animator[0];
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_month)
        ImageView ivMonth;
        @Bind(R.id.tv_month)
        TextView tvMonth;
        @Bind(R.id.tv_count)
        TextView tvCount;
        @Bind(R.id.rl_month)
        RelativeLayout rlMonth;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    static class TitleViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_year)
        TextView tvYear;
        @Bind(R.id.tv_count)
        TextView tvCount;

        TitleViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
