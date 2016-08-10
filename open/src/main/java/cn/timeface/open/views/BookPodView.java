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
    private float scale = 1.f;

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

    public void setupPageScale(float scale) {
        this.scale = scale;
    }

    public float getPageScale() {
        if (scale != 1.f) {
            return scale;
        }
        Point screenInfo = new Point(getWidth(), getHeight());
        if (screenInfo.y > screenInfo.x) {
            //竖屏
            scale = screenInfo.x / (float) tfoBookModel.getBookWidth();
        } else {
            //横屏
            float pageW = tfoBookModel.getBookWidth() * 2;
            float pageH = tfoBookModel.getBookHeight();

            scale = screenInfo.y / pageH;
            if (pageW * scale > screenInfo.x) {
                //按照高度拉伸,如果拉伸后宽度超出屏幕
                scale = screenInfo.x / pageW;
            }
        }
        return scale;
    }

    public void setupPodData(FragmentManager supportFragmentManager, TFOBookModel tfoBookModel) {
        this.tfoBookModel = tfoBookModel;
        tfoBookModel.setPageScale(getPageScale());
        for (TFOBookContentModel cm : tfoBookModel.getContentList()) {
            if (cm.getPageType() == TFOBookContentModel.PAGE_RIGHT) {
                cm.setRightPage(true);
            }
        }
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
     * 获取页数
     * @return
     */
    public int getPageCount() {
        return viewPager.getAdapter().getCount();
    }

    /**
     * 获取当前页码
     * @return
     */
    public int getCurrentIndex() {
        return viewPager.getCurrentItem();
    }

    /**
     * 设置当前页码
     * @return
     */
    public void setCurrentIndex(int index) {
        viewPager.setCurrentItem(index);
    }

    /**
     * 添加监听事件
     * @param listener
     */
    public void addOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        viewPager.addOnPageChangeListener(listener);
    }

    /**
     * 清空监听事件
     */
    public void clearOnPageChangeListeners() {
        viewPager.clearOnPageChangeListeners();
    }

    /**
     * 获取当前页的内容数据
     *
     * @return 竖屏list.size =1;横屏的list.size=2(左右);
     */
    public List<TFOBookContentModel> getCurrentPageData() {
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
