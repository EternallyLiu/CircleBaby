package cn.timeface.circle.baby.adapters;

import android.animation.Animator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.adapters.base.BaseRecyclerAdapter;
import cn.timeface.circle.baby.constants.TypeConstant;
import cn.timeface.circle.baby.support.api.models.PrintCartItem;
import cn.timeface.circle.baby.support.api.models.objs.PrintPropertyPriceObj;
import cn.timeface.circle.baby.support.mvp.model.BookModel;
import cn.timeface.circle.baby.support.utils.DeviceUtil;
import cn.timeface.circle.baby.support.utils.GlideUtil;

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
    public ViewHolder getViewHolder(ViewGroup viewGroup, int i) {
        View view = mLayoutInflater.inflate(R.layout.item_print_cart_property, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void bindData(RecyclerView.ViewHolder viewHolder, int i) {
        final PrintPropertyPriceObj obj = listData.get(i);

        ViewHolder holder = ((ViewHolder) viewHolder);

        holder.mIvRadio.setSelected(true);
        holder.mTvNumber.setText(mContext.getString(R.string.cart_print_property_num, String.valueOf(obj.getNum())));
        String size = cartItem.getPropertyShow("size", String.valueOf(obj.getSize()));
        size = TextUtils.isEmpty(size) || !size.contains(",") ? size : size.substring(0, size.indexOf(","));
        if (cartItem.getPrintCode() == TypeConstant.PRINT_CODE_LIMIT_LESS
                || cartItem.getPrintCode() == TypeConstant.PRINT_CODE_LIMIT_MORE
                || cartItem.getPrintCode() == TypeConstant.PRINT_CODE_LIMIT_HAD_DELETE) {
            holder.mIvRadio.setSelected(false);
        } else {
            holder.mIvRadio.setSelected(obj.isSelect());
        }

        holder.etNumber.setText(String.valueOf(obj.getNum()));
        holder.etNumber.setSelection(String.valueOf(obj.getNum()).length());
        if (obj.getNum() < 99 && obj.getNum() > 1) {
            holder.ibPlus.setBackgroundResource(R.drawable.shape_number_input_bg);
            holder.ibMinus.setBackgroundResource(R.drawable.shape_number_input_bg);
        } else {
            holder.ibPlus.setBackgroundResource(R.drawable.shape_grey_border_bg);
            holder.ibMinus.setBackgroundResource(R.drawable.shape_grey_border_bg);
        }

        switch (propertyState) {
            case PROPERTY_STATE_NOMAL:
                holder.llPriceNo.setVisibility(View.VISIBLE);
                holder.tvDelete.setVisibility(View.GONE);
                holder.mTvColor.setCompoundDrawables(null, null, null, null);
                holder.mTvColor.setClickable(false);
                holder.llPaperPackLayout.setVisibility(View.VISIBLE);
                holder.llPlusMinusLayout.setVisibility(View.GONE);
                break;

            case PROPERTY_STATE_EDIT:
                holder.llPriceNo.setVisibility(View.GONE);
                holder.tvDelete.setVisibility(View.VISIBLE);
                if (cartItem.getBookType() == BookModel.BOOK_TYPE_CALENDAR) {
                    holder.mTvColor.setCompoundDrawables(null, null, null, null);
                    holder.mTvColor.setClickable(false);
                } else {
                    Drawable drawable = mContext.getResources().getDrawable(R.drawable.selector_btn_pull_down);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    holder.mTvColor.setCompoundDrawables(null, null, drawable, null);
                    holder.mTvColor.setClickable(true);
                }
                holder.llPaperPackLayout.setVisibility(View.GONE);
                holder.llPlusMinusLayout.setVisibility(View.VISIBLE);
                break;
        }

        if (i < getItemCount() - 1) {
            holder.ivDivider.setVisibility(View.VISIBLE);
        } else {
            holder.ivDivider.setVisibility(View.GONE);
        }

        int imageWidth = DeviceUtil.dpToPx(mContext.getResources(), 120);
        switch (cartItem.getBookType()) {
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
        holder.ivBookCover.setLayoutParams(lp);
        holder.ivBookCover.setMaxWidth(imageWidth);

        GlideUtil.displayImage(cartItem.getCoverImage(), holder.ivBookCover);

        //台历数据
//        if (TypeConstant.BOOK_TYPE_DESK_CALENDAR == cartItem.getBookType()) {
//            String calendar = cartItem.getPropertyShow("calendar", obj.getCalendar());
////            holder.llPaperPackLayout.setVisibility(View.GONE);
//            holder.mTvColor.setCompoundDrawables(null, null, null, null);
//            holder.mTvSize.setText("规格:" + calendar);
//            holder.mTvColor.setVisibility(View.GONE);
//            holder.mTvColor.setText(mContext.getString(R.string.cart_print_property_num, String.valueOf(obj.getNum())));
//        } else {
//            holder.llPaperPackLayout.setVisibility(View.VISIBLE);
        holder.mTvColor.setVisibility(View.VISIBLE);
        holder.mTvNumber.setText(mContext.getString(R.string.cart_print_property_num, String.valueOf(obj.getNum())));
        holder.mTvSize.setText(mContext.getString(R.string.cart_print_property_size, size));
        holder.tvPaper.setText(mContext.getString(R.string.cart_print_property_paper,
                cartItem.getPropertyShow("paper", String.valueOf(obj.getPaper()))));
        holder.mTvColor.setText(mContext.getString(R.string.cart_print_property_color,
                cartItem.getPropertyShow("color", String.valueOf(obj.getColor()))));
        holder.mTvPack.setText(mContext.getString(R.string.cart_print_property_pack,
                cartItem.getPropertyShow("pack", String.valueOf(obj.getPack()))));

//        }

        holder.ivBookCover.setTag(R.string.tag_obj, cartItem);
        holder.mIvRadio.setTag(R.string.tag_obj, obj);
        holder.mIvRadio.setTag(R.string.tag_ex, cartItem);
        holder.mTvColor.setTag(R.string.tag_ex, cartItem);
        holder.mTvColor.setTag(R.string.tag_obj, obj);
//        holder.mTvSize.setTextAndEvent(mContext.getString(R.string.cart_print_property_size, size));
        holder.mTvPrice.setText(mContext.getString(R.string.total_price, obj.getPrice()));
        holder.ibMinus.setTag(R.string.tag_ex, Integer.parseInt(holder.etNumber.getText().toString()));
        holder.ibMinus.setTag(R.string.tag_obj, obj);
        holder.ibPlus.setTag(R.string.tag_ex, Integer.parseInt(holder.etNumber.getText().toString()));
        holder.ibPlus.setTag(R.string.tag_obj, obj);
        holder.etNumber.setTag(R.string.tag_obj, obj);
        holder.tvDelete.setTag(R.string.tag_index, i);
        holder.tvDelete.setTag(R.string.tag_ex, sPosition);
//        holder.mIvPullDown.setTag(R.string.tag_obj, obj);
//        holder.mIvPullDown.setTag(R.string.tag_ex, cartItem);

        holder.ibMinus.setOnClickListener(minusClickListener);
        holder.ibPlus.setOnClickListener(plusClickListener);
        holder.etNumber.addTextChangedListener(new EditTextWatcher(holder.etNumber, 99));

        //处理printcode
        switch (cartItem.getPrintCode()) {
            case TypeConstant.PRINT_CODE_LIMIT_LESS:
//                holder.mBtnPrintAgain.setVisibility(View.GONE);
                holder.tvLimitInfo.setVisibility(View.VISIBLE);
                holder.tvLimitInfo.setText(mContext.getString(R.string.cart_print_code_limit_less_2));
                break;

            case TypeConstant.PRINT_CODE_LIMIT_MORE:
//                holder.mBtnPrintAgain.setVisibility(View.GONE);
                holder.tvLimitInfo.setVisibility(View.VISIBLE);
                holder.tvLimitInfo.setText(mContext.getString(R.string.cart_print_code_limit_more_2));
                break;

            case TypeConstant.PRINT_CODE_LIMIT_SOFT_PACK:
                holder.tvLimitInfo.setVisibility(View.VISIBLE);
                holder.tvLimitInfo.setText(mContext.getString(R.string.cart_print_code_limit_soft_pack));
                break;

            case TypeConstant.PRINT_CODE_LIMIT_HAD_DELETE:
//                holder.mBtnPrintAgain.setVisibility(View.GONE);
                holder.tvLimitInfo.setVisibility(View.VISIBLE);
//                if(cartItem.getBookType() == TypeConstant.BOOK_TYPE_DESK_CALENDAR){
//                    holder.tvLimitInfo.setText(mContext.getString(R.string.cart_print_code_limit_had_delete_calendar));
//                } else {
                holder.tvLimitInfo.setText(mContext.getString(R.string.cart_print_code_limit_had_delete));
//                }
                break;

            default:
                holder.tvLimitInfo.setVisibility(View.GONE);
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
     *
     * @param state
     */
    public void setPropertyState(int state) {
        this.propertyState = state;
        notifyDataSetChanged();
    }

    /**
     * adapter state
     *
     * @return
     */
    public int getPropertyState() {
        return propertyState;
    }

    /**
     * get adapter bookId
     *
     * @return
     */
    public String getBookId() {
        return cartItem.getBookId();
    }

    public List<PrintPropertyPriceObj> getDataList() {
        return listData;
    }

    /**
     * This class contains all butterknife-binded Views & Layouts from layout file 'item_print_cart_property.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_radio)
        ImageView mIvRadio;
        @Bind(R.id.iv_book_cover)
        ImageView ivBookCover;
        @Bind(R.id.tv_size)
        TextView mTvSize;
        @Bind(R.id.tv_color)
        TextView mTvColor;
        @Bind(R.id.tv_paper)
        TextView tvPaper;
        @Bind(R.id.tv_pack)
        TextView mTvPack;
        @Bind(R.id.ll_paper_pack)
        LinearLayout llPaperPackLayout;
        @Bind(R.id.book_print_number_minus_ib)
        ImageButton ibMinus;
        @Bind(R.id.book_print_number_et)
        EditText etNumber;
        @Bind(R.id.book_print_number_plus_ib)
        ImageButton ibPlus;
        @Bind(R.id.ll_plus_minus)
        LinearLayout llPlusMinusLayout;
        @Bind(R.id.tv_price)
        TextView mTvPrice;
        @Bind(R.id.tv_number)
        TextView mTvNumber;
        @Bind(R.id.ll_price_number)
        LinearLayout llPriceNo;
//        @Bind(R.id.tv_delete)
//        TextView tvDelete;
        @Bind(R.id.fl_delete)
        FrameLayout tvDelete;
        @Bind(R.id.ll_root)
        RelativeLayout llRoot;
        @Bind(R.id.tv_print_limit_info)
        TextView tvLimitInfo;
        @Bind(R.id.cart_property_divider)
        View ivDivider;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    private View.OnClickListener plusClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            PrintPropertyPriceObj propertyObj = (PrintPropertyPriceObj) v.getTag(R.string.tag_obj);
            int num = propertyObj.getNum();
            if (num < 99) {
                num += 1;
                propertyObj.setNum(num);
//                notifyDataSetChanged();

                if (onCartNumberChangeListener != null) {
                    onCartNumberChangeListener.onChange(propertyObj, num);
                }
            }
        }
    };

    private View.OnClickListener minusClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            PrintPropertyPriceObj propertyObj = (PrintPropertyPriceObj) v.getTag(R.string.tag_obj);
            int num = propertyObj.getNum();
            if (num > 1) {
                num -= 1;
                propertyObj.setNum(num);
//                notifyDataSetChanged();

                if (onCartNumberChangeListener != null) {
                    onCartNumberChangeListener.onChange(propertyObj, num);
                }
            }
        }
    };

    private OnCartNumberChangeListener onCartNumberChangeListener;

    public void setOnCartNumberChangeListener(OnCartNumberChangeListener onCartNumberChangeListener) {
        this.onCartNumberChangeListener = onCartNumberChangeListener;
    }

    public interface OnCartNumberChangeListener {
        void onChange(PrintPropertyPriceObj obj, int newNumber);
    }

    private class EditTextWatcher implements TextWatcher {
        private EditText editText;
        private int maxNum;

        public EditTextWatcher(EditText editText, int max) {
            this.editText = editText;
            this.maxNum = max;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            int num = 1;
            if (TextUtils.isEmpty(s) || s.toString().equals("0")) {
                s.replace(0, s.length(), "1");
            } else if (s.toString().startsWith("-") || s.toString().startsWith("0")) {
                s.replace(0, 1, "");
                num = Integer.parseInt(s.toString());
            } else {
                num = Integer.parseInt(s.toString());
                if (num > maxNum) {
                    s.replace(0, s.length(), String.valueOf(maxNum));
                    num = maxNum;
                } else if (num < 1) {
                    num = 1;
                }
            }

            PrintPropertyPriceObj propertyObj = (PrintPropertyPriceObj) editText.getTag(R.string.tag_obj);
            if (num != propertyObj.getNum()) {
                propertyObj.setNum(num);
//            notifyDataSetChanged();

                if (onCartNumberChangeListener != null) {
                    onCartNumberChangeListener.onChange(propertyObj, num);
                }
            }
        }
    }

}
