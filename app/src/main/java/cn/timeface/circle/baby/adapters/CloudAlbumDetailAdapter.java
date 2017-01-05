package cn.timeface.circle.baby.adapters;

import android.animation.Animator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.VideoPlayActivity;
import cn.timeface.circle.baby.adapters.base.BaseRecyclerAdapter;
import cn.timeface.circle.baby.support.api.models.objs.MediaObj;
import cn.timeface.circle.baby.support.utils.DateUtil;
import cn.timeface.circle.baby.support.utils.GlideUtil;

/**
 * Created by zhsheng on 2016/6/8.
 */
public class CloudAlbumDetailAdapter extends BaseRecyclerAdapter<MediaObj> {

    private boolean editState;

    public CloudAlbumDetailAdapter(Context mContext, List<MediaObj> listData) {
        super(mContext, listData);
    }

    public void setAlbumEditState(boolean isEdit) {
        this.editState = isEdit;
    }

    @Override
    public RecyclerView.ViewHolder getViewHolder(ViewGroup viewGroup, int viewType) {
        View albumView = LayoutInflater.from(mContext).inflate(R.layout.item_cloud_album_detail, viewGroup, false);
        return new AlbumDetailHolder(albumView);
    }

    @Override
    public void bindData(RecyclerView.ViewHolder viewHolder, int position) {
        AlbumDetailHolder holder = (AlbumDetailHolder) viewHolder;
        MediaObj detailObj = listData.get(position);
        GlideUtil.displayImage(detailObj.getImgUrl(),holder.ivAlbumImage);
        holder.editInput.setEnabled(editState);
        holder.editInput.setHint("点击填写文字");
        holder.editInput.setHintTextColor(mContext.getResources().getColor(R.color.text_color_hint));
        String content = detailObj.getContent();
        if(TextUtils.isEmpty(detailObj.getVideoUrl())){
            holder.ivVideo.setVisibility(View.GONE);
        }else{
            holder.ivVideo.setVisibility(View.VISIBLE);
        }
        if (editState) {//编辑状态
            holder.editInput.setText(content);
            holder.editInput.setVisibility(View.VISIBLE);
            holder.ivDeleteImg.setVisibility(View.VISIBLE);
            holder.ivDeleteImg.setTag(R.string.tag_obj, detailObj);
        } else {//正常状态
            holder.ivDeleteImg.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(content)) {
                holder.editInput.setText(content);
                holder.editInput.setVisibility(View.VISIBLE);
            } else {
                holder.editInput.setVisibility(View.GONE);
            }
            holder.ivAlbumImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!TextUtils.isEmpty(detailObj.getVideoUrl())){
                        System.out.println("detailObj.getVideoUrl() ============ "+detailObj.getVideoUrl());
                        VideoPlayActivity.open(mContext,detailObj.getVideoUrl());
                    }
                }
            });
        }
        long photographTime = detailObj.getPhotographTime();
        String formatDate = "";
        if (photographTime != 0) {
            formatDate = DateUtil.formatDate("yyyy.MM.dd", photographTime);
        }
        if (!TextUtils.isEmpty(formatDate)) {
            holder.tvDate.setVisibility(View.VISIBLE);
            holder.tvDate.setText(formatDate);
        } else {
            holder.tvDate.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemType(int position) {
        return 0;
    }

    @Override
    protected Animator[] getAnimators(View var1) {
        return new Animator[0];
    }

    class AlbumDetailHolder extends RecyclerView.ViewHolder implements TextWatcher {
        @Bind(R.id.iv_album_image)
        ImageView ivAlbumImage;
        @Bind(R.id.iv_delete_img)
        ImageView ivDeleteImg;
        @Bind(R.id.iv_video)
        ImageView ivVideo;
        @Bind(R.id.et_inputText)
        EditText editInput;
        @Bind(R.id.tv_date)
        TextView tvDate;

        public AlbumDetailHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            editInput.addTextChangedListener(this);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            int adapterPosition = getAdapterPosition();
            MediaObj detailObj = listData.get(adapterPosition);
            detailObj.setContent(editInput.getText().toString());
        }
    }
}
