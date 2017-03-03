package cn.timeface.circle.baby.ui.growth.adapters;

import android.animation.Animator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.adapters.base.BaseRecyclerAdapter;
import cn.timeface.circle.baby.constants.TypeConstants;
import cn.timeface.circle.baby.events.PhotoSelectCountEvent;
import cn.timeface.circle.baby.support.api.models.objs.MediaObj;
import cn.timeface.circle.baby.support.api.models.objs.MediaTipObj;
import cn.timeface.circle.baby.support.api.models.objs.MediaWrapObj;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.views.PhotoSelectImageView;

/**
 * 服务器照片展示adapter
 * author : YW.SUN Created on 2017/2/14
 * email : sunyw10@gmail.com
 */
public class SelectServerPhotosAdapter extends BaseRecyclerAdapter<MediaWrapObj> {
    final int TYPE_TITLE = 0;
    final int TYPE_PHOTOS = 1;
    int[] lineEnd;//用于存储每个PhotoGroupItem的最大行号
    final int COLUMN_NUM = 4;
    final int maxCount;

    List<MediaObj> selMedias = new ArrayList<>(10);//用于存储所有选中的图片
    int[] everyGroupUnSelImgSize;//每组数据没有被选中照片的张数，用于快速判断是否全选的状态
    int contentType;

    public SelectServerPhotosAdapter(Context mContext, List<MediaWrapObj> listData, int maxCount, int contentType, List<MediaObj> mediaObjs) {
        super(mContext, listData);
        this.maxCount = maxCount;
        this.contentType = contentType;
        this.selMedias = mediaObjs;
        setupData();
    }

    private void setupData() {

        int size = listData.size();
        lineEnd = new int[size];
        everyGroupUnSelImgSize = new int[size];

        //重新组装下数据
        for(MediaWrapObj wrapObj : listData){
            MediaTipObj tipObj = wrapObj.getTip();
            for(MediaObj mediaObj : wrapObj.getMediaList()){
                mediaObj.setTip(tipObj);
            }
        }


        for (int i = 0; i < size; i++) {
            int imgCount = listData.get(i).getMediaList().size();
            everyGroupUnSelImgSize[i] = imgCount;//默认所有都没有选中
            for (MediaObj mediaObj : selMedias) {
                if (listData.get(i).getMediaList().contains(mediaObj)) {
                    everyGroupUnSelImgSize[i]--;
                }
            }

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
            return new SelectServerPhotosAdapter.TitleViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_select_photos_time, viewGroup, false));
        } else if (viewType == TYPE_PHOTOS) {
            return new SelectServerPhotosAdapter.PhotosViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_select_photos_photo, viewGroup, false));
        }
        return null;
    }

    @Override
    public void bindData(RecyclerView.ViewHolder viewHolder, int position) {
        int viewType = getItemType(position);
        int dataPosition = getDataPosition(position);
        MediaWrapObj item = getItem(dataPosition);
        if (viewType == TYPE_TITLE) {
            SelectServerPhotosAdapter.TitleViewHolder holder = ((SelectServerPhotosAdapter.TitleViewHolder) viewHolder);
            if(contentType == TypeConstants.PHOTO_TYPE_LOCATION){
                holder.tvTitle.setText(item.getAddress());
            } else {
                holder.tvTitle.setText(TextUtils.isEmpty(item.getDate()) ? item.getTip().getTipName() + " " + item.getMediaCount() + "张照片" : item.getDate());
                holder.cbAllSel.setTag(R.string.tag_index, position);
            }

            holder.cbAllSel.setChecked(everyGroupUnSelImgSize[dataPosition] == 0);
        } else if (viewType == TYPE_PHOTOS) {
            SelectServerPhotosAdapter.PhotosViewHolder holder = ((SelectServerPhotosAdapter.PhotosViewHolder) viewHolder);
            List<MediaObj> imgs = getLineImgObj(position);

            for (int i = 0; i < COLUMN_NUM; i++) {
                if (i < imgs.size()) {
                    holder.ivImgs[i].setVisibility(View.VISIBLE);
                    holder.ivImgs[i].setContent(imgs.get(i));
                    holder.ivImgs[i].getCbSel().setTag(R.string.tag_obj, imgs.get(i));

                    //选中状态
                    holder.ivImgs[i].setChecked(selMedias.contains(imgs.get(i)));
                    holder.ivImgs[i].setTag(R.string.tag_obj, imgs.get(i));
                } else {
                    holder.ivImgs[i].setVisibility(View.INVISIBLE);
                    holder.ivImgs[i].getCbSel().setTag(R.string.tag_obj, null);
                }
                holder.ivImgs[i].getCbSel().setTag(R.string.tag_ex, dataPosition);
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
        @Bind(R.id.cb_all_sel)
        CheckBox cbAllSel;

        TitleViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            cbAllSel.setOnClickListener(onCheckedAllListener);
        }
    }

    class PhotosViewHolder extends RecyclerView.ViewHolder {
        @Bind({R.id.iv_img0, R.id.iv_img1, R.id.iv_img2, R.id.iv_img3})
        PhotoSelectImageView[] ivImgs;

        PhotosViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            for (PhotoSelectImageView selView : ivImgs) {
                selView.setOnCheckedListener(onCheckedListener);
            }
        }
    }

    private View.OnClickListener onCheckedAllListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int line = (int) v.getTag(R.string.tag_index);
            int dataIndex = getDataPosition(line);
            if (selMedias.size() + listData.get(dataIndex).getMediaList().size() > maxCount) {
                ToastUtil.showToast("最多只能选" + maxCount + "张照片");
                ((CheckBox) v).setChecked(false);
                return;
            }

            boolean isChecked = ((CheckBox) v).isChecked();
            for (MediaObj item : listData.get(dataIndex).getMediaList()) {
                if (isChecked) {
                    doSelImg(dataIndex, item);
                } else {
                    doUnSelImg(dataIndex, item);
                }
            }
            EventBus.getDefault().post(new PhotoSelectCountEvent(selMedias.size()));
            notifyDataSetChanged();
        }
    };

    private View.OnClickListener onCheckedListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            CheckBox cb = (CheckBox) v;
            MediaObj img = (MediaObj) v.getTag(R.string.tag_obj);
            int dataIndex = (int) v.getTag(R.string.tag_ex);
            if (cb.isChecked()) {
                if (selMedias.size() + 1 > maxCount) {
                    ToastUtil.showToast("最多只能选" + maxCount + "张照片");
                    ((CheckBox) v).setChecked(false);
                    return;
                }
                doSelImg(dataIndex, img);
            } else {
                doUnSelImg(dataIndex, img);
            }
            EventBus.getDefault().post(new PhotoSelectCountEvent(selMedias.size()));
        }
    };

    private int getTitleLineFromDataIndex(int dataIndex) {
        if (dataIndex == 0) {
            return 0;
        } else {
            return lineEnd[dataIndex - 1];
        }
    }

    private void doSelImg(int dataIndex, MediaObj img) {
        if (!selMedias.contains(img)) {
            //选中上传
//            UploadAllPicService.addUrgent(App.getInstance(), img);
            selMedias.add(img);
            everyGroupUnSelImgSize[dataIndex] -= 1;
            if (everyGroupUnSelImgSize[dataIndex] == 0) {
                //全选

                notifyItemChanged(getTitleLineFromDataIndex(dataIndex) + getHeaderCount());
            }
        }
    }

    private void doUnSelImg(int dataIndex, MediaObj img) {
        if (selMedias.contains(img)) {
            //取消上传
//            UploadAllPicService.addUrgent(App.getInstance(), img);
            selMedias.remove(img);
            everyGroupUnSelImgSize[dataIndex] += 1;
            if (everyGroupUnSelImgSize[dataIndex] == 1) {
                //非全选
                notifyItemChanged(getTitleLineFromDataIndex(dataIndex) + getHeaderCount());
            }
        }
    }

    public List<MediaObj> getSelImgs() {
        return selMedias;
    }

    public void setSelImgs(ArrayList<MediaObj> imgs) {
        this.selMedias = imgs;
        setupData();
    }
}
