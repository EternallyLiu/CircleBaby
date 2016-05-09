package cn.timeface.circle.baby.adapters;

import android.animation.Animator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.FragmentBridgeActivity;
import cn.timeface.circle.baby.adapters.base.BaseRecyclerAdapter;
import cn.timeface.circle.baby.api.models.objs.ImgObj;
import cn.timeface.circle.baby.api.models.objs.Record;
import cn.timeface.circle.baby.api.models.objs.RecordObj;
import cn.timeface.circle.baby.utils.GlideUtil;
import cn.timeface.circle.baby.utils.Remember;

/**
 * Created by JieGuo on 1/28/16.
 */
public class RecodeAdapter extends BaseRecyclerAdapter<Record> {

    private View.OnClickListener onClickListener;
    int normalColor;
    public static Context context;
    private ViewHolder holder;
    public List<Record> listData;

    public RecodeAdapter(Context mContext, List<Record> listData) {
        super(mContext, listData);
        this.listData = listData;
        this.context = mContext;
        normalColor = mContext.getResources().getColor(R.color.gray_normal);
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    public ViewHolder getViewHolder(ViewGroup viewGroup, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_recode, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void bindData(RecyclerView.ViewHolder viewHolder, int position) {
        holder = ((ViewHolder) viewHolder);
        Record record = getItem(position);
//        GlideUtil.displayImage(record.getRecordObj().getUserInfo().getAvatar(), holder.ivAvatar);
        holder.tvContent.setText(record.getRecordObj().getContent());

        if (record.getRecordObj().getImgObjList().size() == 1) {
            holder.gv.setVisibility(View.GONE);
            holder.rflPicture.setVisibility(View.VISIBLE);
            String url = record.getRecordObj().getImgObjList().get(0).getUrl();
            GlideUtil.displayImage(url, holder.ivCover);
//            holder.tvTag.setText("共" + record.getRecordObj().getImgObjList().size() + "张");
        } else {
            holder.rflPicture.setVisibility(View.GONE);
        }

        if (record.getRecordObj().getImgObjList().size() > 1) {
            holder.gv.setVisibility(View.VISIBLE);
            List<ImgObj> imgObjList = record.getRecordObj().getImgObjList();
            ArrayList<String> urls = new ArrayList<>();
            for (ImgObj imgObj : imgObjList) {
                urls.add(imgObj.getUrl());
            }
            MyAdapter myAdapter = new MyAdapter(context, urls);
            holder.gv.setAdapter(myAdapter);
            ViewGroup.LayoutParams layoutParams = holder.gv.getLayoutParams();
            layoutParams.height = Remember.getInt("width", 0);
            if (record.getRecordObj().getImgObjList().size() > 3) {
                layoutParams.height = Remember.getInt("width", 0) * 2;
            }
            if (record.getRecordObj().getImgObjList().size() > 6) {
                layoutParams.height = Remember.getInt("width", 0) * 3;
            }
            holder.gv.setLayoutParams(layoutParams);

        } else {
            holder.gv.setVisibility(View.GONE);
        }

        holder.onClickListener = onClickListener;
        holder.itemView.setTag(R.string.tag_ex, record.getRecordObj());
        holder.setRecordObj(record.getRecordObj());


//        if (record.getGoodList().size() > 0) {
//            holder.extrasHolder.flListUserBar.setVisibility(View.VISIBLE);
//            holder.extrasHolder.tvLikeCount.setText(record.getGoodList().size() + "人赞");
//        } else {
//            holder.extrasHolder.flListUserBar.setVisibility(View.GONE);
//        }
//        int comments = record.getCommentList().size() > 3 ? 3 : record.getCommentList().size();
//        for (int i = 0; i < comments; i++) {
//            CommentObj commentObj = record.getCommentList().get(i);
//            holder.extrasHolder.llCommentWrapper.addView(initCommentItemView(commentObj));
//        }
//
//        if (record.getCommentList().size() > 3) {
//            holder.extrasHolder.tvMoreComment.setVisibility(View.VISIBLE);
//        } else {
//            holder.extrasHolder.tvMoreComment.setVisibility(View.GONE);
//        }
//
//        for (UserObj u : record.getGoodList()) {
//            ImageView imageView = initPraiseItem();
//            imageView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    FragmentBridgeActivity.openUserInfoFragment(v.getContext(), u);
//                }
//            });
//            holder.extrasHolder.llGoodListUsersBar.addView(imageView);
//            GlideUtil.displayImage(u.getAvatar(), imageView);
//        }
    }


//    private static ImageView initPraiseItem() {
//        CircleImageView imageView = new CircleImageView(context);
//        imageView.setImageResource(R.color.gray_pressed);
//        int width = context.getResources().getDimensionPixelSize(R.dimen.size_36);
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, width);
//        int margin = context.getResources().getDimensionPixelSize(R.dimen.size_2);
//        params.setMargins(margin, margin, margin, margin);
//        imageView.setLayoutParams(params);
//        return imageView;
//    }

//    private TextView initCommentItemView(CommentObj comment) {
//        TextView textView = new TextView(context);
//        int size = context.getResources().getDimensionPixelSize(R.dimen.size_2);
//        textView.setPadding(size, size, size, size);
//        textView.setText(Html.fromHtml("<font color='#00b6f8'>name</font>".replace("name", comment.getUserinfo().getUserName()) + ":" + comment.getContent()));
//        textView.setTextColor(context.getResources().getColorStateList(R.color.text_color6));
////        textView.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.text_small_4));
//        textView.setLineSpacing(0, 1.2f);
//        return textView;
//    }

    @Override
    public int getItemType(int position) {
        return 0;
    }

    @Override
    protected Animator[] getAnimators(View var1) {
        return new Animator[0];
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @Bind(R.id.tv_day)
        TextView tvDay;
        @Bind(R.id.tv_year)
        TextView tvYear;
        @Bind(R.id.tv_jieqi)
        TextView tvJieqi;
        @Bind(R.id.tv_age)
        TextView tvAge;
        @Bind(R.id.gv)
        GridView gv;
        @Bind(R.id.fl_photo)
        FrameLayout flPhoto;
        @Bind(R.id.iv_cover)
        ImageView ivCover;
        @Bind(R.id.rfl_picture)
        FrameLayout rflPicture;
        @Bind(R.id.tv_content)
        TextView tvContent;
        @Bind(R.id.ll_habit_detail)
        LinearLayout llHabitDetail;

        View.OnClickListener onClickListener = null;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            itemView.setOnClickListener(this);
//            ivCover.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onClickListener != null) {
                onClickListener.onClick(v);
            }
        }

        public void setRecordObj(RecordObj recordObj) {
//            ivCover.setTag(R.string.tag_ex, recordObj);
            itemView.setTag(R.string.tag_ex, recordObj);
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
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, width);
            iv.setLayoutParams(params);
            GlideUtil.displayImage(urls.get(position), iv);
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentBridgeActivity.openBigimageFragment(v.getContext(), urls, position);
                }
            });
            return view;
        }
    }
}

