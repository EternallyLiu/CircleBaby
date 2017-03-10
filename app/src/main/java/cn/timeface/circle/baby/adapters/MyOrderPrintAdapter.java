package cn.timeface.circle.baby.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.adapters.base.BaseListAdapter;
import cn.timeface.circle.baby.support.api.models.objs.MyOrderBookItem;
import cn.timeface.circle.baby.support.api.models.objs.PrintPropertyPriceObj;
import cn.timeface.circle.baby.support.mvp.model.BookModel;
import cn.timeface.circle.baby.support.utils.DeviceUtil;
import cn.timeface.circle.baby.support.utils.GlideUtil;

/**
 * @author WXW
 * @from 2015/5/18
 * @TODO 我的订单->书列表->打印列表适配器
 */
public class MyOrderPrintAdapter extends BaseListAdapter<PrintPropertyPriceObj> {

    private MyOrderBookItem bookItem;

    public MyOrderPrintAdapter(Context context, MyOrderBookItem bookItem) {
        super(context, bookItem.getPrintList());
        this.bookItem = bookItem;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.item_my_order_print_item, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        PrintPropertyPriceObj obj = listData.get(position);

        int imageWidth = DeviceUtil.dpToPx(mContext.getResources(), 120);
        switch (bookItem.getBookType()) {
            case BookModel.BOOK_TYPE_HARDCOVER_PHOTO_BOOK://精装照片书
            case BookModel.BOOK_TYPE_PAINTING://绘画集
            case BookModel.BOOK_TYPE_GROWTH_QUOTATIONS://成长语录
            case BookModel.BOOK_TYPE_CALENDAR://台历
                imageWidth = DeviceUtil.dpToPx(mContext.getResources(), 120);
                break;
            case BookModel.BOOK_TYPE_GROWTH_COMMEMORATION_BOOK://成长纪念册
                imageWidth = DeviceUtil.dpToPx(mContext.getResources(), 100);
                break;
        }

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                imageWidth, RelativeLayout.LayoutParams.WRAP_CONTENT);
        viewHolder.ivBookCover.setLayoutParams(lp);
        viewHolder.ivBookCover.setMaxWidth(imageWidth);

        GlideUtil.displayImage(bookItem.getCoverImage(), viewHolder.ivBookCover);

        viewHolder.tvPrice.setText(mContext.getString(R.string.total_price, obj.getPrice()));
        viewHolder.tvNumber.setText(mContext.getString(R.string.cart_print_property_num, String.valueOf(obj.getNum())));

//        if (bookItem.getBookType() != TypeConstant.BOOK_TYPE_DESK_CALENDAR) {
        viewHolder.tvColor.setText(mContext.getString(R.string.cart_print_property_color,
                bookItem.getPropertyShow("color", String.valueOf(obj.getColor()))));
        viewHolder.tvPaper.setText(mContext.getString(R.string.cart_print_property_paper,
                bookItem.getPropertyShow("paper", String.valueOf(obj.getPaper()))));
        viewHolder.tvPack.setText(mContext.getString(R.string.cart_print_property_pack,
                bookItem.getPropertyShow("pack", String.valueOf(obj.getPack()))));
        String size = bookItem.getPropertyShow("size", String.valueOf(obj.getSize()));
        size = size.substring(0, size.indexOf(",") > 0 ? size.indexOf(",") : size.length());
        viewHolder.tvSize.setText(mContext.getString(R.string.cart_print_property_size, size));

        viewHolder.tvColor.setVisibility(View.VISIBLE);
        viewHolder.tvPaper.setVisibility(View.VISIBLE);
        viewHolder.tvPack.setVisibility(View.VISIBLE);

//        } else {
//            //台历数据
//            viewHolder.tvSize.setText("规格：" + bookItem.getPropertyShow("calendar", obj.getCalendar()));
//
//            viewHolder.tvColor.setVisibility(View.GONE);
//            viewHolder.tvPaper.setVisibility(View.GONE);
//            viewHolder.tvPack.setVisibility(View.GONE);
//
//            viewHolder.flCover.setBackgroundResource(R.drawable.bg_cart_calendar_cover);
//        }

        return convertView;
    }

    static class ViewHolder {
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

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
