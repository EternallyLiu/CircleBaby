package cn.timeface.circle.baby.adapters;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.App;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.FragmentBridgeActivity;
import cn.timeface.circle.baby.activities.PhotoRecodeDetailActivity;
import cn.timeface.circle.baby.activities.PublishActivity;
import cn.timeface.circle.baby.adapters.base.BaseRecyclerAdapter;
import cn.timeface.circle.baby.support.api.models.PhotoRecode;
import cn.timeface.circle.baby.support.api.models.objs.ImgObj;
import cn.timeface.circle.baby.support.api.models.objs.MediaObj;
import cn.timeface.circle.baby.support.api.models.objs.TimeLineObj;
import cn.timeface.circle.baby.support.utils.FastData;
import cn.timeface.circle.baby.support.utils.GlideUtil;
import cn.timeface.circle.baby.support.utils.Remember;
import cn.timeface.circle.baby.ui.timelines.Utils.JSONUtils;
import cn.timeface.circle.baby.ui.timelines.Utils.LogUtil;
import cn.timeface.circle.baby.ui.timelines.adapters.TimeLineGroupListAdapter;
import rx.Observable;

/**
 * Created by JieGuo on 1/28/16.
 */
public class PublishPhotoAdapter extends BaseRecyclerAdapter<PhotoRecode> implements View.OnClickListener {

    private static final String TAG = "PublishPhotoAdapter";
    private View.OnClickListener onClickListener;
    public static Context context;
    private ViewHolder holder;
    public PublishActivity activity;
    private LayoutInflater inflater;

    private int maxImageHeight = 240;
    private int paddingImage = 4;

    public PublishPhotoAdapter(Context mContext, List<PhotoRecode> listData) {
        super(mContext, listData);
        this.context = mContext;
        inflater = LayoutInflater.from(context);
        this.activity = (PublishActivity) mContext;
        maxImageHeight = (int) (context.getResources().getDimension(R.dimen.size_120) * 1.5f);
        paddingImage = (int) (context.getResources().getDimension(R.dimen.size_2));
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
            int count = doGrid(holder.gv, position, imgObjList);
            if (count < imgObjList.size()) {
                holder.rlPicCount.setVisibility(View.VISIBLE);
                holder.picCount.setText(String.format("共%d张图片", imgObjList.size()));
            } else holder.rlPicCount.setVisibility(View.GONE);
        } else {
            holder.rlPicCount.setVisibility(View.GONE);
            holder.gv.setVisibility(View.GONE);
        }

        holder.llHabitDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PhotoRecodeDetailActivity.class);
                PhotoRecode photoRecode = listData.get(position);
                intent.putExtra("photoRecode", photoRecode);
                intent.putExtra("position", position);
                activity.startActivityForResult(intent, activity.PHOTO_RECORD_DETAIL);
            }
        });
//        holder.gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int posi, long id) {
//                Intent intent = new Intent(context, PhotoRecodeDetailActivity.class);
//                PhotoRecode photoRecode = listData.get(position);
//                intent.putExtra("photoRecode", photoRecode);
//                intent.putExtra("position", position);
//                activity.startActivityForResult(intent, activity.PHOTO_RECORD_DETAIL);
//            }
//        });
    }


    @Override
    public int getItemType(int position) {
        return 0;
    }

    @Override
    protected Animator[] getAnimators(View var1) {
        return new Animator[0];
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.icon:
                Observable.defer(() -> Observable.just(v))
                        .subscribe(view -> {
                            int allDetailsListPosition = (int) view.getTag(R.id.icon);
                            int position = (int) view.getTag(R.id.recycler_item_click_tag);
                            PhotoRecode item = getItem(allDetailsListPosition);
                            if (item != null)
                                FragmentBridgeActivity.openBigimageFragment(v.getContext(), allDetailsListPosition, item.getMediaObjList(), item.getLocalPaths(), position, false, false);
                        }, throwable -> {
                            LogUtil.showError(throwable);
                        });
                break;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_time)
        TextView tvTime;
        @Bind(R.id.gv)
        LinearLayout gv;
        @Bind(R.id.pic_count)
        TextView picCount;
        @Bind(R.id.iv_pic_count)
        ImageView ivPicCount;
        @Bind(R.id.rl_pic_count)
        RelativeLayout rlPicCount;
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

    /**
     * 获取图片视图
     *
     * @param groupIndex
     * @param index
     * @param rowLineaylayout
     * @param mediaObj
     * @return
     */
    private View getView(int groupIndex, int index, LinearLayout rowLineaylayout, ImgObj mediaObj) {
        View view = inflater.inflate(R.layout.time_line_list_image, rowLineaylayout, false);
        ImageView imageView = (ImageView) view.findViewById(R.id.icon);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) rowLineaylayout.getLayoutParams();
        int width = (mediaObj.getWidth() * params.height) / mediaObj.getHeight();
        LinearLayout.LayoutParams itemParams = (LinearLayout.LayoutParams) view.getLayoutParams();
        if (itemParams == null)
            itemParams = new LinearLayout.LayoutParams(width, params.height);
        else {
            itemParams.height = params.height;
            itemParams.width = width;
        }
        LogUtil.showLog(TAG, "image width==" + itemParams.width + "----image height===" + itemParams.height);
        view.setLayoutParams(itemParams);
        imageView.setTag(R.id.icon, groupIndex);
        imageView.setTag(R.id.recycler_item_click_tag, index);
        imageView.setOnClickListener(this);
        GlideUtil.displayImage(mediaObj.getLocalPath(), imageView);
        return view;
    }

    private int doGrid(LinearLayout gv, int groupIndex, List<ImgObj> list) {
        gv.setVisibility(View.GONE);
        if (gv.getChildCount() > 0)
            gv.removeAllViews();
        LinearLayout rowView = null;
        int height = maxImageHeight, width = 0;
        int count = 0;
        int startIndex = count;
        for (int i = 0; i < TimeLineGroupListAdapter.MAX_ROW_COUNT; i++) {
            LogUtil.showLog(TAG, "i==" + i);
            if (count >= list.size()) {
                break;
            }
            width = 0;
            height = maxImageHeight;
            rowView = new LinearLayout(context);
            rowView.setOrientation(LinearLayout.HORIZONTAL);
            rowView.setGravity(Gravity.CENTER_HORIZONTAL);
            LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            for (int k = count; k < list.size(); k++) {
                count++;
                ImgObj mediaObj = list.get(k);
                if (mediaObj.getHeight() <= 0 || mediaObj.getWidth() <= 0) {
                    continue;
                }
                if (mediaObj.getHeight() > height)
                    width += ((mediaObj.getWidth() * height) / mediaObj.getHeight());
                else {
                    if (width <= 0) {
                        height = mediaObj.getHeight();
                        width = mediaObj.getWidth();
                    } else if (mediaObj.getHeight() == height) {
                        height = mediaObj.getHeight();
                        width += (mediaObj.getWidth());
                    } else {
                        width = width * mediaObj.getHeight() / height;
//                            width = (width * mediaObj.getH()) / width;
                        height = mediaObj.getHeight();
                        width += (mediaObj.getWidth());
                    }
                }
                LogUtil.showLog(TAG, "width==" + width + "---height==" + height + "---w==" + mediaObj.getWidth() + "----h==" + mediaObj.getHeight());
                if (width > App.mScreenWidth) {
                    break;
                } else continue;
            }
            LogUtil.showLog(TAG, "width==" + width + "---height==" + height);
            height = (App.mScreenWidth * height) / width;
            width = App.mScreenWidth;
            rowParams.height = height;
            rowView.setLayoutParams(rowParams);
            for (int j = startIndex; j < count; j++) {
                View view = getView(groupIndex, j, rowView, list.get(j));
                if (j >= startIndex && j < count - 1) {
                    view.setPadding(0, 0, (startIndex + 1) == count ? 0 : paddingImage, 0);
                } else {
                    view.setPadding(0, 0, 0, 0);
                }
                rowView.addView(view);
            }
            startIndex = count;
            gv.addView(rowView);
        }
        if (gv.getChildCount() > 0) gv.setVisibility(View.VISIBLE);
        else gv.setVisibility(View.GONE);
        return count;
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
            return view;
        }
    }
}

