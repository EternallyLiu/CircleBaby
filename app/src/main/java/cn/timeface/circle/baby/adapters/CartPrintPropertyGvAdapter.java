package cn.timeface.circle.baby.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.rayboot.widget.ratioview.RatioImageView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.adapters.base.BaseListAdapter;
import cn.timeface.circle.baby.support.api.models.objs.PrintParamObj;
import cn.timeface.circle.baby.support.api.models.objs.PrintParamResponse;

/**
 * @author YW.SUN
 * @from 2015/5/21
 * @TODO
 */
public class CartPrintPropertyGvAdapter extends BaseListAdapter<PrintParamObj>{
    private String key;

    public CartPrintPropertyGvAdapter(Context context, List<PrintParamObj> listData, String key) {
        super(context, listData);
        this.key = key;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.item_print_cart_property_gv, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final PrintParamObj paramObj = listData.get(position);
        viewHolder.mLlRoot.setSelected(paramObj.isSelect());
        if (key.equals(PrintParamResponse.KEY_SIZE)) {
            viewHolder.mTvBookSizeDetail.setVisibility(View.VISIBLE);
            String sizeString = paramObj.getShow();
            viewHolder.mTvBookSize.setText(sizeString.substring(0, sizeString.indexOf(",")));
            viewHolder.mTvBookSizeDetail.setText(sizeString.substring(sizeString.indexOf(",") + 1, sizeString.length()));
        } else {
            viewHolder.mTvBookSizeDetail.setVisibility(View.GONE);
            viewHolder.mTvBookSize.setText(paramObj.getShow());
        }

        if (key.equals(PrintParamResponse.KEY_PACK)) {
            Glide.with(mContext)
                    .load(paramObj.getImgUrl())
                    .placeholder(R.drawable.bg_default_holder_img)
                    .error(R.drawable.bg_default_holder_img)
                    .into(viewHolder.mIcon);
            viewHolder.mIcon.setVisibility(View.VISIBLE);
        } else {
            viewHolder.mIcon.setVisibility(View.GONE);
        }

        //可点击
        if (paramObj.isActive()) {
            viewHolder.mTvBookSizeDetail.setTextColor(mContext.getResources().getColorStateList(R.color.selector_radio_text_color));
            viewHolder.mTvBookSize.setTextColor(mContext.getResources().getColorStateList(R.color.selector_radio_text_color));
            viewHolder.mLlRoot.setBackgroundResource(R.drawable.selector_dash_border_bg);
        } else {
            viewHolder.mTvBookSizeDetail.setTextColor(mContext.getResources().getColor(R.color.divider));
            viewHolder.mTvBookSize.setTextColor(mContext.getResources().getColor(R.color.divider));
            viewHolder.mLlRoot.setBackgroundResource(R.drawable.shape_unreactive_border_bg);
        }
        viewHolder.mLlRoot.setTag(R.string.tag_obj, paramObj);
        viewHolder.mLlRoot.setTag(R.string.tag_ex, key);
        return convertView;
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'item_print_cart_property_gv.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    static class ViewHolder {
        @Bind(R.id.iv_icon)
        RatioImageView mIcon;
        @Bind(R.id.tv_book_size)
        TextView mTvBookSize;
        @Bind(R.id.tv_book_size_detail)
        TextView mTvBookSizeDetail;
        @Bind(R.id.ll_root)
        LinearLayout mLlRoot;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
