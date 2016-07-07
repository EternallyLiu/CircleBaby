package cn.timeface.open.adapters;

import android.graphics.Point;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import cn.timeface.open.api.models.objs.TFOBookContentModel;
import cn.timeface.open.api.models.objs.TFOBookModel;
import cn.timeface.open.fragments.PageFragment;

/**
 * author: rayboot  Created on 16/6/1.
 * email : sy0725work@gmail.com
 */
public class PODViewPagerAdapter extends FragmentStatePagerAdapter {
    TFOBookModel bookModel;
    final Point rootViewInfo;

    public PODViewPagerAdapter(FragmentManager fm, TFOBookModel bookModel, Point rootViewInfo) {
        super(fm);
        this.bookModel = bookModel;
        this.rootViewInfo = rootViewInfo;
    }

    @Override
    public int getCount() {
        return singlePage() ? bookModel.getContentList().size() : (bookModel.getContentList().size() / 2 + 1);
    }

    public boolean singlePage() {
        return rootViewInfo.x < rootViewInfo.y;
    }

    public int[] getContentIndex(int position) {
        if (singlePage()) {
            return new int[]{position};
        } else {
            int rightIndex = position * 2;
            int leftIndex = rightIndex - 1;
            return new int[]{leftIndex, rightIndex};
        }
    }

    @Override
    public Fragment getItem(int position) {
        if (singlePage()) {
            //竖屏,只展示右面
            return PageFragment.newInstance(rootViewInfo, bookModel.getContentList().get(position));
        } else {
            //横屏
            int rightIndex = position * 2;
            int leftIndex = rightIndex - 1;

            TFOBookContentModel left = leftIndex == -1 ? new TFOBookContentModel(true, bookModel.getBookWidth(), bookModel.getBookHeight()) : bookModel.getContentList().get(leftIndex);
            TFOBookContentModel right = rightIndex == bookModel.getContentList().size() ? new TFOBookContentModel(false, bookModel.getBookWidth(), bookModel.getBookHeight()) : bookModel.getContentList().get(rightIndex);

            return PageFragment.newInstance(rootViewInfo, left, right);
        }
    }
}
