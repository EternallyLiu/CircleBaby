package cn.timeface.circle.baby.ui.growth.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.support.mvp.bases.BasePresenterAppCompatActivity;
import cn.timeface.circle.baby.support.mvp.model.BookModel;
import cn.timeface.circle.baby.ui.growth.adapters.MineBookAdapterV2;
import cn.timeface.circle.baby.ui.growth.fragments.BookListFragment;
import cn.timeface.circle.baby.ui.growth.fragments.DiaryCardListFragment;
import cn.timeface.circle.baby.ui.growth.fragments.RecognizeCardListFragment;

public class MineBookActivityV2 extends BasePresenterAppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.tab_layout)
    TabLayout tabLayout;
    @Bind(R.id.view_pager)
    ViewPager viewPager;

    public static void open(Context context) {
        Intent intent = new Intent(context, MineBookActivityV2.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_book_v2);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("我的作品");
        setupViewPager();
    }

    private void setupViewPager() {
        MineBookAdapterV2 adapter = new MineBookAdapterV2(getSupportFragmentManager());
        adapter.addFragment(BookListFragment.newInstance(BookModel.BOOK_TYPE_HARDCOVER_PHOTO_BOOK), "精装照片书");
        adapter.addFragment(BookListFragment.newInstance(BookModel.BOOK_TYPE_GROWTH_COMMEMORATION_BOOK), "成长纪念册");
        adapter.addFragment(BookListFragment.newInstance(BookModel.BOOK_TYPE_PAINTING), "绘画集");
        adapter.addFragment(BookListFragment.newInstance(BookModel.BOOK_TYPE_GROWTH_QUOTATIONS), "成长语录");
        adapter.addFragment(RecognizeCardListFragment.newInstance(), "识图卡片");
        adapter.addFragment(DiaryCardListFragment.newInstance(), "日记卡片");
        viewPager.setOffscreenPageLimit(6);
        viewPager.setAdapter(adapter);

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
    }
}
