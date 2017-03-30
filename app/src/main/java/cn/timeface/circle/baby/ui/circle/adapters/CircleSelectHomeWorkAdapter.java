package cn.timeface.circle.baby.ui.circle.adapters;

import android.animation.Animator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.adapters.base.BaseRecyclerAdapter;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.ui.circle.activities.CircleSelectHomeWorkDetailActivity;
import cn.timeface.circle.baby.ui.circle.bean.CircleHomeWorkExWrapperObj;
import cn.timeface.circle.baby.ui.circle.bean.CircleHomeworkExObj;

/**
 * 圈作品选择homework adapter
 * author : sunyanwei Created on 17-3-22
 * email : sunyanwei@timeface.cn
 */
public class CircleSelectHomeWorkAdapter extends BaseRecyclerAdapter<CircleHomeWorkExWrapperObj> {
    final int TYPE_TITLE = 0;
    final int TYPE_PHOTOS = 1;
    int[] lineEnd;//用于存储每个PhotoGroupItem的最大行号
    final int COLUMN_NUM = 1;
    final int maxCount;
    String taskId;
    int selectCount;

    List<CircleHomeworkExObj> selMedias = new ArrayList<>(10);//用于存储所有选中的图片
    int[] everyGroupUnSelImgSize;//每组数据没有被选中照片的张数，用于快速判断是否全选的状态

    public CircleSelectHomeWorkAdapter(
            Context mContext,
            List<CircleHomeWorkExWrapperObj> listData,
            int maxCount, List<CircleHomeworkExObj> mediaObjs, String taskId) {
        super(mContext, listData);
        this.maxCount = maxCount;
        this.selMedias = mediaObjs;
        this.taskId = taskId;
        setupData();
    }

    private void setupData() {
        int size = listData.size();
        lineEnd = new int[size];
        everyGroupUnSelImgSize = new int[size];

        for (int i = 0; i < size; i++) {
            int imgCount = listData.get(i).getHomeworkList().size();
            everyGroupUnSelImgSize[i] = imgCount;//默认所有都没有选中
            for (CircleHomeworkExObj circleHomeworkExObj : selMedias) {
                if (listData.get(i).getHomeworkList().contains(circleHomeworkExObj)) {
                    everyGroupUnSelImgSize[i]--;
                    selectCount++;
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
            return new TitleViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_select_photos_time, viewGroup, false));
        } else if (viewType == TYPE_PHOTOS) {
            return new HomeworkViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_circle_select_home_work, viewGroup, false));
        }
        return null;
    }

    @Override
    public void bindData(RecyclerView.ViewHolder viewHolder, int position) {
        int viewType = getItemType(position);
        int dataPosition = getDataPosition(position);
        CircleHomeWorkExWrapperObj item = getItem(dataPosition);
        if (viewType == TYPE_TITLE) {
            TitleViewHolder holder = ((TitleViewHolder) viewHolder);
            holder.tvTitle.setText(item.getDate());
            Drawable drawable = ContextCompat.getDrawable(mContext,R.drawable.ic_time_clock);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            if(!TextUtils.isEmpty(taskId)){
                holder.tvTitle.setCompoundDrawables(null, null, null, null);
                holder.tvTitle.setTextColor(ContextCompat.getColor(mContext, R.color.text_color9));
            } else {
                holder.tvTitle.setCompoundDrawables(drawable, null, null, null);
                holder.tvTitle.setTextColor(ContextCompat.getColor(mContext, R.color.text_color12));
            }
            holder.cbTitleAllSel.setTag(R.string.tag_index, position);
            holder.cbTitleAllSel.setChecked(everyGroupUnSelImgSize[dataPosition] == 0);
        } else if (viewType == TYPE_PHOTOS) {
            CircleSelectHomeWorkAdapter.HomeworkViewHolder holder = ((CircleSelectHomeWorkAdapter.HomeworkViewHolder) viewHolder);
            List<CircleHomeworkExObj> imgs = getLineImgObj(position);
            if(imgs.size() > 0){
                final CircleHomeworkExObj timeLineObj = imgs.get(0);//只会有一条数据
                if (timeLineObj.getHomework().getMediaList() != null && timeLineObj.getHomework().getMediaList().size() > 0) {
                    Glide.with(mContext)
                            .load(timeLineObj.getHomework().getMediaList().get(0).getImgUrl())
                            .centerCrop()
                            .placeholder(R.drawable.bg_default_holder_img)
                            .into(holder.ivImage);
                    holder.flImage.setVisibility(View.VISIBLE);
                } else {
                    holder.flImage.setVisibility(View.GONE);
                }
                DateUtils.formatDateTime(mContext, timeLineObj.getHomework().getCreateDate(), DateUtils.FORMAT_SHOW_WEEKDAY);
                if(!TextUtils.isEmpty(taskId)){
                    holder.tvTitle.setText(DateUtils.formatDateTime(mContext, timeLineObj.getHomework().getCreateDate(), DateUtils.FORMAT_SHOW_DATE)
                            + DateUtils.formatDateTime(mContext, timeLineObj.getHomework().getCreateDate(), DateUtils.FORMAT_SHOW_WEEKDAY));
                } else {
                    holder.tvTitle.setText(timeLineObj.getSchoolTaskName());
                }
                holder.tvImgCount.setText(String.valueOf(timeLineObj.getHomework().getMediaList().size()) + "张");
                holder.tvContent.setText(timeLineObj.getHomework().getContent());
                holder.cbSelect.setTag(R.string.tag_ex, dataPosition);
                holder.cbSelect.setTag(R.string.tag_obj, timeLineObj);
                holder.cbSelect.setChecked(selMedias.contains(timeLineObj));
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
    public List<CircleHomeworkExObj> getLineImgObj(int line) {
        int dataPos = getDataPosition(line);
        int imgCount = listData.get(dataPos).getHomeworkList().size();
        int innerLine = line - 1;
        if (dataPos > 0) {
            innerLine = innerLine - lineEnd[dataPos - 1];
        }
        return listData.get(dataPos).getHomeworkList().subList((innerLine) * COLUMN_NUM,
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
        @Bind(R.id.cb_title_all_sel)
        CheckBox cbTitleAllSel;

        TitleViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            cbTitleAllSel.setOnClickListener(onCheckedAllListener);
        }
    }

    private View.OnClickListener onCheckedAllListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int line = (int) v.getTag(R.string.tag_index);
            int dataIndex = getDataPosition(line);
            if (selMedias.size() + listData.get(dataIndex).getHomeworkList().size() > maxCount) {
                ToastUtil.showToast("最多只能选" + maxCount + "张照片");
                ((CheckBox) v).setChecked(false);
                return;
            }

            boolean isChecked = ((CheckBox) v).isChecked();
            for (CircleHomeworkExObj item : listData.get(dataIndex).getHomeworkList()) {
                if (isChecked) {
                    doSelImg(dataIndex, item);
                } else {
                    doUnSelImg(dataIndex, item);
                }
            }
            notifyDataSetChanged();
        }
    };

    private View.OnClickListener onCheckedListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            CheckBox cb = (CheckBox) v;
            CircleHomeworkExObj img = (CircleHomeworkExObj) v.getTag(R.string.tag_obj);
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
            notifyDataSetChanged();
        }
    };

    private int getTitleLineFromDataIndex(int dataIndex) {
        if (dataIndex == 0) {
            return 0;
        } else {
            return lineEnd[dataIndex - 1];
        }
    }

    private void doSelImg(int dataIndex, CircleHomeworkExObj img) {
        if (!selMedias.contains(img)) {
            //选中上传
//            UploadAllPicService.addUrgent(App.getInstance(), img);
            selMedias.add(img);
            selectCount++;
            everyGroupUnSelImgSize[dataIndex] -= 1;
            if (everyGroupUnSelImgSize[dataIndex] == 0) {
//            全选

                notifyItemChanged(getTitleLineFromDataIndex(dataIndex) + getHeaderCount());
            }

            if(mContext instanceof CircleSelectHomeWorkDetailActivity){
                ((CircleSelectHomeWorkDetailActivity) mContext).initPhotoTip();
            }
        }
    }

    private void doUnSelImg(int dataIndex, CircleHomeworkExObj img) {
        if (selMedias.contains(img)) {
            selMedias.remove(img);
            selectCount--;
            everyGroupUnSelImgSize[dataIndex] += 1;
            if (everyGroupUnSelImgSize[dataIndex] == 1) {
            //非全选
                notifyItemChanged(getTitleLineFromDataIndex(dataIndex) + getHeaderCount());
            }

            if(mContext instanceof CircleSelectHomeWorkDetailActivity){
                ((CircleSelectHomeWorkDetailActivity) mContext).initPhotoTip();
            }
        }
    }

    public void doAllUnSelImg(){
        for(CircleHomeworkExObj homeworkExObj : getHomeworkExObjList()){
            if(selMedias.contains(homeworkExObj)){
                selMedias.remove(homeworkExObj);
                selectCount--;
            }
        }
        setupData(false);
        notifyDataSetChanged();
    }

    public void doAllSelImg(){
        for(CircleHomeWorkExWrapperObj homeWorkExWrapperObj : listData){
            for(CircleHomeworkExObj homeworkExObj : homeWorkExWrapperObj.getHomeworkList()){
                if(!selMedias.contains(homeworkExObj)){
                    selMedias.add(homeworkExObj);
                    selectCount++;
                }
            }
        }

        setupData(true);
        notifyDataSetChanged();
    }

    //处理全部选中/全部不选中
    private void setupData(boolean allSelect) {
        int size = listData.size();
        lineEnd = new int[size];
        everyGroupUnSelImgSize = new int[size];

        if(allSelect){
            for(CircleHomeWorkExWrapperObj homeWorkExWrapperObj : listData){
                for(CircleHomeworkExObj homeworkExObj : homeWorkExWrapperObj.getHomeworkList()){
                    if(!selMedias.contains(homeworkExObj))selMedias.add(homeworkExObj);
                }
            }
        }
//        else {
//            selTimeLines.clear();
//        }

        for (int i = 0; i < size; i++) {
            int imgCount = listData.get(i).getHomeworkList().size();
            if(allSelect){
                everyGroupUnSelImgSize[i] = 0;
            } else {
                everyGroupUnSelImgSize[i] = imgCount;//默认所有都没有选中
                for (CircleHomeworkExObj circleTimeLineExObj : selMedias) {
                    if (listData.get(i).getHomeworkList().contains(circleTimeLineExObj)) {
                        everyGroupUnSelImgSize[i]--;
                    }
                }
            }

            //每个PhotoGroupItem需要多少行
            lineEnd[i] = imgCount / COLUMN_NUM + (imgCount % COLUMN_NUM > 0 ? 1 : 0) + 1;

            //每个PhotoGroupItem的最大行号
            if (i > 0) {
                lineEnd[i] += lineEnd[i - 1];
            }
        }
        Log.e("timesAdapter : ", String.valueOf(lineEnd[lineEnd.length - 1]));
    }

    public List<CircleHomeworkExObj> getHomeworkExObjList(){
        List<CircleHomeworkExObj> timeLineObjList = new ArrayList<>();
        for(CircleHomeWorkExWrapperObj timeLineWrapObj : getListData()){
            timeLineObjList.addAll(timeLineWrapObj.getHomeworkList());
        }
        return timeLineObjList;
    }

    public List<CircleHomeworkExObj> getSelImgs() {
        return selMedias;
    }

    public int getSelectCount() {
        return selectCount;
    }

    public void setSelImgs(ArrayList<CircleHomeworkExObj> imgs) {
        this.selMedias = imgs;
        setupData();
    }

    class HomeworkViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_image)
        ImageView ivImage;
        @Bind(R.id.tv_img_count)
        TextView tvImgCount;
        @Bind(R.id.fl_image)
        FrameLayout flImage;
        @Bind(R.id.tv_title)
        TextView tvTitle;
        @Bind(R.id.cb_select)
        CheckBox cbSelect;
        @Bind(R.id.tv_content)
        TextView tvContent;
        @Bind(R.id.ll_work_root)
        LinearLayout llWorkRoot;

        HomeworkViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            cbSelect.setOnClickListener(onCheckedListener);
        }
    }
}
