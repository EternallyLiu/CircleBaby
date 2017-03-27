package cn.timeface.circle.baby.ui.growthcircle.mainpage.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import com.wechat.photopicker.adapter.PhotoPagerAdapter;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.activities.base.BaseAppCompatActivity;
import cn.timeface.circle.baby.support.api.models.objs.MediaObj;
import cn.timeface.circle.baby.ui.growthcircle.mainpage.event.CircleCoverSelectedEvent;

/**
 * 更换圈封面-推荐图片-查看大图
 */
public class CircleInfoRecommendCoverDetailActivity extends BaseAppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.view_pager)
    ViewPager viewPager;

    private ArrayList<MediaObj> dataList;
    private int index;

    private List<String> mPaths;

    public static void open(Context context, ArrayList<MediaObj> dataList, int index) {
        Intent intent = new Intent(context, CircleInfoRecommendCoverDetailActivity.class);
        intent.putExtra("data_list", dataList);
        intent.putExtra("index", index);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_info_recommend_cover_detail);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dataList = getIntent().getParcelableArrayListExtra("data_list");
        index = getIntent().getIntExtra("index", 0);

        if (dataList != null && dataList.size() > 0) {
            mPaths = new ArrayList<>(dataList.size());
            for (MediaObj mediaObj : dataList) {
                mPaths.add(mediaObj.getImgUrl());
            }

            updateTitle(index, dataList.size());
            setupViewPage();
        }
    }

    private void setupViewPage() {
        if (mPaths != null && mPaths.size() > 0) {
            PhotoPagerAdapter mPhotoPagerAdapter = new PhotoPagerAdapter(this, mPaths);
            viewPager.setAdapter(mPhotoPagerAdapter);
            viewPager.setCurrentItem(index);

            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(int position) {
                    updateTitle(position, dataList.size());
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });
        }
    }

    private void updateTitle(int index, int total) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle((index + 1) + "/" + total);
        }
    }

    @OnClick(R.id.tv_submit)
    public void onClick() {
        MediaObj item = dataList.get(viewPager.getCurrentItem());
        if (item != null) {
            EventBus.getDefault().post(new CircleCoverSelectedEvent(item.getImgUrl()));
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

}
