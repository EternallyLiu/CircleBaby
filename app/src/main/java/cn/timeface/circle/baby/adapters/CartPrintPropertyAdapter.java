package cn.timeface.circle.baby.adapters;

import android.animation.Animator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.rayboot.widget.ratioview.RatioImageView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.adapters.base.BaseRecyclerAdapter;
import cn.timeface.circle.baby.api.models.PrintCartItem;
import cn.timeface.circle.baby.api.models.objs.PrintPropertyPriceObj;
import cn.timeface.circle.baby.constants.TypeConstant;

/**
 * @author YW.SUN
 * @from 2015/5/18
 * @TODO
 */
public class CartPrintPropertyAdapter extends BaseRecyclerAdapter<PrintPropertyPriceObj> {
    public final static int PROPERTY_STATE_NOMAL = 0;
    public final static int PROPERTY_STATE_EDIT = 1;
    public int propertyState;
    private int sPosition;
    private PrintCartItem cartItem;

    public CartPrintPropertyAdapter(Context mContext, List listData, int sPosition, PrintCartItem cartItem) {
        super(mContext, listData);
        removeAllFooter();
        this.sPosition = sPosition;
        this.cartItem = cartItem;
    }

    @Override
    public CartPrintPropertyAdapter.ViewHolder getViewHolder(ViewGroup viewGroup, int i) {
        View view = mLayoutInflater.inflate(R.layout.item_print_cart_property, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void bindData(RecyclerView.ViewHolder viewHolder, int i) {
        final PrintPropertyPriceObj obj = listData.get(i);
        ((ViewHolder) viewHolder).mIvRadio.setSelected(true);
        ((ViewHolder) viewHolder).mTvNumber.setText(mContext.getString(R.string.cart_print_property_num, String.valueOf(obj.getNum())));
        String size = cartItem.getPropertyShow("size", String.valueOf(obj.getSize()));
        size = TextUtils.isEmpty(size) ? size : size.substring(0, size.indexOf(","));
        if (cartItem.getPrintCode() == TypeConstant.PRINT_CODE_LIMIT_LESS
                || cartItem.getPrintCode() == TypeConstant.PRINT_CODE_LIMIT_MORE
                || cartItem.getPrintCode() == TypeConstant.PRINT_CODE_LIMIT_HAD_DELETE) {
            ((ViewHolder) viewHolder).mIvRadio.setSelected(false);
        } else {
            ((ViewHolder) viewHolder).mIvRadio.setSelected(obj.isSelect());
        }

        ((ViewHolder) viewHolder).etNumber.setText(String.valueOf(obj.getNum()));
        if (obj.getNum() < 99 && obj.getNum() > 1) {
            ((ViewHolder) viewHolder).ibPlus.setBackgroundResource(R.drawable.shape_number_input_bg);
            ((ViewHolder) viewHolder).ibMinus.setBackgroundResource(R.drawable.shape_number_input_bg);
        } else {
            ((ViewHolder) viewHolder).ibPlus.setBackgroundResource(R.drawable.shape_grey_border_bg);
            ((ViewHolder) viewHolder).ibMinus.setBackgroundResource(R.drawable.shape_grey_border_bg);
        }

        switch (propertyState) {
            case PROPERTY_STATE_NOMAL:
                ((ViewHolder) viewHolder).llPriceNo.setVisibility(View.VISIBLE);
                ((ViewHolder) viewHolder).tvDelete.setVisibility(View.GONE);
                ((ViewHolder) viewHolder).mTvColor.setCompoundDrawables(null, null, null, null);
                ((ViewHolder) viewHolder).mTvColor.setClickable(false);
                ((ViewHolder) viewHolder).llPaperPackLayout.setVisibility(View.VISIBLE);
                ((ViewHolder) viewHolder).llPlusMinusLayout.setVisibility(View.GONE);
                break;

            case PROPERTY_STATE_EDIT:
                ((ViewHolder) viewHolder).llPriceNo.setVisibility(View.GONE);
                ((ViewHolder) viewHolder).tvDelete.setVisibility(View.VISIBLE);
                Drawable drawable = mContext.getResources().getDrawable(R.drawable.selector_btn_pull_down);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                ((ViewHolder) viewHolder).mTvColor.setCompoundDrawables(null, null, drawable, null);
                ((ViewHolder) viewHolder).mTvColor.setClickable(true);
                ((ViewHolder) viewHolder).llPaperPackLayout.setVisibility(View.GONE);
                ((ViewHolder) viewHolder).llPlusMinusLayout.setVisibility(View.VISIBLE);
                break;
        }

        if(i < getItemCount() - 1){
            ((ViewHolder) viewHolder).ivDivider.setVisibility(View.VISIBLE);
        } else {
            ((ViewHolder) viewHolder).ivDivider.setVisibility(View.GONE);
        }

        Glide.with(mContext)
                .load(cartItem.getCoverImage())
                .placeholder(R.drawable.book_default_bg)
                .error(R.drawable.book_default_bg)
                .into(((ViewHolder) viewHolder).ivBookCover);

        //台历数据
//        if (TypeConstant.BOOK_TYPE_DESK_CALENDAR == cartItem.getBookType()) {
//            String calendar = cartItem.getPropertyShow("calendar", obj.getCalendar());
////            ((ViewHolder) viewHolder).llPaperPackLayout.setVisibility(View.GONE);
//            ((ViewHolder) viewHolder).mTvColor.setCompoundDrawables(null, null, null, null);
//            ((ViewHolder) viewHolder).mTvSize.setText("规格:" + calendar);
//            ((ViewHolder) viewHolder).mTvColor.setVisibility(View.GONE);
//            ((ViewHolder) viewHolder).mTvColor.setText(mContext.getString(R.string.cart_print_property_num, String.valueOf(obj.getNum())));
//            ((ViewHolder) viewHolder).flBookBg.setBackgroundResource(R.drawable.bg_cart_calendar_cover);
//            ((ViewHolder) viewHolder).ivBookTopBg.setVisibility(View.VISIBLE);
//        } else {
//            ((ViewHolder) viewHolder).llPaperPackLayout.setVisibility(View.VISIBLE);
            ((ViewHolder) viewHolder).mTvColor.setVisibility(View.VISIBLE);
            ((ViewHolder) viewHolder).mTvNumber.setText(mContext.getString(R.string.cart_print_property_num, String.valueOf(obj.getNum())));
            ((ViewHolder) viewHolder).mTvSize.setText(mContext.getString(R.string.cart_print_property_size, size));
            ((ViewHolder) viewHolder).tvPaper.setText(mContext.getString(R.string.cart_print_property_paper,
                    cartItem.getPropertyShow("paper", String.valueOf(obj.getPaper()))));
            ((ViewHolder) viewHolder).mTvColor.setText(mContext.getString(R.string.cart_print_property_color,
                    cartItem.getPropertyShow("color", String.valueOf(obj.getColor()))));
            ((ViewHolder) viewHolder).mTvPack.setText(mContext.getString(R.string.cart_print_property_pack,
                    cartItem.getPropertyShow("pack", String.valueOf(obj.getPack()))));

            ((ViewHolder) viewHolder).flBookBg.setBackgroundResource(R.drawable.timelist_book_bg);
            ((ViewHolder) viewHolder).ivBookTopBg.setVisibility(View.GONE);
//        }

        ((ViewHolder) viewHolder).ivBookCover.setTag(R.string.tag_obj, cartItem);
                ((ViewHolder) viewHolder).mIvRadio.setTag(R.string.tag_obj, obj);
        ((ViewHolder) viewHolder).mIvRadio.setTag(R.string.tag_ex, cartItem);
        ((ViewHolder) viewHolder).mTvColor.setTag(R.string.tag_ex, cartItem);
        ((ViewHolder) viewHolder).mTvColor.setTag(R.string.tag_obj, obj);
//        ((ViewHolder) viewHolder).mTvSize.setTextAndEvent(mContext.getString(R.string.cart_print_property_size, size));
        ((ViewHolder) viewHolder).mTvPrice.setText(mContext.getString(R.string.total_price, obj.getPrice()));
        ((ViewHolder) viewHolder).ibMinus.setTag(R.string.tag_ex, Integer.parseInt(((ViewHolder) viewHolder).etNumber.getText().toString()));
        ((ViewHolder) viewHolder).ibMinus.setTag(R.string.tag_obj, obj);
        ((ViewHolder) viewHolder).ibPlus.setTag(R.string.tag_ex, Integer.parseInt(((ViewHolder) viewHolder).etNumber.getText().toString()));
        ((ViewHolder) viewHolder).ibPlus.setTag(R.string.tag_obj, obj);
        ((ViewHolder) viewHolder).tvDelete.setTag(R.string.tag_index, i);
        ((ViewHolder) viewHolder).tvDelete.setTag(R.string.tag_ex, sPosition);
//        ((ViewHolder) viewHolder).mIvPullDown.setTag(R.string.tag_obj, obj);
//        ((ViewHolder) viewHolder).mIvPullDown.setTag(R.string.tag_ex, cartItem);

        //处理printcode
        switch (cartItem.getPrintCode()) {
            case TypeConstant.PRINT_CODE_LIMIT_LESS:
//                holder.mBtnPrintAgain.setVisibility(View.GONE);
                ((ViewHolder) viewHolder).tvLimitInfo.setVisibility(View.VISIBLE);
                ((ViewHolder) viewHolder).tvLimitInfo.setText(mContext.getString(R.string.cart_print_code_limit_less_2));
                break;

            case TypeConstant.PRINT_CODE_LIMIT_MORE:
//                holder.mBtnPrintAgain.setVisibility(View.GONE);
                ((ViewHolder) viewHolder).tvLimitInfo.setVisibility(View.VISIBLE);
                ((ViewHolder) viewHolder).tvLimitInfo.setText(mContext.getString(R.string.cart_print_code_limit_more_2));
                break;

            case TypeConstant.PRINT_CODE_LIMIT_SOFT_PACK:
                ((ViewHolder) viewHolder).tvLimitInfo.setVisibility(View.VISIBLE);
                ((ViewHolder) viewHolder).tvLimitInfo.setText(mContext.getString(R.string.cart_print_code_limit_soft_pack));
                break;

            case TypeConstant.PRINT_CODE_LIMIT_HAD_DELETE:
//                holder.mBtnPrintAgain.setVisibility(View.GONE);
                ((ViewHolder) viewHolder).tvLimitInfo.setVisibility(View.VISIBLE);
//                if(cartItem.getBookType() == TypeConstant.BOOK_TYPE_DESK_CALENDAR){
//                    ((ViewHolder) viewHolder).tvLimitInfo.setText(mContext.getString(R.string.cart_print_code_limit_had_delete_calendar));
//                } else {
                    ((ViewHolder) viewHolder).tvLimitInfo.setText(mContext.getString(R.string.cart_print_code_limit_had_delete));
//                }
                break;

            default:
                ((ViewHolder) viewHolder).tvLimitInfo.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public int getItemType(int position) {
        return 0;
    }

    @Override
    protected Animator[] getAnimators(View var1) {
        return new Animator[0];
    }

    /**
     * 设置状态
     * @param state
     */
    public void setPropertyState(int state) {
        this.propertyState = state;
        notifyDataSetChanged();
    }

    /**
     * adapter state
     * @return
     */
    public int getPropertyState(){
        return propertyState;
    }

    /**
     * get adapter bookId
     * @return
     */
    public String getBookId(){
        return cartItem.getBookId();
    }

    public List<PrintPropertyPriceObj> getDataList(){
        return listData;
    }

    /**
     * This class contains all butterknife-binded Views & Layouts from layout file 'item_print_cart_property.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_radio) ImageView mIvRadio;
        @Bind(R.id.tv_size) TextView mTvSize;
        @Bind(R.id.tv_color) TextView mTvColor;
        @Bind(R.id.tv_paper) TextView tvPaper;
        @Bind(R.id.tv_number) TextView mTvNumber;
        @Bind(R.id.tv_pack) TextView mTvPack;
        @Bind(R.id.tv_price) TextView mTvPrice;
        @Bind(R.id.ll_price_number) LinearLayout llPriceNo;
        @Bind(R.id.tv_delete) TextView tvDelete;
        @Bind(R.id.ll_paper_pack) LinearLayout llPaperPackLayout;
        @Bind(R.id.ll_plus_minus) LinearLayout llPlusMinusLayout;
        @Bind(R.id.book_print_number_et) EditText etNumber;
        @Bind(R.id.book_print_number_minus_ib) ImageButton ibMinus;
        @Bind(R.id.book_print_number_plus_ib) ImageButton ibPlus;
        @Bind(R.id.cart_property_divider) View ivDivider;
        @Bind(R.id.iv_book_cover)RatioImageView ivBookCover;
        @Bind(R.id.iv_cart_calendar_top)
        RatioImageView ivBookTopBg;
        @Bind(R.id.fl_cover) FrameLayout flBookBg;
        @Bind(R.id.tv_print_limit_info) TextView tvLimitInfo;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
