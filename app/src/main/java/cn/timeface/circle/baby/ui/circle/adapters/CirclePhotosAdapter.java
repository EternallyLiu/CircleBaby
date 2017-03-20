package cn.timeface.circle.baby.ui.circle.adapters;

import android.animation.Animator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.adapters.base.BaseRecyclerAdapter;
import cn.timeface.circle.baby.constants.TypeConstants;
import cn.timeface.circle.baby.support.api.models.objs.MediaObj;
import cn.timeface.circle.baby.support.api.models.objs.MediaTipObj;
import cn.timeface.circle.baby.support.api.models.objs.MediaWrapObj;
import cn.timeface.circle.baby.support.utils.GlideUtil;
import cn.timeface.circle.baby.ui.growth.events.SelectMediaEvent;
import cn.timeface.circle.baby.views.CirclePhotoImageView;

/**
 * 圈照片展示adapter
 * Created by lidonglin on 2017/3/20.
 */
public class CirclePhotosAdapter extends BaseRecyclerAdapter<MediaWrapObj> {
    final int TYPE_TITLE = 0;
    final int TYPE_PHOTOS = 1;
    int[] lineEnd;//用于存储每个PhotoGroupItem的最大行号
    final int COLUMN_NUM = 4;
    final int maxCount;

    int contentType;

    public CirclePhotosAdapter(Context mContext, List<MediaWrapObj> listData, int maxCount, int contentType) {
        super(mContext, listData);
        this.maxCount = maxCount;
        this.contentType = contentType;
        setupData();
    }

    private void setupData() {

        int size = listData.size();
        lineEnd = new int[size];

        //重新组装下数据
        for (MediaWrapObj wrapObj : listData) {
            MediaTipObj tipObj = wrapObj.getTip();
            for (MediaObj mediaObj : wrapObj.getMediaList()) {
                mediaObj.setTip(tipObj);
            }
        }


        for (int i = 0; i < size; i++) {
            int imgCount = listData.get(i).getMediaList().size();

            //每个PhotoGroupItem需要多少行
            lineEnd[i] = imgCount / COLUMN_NUM + (imgCount % COLUMN_NUM > 0 ? 1 : 0) + 1;

            //每个PhotoGroupItem的最大行号
            if (i > 0) {
                lineEnd[i] += lineEnd[i - 1];
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder getViewHolder(ViewGroup viewGroup, int viewType) {
        if (viewType == TYPE_TITLE) {
            return new CirclePhotosAdapter.TitleViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_circle_photos_time, viewGroup, false));
        } else if (viewType == TYPE_PHOTOS) {
            return new CirclePhotosAdapter.PhotosViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_circle_photos_photo, viewGroup, false));
        }
        return null;
    }

    @Override
    public void bindData(RecyclerView.ViewHolder viewHolder, int position) {
        int viewType = getItemType(position);
        int dataPosition = getDataPosition(position);
        MediaWrapObj item = getItem(dataPosition);
        if (viewType == TYPE_TITLE) {
            CirclePhotosAdapter.TitleViewHolder holder = ((CirclePhotosAdapter.TitleViewHolder) viewHolder);
            if (contentType == TypeConstants.PHOTO_TYPE_LOCATION) {
                holder.tvTitle.setText(item.getAddress());
            } else {
                holder.tvTitle.setText(TextUtils.isEmpty(item.getDate()) ? item.getTip().getTipName() + " " + item.getMediaCount() + "张照片" : item.getDate());
            }

        } else if (viewType == TYPE_PHOTOS) {
            CirclePhotosAdapter.PhotosViewHolder holder = ((CirclePhotosAdapter.PhotosViewHolder) viewHolder);
            List<MediaObj> imgs = getLineImgObj(position);

            for (int i = 0; i < COLUMN_NUM; i++) {
                if (i < imgs.size()) {
                    holder.ivImgs[i].setVisibility(View.VISIBLE);
                    holder.ivImgs[i].setContent(imgs.get(i));
                    holder.ivImgs[i].setTag(R.string.tag_obj, imgs.get(i));
                    //选中状态
                } else {
                    holder.ivImgs[i].setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    //获取该行数据再PhotoGroupItem列表中的位置
    public int getDataPosition(int line) {
        for (int i = 0; i < lineEnd.length; i++) {
            if (line < lineEnd[i]) {
                return i;
            }
        }
        return 0;
    }

    //获取该行数据再PhotoGroupItem对象中需要显示哪些图片
    public List<MediaObj> getLineImgObj(int line) {
        int dataPos = getDataPosition(line);
        int imgCount = listData.get(dataPos).getMediaList().size();
        int innerLine = line - 1;
        if (dataPos > 0) {
            innerLine = innerLine - lineEnd[dataPos - 1];
        }
        return listData.get(dataPos).getMediaList().subList((innerLine) * COLUMN_NUM,
                Math.min((innerLine + 1) * COLUMN_NUM, imgCount));
    }

    @Override
    public int getItemType(int position) {
        if (position == 0) {
            return TYPE_TITLE;
        }

        for (int line : lineEnd) {
            if (position == line) {
                return TYPE_TITLE;
            }
        }

        return TYPE_PHOTOS;
    }

    @Override
    public int getCount() {
        return lineEnd.length > 0 ? lineEnd[lineEnd.length - 1] : 0;//最后一个存储行数
    }

    @Override
    protected Animator[] getAnimators(View var1) {
        return new Animator[0];
    }

    class TitleViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_title)
        TextView tvTitle;

        TitleViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    class PhotosViewHolder extends RecyclerView.ViewHolder {
        @Bind({R.id.iv_img0, R.id.iv_img1, R.id.iv_img2, R.id.iv_img3})
        CirclePhotoImageView[] ivImgs;

        PhotosViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }


    private int getTitleLineFromDataIndex(int dataIndex) {
        if (dataIndex == 0) {
            return 0;
        } else {
            return lineEnd[dataIndex - 1];
        }
    }



}
