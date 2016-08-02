package cn.timeface.circle.baby.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.timeface.circle.baby.R;
import cn.timeface.circle.baby.api.models.responses.MyOrderConfirmListResponse;
import cn.timeface.circle.baby.constants.TypeConstant;

/**
 * Created by zhsheng on 2016/6/22.
 */
public class OrderDetailFootView extends LinearLayout {

    @Bind(R.id.tv_total_count)
    TextView tvTotalCount;
    @Bind(R.id.tv_exchange_point)
    TextView tvExchangePoint;
    @Bind(R.id.ll_exchange_point)
    LinearLayout llExchangePoint;
    @Bind(R.id.tv_coupon_label)
    TextView tvCouponLabel;
    @Bind(R.id.tv_coupon)
    TextView tvCoupon;
    @Bind(R.id.ll_coupon)
    LinearLayout llCoupon;
    @Bind(R.id.tv_pv_code)
    TextView tvPvCode;
    @Bind(R.id.ll_pv_code)
    LinearLayout llPvCode;
    @Bind(R.id.tv_express_fee)
    TextView tvExpressFee;

    public OrderDetailFootView(Context context) {
        super(context);
        initView();
    }

    public OrderDetailFootView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public OrderDetailFootView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public OrderDetailFootView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    private void initView() {
        View footView = LayoutInflater.from(getContext()).inflate(R.layout.activity_cart_detail_foot, this, false);
        ButterKnife.bind(this, footView);
        addView(footView);
    }

    public void setupViewData(MyOrderConfirmListResponse listResponse) {

//        // 配送中 || 已送达
//        if (listResponse.getOrderStatus() == 3 || listResponse.getOrderStatus() == 5) {
//            // 商家优惠码现场配送不显示运单号、物流信息
//            if (listResponse.getOrderStatus() == 5) {
//                orderActionBtn.setText(getResources().getString(R.string.show_order));
//                orderActionBtn.setVisibility(View.INVISIBLE);//不显示晒单
//            }
//        }
//        // 待确认(未支付)
//        if (listResponse.getOrderStatus() == TypeConstant.STATUS_NOT_PAY) {
//            orderActionBtn.setVisibility(View.VISIBLE);
//            orderActionCancelBtn.setVisibility(View.VISIBLE);
//            orderActionBtn.setText(getResources().getString(R.string.payoff_at_once));
//            orderActionBtn.setBackgroundResource(R.drawable.selector_red_btn_bg);
//        }
//        orderTotalPriceTv.setText(String.format(getResources().getString(R.string.total_price), listResponse.getOrderPrice()));
        tvTotalCount.setText(String.format(getResources().getString(R.string.total_price), listResponse.getTotalPrice()));
       /* // 是否使用了商家优惠
        boolean pvCodeEnable = listResponse.getPersonType() == 5;
        // 是否使用了全场优惠
        boolean fullSiteCouponEnable = listResponse.getPersonType() == 2;

        llExchangePoint.setVisibility(pvCodeEnable ? View.GONE : View.VISIBLE);
        tvExchangePoint.setText(String.format("—%s",
                getResources().getString(R.string.total_price,
                        (float) (listResponse.getPointsExchanged() / listResponse.getExchangeRate()))));
        llCoupon.setVisibility(pvCodeEnable ? View.GONE : View.VISIBLE);
        tvCouponLabel.setText(fullSiteCouponEnable ? listResponse.getCouponDesc() + ":" : getResources().getString(R.string.cart_coupon_label));
        tvCoupon.setText(String.format("—%s", getResources().getString(R.string.total_price, listResponse.getCoupon())));
        llPvCode.setVisibility(pvCodeEnable ? View.VISIBLE : View.GONE);
        tvPvCode.setText(String.format("—%s", getResources().getString(R.string.total_price, listResponse.getCoupon())));*/
        tvExpressFee.setText(getResources().getString(R.string.total_price, listResponse.getExpressPrice()));
    }
}
