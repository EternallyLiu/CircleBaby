package cn.timeface.circle.baby.ui.growth.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import cn.timeface.circle.baby.support.mvp.bases.BasePresenterFragment;

/**
 * 我的作品adapter
 * author : YW.SUN Created on 2017/2/13
 * email : sunyw10@gmail.com
 */
public class MineBookAdapterV2 extends FragmentPagerAdapter {
    private final List<BasePresenterFragment> mFragments = new ArrayList<>();
    private final List<String> mFragmentTitles = new ArrayList<>();

    public MineBookAdapterV2(FragmentManager fm) {
        super(fm);
    }

    public void addFragment(BasePresenterFragment fragment, String title) {
        mFragments.add(fragment);
        mFragmentTitles.add(title);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitles.get(position);
    }
}
