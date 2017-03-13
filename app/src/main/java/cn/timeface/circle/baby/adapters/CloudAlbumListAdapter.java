package cn.timeface.circle.baby.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.rayboot.widget.ratioview.RatioImageView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.support.api.models.objs.CloudAlbumObj;
import cn.timeface.circle.baby.support.utils.DateUtil;

/**
 * Created by zhsheng on 2016/6/7.
 */
public class CloudAlbumListAdapter extends RecyclerView.Adapter<CloudAlbumListAdapter.CloudAlbumViewHolder> {

    private Context context;
    private List<CloudAlbumObj> dataList;

    public CloudAlbumListAdapter(Activity activity, List<CloudAlbumObj> dataList) {
        this.context = activity;
        this.dataList = dataList;
    }

    @Override
    public CloudAlbumViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View albumItem = LayoutInflater.from(context).inflate(R.layout.item_layout_cloud_album, parent, false);
        return new CloudAlbumViewHolder(albumItem);
    }

    @Override
    public void onBindViewHolder(CloudAlbumViewHolder holder, int position) {
        CloudAlbumObj cloudAlbumObj = dataList.get(position);
        String imgUrl = cloudAlbumObj.getImgUrl();
        if (!TextUtils.isEmpty(imgUrl)) {
//            GlideUtil.displayImage(imgUrl,holder.ivAlbumCover);
            Glide.with(context).load(imgUrl).thumbnail(0.1f).into(holder.ivAlbumCover);
        }
        holder.tvAlbumCount.setText(cloudAlbumObj.getContentInfo());
        holder.tvAlbumTitle.setText(cloudAlbumObj.getDesc());
        holder.tvAlbumUpdateDate.setText(DateUtil.formatDate("yyyy.MM.dd", cloudAlbumObj.getTime()));
        holder.itemView.setTag(R.string.tag_obj, cloudAlbumObj);

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class CloudAlbumViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_album_cover)
        RatioImageView ivAlbumCover;
        @Bind(R.id.tv_album_title)
        TextView tvAlbumTitle;
        @Bind(R.id.tv_album_count)
        TextView tvAlbumCount;
        @Bind(R.id.tv_album_update_date)
        TextView tvAlbumUpdateDate;
        @Bind(R.id.iv_right_arrow)
        ImageView ivRightArrow;

        public CloudAlbumViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
