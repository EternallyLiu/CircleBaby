package cn.timeface.circle.baby.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.api.models.objs.CloudAlbumDetailObj;
import cn.timeface.circle.baby.utils.DateUtil;

/**
 * Created by zhsheng on 2016/6/8.
 */
public class CloudAlbumDetailAdapter extends RecyclerView.Adapter<CloudAlbumDetailAdapter.AlbumDetailHolder> {

    private Activity context;
    private List<CloudAlbumDetailObj> albumDetailObjs;
    private boolean editState;
    private int currentVisibleItemPosition;

    public CloudAlbumDetailAdapter(Activity activity, List<CloudAlbumDetailObj> albumDetailObjs) {
        this.context = activity;
        this.albumDetailObjs = albumDetailObjs;
    }

    public void setAlbumEditState(boolean isEdit) {
        this.editState = isEdit;
    }

    public void setCurrentVisibleItemState(int position) {
        this.currentVisibleItemPosition = position;
    }

    @Override
    public CloudAlbumDetailAdapter.AlbumDetailHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View albumView = LayoutInflater.from(context).inflate(R.layout.item_cloud_album_detail, parent, false);
        return new AlbumDetailHolder(albumView);
    }

    @Override
    public void onBindViewHolder(CloudAlbumDetailAdapter.AlbumDetailHolder holder, int position) {
        CloudAlbumDetailObj detailObj = albumDetailObjs.get(position);
        Glide.with(context)
                .load(detailObj.getImgUrl())
                .into(holder.ivAlbumImage);
        holder.editInput.setEnabled(editState);
        String content = detailObj.getContent();
        holder.editInput.setText(TextUtils.isEmpty(content) ? DateUtil.formatDate("yyyy.MM.dd", detailObj.getPhotographTime()) : content);
        if (editState) {
            if (position == currentVisibleItemPosition) {
                holder.editInput.setSelection(holder.editInput.getText().length());
                holder.editInput.requestFocus();
            }
        }
    }

    @Override
    public int getItemCount() {
        return albumDetailObjs.size();
    }

    class AlbumDetailHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_album_image)
        ImageView ivAlbumImage;
        @Bind(R.id.et_inputText)
        EditText editInput;

        public AlbumDetailHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
