package cn.timeface.circle.baby.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.CheckBox;

import com.bumptech.glide.Glide;


import java.util.List;

import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.api.models.objs.ImgObj;
import cn.timeface.circle.baby.events.PhotoSelectEvent;
import cn.timeface.circle.baby.utils.ToastUtil;
import cn.timeface.circle.baby.views.PhotoSelectView;
import de.greenrobot.event.EventBus;


/**
 * @author YW.SUN
 * @from 2016/3/24
 * @TODO
 */
public class PhotosViewPagerAdapter extends PagerAdapter {

    private final Context mContext;
    private List<ImgObj> mItems;
    private List<ImgObj> selItems;
    private int maxCount;

    public PhotosViewPagerAdapter(Context context, List<ImgObj> mItems, List<ImgObj> selItems, int maxCount) {
        mContext = context;
        this.mItems = mItems;
        this.selItems = selItems;
        this.maxCount = maxCount;
    }

    @Override
    public void destroyItem(View container, int position, Object object) {
        View view = (View) object;
        ((ViewPager) container).removeView(view);
    }

    @Override
    public int getCount() {
        return null != mItems ? mItems.size() : 0;
    }

    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public ImgObj getItem(int position) {
        if (position >= 0 && position < getCount()) {
            return mItems.get(position);
        }
        return null;
    }

    @Override
    public Object instantiateItem(View container, int position) {
        ImgObj upload = mItems.get(position);
        PhotoSelectView view = new PhotoSelectView(getContext());
        view.getCbSel().setTag(R.string.tag_obj, upload);
        view.setOnCheckedListener(onCheckedListener);
        if (selItems.contains(upload)) {
            view.setChecked(true);
        }
        Glide.with(getContext())
                .load(upload.getUri())
                .thumbnail(0.1f)
                .fitCenter()
                .into(view.getIvPhoto());
        ((ViewPager) container).addView(view);
        return view;
    }

    private View.OnClickListener onCheckedListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            CheckBox checkBox = (CheckBox) v;
            ImgObj img = (ImgObj) v.getTag(R.string.tag_obj);
            if (checkBox.isChecked()) {
                if (selItems.size() + 1 > maxCount) {
                    ToastUtil.showToast("最多只能选" + maxCount + "张照片");
                    ((CheckBox) v).setChecked(false);
                    return;
                }
                selItems.add(img);
            } else {
                selItems.remove(img);
            }
            EventBus.getDefault().post(new PhotoSelectEvent(selItems.size()));
        }
    };

    public List<ImgObj> getSelImgs() {
        return selItems;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    protected Context getContext() {
        return mContext;
    }
}
