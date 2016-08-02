package cn.timeface.circle.baby.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.rayboot.widget.ratioview.RatioFrameLayout;
import com.github.rayboot.widget.ratioview.RatioImageView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.adapters.base.BaseListAdapter;
import cn.timeface.circle.baby.api.models.objs.MyOrderBookItem;
import cn.timeface.circle.baby.api.models.objs.PrintPropertyPriceObj;

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
            convertView = mLayoutInflater.inflate(R.layout.item_my_order_print_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final PrintPropertyPriceObj obj = listData.get(position);

        Glide.with(mContext)
                .load(bookItem.getCoverImage())
                .error(R.drawable.book_default_bg)
                .placeholder(R.drawable.book_default_bg)
                .into(viewHolder.ivBookCover);
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
        size = size.substring(0, size.indexOf(","));
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

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'item_my_order_print_item.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    static class ViewHolder {
        @Bind(R.id.iv_bookbg)
        ImageView ivBookbg;
        @Bind(R.id.iv_book_cover)
        ImageView ivBookCover;
        @Bind(R.id.fl_book_cover)
        RatioFrameLayout flBookCover;
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
        LinearLayout llRoot;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
