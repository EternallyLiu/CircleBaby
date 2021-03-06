package cn.timeface.circle.baby.ui.circle.adapters;

import android.animation.Animator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.adapters.base.BaseRecyclerAdapter;
import cn.timeface.circle.baby.events.TimeSelectCountEvent;
import cn.timeface.circle.baby.support.api.models.objs.MediaObj;
import cn.timeface.circle.baby.support.utils.ToastUtil;
import cn.timeface.circle.baby.ui.circle.bean.CircleMediaObj;
import cn.timeface.circle.baby.ui.circle.bean.CircleTimeLineExObj;
import cn.timeface.circle.baby.ui.circle.bean.CircleTimeLineWrapperObj;
import cn.timeface.circle.baby.ui.circle.events.CircleSelectTimeLineEvent;

/**
 * 圈时光选择时光adapter
 * author : sunyanwei Created on 17-3-20
 * email : sunyanwei@timeface.cn
 */
public class CircleSelectServerTimesAdapter extends BaseRecyclerAdapter<CircleTimeLineWrapperObj> {
    final int TYPE_TITLE = 0;
    final int TYPE_TIMES = 1;
    int[] lineEnd;//用于存储每个PhotoGroupItem的最大行号
    final int COLUMN_NUM = 1;
    final int maxCount;

    List<CircleTimeLineExObj> selTimeLines = new ArrayList<>(10);//用于存储所有选中的时光
    List<CircleMediaObj> selMedias = new ArrayList<>(10);
    int[] everyGroupUnSelImgSize;//每组数据没有被选中照片的张数，用于快速判断是否全选的状态
    View.OnClickListener clickListener;

    public CircleSelectServerTimesAdapter(
            Context mContext,
            List<CircleTimeLineWrapperObj> listData,
            int maxCount,
            View.OnClickListener clickListener,
            List<CircleMediaObj> mediaObjs,
            List<CircleTimeLineExObj> timeLineObjs) {
        super(mContext, listData);
        this.maxCount = maxCount;
        this.clickListener = clickListener;
        this.selMedias = mediaObjs;
        this.selTimeLines = timeLineObjs;
        setupData();
    }

    private void setupData() {
        int size = listData.size();
        lineEnd = new int[size];
        everyGroupUnSelImgSize = new int[size];

        for (int i = 0; i < size; i++) {
            int imgCount = listData.get(i).getTimelineList().size();
            everyGroupUnSelImgSize[i] = imgCount;//默认所有都没有选中
            for (CircleTimeLineExObj circleTimeLineExObj : selTimeLines) {
                if (listData.get(i).getTimelineList().contains(circleTimeLineExObj)) {
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

    //处理全部选中/全部不选中
    private void setupData(boolean allSelect) {
        int size = listData.size();
        lineEnd = new int[size];
        everyGroupUnSelImgSize = new int[size];

        if(allSelect){
            for(CircleTimeLineWrapperObj timeLineWrapObj : listData){
                for(CircleTimeLineExObj timeLineExObj : timeLineWrapObj.getTimelineList()){
                    if(!selTimeLines.contains(timeLineExObj))selTimeLines.add(timeLineExObj);
                }
            }
        } else {
            selTimeLines.clear();
        }

        for (int i = 0; i < size; i++) {
            int imgCount = listData.get(i).getTimelineList().size();
            if(allSelect){
                everyGroupUnSelImgSize[i] = 0;
            } else {
                everyGroupUnSelImgSize[i] = imgCount;//默认所有都没有选中
                for (CircleTimeLineExObj circleTimeLineExObj : selTimeLines) {
                    if (listData.get(i).getTimelineList().contains(circleTimeLineExObj)) {
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

    @Override
    public RecyclerView.ViewHolder getViewHolder(ViewGroup viewGroup, int viewType) {
        if (viewType == TYPE_TITLE) {
            return new TitleViewHolder(mLayoutInflater.inflate(R.layout.item_circle_select_photos_time, viewGroup, false));
        } else if (viewType == TYPE_TIMES) {
            return new TimesViewHolder(mLayoutInflater.inflate(R.layout.item_select_times, viewGroup, false));
        }
        return null;
    }

    @Override
    public void bindData(RecyclerView.ViewHolder viewHolder, int position) {
        int viewType = getItemType(position);
        int dataPosition = getDataPosition(position);
        CircleTimeLineWrapperObj item = getItem(dataPosition);
        if (viewType == TYPE_TITLE) {
            CircleSelectServerTimesAdapter.TitleViewHolder holder = ((CircleSelectServerTimesAdapter.TitleViewHolder) viewHolder);
            holder.tvTitle.setText(item.getDate());
            holder.cbTitleAllSel.setTag(R.string.tag_index, position);

            holder.cbTitleAllSel.setChecked(everyGroupUnSelImgSize[dataPosition] == 0);
        } else if (viewType == TYPE_TIMES) {
            CircleSelectServerTimesAdapter.TimesViewHolder holder = ((CircleSelectServerTimesAdapter.TimesViewHolder) viewHolder);
            List<CircleTimeLineExObj> imgs = getLineImgObj(position);
            if(imgs.size() > 0){
                final CircleTimeLineExObj timeLineObj = imgs.get(0);//只会有一条数据
                if (timeLineObj.getCircleTimeline().getMediaList().size() > 0) {
                    Glide.with(mContext)
                            .load(timeLineObj.getCircleTimeline().getMediaList().get(0).getImgUrl())
                            .centerCrop()
                            .placeholder(R.drawable.bg_default_holder_img)
                            .into(holder.ivImage);
                    holder.flImage.setVisibility(View.VISIBLE);
                } else {
                    holder.flImage.setVisibility(View.GONE);
                }
                holder.tvTitle.setText(DateUtils.formatDateTime(mContext, timeLineObj.getCircleTimeline().getCreateDate(), DateUtils.FORMAT_SHOW_DATE)
                        + DateUtils.formatDateTime(mContext, timeLineObj.getCircleTimeline().getCreateDate(), DateUtils.FORMAT_SHOW_WEEKDAY));
                holder.tvImgCount.setText(String.valueOf(timeLineObj.getCircleTimeline().getMediaList().size()) + "张");
                holder.tvContent.setText(timeLineObj.getCircleTimeline().getContent());
                holder.cbSelect.setTag(R.string.tag_ex, dataPosition);
                holder.cbSelect.setTag(R.string.tag_obj, timeLineObj);

                holder.cbSelect.setChecked(selTimeLines.contains(timeLineObj));
                if(clickListener != null) holder.llRoot.setOnClickListener(clickListener);
                holder.llRoot.setTag(R.string.tag_obj, timeLineObj);
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
    public List<CircleTimeLineExObj> getLineImgObj(int line) {
        int dataPos = getDataPosition(line);
        int imgCount = listData.get(dataPos).getTimelineList().size();
        int innerLine = line - 1;
        if (dataPos > 0) {
            innerLine = innerLine - lineEnd[dataPos - 1];
        }
        return listData.get(dataPos).getTimelineList().subList((innerLine) * COLUMN_NUM,
                Math.min((innerLine + 1) * COLUMN_NUM, imgCount));
    }

    public List<CircleTimeLineExObj> getTimeLines(){
        List<CircleTimeLineExObj> timeLineObjList = new ArrayList<>();
        for(CircleTimeLineWrapperObj timeLineWrapObj : getListData()){
            timeLineObjList.addAll(timeLineWrapObj.getTimelineList());
        }
        return timeLineObjList;
    }

    public boolean isAllSelect(){
        List<CircleTimeLineExObj> timeLineObjList = getTimeLines();
        return timeLineObjList.containsAll(selTimeLines)
                && selTimeLines.containsAll(timeLineObjList);
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

        return TYPE_TIMES;
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

    class TimesViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_image)
        ImageView ivImage;
        @Bind(R.id.tv_title)
        TextView tvTitle;
        @Bind(R.id.fl_image)
        FrameLayout flImage;
        @Bind(R.id.cb_select)
        CheckBox cbSelect;
        @Bind(R.id.tv_content)
        TextView tvContent;
        @Bind(R.id.tv_img_count)
        TextView tvImgCount;
        @Bind(R.id.ll_time_root)
        LinearLayout llRoot;

        TimesViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            cbSelect.setOnClickListener(onCheckedListener);
        }
    }

    private View.OnClickListener onCheckedAllListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int line = (int) v.getTag(R.string.tag_index);
            int dataIndex = getDataPosition(line);
            if (selTimeLines.size() + listData.get(dataIndex).getTimelineList().size() > maxCount) {
                ToastUtil.showToast("最多只能选" + maxCount + "张照片");
                ((CheckBox) v).setChecked(false);
                return;
            }

            boolean isChecked = ((CheckBox) v).isChecked();
            for (CircleTimeLineExObj item : listData.get(dataIndex).getTimelineList()) {
                if (isChecked) {
                    doSelImg(dataIndex, item);
                } else {
                    doUnSelImg(dataIndex, item);
                }
            }
//            EventBus.getDefault().post(new PhotoSelectCountEvent(selTimeLines.size()));
            EventBus.getDefault().post(new TimeSelectCountEvent(selTimeLines.size()));
            notifyDataSetChanged();
        }
    };

    private View.OnClickListener onCheckedListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            CheckBox cb = (CheckBox) v;
            CircleTimeLineExObj img = (CircleTimeLineExObj) v.getTag(R.string.tag_obj);
            int dataIndex = (int) v.getTag(R.string.tag_ex);
            if (cb.isChecked()) {
                if (selTimeLines.size() + 1 > maxCount) {
                    ToastUtil.showToast("最多只能选" + maxCount + "张照片");
                    ((CheckBox) v).setChecked(false);
                    return;
                }
                doSelImg(dataIndex, img);
            } else {
                doUnSelImg(dataIndex, img);
            }
//            EventBus.getDefault().post(new TimeSelectCountEvent(selTimeLines.size()));
            EventBus.getDefault().post(new CircleSelectTimeLineEvent(((CheckBox) v).isChecked(), img));
        }
    };

    private int getTitleLineFromDataIndex(int dataIndex) {
        if (dataIndex == 0) {
            return 0;
        } else {
            return lineEnd[dataIndex - 1];
        }
    }

    private void doSelImg(int dataIndex, CircleTimeLineExObj img) {
        if (!selTimeLines.contains(img)) {
            //选中上传
//            UploadAllPicService.addUrgent(App.getInstance(), img);
            selTimeLines.add(img);
            everyGroupUnSelImgSize[dataIndex] -= 1;
            if (everyGroupUnSelImgSize[dataIndex] == 0) {
                //全选
                notifyItemChanged(getTitleLineFromDataIndex(dataIndex) + getHeaderCount());
            }
        }

        //处理照片选中状态
        if(!selMedias.containsAll(img.getCircleTimeline().getMediaList())){
            selMedias.addAll(img.getCircleTimeline().getMediaList());
        }

        EventBus.getDefault().post(new CircleSelectTimeLineEvent(true, img));
    }

    public void doAllSelImg(){
        selTimeLines.clear();
        selMedias.clear();
        for(CircleTimeLineWrapperObj timeLineWrapObj : listData){
            selTimeLines.addAll(timeLineWrapObj.getTimelineList());
            for(CircleTimeLineExObj circleTimeLineExObj : timeLineWrapObj.getTimelineList()){
                selMedias.addAll(circleTimeLineExObj.getCircleTimeline().getMediaList());
            }
        }
        setupData(true);
        notifyDataSetChanged();
    }

    public void doAllUnSelImg(){
        selTimeLines.clear();
        selMedias.clear();
        setupData(false);
        notifyDataSetChanged();
    }

    private void doUnSelImg(int dataIndex, CircleTimeLineExObj img) {
        if (selTimeLines.contains(img)) {
            for(MediaObj mediaObj : img.getCircleTimeline().getMediaList()){
                mediaObj.setSelected(0);
            }
            //取消上传
//            UploadAllPicService.addUrgent(App.getInstance(), img);
            selTimeLines.remove(img);
            everyGroupUnSelImgSize[dataIndex] += 1;
            if (everyGroupUnSelImgSize[dataIndex] == 1) {
                //非全选
                notifyItemChanged(getTitleLineFromDataIndex(dataIndex) + getHeaderCount());
            }
        }

        //处理照片选中状态
        if(selMedias.containsAll(img.getCircleTimeline().getMediaList())){
            selMedias.removeAll(img.getCircleTimeline().getMediaList());
        }

        EventBus.getDefault().post(new CircleSelectTimeLineEvent(false, img));
    }

    public List<CircleTimeLineExObj> getSelImgs() {
        return selTimeLines;
    }

    public List<CircleMediaObj> getSelMedias() {
        return selMedias;
    }

    public void setSelImgs(ArrayList<CircleTimeLineExObj> imgs) {
        this.selTimeLines = imgs;
        setupData();
    }
}
