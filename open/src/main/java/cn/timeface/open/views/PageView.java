package cn.timeface.open.views;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import cn.timeface.open.R;
import cn.timeface.open.api.models.objs.TFOBookContentModel;
import cn.timeface.open.api.models.objs.TFOBookModel;
import cn.timeface.open.utils.BookModelCache;

/**
 * author: rayboot  Created on 16/4/25.
 * email : sy0725work@gmail.com
 */
public class PageView extends FrameLayout {
    TFOBookModel bookModel;
    TFOBookContentModel leftContent;
    TFOBookContentModel rightContent;
    SingleContentView singleContentView;
    DoubleContentView doubleContentView;
    boolean editMode = false;
    boolean isCover = false;
    float pageScale = 1.0f;

    public PageView(Context context, TFOBookContentModel contentModel, boolean isCover) {
        this(context, false, null, contentModel, isCover);
    }

    public PageView(Context context, TFOBookContentModel leftContent, TFOBookContentModel rightContent, boolean isCover) {
        this(context, false, leftContent, rightContent, isCover);
    }

    public PageView(Context context, boolean editMode, TFOBookContentModel leftContent, TFOBookContentModel rightContent, boolean isCover) {
        super(context);
        this.leftContent = leftContent;
        this.rightContent = rightContent;
        this.editMode = editMode;
        this.isCover = isCover;
        this.bookModel = BookModelCache.getInstance().getBookModel();

        //右页需要修改paddingleft和paddingtop值

        setupView();
    }

    private void setupView() {
        setLayoutParams(new LayoutParams(getViewW(), getViewH()));

        if (singlePage()) {
            genSingleView(rightContent);
        } else {
            genDoubleView(leftContent, rightContent);
        }
    }

    public FrameLayout getContentView() {
        if (singlePage()) {
            return singleContentView;
        } else {
            return doubleContentView;
        }
    }

    private void genSingleView(TFOBookContentModel contentModel) {
        int offsetLeft;
        int offsetTop;
        {
            //画背景颜色
            if (!TextUtils.isEmpty(contentModel.getPageColor())) {
                this.setBackgroundColor(Color.parseColor(contentModel.getPageColor()));
            }

            ImageView ivBg = new ImageView(getContext());
            ivBg.setId(R.id.right_page_id);
            ivBg.setLayoutParams(new LayoutParams(getViewW(), getViewH()));

            //画背景图片
            if (!TextUtils.isEmpty(contentModel.getPageImage())) {
                Glide.with(getContext())
                        .load(contentModel.getPageImage())
                        .centerCrop()
                        .into(ivBg);
            }
            this.addView(ivBg);
        }

        {
            //根据 content_zoom 设置偏移量
            offsetLeft = (int) (bookModel.getContentPaddingLeft() * contentModel.getPageZoom());
            offsetTop = (int) (bookModel.getContentPaddingTop() * contentModel.getPageZoom());
        }

        {
            //绘制版心
            this.singleContentView = new SingleContentView(getContext(), contentModel);
            LayoutParams lp = new LayoutParams(bookModel.getContentWidth(), bookModel.getContentHeight());
            {
                //根据 content_zoom 设置偏移量
                lp.leftMargin = bookModel.getContentPaddingLeft() - offsetLeft;
                lp.topMargin = bookModel.getContentPaddingTop() - offsetTop;
            }
            singleContentView.setLayoutParams(lp);
            addView(singleContentView);
        }

        {
            //根据 content_zoom 缩放版心
            if (offsetLeft > 0 || offsetTop > 0) {
                singleContentView.setPivotX(0);
                singleContentView.setPivotY(0);
                singleContentView.setScaleX(1 + offsetLeft * 2 / bookModel.getContentWidth());
                singleContentView.setScaleY(1 + offsetTop * 2 / bookModel.getContentHeight());
            }
        }
    }

    private void genDoubleView(TFOBookContentModel leftModel, TFOBookContentModel rightModel) {
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        {
            //画左面右面底图的背景
            lp.height = bookModel.getBookHeight();
            lp.width = bookModel.getBookWidth();
            ImageView ivBgLeft = new ImageView(getContext());
            ivBgLeft.setId(R.id.left_page_id);
            ivBgLeft.setLayoutParams(lp);
            lp = new LayoutParams(bookModel.getBookWidth(), bookModel.getBookHeight());
            lp.leftMargin = bookModel.getBookWidth();
            ImageView ivBgRight = new ImageView(getContext());
            ivBgRight.setId(R.id.right_page_id);
            ivBgRight.setLayoutParams(lp);

            {
                if (!TextUtils.isEmpty(leftModel.getPageColor())) {
                    ivBgLeft.setBackgroundColor(Color.parseColor(leftModel.getPageColor()));
                }
                if (!TextUtils.isEmpty(leftModel.getPageImage())) {
                    Glide.with(getContext())
                            .load(leftModel.getPageImage())
                            .centerCrop()
                            .into(ivBgLeft);
                }
                this.addView(ivBgLeft);
            }

            {
                if (!TextUtils.isEmpty(rightModel.getPageColor())) {
                    ivBgRight.setBackgroundColor(Color.parseColor(rightModel.getPageColor()));
                }
                if (!TextUtils.isEmpty(rightModel.getPageImage())) {
                    Glide.with(getContext())
                            .load(rightModel.getPageImage())
                            .centerCrop()
                            .into(ivBgRight);
                }
                this.addView(ivBgRight);
            }
        }

        {
            //根据 content_zoom 设置偏移量  待完善
        }


        this.setPadding(bookModel.getContentPaddingLeft(), bookModel.getContentPaddingTop(), bookModel.getContentPaddingLeft(), 0);

        {
            //绘制版心
            if (editMode) {
                this.doubleContentView = new EditDoubleContentView(getContext(), bookModel.getBookWidth(), leftContent, rightContent, isCover);
            } else {
                this.doubleContentView = new DoubleContentView(getContext(), bookModel.getBookWidth(), leftContent, rightContent);
            }
            lp = new LayoutParams((bookModel.getBookWidth() - bookModel.getContentPaddingLeft()) * 2, bookModel.getContentHeight());
            doubleContentView.setLayoutParams(lp);
            addView(doubleContentView);
        }

    }

    private int getViewW() {
        return singlePage() ? bookModel.getBookWidth() : bookModel.getBookWidth() * 2;
    }

    private int getViewH() {
        return bookModel.getBookHeight();
    }

    private boolean singlePage() {
        return leftContent == null;
    }

    /**
     * 设置背景图片或背景色
     *
     * @param url
     */
    public void setPageBG(String url) {
        ImageView left = (ImageView) this.findViewById(R.id.left_page_id);
        ImageView right = (ImageView) this.findViewById(R.id.right_page_id);
        if (url.contains("http")) {//图片
            if (left != null) {
                Glide.with(getContext())
                        .load(url)
                        .centerCrop()
                        .into(left);
                leftContent.setPageImage(url);
            }

            if (right != null) {
                Glide.with(getContext())
                        .load(url)
                        .centerCrop()
                        .into(right);
                rightContent.setPageImage(url);
            }
        } else {//颜色值
            if (left != null) {
                left.setBackgroundColor(Color.parseColor(url));
                leftContent.setPageColor(url);
            }

            if (right != null) {
                right.setBackgroundColor(Color.parseColor(url));
                rightContent.setPageColor(url);
            }
        }
    }
}
