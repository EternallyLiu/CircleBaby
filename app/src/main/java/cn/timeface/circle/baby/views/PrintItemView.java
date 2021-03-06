package cn.timeface.circle.baby.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.support.api.models.objs.MyOrderBookItem;
import cn.timeface.circle.baby.support.api.models.objs.PrintPropertyPriceObj;
import cn.timeface.circle.baby.support.mvp.model.BookModel;
import cn.timeface.circle.baby.support.utils.DeviceUtil;
import cn.timeface.circle.baby.support.utils.GlideUtil;

/**
 * Created by zhsheng on 2016/6/22.
 */
public class PrintItemView extends LinearLayout {

    @Bind(R.id.iv_book_cover)
    ImageView ivBookCover;
    @Bind(R.id.tv_size)
    TextView tvSize;
    @Bind(R.id.tv_color)
    TextView tvColor;
    @Bind(R.id.tv_paper)
    TextView tvPaper;
    @Bind(R.id.tv_pack)
    TextView tvPack;
    @Bind(R.id.tv_price)
    TextView tvPrice;
    @Bind(R.id.tv_number)
    TextView tvNumber;
    @Bind(R.id.ll_price_number)
    LinearLayout llPriceNumber;
    @Bind(R.id.ll_root)
    RelativeLayout llRoot;

    public PrintItemView(Context context) {
        super(context);
        initView();
    }

    public PrintItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public PrintItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PrintItemView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    private void initView() {
        setOrientation(VERTICAL);
        View view = new View(getContext());
        view.setBackgroundColor(getResources().getColor(R.color.divider));
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, 1);
        layoutParams.leftMargin = getResources().getDimensionPixelSize(R.dimen.view_space_normal);
        view.setLayoutParams(layoutParams);
        addView(view);
        View itemView = LayoutInflater.from(getContext()).inflate(R.layout.item_my_order_print_item, this, false);
        ButterKnife.bind(this, itemView);
        addView(itemView);
    }

    public void setupViewData(MyOrderBookItem bookItem, PrintPropertyPriceObj obj) {
        int imageWidth = DeviceUtil.dpToPx(getResources(), 120);
        switch (bookItem.getBookType()) {
            case BookModel.BOOK_TYPE_HARDCOVER_PHOTO_BOOK://精装照片书
            case BookModel.BOOK_TYPE_PAINTING://绘画集
            case BookModel.BOOK_TYPE_GROWTH_QUOTATIONS://成长语录
            case BookModel.BOOK_TYPE_CALENDAR://台历
                imageWidth = DeviceUtil.dpToPx(getResources(), 120);
                break;
            case BookModel.BOOK_TYPE_GROWTH_COMMEMORATION_BOOK://成长纪念册
                imageWidth = DeviceUtil.dpToPx(getResources(), 100);
                break;
        }

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                imageWidth, RelativeLayout.LayoutParams.WRAP_CONTENT);
        ivBookCover.setLayoutParams(lp);
        ivBookCover.setMaxWidth(imageWidth);

        GlideUtil.displayImage(bookItem.getCoverImage(), ivBookCover);

        Resources resources = getResources();
        tvPrice.setText(resources.getString(R.string.total_price, obj.getPrice()));
        tvNumber.setText(resources.getString(R.string.cart_print_property_num, String.valueOf(obj.getNum())));
        tvColor.setText(resources.getString(R.string.cart_print_property_color,
                bookItem.getPropertyShow("color", String.valueOf(obj.getColor()))));
        tvPaper.setText(resources.getString(R.string.cart_print_property_paper,
                bookItem.getPropertyShow("paper", String.valueOf(obj.getPaper()))));
        tvPack.setText(resources.getString(R.string.cart_print_property_pack,
                bookItem.getPropertyShow("pack", String.valueOf(obj.getPack()))));
        String size = bookItem.getPropertyShow("size", String.valueOf(obj.getSize()));
        size = size.substring(0, size.indexOf(",") > 0 ? size.indexOf(",") : size.length());
        tvSize.setText(resources.getString(R.string.cart_print_property_size, size));

        tvColor.setVisibility(View.VISIBLE);
        tvPaper.setVisibility(View.VISIBLE);
        tvPack.setVisibility(View.VISIBLE);
    }
}
