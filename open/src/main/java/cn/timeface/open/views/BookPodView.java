package cn.timeface.open.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

import cn.timeface.open.R;
import cn.timeface.open.adapters.PODViewPagerAdapter;
import cn.timeface.open.api.models.objs.TFOBookContentModel;
import cn.timeface.open.api.models.objs.TFOBookElementModel;
import cn.timeface.open.api.models.objs.TFOBookModel;
import cn.timeface.open.views.viewpager.transforms.StackTransformer;

/**
 * Created by zhsheng on 2016/7/8.
 */
public class BookPodView extends FrameLayout {

    private ViewPager viewPager;
    private PODViewPagerAdapter adapter;
    private TFOBookModel tfoBookModel;
    List<TFOBookContentModel> imageModels = new ArrayList<>(2);
    private boolean isCover;

    public BookPodView(Context context) {
        super(context);
        initView();
    }

    public BookPodView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public BookPodView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BookPodView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    public void setupPodDate(FragmentManager supportFragmentManager, TFOBookModel tfoBookModel) {
        Point screenInfo = new Point(getWidth(), getHeight());
        float pageScale;
        if (screenInfo.y > screenInfo.x) {
            //竖屏
            pageScale = screenInfo.x / (float) tfoBookModel.getBookWidth();
        } else {
            //横屏
            float pageW = tfoBookModel.getBookWidth() * 2;
            float pageH = tfoBookModel.getBookHeight();

            pageScale = screenInfo.y / pageH;
            if (pageW * pageScale > screenInfo.x) {
                //按照高度拉伸,如果拉伸后宽度超出屏幕
                pageScale = screenInfo.x / pageW;
            }
        }
        tfoBookModel.setPageScale(pageScale);
        for (TFOBookContentModel cm : tfoBookModel.getContentList()) {
            if (cm.getPageType() == TFOBookContentModel.PAGE_RIGHT) {
                for (TFOBookElementModel em : cm.getElementList()) {
                    em.setRight(true);
                }
            }
        }
        this.tfoBookModel = tfoBookModel;
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                getCurrentPage();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setPageTransformer(true, new StackTransformer());
        adapter = new PODViewPagerAdapter(supportFragmentManager, tfoBookModel, new Point(getWidth(), getHeight()));
        viewPager.setAdapter(adapter);

    }

    private void initView() {
        viewPager = new ViewPager(getContext());
        viewPager.setId(R.id.book_pod_viewPage);//FragmentStatePagerAdapter竟然这里这个是必须要有的！
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        layoutParams.gravity = Gravity.CENTER;
        viewPager.setLayoutParams(layoutParams);
        addView(viewPager);
    }

    /**
     * 翻到上一页
     */
    public void clickPre() {

        if (viewPager.getCurrentItem() > 0) {
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
    }

    /**
     * 翻到下一页
     */
    public void clickNext() {
        if (viewPager.getCurrentItem() < viewPager.getAdapter().getCount()) {
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
        }
    }

    /**
     * 翻到指定页
     *
     * @param index
     */
    public void scrollBookPageIndex(int index) {
        if (index < viewPager.getAdapter().getCount()) {
            viewPager.setCurrentItem(index);
        }
    }

    /**
     * 获取当前页的内容数据
     *
     * @return 竖屏list.size =1;横屏的list.size=2(左右);
     */
    public List<TFOBookContentModel> getCurrentPage() {
        imageModels.clear();
        int[] index = adapter.getContentIndex(viewPager.getCurrentItem());
        if (index.length == 1) {
            TFOBookContentModel contentModel = tfoBookModel.getContentList().get(index[0]);
            imageModels.add(contentModel);
        } else {
            int leftIndex = index[0];
            int rightIndex = index[1];
            isCover = false;
            //左页为封底,右页为封面
            if (leftIndex < 0) {
                leftIndex = tfoBookModel.getContentList().size() - 1;
                isCover = true;
            } else if (leftIndex == tfoBookModel.getContentList().size() - 1) {
                rightIndex = 0;
                isCover = true;
            }
            TFOBookContentModel leftModel = tfoBookModel.getContentList().get(leftIndex);
            TFOBookContentModel rightModel = tfoBookModel.getContentList().get(rightIndex);
            imageModels.add(leftModel);
            imageModels.add(rightModel);
        }
        return imageModels;
    }

    /**
     * 判断当前页是不是封面
     *
     * @return
     */
    public boolean currentPageIsCover() {
        return isCover;
    }
}
