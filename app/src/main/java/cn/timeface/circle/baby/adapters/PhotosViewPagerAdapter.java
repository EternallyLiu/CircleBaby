package cn.timeface.circle.baby.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.bumptech.glide.Glide;

import java.util.List;

import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.support.api.models.db.PhotoModel;
import cn.timeface.circle.baby.views.PhotoSelectView;


/**
 * @author YW.SUN
 * @from 2016/3/24
 * @TODO
 */
public class PhotosViewPagerAdapter extends PagerAdapter {


    private final Context mContext;
    private List<PhotoModel> mItems;

    public PhotosViewPagerAdapter(Context context, List<PhotoModel> mItems) {
        mContext = context;
        this.mItems = mItems;
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

    public PhotoModel getItem(int position) {
        if (position >= 0 && position < getCount()) {
            return mItems.get(position);
        }
        return null;
    }

    @Override
    public Object instantiateItem(View container, int position) {
        PhotoModel upload = mItems.get(position);
        PhotoSelectView view = new PhotoSelectView(getContext());
        view.getCbSel().setTag(R.string.tag_obj, upload);
        view.getCbSel().setVisibility(View.GONE);
        Glide.with(getContext())
                .load(upload.getUri())
                .thumbnail(.2f)
                .fitCenter()
                .into(view.getIvPhoto());
        ((ViewPager) container).addView(view);
        return view;
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
