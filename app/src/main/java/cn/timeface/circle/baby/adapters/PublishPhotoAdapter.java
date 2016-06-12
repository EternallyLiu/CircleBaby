package cn.timeface.circle.baby.adapters;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.PhotoRecodeDetailActivity;
import cn.timeface.circle.baby.activities.PublishActivity;
import cn.timeface.circle.baby.adapters.base.BaseRecyclerAdapter;
import cn.timeface.circle.baby.api.models.PhotoRecode;
import cn.timeface.circle.baby.api.models.objs.ImgObj;
import cn.timeface.circle.baby.utils.GlideUtil;
import cn.timeface.circle.baby.utils.Remember;

/**
 * Created by JieGuo on 1/28/16.
 */
public class PublishPhotoAdapter extends BaseRecyclerAdapter<PhotoRecode> {

    private View.OnClickListener onClickListener;
    public static Context context;
    private ViewHolder holder;
    public PublishActivity activity;

    public PublishPhotoAdapter(Context mContext, List<PhotoRecode> listData) {
        super(mContext, listData);
        this.context = mContext;
        this.activity = (PublishActivity) mContext;
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    public ViewHolder getViewHolder(ViewGroup viewGroup, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_publish_photo, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void bindData(RecyclerView.ViewHolder viewHolder, int position) {
        holder = ((ViewHolder) viewHolder);
        PhotoRecode record = getItem(position);

        holder.tvTime.setText(record.getTitle());
        holder.etContent.setText(record.getContent());

        if (record.getImgObjList().size() == 1) {
            holder.gv.setVisibility(View.GONE);
            holder.ivCover.setVisibility(View.VISIBLE);
            String url = record.getImgObjList().get(0).getLocalPath();
            GlideUtil.displayImage(url, holder.ivCover);
        } else {
            holder.ivCover.setVisibility(View.GONE);
        }

        if (record.getImgObjList().size() > 1) {
            holder.gv.setVisibility(View.VISIBLE);
            List<ImgObj> imgObjList = record.getImgObjList();
            ArrayList<String> urls = new ArrayList<>();
            for (ImgObj imgObj : imgObjList) {
                urls.add(imgObj.getLocalPath());
            }
            MyAdapter myAdapter = new MyAdapter(context, urls);
            holder.gv.setAdapter(myAdapter);
            ViewGroup.LayoutParams layoutParams = holder.gv.getLayoutParams();
            layoutParams.height = Remember.getInt("width", 0);
            if (record.getImgObjList().size() == 2) {
                layoutParams.height = Remember.getInt("width", 0) * 3 / 2;
            }
            if (record.getImgObjList().size() > 3) {
                layoutParams.height = Remember.getInt("width", 0) * 2;
            }
            if (record.getImgObjList().size() == 4) {
                layoutParams.height = Remember.getInt("width", 0) * 3;
            }

            if (record.getImgObjList().size() > 6) {
                layoutParams.height = Remember.getInt("width", 0) * 3;
            }
            holder.gv.setLayoutParams(layoutParams);

        } else {
            holder.gv.setVisibility(View.GONE);
        }

        if (record.getImgObjList().size() == 2 || record.getImgObjList().size() == 4) {
            holder.gv.setNumColumns(2);
        }

        holder.llHabitDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PhotoRecodeDetailActivity.class);
                PhotoRecode photoRecode = listData.get(position);
//                intent.putParcelableArrayListExtra("list_photorecord", (ArrayList<? extends Parcelable>) listData);
                intent.putExtra("photoRecode", photoRecode);
                intent.putExtra("position", position);
                activity.startActivityForResult(intent, activity.PHOTO_RECORD_DETAIL);
            }
        });
        holder.gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int posi, long id) {
                Intent intent = new Intent(context, PhotoRecodeDetailActivity.class);
                PhotoRecode photoRecode = listData.get(position);
//                intent.putParcelableArrayListExtra("list_photorecord", (ArrayList<? extends Parcelable>) listData);
                intent.putExtra("photoRecode", (Serializable) photoRecode);
                intent.putExtra("position", position);
                activity.startActivityForResult(intent, activity.PHOTO_RECORD_DETAIL);
            }
        });
    }


    @Override
    public int getItemType(int position) {
        return 0;
    }

    @Override
    protected Animator[] getAnimators(View var1) {
        return new Animator[0];
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_time)
        TextView tvTime;
        @Bind(R.id.gv)
        GridView gv;
        @Bind(R.id.iv_cover)
        ImageView ivCover;
        @Bind(R.id.et_content)
        TextView etContent;
        @Bind(R.id.ll_habit_detail)
        LinearLayout llHabitDetail;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    private class MyAdapter extends BaseAdapter {
        ArrayList<String> urls;

        public MyAdapter(Context context, ArrayList<String> urls) {
            this.urls = urls;
        }

        @Override
        public int getCount() {
            return urls.size() > 9 ? 9 : urls.size();
        }

        @Override
        public Object getItem(int position) {
            return urls.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(context, R.layout.item_image, null);
            ImageView iv = (ImageView) view.findViewById(R.id.iv_image);
            int width = Remember.getInt("width", 0);
            if (urls.size() == 2 || urls.size() == 4) {
                width = width * 3 / 2;
            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, width);
            iv.setLayoutParams(params);
            GlideUtil.displayImage(urls.get(position), iv);
//            iv.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    FragmentBridgeActivity.openBigimageFragment(v.getContext(), urls, position);
//                }
//            });
            return view;
        }
    }
}

